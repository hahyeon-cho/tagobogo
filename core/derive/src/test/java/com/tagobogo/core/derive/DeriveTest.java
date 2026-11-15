package com.tagobogo.core.derive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/** 차량 SDK 없이 Derive의 이벤트 처리와 체류 시간 분류를 검사한다. */
public class DeriveTest {

    private static final String SEOUL = "Asia/Seoul";

    private long t = 0L;
    private final List<VehicleEvent> events = new ArrayList<>();

    private void ev(EventType type, long afterMs, EventSource source) {
        t += afterMs;
        events.add(VehicleEvent.of(t, type, source, SEOUL));
    }

    private Stop only() {
        List<Stop> stops = Derive.derive(events);
        assertEquals(1, stops.size());
        return stops.get(0);
    }

    // 착좌와 기어 이벤트에 따른 시작·종료

    @Test
    public void 착좌해제_30분_후_착좌하면_RETURNED() {
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.SEAT_VACATED, 1_000, EventSource.SEAT);
        ev(EventType.SEAT_OCCUPIED, 30 * 60_000, EventSource.SEAT);

        Stop stop = only();
        assertEquals(StopState.RETURNED, stop.state());
        assertEquals(MemoSkipReason.NONE, stop.memoSkipReason());
        assertFalse(stop.needsReview());
    }

    @Test
    public void 기어신호가_없으면_PARK조건을_생략한다() {
        // 기어 이벤트가 없으면 P 조건을 적용하지 않는다.
        ev(EventType.SEAT_VACATED, 0, EventSource.SEAT);
        ev(EventType.SEAT_OCCUPIED, 20 * 60_000, EventSource.SEAT);
        assertEquals(StopState.RETURNED, only().state());
    }

    @Test
    public void 기어가_PARK가_아니면_착좌해제를_무시한다() {
        ev(EventType.PARK_OFF, 0, EventSource.GEAR);
        ev(EventType.SEAT_VACATED, 1_000, EventSource.SEAT);
        ev(EventType.SEAT_OCCUPIED, 20 * 60_000, EventSource.SEAT);
        assertEquals(0, Derive.derive(events).size());
    }

    // 도어와 벨트 이벤트는 하차 상태를 바꾸지 않는다.

    @Test
    public void 도어_개폐만으로는_기록이_생기지_않는다() {
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.DOOR_OPEN, 1_000, EventSource.DOOR);
        ev(EventType.DOOR_CLOSE, 1_000, EventSource.DOOR);
        ev(EventType.DOOR_OPEN, 30 * 60_000, EventSource.DOOR);
        ev(EventType.DOOR_CLOSE, 1_000, EventSource.DOOR);
        assertEquals(0, Derive.derive(events).size());
    }

    @Test
    public void 하차_직후_문을_닫아도_기록이_끝나지_않는다() {
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.SEAT_VACATED, 1_000, EventSource.SEAT);
        ev(EventType.DOOR_OPEN, 500, EventSource.DOOR);
        ev(EventType.DOOR_CLOSE, 500, EventSource.DOOR);
        ev(EventType.SEAT_OCCUPIED, 30 * 60_000, EventSource.SEAT);
        assertEquals(StopState.RETURNED, only().state());
    }

    @Test
    public void 벨트_해제는_기록을_만들지_않는다() {
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.BELT_OFF, 1_000, EventSource.SEAT);
        ev(EventType.DOOR_OPEN, 1_000, EventSource.DOOR);
        ev(EventType.DOOR_CLOSE, 1_000, EventSource.DOOR);
        assertEquals(0, Derive.derive(events).size());
    }

    @Test
    public void 착좌_신호가_없는_차는_자동판정을_하지_않는다() {
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.SEAT_VACATED, 1_000, EventSource.SEAT);
        ev(EventType.SEAT_OCCUPIED, 30 * 60_000, EventSource.SEAT);
        assertEquals(0, Derive.derive(events, Signals.NONE).size());
    }

    // 종료 상태와 메모 제외 사유를 별도로 확인한다.

    @Test
    public void 짧은_정차도_RETURNED_이고_메모만_제외된다() {
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.SEAT_VACATED, 1_000, EventSource.SEAT);
        ev(EventType.SEAT_OCCUPIED, 60_000, EventSource.SEAT);

        Stop stop = only();
        assertEquals(StopState.RETURNED, stop.state());
        assertEquals(MemoSkipReason.SHORT_STOP, stop.memoSkipReason());
    }

    @Test
    public void 장기_주차도_RETURNED_이고_사유만_다르다() {
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.SEAT_VACATED, 1_000, EventSource.SEAT);
        ev(EventType.SEAT_OCCUPIED, 8 * 60 * 60_000, EventSource.SEAT);

        Stop stop = only();
        assertEquals(StopState.RETURNED, stop.state());
        assertEquals(MemoSkipReason.LONG_PARKING, stop.memoSkipReason());
    }

    @Test
    public void 여섯시간이_지나도_임의로_종료하지_않는다() {
        // 복귀가 아닌 이벤트는 종료 시각이 될 수 없다.
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.SEAT_VACATED, 1_000, EventSource.SEAT);
        ev(EventType.HEARTBEAT, 7 * 60 * 60_000, EventSource.APP);

        Stop stop = only();
        assertEquals(StopState.OPEN, stop.state());
        assertNull(stop.endTs());
        assertEquals(MemoSkipReason.STILL_OPEN, stop.memoSkipReason());
    }

    // 앱 재실행과 전원 경고는 열린 기록에 확인 표시만 남긴다.

    @Test
    public void APP_WAKE_는_기록을_끝내지_않고_확인대상으로_표시한다() {
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.SEAT_VACATED, 1_000, EventSource.SEAT);
        ev(EventType.APP_WAKE, 20 * 60_000, EventSource.APP);

        Stop stop = only();
        assertEquals(StopState.OPEN, stop.state());
        assertNull(stop.endTs());
        assertTrue(stop.needsReview());
    }

    @Test
    public void 전원_하강_경고도_기록을_끝내지_않는다() {
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.SEAT_VACATED, 1_000, EventSource.SEAT);
        ev(EventType.POWER_DOWN_WARNING, 20 * 60_000, EventSource.SAFETY);

        Stop stop = only();
        assertEquals(StopState.OPEN, stop.state());
        assertTrue(stop.needsReview());
    }

    @Test
    public void 확인대상_표시는_복귀해도_유지된다() {
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.SEAT_VACATED, 1_000, EventSource.SEAT);
        ev(EventType.POWER_DOWN_WARNING, 10 * 60_000, EventSource.SAFETY);
        ev(EventType.SEAT_OCCUPIED, 10 * 60_000, EventSource.SEAT);

        Stop stop = only();
        assertEquals(StopState.RETURNED, stop.state());
        assertTrue(stop.needsReview());
    }

    // 시작 이벤트의 시간대로 시작일을 계산한다.

    @Test
    public void 자정을_넘겨도_시작일의_한_건이다() {
        long start = ZonedDateTime.of(2026, 11, 11, 23, 40, 0, 0, ZoneId.of(SEOUL))
                .toInstant().toEpochMilli();
        events.add(VehicleEvent.of(start, EventType.SEAT_VACATED, EventSource.SEAT, SEOUL));
        events.add(VehicleEvent.of(start + 40 * 60_000, EventType.SEAT_OCCUPIED, EventSource.SEAT, SEOUL));

        Stop stop = only();
        assertEquals("2026-11-11", stop.startLocalDate());
        assertEquals(SEOUL, stop.startZone());
        assertEquals(Long.valueOf(40 * 60_000L), stop.dwellMs());
        assertEquals(MemoSkipReason.NONE, stop.memoSkipReason());
    }

    @Test
    public void 다른_시간대에서_다시_계산해도_날짜가_같다() {
        long start = ZonedDateTime.of(2026, 11, 11, 23, 40, 0, 0, ZoneId.of(SEOUL))
                .toInstant().toEpochMilli();
        events.add(VehicleEvent.of(start, EventType.SEAT_VACATED, EventSource.SEAT, SEOUL));
        events.add(VehicleEvent.of(start + 40 * 60_000, EventType.SEAT_OCCUPIED, EventSource.SEAT, SEOUL));

        // 같은 입력 이벤트를 재계산해도 지정된 시간대의 시작일을 쓴다.
        assertEquals("2026-11-11", Derive.derive(events).get(0).startLocalDate());
    }

    // 입력 목록을 바꾸거나 재사용해도 결과가 안정적인지 확인한다.

    @Test
    public void derive_는_멱등이다() {
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.SEAT_VACATED, 1_000, EventSource.SEAT);
        ev(EventType.SEAT_OCCUPIED, 10 * 60_000, EventSource.SEAT);
        assertEquals(Derive.derive(events), Derive.derive(events));
    }

    @Test
    public void 이벤트_순서가_뒤섞여_들어와도_같은_결과를_낸다() {
        ev(EventType.PARK_ON, 0, EventSource.GEAR);
        ev(EventType.SEAT_VACATED, 1_000, EventSource.SEAT);
        ev(EventType.SEAT_OCCUPIED, 20 * 60_000, EventSource.SEAT);

        List<VehicleEvent> shuffled = new ArrayList<>(events);
        java.util.Collections.reverse(shuffled);
        assertEquals(Derive.derive(events), Derive.derive(shuffled));
    }
}
