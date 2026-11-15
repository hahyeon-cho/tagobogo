package com.tagobogo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import ai.pleos.playground.location.fused.FusedLocation;
import ai.pleos.playground.location.fused.listener.FusedLocationListener;
import ai.pleos.playground.location.fused.listener.ProximityAlertListener;
import ai.pleos.playground.location.fused.model.LocationData;
import ai.pleos.playground.location.fused.model.ProximityAlertEvent;
import ai.pleos.playground.location.fused.model.ProximityAlertRequest;
import ai.pleos.playground.location.fused.model.ProximityTransition;
import ai.pleos.playground.vehicle.Vehicle;
import ai.pleos.playground.vehicle.api.model.area.DoorArea;
import ai.pleos.playground.vehicle.api.model.area.SeatArea;
import ai.pleos.playground.vehicle.constant.area.DoorValue;
import ai.pleos.playground.vehicle.constant.area.SeatValue;
import ai.pleos.playground.vehicle.listener.DistanceDrivenListener;
import ai.pleos.playground.vehicle.listener.DoorPositionListener;
import ai.pleos.playground.vehicle.listener.PowerDownWarningIndicatorListener;
import ai.pleos.playground.vehicle.listener.SeatBeltBuckledStateListener;
import ai.pleos.playground.vehicle.listener.SeatOccupancyListener;

import java.util.Collections;
import java.util.Locale;

import kotlin.Unit;

/**
 * 에뮬레이터에서 차량·위치·음성 SDK 호출을 직접 확인하는 화면이다.
 * 결과는 logcat의 TBGV 태그로 기록한다. 이 화면은 방문 판정이나 저장 기능과
 * 연결되지 않으며, 음성 검사는 초기화까지만 수행한다.
 */
public class VerifyActivity extends Activity {

    private static final String TAG = "TBGV";

    /** 리플렉션으로 조회하는 AAOS 기어 선택 속성 ID. */
    private static final int GEAR_SELECTION = 289408000;

    private Vehicle vehicle;
    private FusedLocation fused;

    private final SeatArea driverSeat = new SeatArea(SeatValue.ROW_1_LEFT);
    private final DoorArea driverDoor = new DoorArea(DoorValue.ROW_1_LEFT);

    private static void ok(String v, String item, Object value) {
        Log.i(TAG, "[" + v + "] " + item + " = OK : " + value);
    }

    private static void fail(String v, String item, Throwable e) {
        Log.w(TAG, "[" + v + "] " + item + " = FAIL : "
                + e.getClass().getSimpleName() + " : " + e.getMessage());
    }

    private static Unit failU(String v, String item, Exception e) {
        fail(v, item, e);
        return Unit.INSTANCE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "===== VERIFY START =====");

        vehicle = new Vehicle(getApplicationContext());
        vehicle.initialize();

        fused = new FusedLocation(getApplicationContext());
        fused.initialize();

