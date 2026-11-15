package com.tagobogo;

import android.content.Intent;
import android.os.IBinder;
import androidx.lifecycle.LifecycleService;

/**
 * 차량 기록 서비스의 자리만 마련한 클래스다.
 * 현재는 차량 신호 구독, foreground 실행, 이벤트 저장을 하지 않는다.
 */
public class TripService extends LifecycleService {

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }
}
