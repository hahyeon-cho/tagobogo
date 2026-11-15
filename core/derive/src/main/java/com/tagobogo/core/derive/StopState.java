package com.tagobogo.core.derive;

/** 하차 구간의 상태. Derive는 OPEN과 RETURNED만 생성한다. */
public enum StopState {
    /** 시작 이벤트만 관측했다. */
    OPEN,
    /** 복귀 이벤트로 종료 시각을 채웠다. */
    RETURNED,
    /** 메모 저장 후 사용할 상태로 예약돼 있으며 현재 판정 함수는 반환하지 않는다. */
    DONE
}
