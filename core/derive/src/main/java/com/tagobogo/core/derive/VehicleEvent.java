package com.tagobogo.core.derive;

import java.time.ZoneId;

/**
 * 판정 함수의 입력 이벤트다. 선택적 측정값은 null일 수 있다.
 * zone은 하차 시작일 계산에 사용한다. 이 record 자체에는 저장 기능이 없으며,
 * 아래 간편 생성자는 영구 ID 대신 0을 넣는다.
 */
public record VehicleEvent(
        long id,
        long ts,
        EventType type,
        Integer odometer,
        Double lat,
        Double lng,
        Float confidence,
        EventSource source,
        String zone
) {
    /** 선택적 측정값 없이 현재 실행 환경의 시간대를 넣는다. */
    public static VehicleEvent of(long ts, EventType type, EventSource source) {
        return new VehicleEvent(0L, ts, type, null, null, null, null, source, currentZone());
    }

    public static VehicleEvent of(long ts, EventType type, EventSource source, Integer odometer) {
        return new VehicleEvent(0L, ts, type, odometer, null, null, null, source, currentZone());
    }

    /** 입력 시간대를 직접 지정한다. */
    public static VehicleEvent of(long ts, EventType type, EventSource source, String zone) {
        return new VehicleEvent(0L, ts, type, null, null, null, null, source, zone);
    }

    private static String currentZone() {
        return ZoneId.systemDefault().getId();
    }
}
