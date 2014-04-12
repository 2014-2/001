package com.crtb.tunnelmonitor.mydefine;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.crtb.tunnelmonitor.activity.R;

public abstract class CrtbDialog extends Dialog {
	
	protected Context 			mContext;
	protected LayoutInflater 	mInflater ;

	public CrtbDialog(Context context) {
		super(context, R.style.crtb_theme_dialog);
		
		mContext	= context ;
		mInflater	= LayoutInflater.from(context);
	}

}
