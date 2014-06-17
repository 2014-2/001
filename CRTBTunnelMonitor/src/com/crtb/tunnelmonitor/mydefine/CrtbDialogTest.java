package com.crtb.tunnelmonitor.mydefine;

import java.util.Date;

import org.zw.android.framework.util.DateUtils;
import org.zw.android.framework.util.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
import com.crtb.tunnelmonitor.utils.Time;

public class CrtbDialogTest extends CrtbDialog {
	
	private EditText xTxt ;
	private EditText yTxt ;
	private EditText hTxt ;
	private EditText time ;
	private Activity owner ;
	private Callback callback ;
	
	public CrtbDialogTest(Context context,Callback callback) {
		super(context);
		
		owner	= (Activity) context ;
		this.callback	= callback ;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_debug_dialog);
		
		xTxt	= (EditText)findViewById(R.id.test_point_x);
		yTxt	= (EditText)findViewById(R.id.test_point_y);
		hTxt	= (EditText)findViewById(R.id.test_point_z);
		time	= (EditText)findViewById(R.id.test_point_time);
		
		time.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String str = time.getEditableText().toString().trim() ;
				
				Date date =	DateUtils.getCurrtentTimes() ;
				
				if(!StringUtils.isEmpty(str)){
					 date = DateUtils.toDate(str, DateUtils.PART_TIME_FORMAT); 
				}
				
				CrtbDateDialogUtils.setAnyDateDialog(owner, time, date);
			}
		}) ;
		
		Button bnt = (Button)findViewById(R.id.bnt_test);
		
		time.setText(Time.getDateEN());
		
		bnt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String x	= xTxt.getEditableText().toString().trim() ;
				String y	= yTxt.getEditableText().toString().trim() ;
				String z	= hTxt.getEditableText().toString().trim() ;
				String t	= time.getEditableText().toString().trim() ;
				
				if(StringUtils.isEmpty(x) 
						|| StringUtils.isEmpty(y)
						|| StringUtils.isEmpty(z)){
					Toast.makeText(mContext, "输入参数错误", Toast.LENGTH_SHORT).show() ;
					return ;
				}
				
				if(StringUtils.isEmpty(t)){
					Toast.makeText(mContext, "时间不能为空", Toast.LENGTH_SHORT).show() ;
					return ;
				}
				
				if(callback != null){
					callback.callback(CrtbUtils.formatDouble(x), CrtbUtils.formatDouble(y), CrtbUtils.formatDouble(z), t) ;
				}
				
				dismiss() ;
			}
		}) ;
	}

	public static interface Callback {
		
		public void callback(double x, double y,double z, String time) ;
	}
}
