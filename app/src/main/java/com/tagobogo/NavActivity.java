package com.tagobogo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

abstract class NavActivity extends Activity {

    protected static final int NAV_TODAY = 0;
    protected static final int NAV_STOPS = 1;
    protected static final int NAV_REPORT = 2;
    protected static final int NAV_SETTINGS = 3;

    @Override
    protected void onResume() {
        super.onResume();
        Role.applyTopbar(this);
    }

    protected void bindNav(int current) {
        bindRail(R.id.rail_today, NAV_TODAY, current, MainActivity.class);
        bindRail(R.id.rail_stops, NAV_STOPS, current, StopsActivity.class);
        bindRail(R.id.rail_report, NAV_REPORT, current, ReportActivity.class);
        bindRail(R.id.rail_settings, NAV_SETTINGS, current, SettingsActivity.class);

        bindTab(R.id.tab_today, NAV_TODAY, current, MainActivity.class);
        bindTab(R.id.tab_stops, NAV_STOPS, current, StopsActivity.class);
        bindTab(R.id.tab_report, NAV_REPORT, current, ReportActivity.class);
        bindTab(R.id.tab_settings, NAV_SETTINGS, current, SettingsActivity.class);
    }

    private void bindRail(int viewId, int item, int current, Class<?> target) {
        TextView view = findViewById(viewId);
        if (view == null) {
            return;
        }
        if (item == current) {
            view.setBackgroundResource(R.drawable.bg_rail_item_on);
            view.setTextColor(getColor(R.color.tb_rail_on_ink));
        } else {
            view.setAlpha(0.72f);
            view.setOnClickListener(open(target));
        }
    }

    private void bindTab(int viewId, int item, int current, Class<?> target) {
        TextView view = findViewById(viewId);
        if (view == null) {
            return;
        }
        if (item == current) {
            view.setTextColor(getColor(R.color.tb_accent));
            view.setTypeface(null, Typeface.BOLD);
        } else {
            view.setOnClickListener(open(target));
        }
    }

    private View.OnClickListener open(final Class<?> target) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavActivity.this, target));
            }
        };
    }

    protected void onClick(int viewId, final Class<?> target) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(open(target));
        }
    }
}
