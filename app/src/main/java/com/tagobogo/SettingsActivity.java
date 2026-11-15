package com.tagobogo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends NavActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bindNav(NAV_SETTINGS);

        applyRole();

        setRoleOnClick(R.id.seg_role_tech, Role.TECH);
        setRoleOnClick(R.id.seg_role_sales, Role.SALES);
    }

    private void setRoleOnClick(int viewId, final String role) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Role.set(SettingsActivity.this, role);
                    applyRole();
                }
            });
        }
    }

    private void applyRole() {
        boolean sales = Role.isSales(this);
        markSegment(R.id.seg_role_tech, !sales);
        markSegment(R.id.seg_role_sales, sales);

        TextView hint = findViewById(R.id.role_hint);
        if (hint != null) {
            hint.setText(Role.settingsHint(sales));
        }
        Role.applyTopbar(this);
    }

    private void markSegment(int viewId, boolean selected) {
        TextView view = findViewById(viewId);
        if (view == null) {
            return;
        }
        view.setBackgroundResource(selected ? R.drawable.bg_seg_on : R.drawable.bg_seg_off);
        view.setTextColor(getColor(selected ? R.color.tb_ink : R.color.tb_muted));
    }
}
