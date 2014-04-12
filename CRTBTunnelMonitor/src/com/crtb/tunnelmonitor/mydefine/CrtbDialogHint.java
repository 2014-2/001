package com.crtb.tunnelmonitor.mydefine;

import com.crtb.tunnelmonitor.activity.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CrtbDialogHint extends CrtbDialog {

	private int mIconResId;
	private String mMessage;
	private TextView	mIcon ;
	private TextView	mTextView ;
	private Button		mButton ;
	private View.OnClickListener mListener;

	public CrtbDialogHint(Context context, int iconRid, String message) {
		super(context);

		mIconResId = iconRid;
		mMessage = message;
	}

	public void setButtonOnClick(View.OnClickListener l) {
		mListener = l;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hint_dialog_layout);
		
		mIcon		= (TextView)findViewById(R.id.hint_icon);
		mTextView	= (TextView)findViewById(R.id.hint_text);
		mButton		= (Button)findViewById(R.id.hint_button);
		
		mIcon.setBackgroundResource(mIconResId);
		mTextView.setText(mMessage);
		
		mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mListener != null){
					mListener.onClick(v);
				}
				
				dismiss() ;
			}
		}) ;
	}
}
