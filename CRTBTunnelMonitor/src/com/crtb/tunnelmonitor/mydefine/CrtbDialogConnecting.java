package com.crtb.tunnelmonitor.mydefine;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;

public class CrtbDialogConnecting extends CrtbDialog {
	
	private TextView 	mMessageView ;
	private String 		mContent ;

	public CrtbDialogConnecting(Context context) {
		super(context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect_dialog_layout);
		
		mMessageView	= (TextView)findViewById(R.id.hint_text);
		mMessageView.setText(mContent);
		
		setCancelable(false);
		setCanceledOnTouchOutside(false);
	}
	
	public void showDialog(String message){
		
		mContent	= message ;
		
		if(mMessageView != null){
			mMessageView.setText(message);
		}
		
		show() ;
	}
}
