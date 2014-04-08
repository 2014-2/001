/**
 * 
 */
package com.crtb.tunnelmonitor.adapter;

import java.util.List;

import com.crtb.tunnelmonitor.entity.ControlPointsInfo;
import com.crtb.tunnelmonitor.entity.TotalStationInfo;
import com.crtb.tunnelmonitor.activity.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ControlPonitsListAdapter2 extends BaseAdapter {
	private List<ControlPointsInfo> listinfos;
	private Context context;

	public ControlPonitsListAdapter2(Context ct, List<ControlPointsInfo> lis) {
		context = ct;
		listinfos = lis;
	}

	@Override
	public int getCount() {
		if (listinfos != null) {
			return listinfos.size();
		}
		return 0;
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
		if (convertView == null) {
			lay = LayoutInflater.from(context);
			convertView = lay.inflate(R.layout.listinfos_pic, null);
		}
		TextView name = (TextView) convertView.findViewById(R.id.t1);
		ImageView start = (ImageView) convertView.findViewById(R.id.t2);
		name.setTextColor(Color.BLACK);
		// start.setTextColor(Color.BLACK);
		name.setText(listinfos.get(position).getName());
		if (listinfos.get(position).isbCheck()) {
			start.setImageResource(R.drawable.yes);
		} else {
			start.setImageResource(R.drawable.no);
		}
		return convertView;
	}
}
