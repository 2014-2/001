package com.crtb.tunnelmonitor.mydefine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;

public class CrtbDialogList<T> extends CrtbDialog {

	private List<String> mList = new ArrayList<String>();
	private String 		 mTitleStr ;
	private TextView	 mTitle ;
	private ListView	 mListView ;
	private OnMenuItemClick<T> mListener ;
	private T			 mTag ;
	private RelativeLayout mTitleLayout ;

	public CrtbDialogList(Context context, String[] menus,String title) {
		super(context);

		for (String item : menus) {
			mList.add(item);
		}

		mTitleStr	= title ;
	}
	
	public void setMenuItemClick(OnMenuItemClick<T> listener){
		mListener = listener ;
	}

	public void setTitleStr(String title){
		mTitleStr	= title ;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_list_dialog_layout);
		
		mTitleLayout = (RelativeLayout)findViewById(R.id.dialog_title_layout);
		mTitle		= (TextView)findViewById(R.id.dialog_title);
		mListView	= (ListView)findViewById(R.id.dialog_list);
		
		if(mTitleStr == null){
			mTitleLayout.setVisibility(View.GONE);
		} else {
			mTitleLayout.setVisibility(View.VISIBLE);
		}
		
		mTitle.setText(mTitleStr);
		
		mListView.setAdapter(new MenuAdapter());
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				dismiss() ;
				
				if(mListener != null){
					mListener.onItemClick(mTag,position, mList.get(position));
				}
			}
		}) ;
		
		mListView.setSelector(R.drawable.item_menu_selector);
	}
	
	public void showDialog(T t){
		if(mTitle != null){
			mTitle.setText(mTitleStr);
		}
		super.show() ;
		mTag = t ;
	}
	
	class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList.size() ;
		}

		@Override
		public String getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position ;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView == null){
				convertView	= mInflater.inflate(R.layout.menu_item, null);
			}
			
			TextView tv = (TextView)convertView.findViewById(R.id.menu_name);
			tv.setText(getItem(position));
			
			return convertView ;
		}
	}
	
	public static interface OnMenuItemClick<T> {
		
		public void onItemClick(T bean,int position,String menu) ;
	}
}
