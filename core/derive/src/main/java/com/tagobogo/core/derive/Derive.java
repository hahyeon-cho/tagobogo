package com.tagobogo.core.derive;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 이벤트를 시각순으로 읽어 운전석 착좌 해제와 재착좌 사이의 기록을 계산한다.
 * 기어 이벤트가 한 번이라도 들어왔으면 하차 시작 때 P 상태를 요구한다.
 * 이 함수는 이벤트를 저장하지 않으며 좌석값이 실제 상태 변화인지도 확인하지 않는다.
 */
public final class Derive {
    private Derive() {}

    /** 착좌 신호가 사용 가능하다고 가정하는 간편 호출이다. */
    public static List<Stop> derive(List<VehicleEvent> events) {
        return derive(events, Signals.SEAT);
    }

    public static List<Stop> derive(List<VehicleEvent> events, Signals signals) {
        // 빈 결과만으로 센서 미지원과 실제 기록 없음은 구분할 수 없다.
        if (!signals.autoDeriveSupported()) {
            return Collections.emptyList();
        }

        List<VehicleEvent> sorted = new ArrayList<>(events);
        sorted.sort(Comparator.comparingLong(VehicleEvent::ts));

        List<Stop> stops = new ArrayList<>();

        boolean gearSeen = false;
        boolean parked = false;
        Stop open = null;

        for (VehicleEvent e : sorted) {
            boolean parkOk = !gearSeen || parked;

            switch (e.type()) {
                case PARK_ON -> {
                    gearSeen = true;
                    parked = true;
                }
                case PARK_OFF -> {
                    gearSeen = true;
                    parked = false;
                }

                // 이벤트 자체를 상태 변화로 신뢰한다. 이전 착좌 상태는 추적하지 않는다.
                case SEAT_VACATED -> {
                    if (parkOk && open == null) {
                        open = start(e);
                    }
                }

                // 체류 시간은 종료 여부와 별개로 메모 제외 사유를 계산할 때 사용한다.
                case SEAT_OCCUPIED -> {
                    if (open != null) {
                        stops.add(open.withEnd(e.ts(), e.odometer(), StopState.RETURNED));
                        open = null;
                    }
                }

                // 기록은 열어 두고 확인 표시만 남긴다. 이후 착좌로 종료돼도 표시는 유지된다.
                case APP_WAKE, POWER_DOWN_WARNING -> {
                    if (open != null) {
                        open = open.withNeedsReview(true);
                    }
                }

                // 도어·벨트 등 다른 이벤트는 하차 상태를 바꾸지 않는다.
                default -> { }
            }
        }

        // 복귀 이벤트가 없으면 종료 시각을 채우지 않는다.
        if (open != null) {
            stops.add(open);
        }
        return stops;
    }

    private static Stop start(VehicleEvent e) {
        String zone = resolveZone(e.zone());
        String localDate = Instant.ofEpochMilli(e.ts()).atZone(ZoneId.of(zone)).toLocalDate().toString();
        return new Stop("stop-" + e.ts(), null, e.ts(), null, StopState.OPEN,
                e.odometer(), null, e.lat(), e.lng(), e.confidence(), localDate, zone, false);
    }

    /** 시간대가 없거나 잘못됐으면 현재 실행 환경의 시간대를 사용한다. */
    private static String resolveZone(String zone) {
        if (zone != null) {
            try {
                return ZoneId.of(zone).getId();
            } catch (RuntimeException ignored) {
                // 입력 시간대를 사용할 수 없다.
            }
        }
        return ZoneId.systemDefault().getId();
    }
}
