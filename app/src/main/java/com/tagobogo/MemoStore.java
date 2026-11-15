package com.tagobogo;

import android.content.Context;
import android.content.SharedPreferences;

/** 방문 기록 연결 전까지 업무 유형별로 마지막 메모 한 벌을 보관한다. */
final class MemoStore {

    static final int FIELD_COUNT = 4;

    private static final String PREFS = "tagobogo";
    private static final String LEGACY_SAVED = "memo_saved";
    private static final String LEGACY_FIELD_PREFIX = "memo_field_";
    private static final String KEY_PREFIX = "memo_";

    private MemoStore() {
    }

    static String[] load(Context context, boolean sales) {
        SharedPreferences preferences = prefs(context);
        migrateLegacyMemo(preferences);
        String[] values = new String[FIELD_COUNT];
        String prefix = fieldPrefix(sales);
        boolean hasSavedValues = false;
        for (int i = 0; i < FIELD_COUNT; i++) {
            hasSavedValues |= preferences.contains(prefix + (i + 1));
        }
        if (!hasSavedValues) {
            int[] demo = Role.demoMemoValues(sales);
            SharedPreferences.Editor editor = preferences.edit();
            for (int i = 0; i < FIELD_COUNT; i++) {
                editor.putString(prefix + (i + 1), context.getString(demo[i]));
            }
            editor.apply();
        }
        for (int i = 0; i < FIELD_COUNT; i++) {
            values[i] = preferences.getString(prefix + (i + 1), "");
        }
        return values;
    }

    static void save(Context context, boolean sales, String[] values) {
        SharedPreferences preferences = prefs(context);
        migrateLegacyMemo(preferences);
        SharedPreferences.Editor editor = preferences.edit();
        String prefix = fieldPrefix(sales);
        for (int i = 0; i < FIELD_COUNT; i++) {
            String value = i < values.length && values[i] != null ? values[i] : "";
            editor.putString(prefix + (i + 1), value);
        }
        editor.apply();
    }

    private static String fieldPrefix(boolean sales) {
        return KEY_PREFIX + (sales ? Role.SALES : Role.TECH) + "_field_";
    }

    /** 업무 유형 정보가 없던 공용 메모는 현재 선택된 유형으로 한 번만 옮긴다. */
    private static void migrateLegacyMemo(SharedPreferences preferences) {
        if (!preferences.getBoolean(LEGACY_SAVED, false)) {
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        String role = preferences.getString("role", Role.TECH);
        String targetPrefix = fieldPrefix(Role.SALES.equals(role));
        for (int i = 0; i < FIELD_COUNT; i++) {
            String legacyKey = LEGACY_FIELD_PREFIX + (i + 1);
            String targetKey = targetPrefix + (i + 1);
            if (!preferences.contains(targetKey)) {
                editor.putString(targetKey, preferences.getString(legacyKey, ""));
            }
            editor.remove(legacyKey);
        }
        editor.remove(LEGACY_SAVED);
        editor.apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
