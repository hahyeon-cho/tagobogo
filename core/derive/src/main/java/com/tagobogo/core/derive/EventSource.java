package com.tagobogo.core.derive;

/** 이벤트를 만든 신호 계층을 표시한다. 판정은 주로 EventType을 사용한다. */
public enum EventSource {
    /** 기어 선택값. */
    GEAR,
    /** 좌석 착좌 또는 안전벨트 값. */
    SEAT,
    /** 도어 위치값. */
    DOOR,
    ODOMETER,
    /** 안전 관련 차량 신호. */
    SAFETY,
    BRAKE,
    LOCATION,
    APP
}
