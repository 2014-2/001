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
import com.crtb.tunnelmonitor.entity.list_infos;

public class listAdapter extends BaseAdapter
{
	private List<list_infos> listinfos;
	private Context context;
	public listAdapter(Context ct,List<list_infos> lis){
		context=ct;
		listinfos=lis;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listinfos.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listinfos.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater lay;
		if(convertView==null){
			lay=LayoutInflater.from(context);
			convertView=lay.inflate(R.layout.item_workplan_layout, null);
		}
		TextView name=(TextView) convertView.findViewById(R.id.t1);
		TextView start=(TextView) convertView.findViewById(R.id.t2);
		TextView end=(TextView) convertView.findViewById(R.id.tv_workplan_end_mileage);
		name.setTextColor(Color.BLACK);
		start.setTextColor(Color.BLACK);
		end.setTextColor(Color.BLACK);
		name.setText(listinfos.get(position).getWorkpagename());
		start.setText(listinfos.get(position).getStart());
		end.setText(listinfos.get(position).getEnd());
		return convertView;
	}
}
