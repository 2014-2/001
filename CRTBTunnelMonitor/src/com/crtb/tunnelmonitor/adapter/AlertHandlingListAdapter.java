package com.crtb.tunnelmonitor.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.AlertHandlingList;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public class AlertHandlingListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<AlertHandlingList> mList;

    public AlertHandlingListAdapter(Context context, List<AlertHandlingList> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList ==  null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList ==  null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = mInflater.inflate(R.layout.handling_item_layout, null);
            ViewHolder h = new ViewHolder();
            h.dateView = (TextView) v.findViewById(R.id.date);
            h.personView = (TextView) v.findViewById(R.id.person);
            h.remarkView = (TextView) v.findViewById(R.id.remark);
            h.statusView = (TextView) v.findViewById(R.id.status);
            v.setTag(h);
        }
        AlertHandlingList item = (AlertHandlingList) getItem(position);
        if (item != null) {
            ViewHolder holder = (ViewHolder) v.getTag();
            holder.dateView.setText(mContext.getString(R.string.handling_date, CrtbUtils.formatDate(item.getHandlingTime())));
            holder.personView.setText(mContext.getString(R.string.handling_person, item.getDuePerson()));
            String handling = item.getInfo();
            if (handling == null || handling.equalsIgnoreCase("null")) {
                handling = "";
            }
            holder.remarkView.setText(mContext.getString(R.string.handling_remark, handling));
            holder.statusView.setText(mContext.getString(R.string.handling_status, AlertUtils.ALERT_STATUS_MSGS[item.getAlertStatus()]));
        }
        return v;
    }

    class ViewHolder {
        TextView dateView;
        TextView personView;
        TextView remarkView;
        TextView statusView;
    }
}
