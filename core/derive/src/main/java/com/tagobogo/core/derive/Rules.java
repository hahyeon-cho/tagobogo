package com.tagobogo.core.derive;

/** 체류 시간에 따른 메모 제외 사유와 관련 상수를 제공한다. */
public final class Rules {
    private Rules() {}

    /** 이 값보다 짧은 체류는 짧은 정차로 분류한다. */
    public static final long MIN_DWELL_MS = 5L * 60 * 1000;

    /** 이 값보다 긴 체류는 장기 주차로 분류한다. */
    public static final long MAX_DWELL_MS = 6L * 60 * 60 * 1000;

    /** 예정된 신호 확인 간격. 현재 이 모듈에서는 사용하지 않는다. */
    public static final long HEARTBEAT_INTERVAL_MS = 30L * 1000;

    public static final int PLACE_RADIUS_M = 80;
    public static final int APARTMENT_RADIUS_M = 300;

    /** 위치나 업무 여부를 고려하지 않고 체류 시간만 분류한다. */
    public static MemoSkipReason memoSkipReason(long dwellMs) {
        if (dwellMs < MIN_DWELL_MS) {
            return MemoSkipReason.SHORT_STOP;
        }
        if (dwellMs > MAX_DWELL_MS) {
            return MemoSkipReason.LONG_PARKING;
        }
        return MemoSkipReason.NONE;
    }
}