        // 초기화 직후 바인딩 완료를 확인하지 않고 4초 뒤 조회를 시작한다.
        new Handler(Looper.getMainLooper()).postDelayed(this::runChecks, 4000);
    }

    private void runChecks() {
        v1SeatOccupancy();
        v2DoorPosition();
        v3Gear();
        v4Odometer();
        v5SeatBelt();
        v6Location();
        v7Proximity();
        v8PowerDown();
        v10Speech();

        new Handler(Looper.getMainLooper()).postDelayed(
                () -> Log.i(TAG, "===== VERIFY END ====="), 10000);
    }

    private void v1SeatOccupancy() {
        try {
            vehicle.getSeat().checkSeatOccupancyCapabilities(
                    areas -> {
                        ok("V1", "SeatOccupancy areas", areas.size() + " " + areas);
                        return Unit.INSTANCE;
                    },
                    e -> failU("V1", "SeatOccupancy capabilities", e));

            vehicle.getSeat().getCurrentSeatOccupancy(driverSeat,
                    value -> {
                        ok("V1", "driver seat occupancy", value);
                        return Unit.INSTANCE;
                    },
                    e -> failU("V1", "driver seat occupancy", e));

            vehicle.getSeat().registerSeatOccupancy(driverSeat, new SeatOccupancyListener() {
                @Override
                public void onSeatOccupancyUpdated(Boolean occupied) {
                    ok("V1", "occupancy event", occupied);
                }

                @Override
                public void onFailed(Exception e) {
                    fail("V1", "occupancy listener", e);
                }
            });
        } catch (Throwable t) {
            fail("V1", "SeatOccupancy", t);
        }
    }

    private void v2DoorPosition() {
        try {
            vehicle.getDoor().checkDoorPositionCapabilities(
                    map -> {
                        ok("V2", "DoorPosition areas", map.size() + " " + map.keySet());
                        return Unit.INSTANCE;
                    },
                    e -> failU("V2", "DoorPosition capabilities", e));

            vehicle.getDoor().getCurrentDoorPosition(driverDoor,
                    pos -> {
                        ok("V2", "driver door position", pos);
                        return Unit.INSTANCE;
                    },
                    e -> failU("V2", "driver door position", e));

            vehicle.getDoor().registerDoorPosition(driverDoor, new DoorPositionListener() {
                @Override
                public void onPositionUpdated(Integer position) {
                    ok("V2", "door event", position);
                }

                @Override
                public void onFailed(Exception e) {
                    fail("V2", "door listener", e);
                }
            });
        } catch (Throwable t) {
            fail("V2", "DoorPosition", t);
        }
    }

    private void v3Gear() {
        // AAOS 속성과 Pleos 속성을 각각 조회한다. 두 결과를 하나로 합치지는 않는다.
        try {
            Class<?> carCls = Class.forName("android.car.Car");
            Object car = carCls.getMethod("createCar", android.content.Context.class)
                    .invoke(null, getApplicationContext());
            Object mgr = carCls.getMethod("getCarManager", String.class).invoke(car, "property");
            if (mgr == null) {
                Log.w(TAG, "[V3] AAOS CarPropertyManager = FAIL : manager is null");
            } else {
                Object value = mgr.getClass()
                        .getMethod("getProperty", Class.class, int.class, int.class)
                        .invoke(mgr, Integer.class, GEAR_SELECTION, 0);
                ok("V3", "AAOS GEAR_SELECTION", value);
            }
        } catch (Throwable t) {
            fail("V3", "AAOS GEAR_SELECTION", t);
        }

        try {
            vehicle.getGear().getCurrentGearSelectionState(
                    gear -> {
                        ok("V3", "Pleos Gear (deprecated)", gear);
                        return Unit.INSTANCE;
                    },
                    e -> failU("V3", "Pleos Gear (deprecated)", e));
        } catch (Throwable t) {
            fail("V3", "Pleos Gear", t);
        }
    }

    private void v4Odometer() {
        try {
            vehicle.getOdometer().getCurrentDistanceDriven(
                    km -> {
                        ok("V4", "total distance driven", km);
                        return Unit.INSTANCE;
                    },
                    e -> failU("V4", "total distance driven", e));

            vehicle.getOdometer().getTripCurrentDrivingDistance(
                    km -> {
                        ok("V4", "trip current distance", km);
                        return Unit.INSTANCE;
                    },
                    e -> failU("V4", "trip current distance", e));

            vehicle.getOdometer().registerDistanceDriven(new DistanceDrivenListener() {
                @Override
                public void onDistanceDrivenUpdated(Float km) {
                    ok("V4", "distance event", km);
                }

                @Override
                public void onFailed(Exception e) {
                    fail("V4", "distance listener", e);
                }
            });
        } catch (Throwable t) {
            fail("V4", "Odometer", t);
        }
    }

    private void v5SeatBelt() {
        try {
            vehicle.getSeat().getCurrentSeatBeltBuckledState(driverSeat,
                    buckled -> {
                        ok("V5", "driver belt buckled", buckled);
                        return Unit.INSTANCE;
                    },
                    e -> failU("V5", "driver belt buckled", e));

            vehicle.getSeat().registerSeatBeltBuckledState(driverSeat,
                    new SeatBeltBuckledStateListener() {
                        @Override
                        public void onSeatBeltBuckledStateUpdated(Boolean buckled) {
                            ok("V5", "belt event", buckled);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            fail("V5", "belt listener", e);
                        }
                    });
        } catch (Throwable t) {
            fail("V5", "SeatBelt", t);
        }
    }

    private void v6Location() {
        try {
            boolean supported = fused.isSupported();
            ok("V6", "FusedLocation.isSupported()", supported);
            if (!supported) {
                return;
            }
            fused.getLastKnownLocation(new FusedLocationListener() {
                @Override
                public void onChanged(LocationData d) {
                    ok("V6", "last known location", d.getLatitude() + "," + d.getLongitude()
                            + " conf=" + d.getConfidence() + " ts=" + d.getTimestampMillis());
                }

                @Override
                public void onError(Exception e) {
                    fail("V6", "last known location", e);
                }
            });
        } catch (Throwable t) {
            fail("V6", "FusedLocation", t);
        }
    }

    private void v7Proximity() {
        try {
            ProximityAlertRequest req = new ProximityAlertRequest(
                    37.5665, 126.9780, 80f,
                    Collections.singleton(ProximityTransition.ENTER));

            fused.addProximityAlert(req, new ProximityAlertListener() {
                @Override
                public void onRegistered(String alertId) {
                    ok("V7", "proximity registered", alertId);
                }

                @Override
                public void onTransition(ProximityAlertEvent event) {
                    ok("V7", "proximity transition",
                            event.getAlertId() + " " + event.getTransition());
                }

                @Override
                public void onError(Exception e) {
                    fail("V7", "proximity", e);
                }
            });
        } catch (Throwable t) {
            fail("V7", "ProximityAlert", t);
        }
    }

    private void v8PowerDown() {
        try {
            vehicle.getSafety().hasPowerDownWarningIndicator(
                    on -> {
                        ok("V8", "power down warning", on);
                        return Unit.INSTANCE;
                    },
                    e -> failU("V8", "power down warning", e));

            vehicle.getSafety().registerPowerDownWarningIndicator(
                    new PowerDownWarningIndicatorListener() {
                        @Override
                        public void onPowerDownWarningIndicatorUpdated(Boolean on) {
                            ok("V8", "power down event", on);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            fail("V8", "power down listener", e);
                        }
                    });
        } catch (Throwable t) {
            fail("V8", "PowerDownWarning", t);
        }
    }

    private void v10Speech() {
        try {
            ai.pleos.playground.stt.SpeechToText stt =
                    new ai.pleos.playground.stt.SpeechToText(
                            getApplicationContext(),
                            ai.pleos.playground.stt.constant.Mode.ON_DEVICE);
            stt.initialize(new ai.pleos.playground.stt.listener.OnInitializeListener() {
                @Override
                public void onSuccess() {
                    ok("V10", "STT ON_DEVICE initialize", "success");
                }

                @Override
                public void onFailure(ai.pleos.playground.stt.constant.ErrorType type) {
                    Log.w(TAG, "[V10] STT ON_DEVICE initialize = FAIL : " + type);
                }
            });
            stt.setLocale(Locale.KOREA);
        } catch (Throwable t) {
            fail("V10", "SpeechToText", t);
        }

        try {
            ai.pleos.playground.tts.TextToSpeech tts =
                    new ai.pleos.playground.tts.TextToSpeech(
                            getApplicationContext(),
                            ai.pleos.playground.tts.constant.Mode.ON_DEVICE);
            tts.initialize(new ai.pleos.playground.tts.listener.OnInitializeListener() {
                @Override
                public void onSuccess() {
                    ok("V10", "TTS ON_DEVICE initialize", "success");
                }

                @Override
                public void onFailure(ai.pleos.playground.tts.constant.ErrorType type) {
                    Log.w(TAG, "[V10] TTS ON_DEVICE initialize = FAIL : " + type);
                }
            });
        } catch (Throwable t) {
            fail("V10", "TextToSpeech", t);
        }
    }
}
