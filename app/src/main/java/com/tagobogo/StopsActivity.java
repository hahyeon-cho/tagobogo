package com.tagobogo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StopsActivity extends NavActivity {

    /** {시각, 이름, 배지, 경고 배지 여부} */
    private static final int[][] SAMPLE_ROWS = {
            {R.string.stops_row1_time_sample, R.string.stops_row1_name_sample, R.string.tag_unconfirmed, 1},
            {R.string.stops_row2_time_sample, R.string.stops_row2_name_sample, R.string.tag_needs_review, 1},
            {R.string.stops_row3_time_sample, R.string.stops_row3_name_sample, R.string.tag_memo_done, 0},
            {R.string.stops_row4_time_sample, R.string.stops_row4_name_sample, R.string.tag_memo_skipped, 0},
            {R.string.stops_row5_time_sample, R.string.stops_row5_name_sample, R.string.tag_memo_done, 0}};

    private static final int[][] SALES_SAMPLE_ROWS = {
            {R.string.stops_row1_time_sample, R.string.stops_row1_name_sample, R.string.tag_unconfirmed, 1},
            {R.string.stops_row2_time_sample, R.string.stops_row2_name_sample, R.string.tag_needs_review, 1},
            {R.string.stops_row3_time_sample, R.string.stops_row3_name_sales_sample, R.string.tag_memo_done, 0},
            {R.string.stops_row4_time_sample, R.string.stops_row4_name_sample, R.string.tag_memo_skipped, 0},
            {R.string.stops_row5_time_sample, R.string.stops_row5_name_sales_sample, R.string.tag_memo_done, 0}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);
        bindNav(NAV_STOPS);

        onClick(R.id.btn_confirm_place, StopConfirmActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillSampleRows();
    }

    private void fillSampleRows() {
        LinearLayout list = findViewById(R.id.stops_list);
        if (list == null) {
            return;
        }
        list.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        int gap = getResources().getDimensionPixelSize(R.dimen.tb_gap_sm);

        int[][] rows = Role.isSales(this) ? SALES_SAMPLE_ROWS : SAMPLE_ROWS;
        for (int i = 0; i < rows.length; i++) {
            int[] row = rows[i];
            View item = inflater.inflate(R.layout.item_stop, list, false);

            ((TextView) item.findViewById(R.id.stop_time)).setText(row[0]);
            ((TextView) item.findViewById(R.id.stop_name)).setText(row[1]);

            TextView tag = item.findViewById(R.id.stop_tag);
            tag.setText(row[2]);
            if (row[3] == 1) {
                tag.setBackgroundResource(R.drawable.bg_chip_warn);
                tag.setTextColor(getColor(R.color.tb_warn));
            }

            if (i == 0) {
                item.setBackgroundResource(R.drawable.bg_row_selected);
            }

            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) item.getLayoutParams();
            if (i > 0) {
                params.topMargin = gap;
            }
            list.addView(item, params);
        }
    }
}
