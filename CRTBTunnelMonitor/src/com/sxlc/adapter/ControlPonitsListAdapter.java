package com.sxlc.adapter;

import java.util.List;

import com.sxlc.activity.MainActivity;
import com.sxlc.activity.R;
import com.sxlc.entity.TotalStationInfo;
import com.sxlc.entity.TunnelCrossSectionInfo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ControlPonitsListAdapter extends BaseAdapter {
	private List<TotalStationInfo>listinfos;
	private Context context;
	public ControlPonitsListAdapter(Context ct,List<TotalStationInfo> lis){
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
			convertView=lay.inflate(R.layout.listinfos_pic, null);
		}
		TextView name=(TextView) convertView.findViewById(R.id.t1);
		ImageView start=(ImageView) convertView.findViewById(R.id.t2);
		name.setTextColor(Color.BLACK);
		//start.setTextColor(Color.BLACK);
		name.setText(listinfos.get(position).getName());
		if (listinfos.get(position).isbCheck()) {
			start.setImageResource(R.drawable.use);
		}
		else {
			start.setImageResource(R.drawable.nouse);
		}
		return convertView;
	}
}
