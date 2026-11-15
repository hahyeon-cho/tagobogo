package com.tagobogo.core.derive;

/**
 * 판정 함수가 계산한 하차 구간이다. 종료 이벤트가 없으면 endTs는 null이다.
 * 시작일과 시간대는 시작 이벤트에서 계산하며, 재실행이나 전원 경고가 열린 구간에
 * 들어오면 needsReview가 true가 된다. 이후 종료돼도 이 표시는 유지된다.
 */
public record Stop(
        String id,
        Long placeId,
        long startTs,
        Long endTs,
        StopState state,
        Integer odoStart,
        Integer odoEnd,
        Double startLat,
        Double startLng,
        Float confidence,
        /** 시작 이벤트의 시간대로 계산한 ISO 날짜. */
        String startLocalDate,
        /** 시작일 계산에 사용한 시간대 ID. */
        String startZone,
        /** 열린 동안 재실행 또는 전원 경고를 관측했는지 여부. */
        boolean needsReview
) {

    public Stop withEnd(long endTs, Integer odoEnd, StopState state) {
        return new Stop(id, placeId, startTs, endTs, state, odoStart, odoEnd,
                startLat, startLng, confidence, startLocalDate, startZone, needsReview);
    }

    public Stop withNeedsReview(boolean needsReview) {
        return new Stop(id, placeId, startTs, endTs, state, odoStart, odoEnd,
                startLat, startLng, confidence, startLocalDate, startZone, needsReview);
    }

    /** 종료 시각이 있으면 시작 시각과의 차이를 밀리초로 반환한다. */
    public Long dwellMs() {
        return endTs == null ? null : endTs - startTs;
    }

    /** 체류 시간만으로 메모 제외 사유를 계산한다. */
    public MemoSkipReason memoSkipReason() {
        Long dwell = dwellMs();
        return dwell == null ? MemoSkipReason.STILL_OPEN : Rules.memoSkipReason(dwell);
    }
}
