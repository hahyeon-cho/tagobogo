package com.tagobogo.core.derive;

/** 하차 기록의 한 필드에 대한 수정값과 수정 시각을 담는다. 적용 로직은 아직 없다. */
public record StopOverlay(String stopId, String field, String value, long fixedAt) {}
