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
import android.widget.ImageView;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.RecordInfo;


public class TestRecordAdapter extends BaseAdapter {
	
	private List<RecordInfo>listinfos;
	private Context context;
	
	public TestRecordAdapter(Context ct,List<RecordInfo> lis){
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
			convertView=lay.inflate(R.layout.testlistinfos_pic, null);
		}
		TextView name=(TextView) convertView.findViewById(R.id.t1);
		TextView name1=(TextView) convertView.findViewById(R.id.t2);
		ImageView start=(ImageView) convertView.findViewById(R.id.tv_workplan_end_mileage);
		name.setTextColor(Color.BLACK);
		name1.setTextColor(Color.BLACK);
		name.setText(Integer.toString(position));
		name1.setText(listinfos.get(position).getCreateTime().toString());
		
		if(start != null){
			
			if (listinfos.get(position).isbUse()) {
				start.setBackgroundResource(R.drawable.use);
			}
			else {
				start.setBackgroundResource(R.drawable.nouse);
			}
		}
		
		return convertView;
	}
}
