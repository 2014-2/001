package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity implements OnClickListener{

	private EditText mRegisterCode,mSerialNumber;
	
	private Button mOk,mCancel;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
		mRegisterCode=(EditText) findViewById(R.id.regist_code);
		mSerialNumber=(EditText) findViewById(R.id.serial_number);
	    mOk=(Button) findViewById(R.id.ok);
	    mCancel=(Button) findViewById(R.id.cancel);
	    mOk.setOnClickListener(this);
	    mCancel.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ok:
		    showDialog(false, null);
			break;
		case R.id.cancel:
			RegisterActivity.this.finish();
			break;
		}	
	}

	private void showDialog(boolean bSuccess,final OnClickListener listener){
        final Dialog dlg=new Dialog(this,R.style.custom_dlg);
        View view=LayoutInflater.from(this).inflate(R.layout.success_dialog_layout, null);      
        dlg.setContentView(view);
        TextView text=(TextView) dlg.findViewById(R.id.text);
        if(!bSuccess){
            text.setText("注册失败！ 请确定输入的信息正确！");
            text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warnning,0, 0, 0);
        }
        Button bt=(Button) dlg.findViewById(R.id.bt);
        bt.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
               if(listener!=null){
                   listener.onClick(v);
               }
               if(dlg!=null){
                   dlg.dismiss();
               }
            }
        });
        dlg.show();
        WindowManager.LayoutParams param=dlg.getWindow().getAttributes();
        param.width=getWindowManager().getDefaultDisplay().getWidth()*3/4;
        dlg.getWindow().setAttributes(param);
    }
}
