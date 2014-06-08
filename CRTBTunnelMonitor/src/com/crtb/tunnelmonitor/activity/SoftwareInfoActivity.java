package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SoftwareInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.software_info_layout);
        TextView version = (TextView) findViewById(R.id.version);
        version.setText(getString(R.string.version, "1.1.87.0"));
    }



}
