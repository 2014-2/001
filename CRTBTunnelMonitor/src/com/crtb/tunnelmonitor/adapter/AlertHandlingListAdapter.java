package com.crtb.tunnelmonitor.adapter;

import java.util.List;

import com.crtb.tunnelmonitor.entity.AlertHandlingList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AlertHandlingListAdapter extends BaseAdapter {

    private List<AlertHandlingList> mList;

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
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }

    
}
