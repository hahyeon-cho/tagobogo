package com.tagobogo.core.derive;

/** 판정 호출 시 운전석 착좌 신호를 사용할 수 있는지 나타낸다. 실제 차량 조회는 하지 않는다. */
public record Signals(boolean driverSeat) {

    /** 착좌 신호를 사용한다고 가정한다. */
    public static final Signals SEAT = new Signals(true);

    /** 자동 판정을 끈다. 화면 안내는 호출자가 구현해야 한다. */
    public static final Signals NONE = new Signals(false);

    /** 자동 판정을 수행할 수 있는지 반환한다. */
    public boolean autoDeriveSupported() {
        return driverSeat;
    }
}
