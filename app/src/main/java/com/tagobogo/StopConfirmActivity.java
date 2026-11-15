package com.tagobogo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StopConfirmActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_confirm);

        View confirm = findViewById(R.id.btn_confirm_and_memo);
        if (confirm != null) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(StopConfirmActivity.this, MemoActivity.class));
                    finish();
                }
            });
        }

        closeOn(R.id.btn_later);
        closeOn(R.id.btn_not_work);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Role.applyTopbar(this);
        boolean sales = Role.isSales(this);
        setText(R.id.confirm_candidate_kick, sales
                ? R.string.confirm_place_kick_sales : R.string.confirm_job_kick);
        setText(R.id.job_name, sales
                ? R.string.confirm_place_name_sales_sample : R.string.confirm_job_name_sample);
        setText(R.id.job_detail, sales
                ? R.string.confirm_place_detail_sales_sample : R.string.confirm_job_detail_sample);
        setText(R.id.btn_pick_job, sales
                ? R.string.action_pick_place_sales : R.string.action_pick_job);
    }

    private void setText(int viewId, int textId) {
        TextView view = findViewById(viewId);
        if (view != null) {
            view.setText(textId);
        }
    }

    private void closeOn(int viewId) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
