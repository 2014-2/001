package com.crtb.tunnelmonitor.mydefine;



import com.crtb.tunnelmonitor.activity.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

/**
 * 自定义对话框
 * @author   代世明
 * 创建时间  2013-12-7   下午1:58:09
 * @version  1.0
 * @since     JDK  1.6
 */
public class UserDefinedDialog extends Dialog implements android.view.View.OnClickListener {
	/**上下文*/
	private Context ctx;
	/**信息*/
	private String msg;
	// 按钮
	private Button btnleft;
	private Button btncenter;
	private Button btnright;
	/**判断*/
	private boolean IsTwoButton = false;
	/**确定按钮*/
	private View.OnClickListener okListener;
	/**取消按钮*/
	private View.OnClickListener cancelListener;
	/**对话框标题*/
	private TextView tvtitle;
	/**对话框内容*/
	private TextView tvcontent;

	public UserDefinedDialog(Context context, String message,View.OnClickListener onclicklistener,View.OnClickListener cancelListener) {
		super(context,R.style.Theme_Dialog);
		this.ctx = context;
		this.msg = message;
		if (onclicklistener != null) {
			this.okListener=onclicklistener;
		}
		if(cancelListener != null){
			IsTwoButton=true;
			this.cancelListener=cancelListener;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.alertdialog);
		
		
		tvtitle = (TextView) findViewById(R.id.dialogtitle);
		tvcontent = (TextView) findViewById(R.id.dialogcontent);

		btnleft = (Button) findViewById(R.id.btnleft);
		btnright = (Button) findViewById(R.id.btnright);

		btnleft.setOnClickListener(this);
		btnright.setOnClickListener(this);

		btncenter = (Button) findViewById(R.id.btncenter);
		btncenter.setOnClickListener(this);
		if(IsTwoButton){
			btnleft.setVisibility(View.VISIBLE);
			btnright.setVisibility(View.VISIBLE);
			btncenter.setVisibility(View.GONE);
		}
		tvtitle.setText("提示");
		tvcontent.setText(msg);

		WindowManager m = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display d = m.getDefaultDisplay();
		LayoutParams p = getWindow().getAttributes();
//		p.height = (int) (d.getHeight() * 0.3);
		p.width = (int) (d.getWidth() * 0.9);
		//p.alpha = 0.8f;
		//p.dimAmount = 0.0f;
		getWindow().setAttributes(p);
		getWindow().setGravity(Gravity.CENTER);
		
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnleft:
			if(cancelListener != null){
				cancelListener.onClick(v);
			}
			break;
		case R.id.btncenter:
			if(okListener != null){
				okListener.onClick(v);
			}
			break;
		case R.id.btnright:
			if(okListener != null){
				okListener.onClick(v);
			}
			break;
			
		}
		this.cancel();
	}
}
