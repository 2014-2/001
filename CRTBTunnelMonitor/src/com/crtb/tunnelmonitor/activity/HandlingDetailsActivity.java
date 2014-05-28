package com.crtb.tunnelmonitor.activity;

import java.util.List;

import com.crtb.tunnelmonitor.adapter.AlertHandlingListAdapter;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertHandlingInfoDao;
import com.crtb.tunnelmonitor.entity.AlertHandlingList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class HandlingDetailsActivity extends Activity {

    public static final String EXTRA_ALERT_ID = "ext_alert_id";

    private int mAlertId;
    private AlertHandlingListAdapter mAdapter;

    private TextView mTitleView;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handling_detail_layout);
        mTitleView = (TextView) findViewById(R.id.title);
        mListView = (ListView) findViewById(R.id.handling_list);

        Intent i = getIntent();
        mAlertId = i.getIntExtra(EXTRA_ALERT_ID, -1);
        if (mAlertId >= 0) {
          List<AlertHandlingList> handlings = AlertHandlingInfoDao.defaultDao().queryByAlertIdOrderByHandlingTimeDesc(mAlertId);

          if (handlings != null) {
              String hc = getString(R.string.handling_count, handlings.size());
              mTitleView.setText(hc);
              mAdapter = new AlertHandlingListAdapter(this, handlings);
              mListView.setAdapter(mAdapter);
          }
        }
    }

}
