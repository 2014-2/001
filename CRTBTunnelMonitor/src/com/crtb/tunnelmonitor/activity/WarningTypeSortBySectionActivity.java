package com.crtb.tunnelmonitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.crtb.tunnelmonitor.BaseActivity;
import com.crtb.tunnelmonitor.common.Constant;

public class WarningTypeSortBySectionActivity  extends BaseActivity implements OnClickListener {	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waring_type);
		setTopbarTitle("预警管理");
		findViewById(R.id.tunnel_crosssection).setOnClickListener(this);
		findViewById(R.id.sub_crosssection).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(WarningTypeSortBySectionActivity.this,WarningActivity.class);
		switch (v.getId()) {
		case R.id.sub_crosssection: {
			intent.putExtra(Constant.WARNING_TYPE, "SUB");
			startActivity(intent);
			break;
		}
		case R.id.tunnel_crosssection: {
			intent.putExtra(Constant.WARNING_TYPE, "TUNNEL");
			startActivity(intent);
			break;
		}
		}
	}
}
