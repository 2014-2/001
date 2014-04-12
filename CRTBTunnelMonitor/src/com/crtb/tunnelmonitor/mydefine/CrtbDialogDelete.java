package com.crtb.tunnelmonitor.mydefine;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;

public class CrtbDialogDelete extends CrtbDialog {
	
	public static final int BUTTON_ID_CANCEL	= 0 ;
	public static final int BUTTON_ID_CONFIRM	= 1 ;
	
	private int 			mIconResId;
	private String 			mMessage;
	private TextView		mIcon ;
	private TextView		mTextView ;
	private Button			mButtonCancel ;
	private Button			mButtonConfirm ;
	private IButtonOnClick	mListener ;

	public CrtbDialogDelete(Context context,int rid,String message) {
		super(context);
		
		mIconResId	= rid ;
		mMessage	= message ;
	}
	
	public void setButtonClick(IButtonOnClick l){
		mListener	= l ;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hint_dialog_delete_layout);
		
		mIcon		= (TextView)findViewById(R.id.hint_icon);
		mTextView	= (TextView)findViewById(R.id.hint_text);
		mButtonCancel	= (Button)findViewById(R.id.hint_button_cancel);
		mButtonConfirm	= (Button)findViewById(R.id.hint_button_confirm);
		
		mIcon.setBackgroundResource(mIconResId);
		mTextView.setText(mMessage);
		
		mButtonCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mListener != null){
					mListener.onClick(BUTTON_ID_CANCEL);
				}
				
				dismiss() ;
			}
		}) ;
		
		mButtonConfirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mListener != null){
					mListener.onClick(BUTTON_ID_CONFIRM);
				}
				
				dismiss() ;
			}
		}) ;
	}
	
	public static interface IButtonOnClick {
		
		public void onClick(int id) ;
	}
}
