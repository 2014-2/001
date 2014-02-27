package com.byd.player.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.byd.player.bluetooth.BaseActivity;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
/**
 * 基本的ListAdapter,实现一些方法
 */
public abstract class BaseContentAdapter<T> extends BaseAdapter implements 
	OnClickListener, OnCheckedChangeListener{

	Activity activity;
	List<? extends T> list;
	HashMap<Integer, View> views = new HashMap<Integer, View>();
	private OnMyAdapterItemClick onMyAdapterItemClick = null;
	private OnMyAdapterCheckedChange onMyAdapterCheckedChange = null;
	public BaseContentAdapter(BaseActivity activity, List<? extends T> list) {
		super();
		this.activity = activity;
		this.list = list;
		if (this.list==null) {
			this.list= new ArrayList<T>();
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void changeData(List<? extends T> newList){
		this.list=newList;
		if (this.list==null) {
			this.list=new ArrayList<T>();
		}
		notifyDataSetChanged();
	}
	
	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent) ;

	public View getView(int position) {
		// TODO Auto-generated method stub
		if (views.containsKey(position)) {
		     return views.get(position);	
		}
		return null;
	}
	public void setOnMyAdapterItemClick(OnMyAdapterItemClick onPhoneBookItemClick){
		this.onMyAdapterItemClick = onPhoneBookItemClick;
	}
	public void setOnMyAdapterCheckedChange(OnMyAdapterCheckedChange onMyAdapterCheckedChange){
		this.onMyAdapterCheckedChange = onMyAdapterCheckedChange;
	}
	public interface OnMyAdapterItemClick{
		public void onMyAdapterItemClick(View v);
	}
	public interface OnMyAdapterCheckedChange{
		public void onCheckChanged(CompoundButton cb, boolean isChecked);
	}
	@Override
	public void onClick(View v) {
		if(onMyAdapterItemClick != null){
			onMyAdapterItemClick.onMyAdapterItemClick(v);
		}
	}
	@Override
	public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
		if(onMyAdapterCheckedChange != null){
			onMyAdapterCheckedChange.onCheckChanged(cb, isChecked);
		}
	}
	
}
