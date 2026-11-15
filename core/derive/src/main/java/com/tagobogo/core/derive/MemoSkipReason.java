package com.tagobogo.core.derive;

/** 체류 시간만으로 계산한 메모 제외 사유다. 장소와 업무 여부는 반영하지 않는다. */
public enum MemoSkipReason {
    /** 시간 기준으로 제외되지 않는다. */
    NONE,
    /** 체류 시간이 5분 미만이다. */
    SHORT_STOP,
    /** 체류 시간이 6시간을 넘는다. */
    LONG_PARKING,
    /** 종료 시각이 없어 체류 시간을 계산할 수 없다. */
    STILL_OPEN
}
