/**
 * 
 */
package com.crtb.tunnelmonitor.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;


public class TunnelCrossSectionInfoAdapter extends BaseAdapter {
	private List<TunnelCrossSectionInfo>listinfos;
	private Context context;
	public TunnelCrossSectionInfoAdapter(Context ct,List<TunnelCrossSectionInfo> lis){
		context=ct;
		listinfos=lis;
	}
	@Override
	public int getCount() {
		return listinfos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listinfos.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater lay;
		if(convertView==null){
			lay=LayoutInflater.from(context);
			convertView=lay.inflate(R.layout.listinfos_work, null);
		}
		TextView name=(TextView) convertView.findViewById(R.id.t1);
		TextView start=(TextView) convertView.findViewById(R.id.t2);
		name.setTextColor(Color.BLACK);
		start.setTextColor(Color.BLACK);
		name.setText(listinfos.get(position).getChainageName());
		//start.setText(listinfos.get(position).getsExcavateMethod());
		return convertView;
	}
}
