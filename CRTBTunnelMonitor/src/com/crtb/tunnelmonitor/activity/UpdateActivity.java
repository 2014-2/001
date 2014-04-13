package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class UpdateActivity extends Activity {
    private TextView mtvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        mtvHeader = (TextView)findViewById(R.id.tv_topbar_title);
        mtvHeader.setText(R.string.update_title);
    }

}
