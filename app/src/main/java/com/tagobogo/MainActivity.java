package com.tagobogo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends NavActivity {

    static final int STATE_BEFORE = 0;
    static final int STATE_DRIVING = 1;
    static final int STATE_READY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindNav(NAV_TODAY);

        onClick(R.id.btn_confirm_stop, StopConfirmActivity.class);
        onClick(R.id.btn_check_gap, SettingsActivity.class);
        onClick(R.id.btn_review_report, ReportActivity.class);

        showState(STATE_READY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean sales = Role.isSales(this);
        ((TextView) findViewById(R.id.report_card_title)).setText(Role.reportCardTitle(sales));
        ((TextView) findViewById(R.id.hero_sub)).setText(Role.heroSubtitle(sales));
    }

    void showState(int state) {
        setVisible(R.id.today_state_before, state == STATE_BEFORE);
        setVisible(R.id.today_state_driving, state == STATE_DRIVING);
        setVisible(R.id.today_state_ready, state == STATE_READY);

    }

    private void setVisible(int viewId, boolean visible) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}
