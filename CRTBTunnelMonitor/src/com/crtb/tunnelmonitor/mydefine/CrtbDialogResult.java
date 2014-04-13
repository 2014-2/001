package com.crtb.tunnelmonitor.mydefine;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;

public class CrtbDialogResult extends CrtbDialog {

	private int 		mIconResId;
	private String 		mMessage;
	private TextView	mIcon ;
	private TextView	mTextView ;
	
	public CrtbDialogResult(Context context, int iconRid, String message) {
		super(context);

		mIconResId = iconRid;
		mMessage = message;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hint_dialog_result_layout);
		
		mIcon		= (TextView)findViewById(R.id.hint_icon);
		mTextView	= (TextView)findViewById(R.id.hint_text);
		
		mIcon.setBackgroundResource(mIconResId);
		mTextView.setText(mMessage);
	}
}
