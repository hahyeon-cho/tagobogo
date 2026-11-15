package com.tagobogo.core.derive;

/** 판정 함수가 받을 수 있는 차량·앱 이벤트 종류다. */
public enum EventType {
    /** 기어가 P로 바뀌었다. */
    PARK_ON,
    PARK_OFF,

    /** 운전석 비착좌 통지. */
    SEAT_VACATED,
    /** 운전석 착좌 통지. */
    SEAT_OCCUPIED,

    /* 다음 도어·벨트 값은 현재 Derive에서 상태 전이를 일으키지 않는다. */
    DOOR_OPEN,
    DOOR_CLOSE,
    BELT_ON,
    BELT_OFF,

    PROXIMITY_ENTER,
    PROXIMITY_EXIT,
    HEARTBEAT,
    APP_WAKE,

    /** 전원 하강 경고. 열린 기록에 확인 표시를 남긴다. */
    POWER_DOWN_WARNING
}
