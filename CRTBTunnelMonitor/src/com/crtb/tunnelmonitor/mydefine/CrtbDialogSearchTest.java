package com.crtb.tunnelmonitor.mydefine;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.util.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.activity.TestSectionExecuteActivity;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.widget.CrtbInputDrowHint;
import com.crtb.tunnelmonitor.widget.CrtbTestRecordSearchListView;

public class CrtbDialogSearchTest extends CrtbDialog {
	
	private int			 					mHeight = 300 ;
	private CrtbTestRecordSearchListView 	mListView ;
	private int 							mType ;
	private CrtbInputDrowHint				mHintDrop ;
	private LayoutInflater					mInflater ;
	private EditText						mInput ;
	private RelativeLayout					mInputLayout ;
	private int 							mInputHeight ;
	private Context							mContext ;
	
	public CrtbDialogSearchTest(Context context,int height,int type) {
		super(context);
		
		mContext		= context ;
		mHeight			= height ;
		mType			= type ;
		mInflater		= LayoutInflater.from(context);
		mInputHeight	= context.getResources().getDimensionPixelSize(R.dimen.input_hint_height);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hint_dialog_search_test_layout);
		
		LinearLayout container = (LinearLayout)findViewById(R.id.search_container);
		ViewGroup.LayoutParams lp = container.getLayoutParams() ;
		lp.width 	= ViewGroup.LayoutParams.MATCH_PARENT ;
		lp.height 	= mHeight ;
		
		mInputLayout	= (RelativeLayout)findViewById(R.id.input_layout) ;
		TextView bnt 	= (TextView)findViewById(R.id.bnt_search_test) ;
		mInput 			= (EditText)findViewById(R.id.search_input) ;
		mListView		= (CrtbTestRecordSearchListView)findViewById(R.id.search_listview);
		
		mInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				String key = s.toString().trim() ;
				
				search(key);
			}
		});
		
		bnt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				search(mInput.getEditableText().toString().trim()) ;
			}
		}) ;
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(mHintDrop != null){
					mHintDrop.dismiss() ;
				}
				
				dismiss() ;
				
				RawSheetIndex bean = mListView.getItem(position);
				
				List<RawSheetIndex> list = new ArrayList<RawSheetIndex>();
				list.add(bean);
				
				CommonObject.putObject(TestSectionExecuteActivity.KEY_TEST_RAWSHEET_LIST, list) ;
				
				Intent intent = new Intent() ;
				intent.setClass(mContext, TestSectionExecuteActivity.class);
				mContext.startActivity(intent);
			}
		}) ;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(mHintDrop != null){
			mHintDrop.onTouchEvent(event);
		}
		
		return super.onTouchEvent(event);
	}

	private void search(String key){
		
		if(StringUtils.isEmpty(key)){
			mListView.onClear() ;
			
			if(mHintDrop != null){
				mHintDrop.dismiss() ;
			}
			
			return ;
		}
		
		if(mHintDrop == null){
			
			LinearLayout view = (LinearLayout)mInflater.inflate(R.layout.item_input_hint_layout, null);
			
			mHintDrop	= new CrtbInputDrowHint(getContext(), view, mInputLayout.getWidth(), mInputHeight);
		}
		
		// show input hint
		mHintDrop.show(mInputLayout, key);
		
		// search
		mListView.search(key, mType);
	}
}
