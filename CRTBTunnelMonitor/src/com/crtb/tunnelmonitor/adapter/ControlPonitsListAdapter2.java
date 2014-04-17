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
import com.crtb.tunnelmonitor.entity.ControlPointsInfo;

public class ControlPonitsListAdapter2 extends BaseAdapter {
	private List<ControlPointsInfo> mControlPoints;
	private Context context;

	public ControlPonitsListAdapter2(Context ct, List<ControlPointsInfo> lis) {
		context = ct;
		mControlPoints = lis;
	}

	@Override
	public int getCount() {
		if (mControlPoints != null) {
			return mControlPoints.size();
		}
		return 0;
	}
	
	public void remove(ControlPointsInfo bean){
		
		if(mControlPoints != null){
			
			for(ControlPointsInfo info : mControlPoints){
				
				if(info.getId() == bean.getId()){
					mControlPoints.remove(info);
					break ;
				}
			}
			
			notifyDataSetChanged() ;
		}
	}

	@Override
	public Object getItem(int arg0) {
		return mControlPoints.get(arg0);
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
		name.setText(mControlPoints.get(position).getName());
		if (mControlPoints.get(position).isChecked()) {
			start.setImageResource(R.drawable.yes);
		} else {
			start.setImageResource(R.drawable.no);
		}
		return convertView;
	}
}
