package com.crtb.tunnelmonitor.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.crtb.tunnelmonitor.entity.AlertInfo;

public class ReviewWarningActivity extends Activity {

    private List<AlertInfo> mData;

    private ListView mLvAlertData;

    private AlertDataAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_warning);
        TextView title = (TextView)findViewById(R.id.tv_topbar_title);
        title.setText(R.string.review_warning_title);
        mData = (List<AlertInfo>)getIntent().getSerializableExtra("alert_info");
        init();
    }

    private void init() {
        mLvAlertData = (ListView)findViewById(R.id.alert_list);
        mAdapter = new AlertDataAdapter();
        mLvAlertData.setAdapter(mAdapter);
    }

    private class AlertDataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AlertInfo alertInfo = mData.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.item_alert_info, null);
                AlertDataHolder holder = new AlertDataHolder();
                holder.mTvXinghao = (TextView)convertView.findViewById(R.id.alert_xinghao);
                holder.mTvDate = (TextView)convertView.findViewById(R.id.alert_date);
                holder.mTvPntType = (TextView)convertView.findViewById(R.id.alert_pnt_type);
                holder.mTvTypeMsg = (TextView)convertView.findViewById(R.id.alert_type_msg);
                holder.mTvState = (TextView)convertView.findViewById(R.id.alert_state);
                holder.mTvHandleMethod = (TextView)convertView
                        .findViewById(R.id.alert_handle_method);
                holder.mEtHandling = (EditText)convertView.findViewById(R.id.alert_handling);
                convertView.setTag(holder);
            }
            bindView(alertInfo, convertView);
            return convertView;
        }

        private void bindView(AlertInfo alertInfo, View convertView) {
            AlertDataHolder holder = (AlertDataHolder)convertView.getTag();
            holder.mTvXinghao.setText(alertInfo.getXinghao());
            holder.mTvDate.setText(alertInfo.getDate());
            holder.mTvPntType.setText(getString(R.string.alert_point_type, alertInfo.getPntType()));
            holder.mTvTypeMsg.setText(alertInfo.getUTypeMsg());
            holder.mTvState.setText(getString(R.string.alert_state, alertInfo.getAlertStatusMsg()));
            holder.mTvHandleMethod.setText(getString(R.string.alert_handle_method, alertInfo.getChuliFangshi()));
            holder.mEtHandling.setText(alertInfo.getHandling());
        }
    }

    private class AlertDataHolder {
        TextView mTvXinghao;
        TextView mTvDate;
        TextView mTvPntType;
        TextView mTvTypeMsg;
        TextView mTvState;
        TextView mTvHandleMethod;
        EditText mEtHandling;
    }
}
