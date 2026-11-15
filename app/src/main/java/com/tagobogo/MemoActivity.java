package com.tagobogo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class MemoActivity extends Activity {

    private static final int[] LABEL_IDS = {
            R.id.memo_label_1, R.id.memo_label_2, R.id.memo_label_3, R.id.memo_label_4};
    private static final int[] FIELD_IDS = {
            R.id.memo_field_1, R.id.memo_field_2, R.id.memo_field_3, R.id.memo_field_4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        boolean sales = Role.isSales(this);
        Role.applyTopbar(this);
        ((TextView) findViewById(R.id.memo_context)).setText(Role.memoContext(sales));
        int[] labels = Role.memoLabels(sales);
        for (int i = 0; i < LABEL_IDS.length; i++) {
            ((TextView) findViewById(LABEL_IDS[i])).setText(labels[i]);
            ((EditText) findViewById(FIELD_IDS[i])).setEnabled(false);
        }

        findViewById(R.id.btn_save_memo).setEnabled(false);
        findViewById(R.id.btn_later).setOnClickListener(view -> finish());
    }
}
