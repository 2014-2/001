package com.byd.player.bluetooth;

import com.byd.player.R;
import com.byd.player.receiver.BtStatusChangedBroadcastReceiver;
import com.byd.player.services.BtService;
import com.byd.player.services.BtService.LocalBinder;
import com.byd.player.receiver.BtStatusChangedBroadcastReceiver.onBtStatusListener;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * 基本的Activity,实现一些方法
 */
public abstract class BTBaseActivity extends Activity implements OnClickListener, onBtStatusListener{
	
	public static final int ALERT_TYPE_RETURN_BACK = 0;
	public static final int ALERT_TYPE_SAFE_CODE_ERROR = 1;
	protected static final int ALERT_TYPE_CUSTOM_BEGIN = 2; 
	
	static public BtService btService;
	boolean isBtServiceBinded=false;
	protected ProgressDialog progressDialog;
	
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        LocalBinder binder = (LocalBinder) service;
	        btService = binder.getService();
			if (createComplete && btService != null){
				onCreateComplete();
			}
	    }
	    public void onServiceDisconnected(ComponentName arg0) { 
	    	btService = null;
	    }
	};
	
	protected void initBtService(){
		if (btService == null){
			Intent in = new Intent();
			in.setClass(this, BtService.class);
			isBtServiceBinded=bindService(in, mConnection, Context.BIND_AUTO_CREATE);
			startService(in);
		}
	}
	
	protected void unbindService(){
		if (mConnection != null && isBtServiceBinded == true){
			Intent in = new Intent();
			in.setClass(this, BtService.class);
			stopService(in);
			unbindService(mConnection);
			isBtServiceBinded = false;
			btService = null;
		}
	}
	
	protected ImageButton btnReback, btnLeft, btnRight;
	protected TextView tvTitle;
	protected Button btnLeftText, btnRightText;
	
	protected boolean listenBtStatus = false;
	private boolean createComplete = false;
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initBtService();
		initDataFromIntent();
		setContentView();
		initView();
		createComplete = true;
		if (createComplete && btService != null){
			onCreateComplete();
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		//unbindService();
		if (listenBtStatus){
			BtStatusChangedBroadcastReceiver.unRegisterStatusListener(this);
		}
	}

	protected void onCreateComplete(){
		initData();
		if (listenBtStatus){
			BtStatusChangedBroadcastReceiver.registerStatusListener(this);
		}
	}
	
	protected void initDataFromIntent() {
	}
	
	protected abstract void setContentView();

	protected void initView() {
		btnReback = (ImageButton) findViewById(R.id.btn_reback);
		btnLeft = (ImageButton) findViewById(R.id.btn_left);
		btnRight = (ImageButton) findViewById(R.id.btn_right);
		btnLeftText = (Button) findViewById(R.id.btn_left_text);
		btnRightText = (Button) findViewById(R.id.btn_right_text);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		btnReback.setOnClickListener(this);
	}
		
	protected void initData() {
	}
	
	/*
	 * 
	 */
	protected void showAlertDialog(final int type, String title, String message, boolean showNegative){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Dialog dialog = new Dialog(this, R.style.SafeCodeDialog);
        View layout = inflater.inflate(R.layout.custom_alert_dialog, null);
        
        TextView tv_title = (TextView) layout.findViewById(R.id.tv_title);
        TextView tv_message = (TextView) layout.findViewById(R.id.tv_content);
        tv_title.setText(title);
        tv_message.setText(message);
        
        Button btnYes = (Button) layout.findViewById(R.id.btn_yes);
        Button btnNo = (Button) layout.findViewById(R.id.btn_no);
        Button btnCommit = (Button) layout.findViewById(R.id.btn_confirm);
        if (showNegative){
        	btnYes.setVisibility(View.VISIBLE);
        	btnNo.setVisibility(View.VISIBLE);
        	btnCommit.setVisibility(View.GONE);
        	btnYes.setOnClickListener(new OnClickListener(){

    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				doAlertPositiveAction(type, dialog, 0);
    				dialog.dismiss();
    			}
            	
            });
        	btnNo.setOnClickListener(new OnClickListener(){

    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				doAlertNegativeAction(type, dialog, 1);
    				dialog.dismiss();
    			}
            	
            });
        } else {
        	btnCommit.setOnClickListener(new OnClickListener(){

    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				doAlertPositiveAction(type, dialog, 0);
    				dialog.dismiss();
    			}
            	
            });
        }
        dialog.addContentView(layout, new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
//		Builder builder = new AlertDialog.Builder(this);
//    	builder.setTitle(title);
//        builder.setMessage(message);
//    	builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
//
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//				doAlertPositiveAction(type, arg0, arg1);
//				arg0.dismiss();
//			}
//    		
//    	});
//    	if (showNegative){
//    		builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
//
//    			@Override
//    			public void onClick(DialogInterface arg0, int arg1) {
//    				doAlertNegativeAction(type, arg0, arg1);
//    				arg0.dismiss();
//    			}
//        		
//        	});
//    	}
//        Dialog alertDialog = builder.create();
//        alertDialog.show();
	}
	
	protected void doAlertPositiveAction(int type, DialogInterface arg0, int arg1){
		if (type == ALERT_TYPE_RETURN_BACK){
			finish();
			System.gc();
		}
	}
	
    protected void doAlertNegativeAction(int type, DialogInterface arg0, int arg1){
		if (type == ALERT_TYPE_RETURN_BACK){
		} else if (type == ALERT_TYPE_SAFE_CODE_ERROR){
			finish();
			System.gc();
		}
	}

	protected void returnback(){
		finish();
		//showAlertDialog(ALERT_TYPE_RETURN_BACK, "提示", "确定要退出吗?", true);
	}

	public static void hideIM(Context mContext, View view){
		try{
			InputMethodManager inputMethodManager = (InputMethodManager) ((Activity)mContext).getSystemService(Context.INPUT_METHOD_SERVICE);
			if(inputMethodManager != null){
				if(view == null)
					view = ((Activity)mContext).getCurrentFocus();
				inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.btn_reback:
			returnback();
			break;
		default:
			break;
		}
	}
	
	public void onStatusChanged(int status) {
	}
	
	
	protected void onCheckSafeCodeSuccess(){
	}
	
	protected void onCheckSafeCodeFail(String code){
		showAlertDialog(ALERT_TYPE_SAFE_CODE_ERROR, "提示", "安全密码输入错误，是否重试?", true);
	}
	
	public void showProcessDialog(String title, String message, final int type){
		if (progressDialog == null){
			progressDialog = ProgressDialog.show(this, title, message);
		} else {
			progressDialog.setTitle(title);
			progressDialog.setMessage(message);
			progressDialog.show();
		}
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				onProcessDialogCanceled(type);
			}
			
		});
	}
	
	public void finishProcessDialog(){
		if (progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	protected void onProcessDialogCanceled(int type){
		
	}
}

