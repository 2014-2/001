package com.crtb.tunnelmonitor.adapter;

import java.util.List;


import java.util.Map;

import com.crtb.tunnelmonitor.entity.yujingInfors;
import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.activity.R.id;
import com.crtb.tunnelmonitor.activity.R.layout;


import android.R.color;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Myadapter extends BaseAdapter {
	private List<yujingInfors> list;
	private	LayoutInflater inflater;
	private	Context context;
	private	int gaoliang=-1;
	private	static int select=-1;
	public int getselect() {
		return select;
	}
	public void setselect(int select) {
		this.select = select;
	}
	public Myadapter(Context cont,List<yujingInfors> list) {
		// TODO Auto-generated constructor stub
		context=cont;
		this.list=list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public List<yujingInfors> getList() {
		return list;
	}
	public void setList(List<yujingInfors> list) {
		this.list = list;
	}
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	public void setbackgroud(int a){
		gaoliang=a;
		notifyDataSetChanged();
	}
	@Override
	public View getView(int pos, View view, ViewGroup arg2) {
			inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view=inflater.inflate(R.layout.list,null);
			if(list.get(pos).isState1()){
			TextView data=(TextView) view.findViewById(R.id.date);
			data.setText(list.get(pos).getDate());
			TextView xinghao=(TextView) view.findViewById(R.id.xinghao);
			xinghao.setText(list.get(pos).getXinghao());
			TextView dianhao=(TextView) view.findViewById(R.id.dianhao);
			dianhao.setText("点号："+list.get(pos).getDianhao());
			TextView chushi=(TextView) view.findViewById(R.id.chulifangshi);
			chushi.setText("处理方式："+list.get(pos).getChuliFangshi());
			TextView state=(TextView) view.findViewById(R.id.state);
			state.setText("状态："+list.get(pos).getState());
			TextView message=(TextView) view.findViewById(R.id.message);
			message.setText(list.get(pos).getMessage());
			EditText edtstate=(EditText) view.findViewById(R.id.edtstate);
			edtstate.setText(list.get(pos).getEdtState());
			
			return view;
			}else{
				view.setVisibility(view.GONE);
			}
			
		return new TextView(context);
	}
	
}
