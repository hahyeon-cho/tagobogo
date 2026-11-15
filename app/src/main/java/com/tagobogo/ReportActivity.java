package com.tagobogo;

import android.os.Bundle;
import android.widget.TextView;

public class ReportActivity extends NavActivity {

    private static final int[] KEY_IDS = {
            R.id.report_key_1, R.id.report_key_2, R.id.report_key_3,
            R.id.report_key_4, R.id.report_key_5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        bindNav(NAV_REPORT);

        boolean sales = Role.isSales(this);
        ((TextView) findViewById(R.id.report_title)).setText(Role.reportTitle(sales));
        int[] keys = Role.reportKeys(sales);
        for (int i = 0; i < KEY_IDS.length; i++) {
            ((TextView) findViewById(KEY_IDS[i])).setText(keys[i]);
        }

        onClick(R.id.btn_edit_memo, MemoActivity.class);
        findViewById(R.id.btn_submit).setEnabled(false);
    }
}
