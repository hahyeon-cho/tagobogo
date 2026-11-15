package com.tagobogo.core.derive;

/** 이름, 중심 좌표, 반경을 담는 장소 값이다. 학습이나 매칭 로직은 포함하지 않는다. */
public record Place(
        long id,
        String name,
        double lat,
        double lng,
        int radiusM,
        PlaceKind kind,
        String parkingNote
) {}
