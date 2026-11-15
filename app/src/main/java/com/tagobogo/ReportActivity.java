package com.tagobogo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/** 저장된 메모를 업무 유형별 보고서 항목에 표시한다. 방문 데이터는 아직 연결되지 않았다. */
public class ReportActivity extends NavActivity {

    private static final int[] KEY_IDS = {
            R.id.report_key_1, R.id.report_key_2, R.id.report_key_3,
            R.id.report_key_4, R.id.report_key_5};
    private static final int[] VALUE_IDS = {
            R.id.report_value_1, R.id.report_value_2, R.id.report_value_3,
            R.id.report_value_4, R.id.report_value_5};
    private static final int[] SOURCE_IDS = {
            R.id.report_source_1, R.id.report_source_2, R.id.report_source_3,
            R.id.report_source_4, R.id.report_source_5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        bindNav(NAV_REPORT);
        onClick(R.id.btn_edit_memo, MemoActivity.class);
        findViewById(R.id.btn_submit).setOnClickListener(view -> submitReport());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showReport();
    }

    private void showReport() {
        boolean sales = Role.isSales(this);
        ((TextView) findViewById(R.id.report_title)).setText(Role.reportTitle(sales));
        int[] keys = Role.reportKeys(sales);
        String[] memo = MemoStore.load(this, sales);
        for (int i = 0; i < KEY_IDS.length; i++) {
            ((TextView) findViewById(KEY_IDS[i])).setText(keys[i]);
            String value = i == 0
                    ? getString(sales ? R.string.report_customer_sales_sample
                            : R.string.report_customer_tech_sample)
                    : memo[i - 1];
            ((TextView) findViewById(VALUE_IDS[i])).setText(
                    value.isEmpty() ? getString(R.string.report_blank) : value);
            ((TextView) findViewById(SOURCE_IDS[i])).setText(
                    i == 0 ? (sales ? R.string.report_source_confirmed : R.string.report_source_job)
                            : value.isEmpty() ? R.string.report_source_missing : R.string.report_source_memo);
        }
        ((TextView) findViewById(R.id.report_value_visit)).setText(R.string.report_visit_sample);
        ((TextView) findViewById(R.id.report_source_visit)).setText(R.string.report_source_auto);
    }

    private void submitReport() {
        StringBuilder body = new StringBuilder();
        body.append(((TextView) findViewById(R.id.report_title)).getText()).append('\n');
        for (int i = 0; i < KEY_IDS.length; i++) {
            body.append(((TextView) findViewById(KEY_IDS[i])).getText())
                    .append(": ")
                    .append(((TextView) findViewById(VALUE_IDS[i])).getText())
                    .append('\n');
            if (i == 1) {
                body.append(getString(R.string.report_key_visit)).append(": ")
                        .append(((TextView) findViewById(R.id.report_value_visit)).getText())
                        .append('\n');
            }
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(
                getString(Role.reportTitle(Role.isSales(this))), body.toString()));
        Toast.makeText(this, R.string.report_submit_pending, Toast.LENGTH_SHORT).show();
    }
}
