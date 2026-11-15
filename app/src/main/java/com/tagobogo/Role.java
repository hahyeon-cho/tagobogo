package com.tagobogo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

final class Role {

    static final String TECH = "tech";
    static final String SALES = "sales";

    private static final String PREFS = "tagobogo";
    private static final String KEY = "role";

    private Role() {
    }

    static String current(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(KEY, TECH);
    }

    static void set(Context context, String role) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY, role)
                .apply();
    }

    static boolean isSales(Context context) {
        return SALES.equals(current(context));
    }

    static void applyTopbar(Activity activity) {
        TextView role = activity.findViewById(R.id.topbar_role);
        if (role != null) {
            role.setText(isSales(activity) ? R.string.role_sales : R.string.role_tech);
        }
    }

    static int[] memoLabels(boolean sales) {
        if (sales) {
            return new int[]{
                    R.string.memo_label_sales_person,
                    R.string.memo_label_sales_topic,
                    R.string.memo_label_sales_reaction,
                    R.string.memo_label_sales_next};
        }
        return new int[]{
                R.string.memo_label_symptom,
                R.string.memo_label_action,
                R.string.memo_label_part,
                R.string.memo_label_result};
    }

    static int[] reportKeys(boolean sales) {
        if (sales) {
            return new int[]{
                    R.string.report_key_sales_place,
                    R.string.memo_label_sales_person,
                    R.string.memo_label_sales_topic,
                    R.string.memo_label_sales_reaction,
                    R.string.memo_label_sales_next};
        }
        return new int[]{
                R.string.report_key_customer,
                R.string.report_key_symptom,
                R.string.report_key_action,
                R.string.report_key_part,
                R.string.report_key_result};
    }

    static int reportTitle(boolean sales) {
        return sales ? R.string.report_title_sales : R.string.report_title;
    }

    static int settingsHint(boolean sales) {
        return sales ? R.string.settings_role_hint_sales : R.string.settings_role_hint;
    }

    static int memoContext(boolean sales) {
        return sales ? R.string.memo_context_sales : R.string.memo_context_tech;
    }

    static int[] demoMemoValues(boolean sales) {
        return sales ? new int[]{
                R.string.memo_demo_sales_1, R.string.memo_demo_sales_2,
                R.string.memo_demo_sales_3, R.string.memo_demo_sales_4}
                : new int[]{
                R.string.memo_demo_tech_1, R.string.memo_demo_tech_2,
                R.string.memo_demo_tech_3, R.string.memo_demo_tech_4};
    }

    static int reportCardTitle(boolean sales) {
        return sales ? R.string.report_card_title_sales : R.string.report_card_title;
    }

    static int heroSubtitle(boolean sales) {
        return sales ? R.string.hero_sub_sales_sample : R.string.hero_sub_tech_sample;
    }
}
