package com.tagobogo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/** 업무 유형에 맞는 네 항목을 편집하고 기기에 임시 저장한다. */
public class MemoActivity extends Activity {

    private static final int[] LABEL_IDS = {
            R.id.memo_label_1, R.id.memo_label_2, R.id.memo_label_3, R.id.memo_label_4};
    private static final int[] FIELD_IDS = {
            R.id.memo_field_1, R.id.memo_field_2, R.id.memo_field_3, R.id.memo_field_4};

    private final EditText[] fields = new EditText[MemoStore.FIELD_COUNT];
    private boolean sales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        sales = Role.isSales(this);
        Role.applyTopbar(this);
        ((TextView) findViewById(R.id.memo_context)).setText(Role.memoContext(sales));
        int[] labels = Role.memoLabels(sales);
        String[] saved = MemoStore.load(this, sales);
        for (int i = 0; i < FIELD_IDS.length; i++) {
            ((TextView) findViewById(LABEL_IDS[i])).setText(labels[i]);
            fields[i] = findViewById(FIELD_IDS[i]);
            fields[i].setText(saved[i]);
        }

        findViewById(R.id.btn_save_memo).setOnClickListener(view -> saveAndClose());
        findViewById(R.id.btn_later).setOnClickListener(view -> finish());
    }

    private void saveAndClose() {
        String[] values = new String[MemoStore.FIELD_COUNT];
        for (int i = 0; i < values.length; i++) {
            values[i] = fields[i].getText().toString().trim();
        }
        MemoStore.save(this, sales, values);
        Toast.makeText(this, R.string.memo_saved, Toast.LENGTH_SHORT).show();
        finish();
    }
}
