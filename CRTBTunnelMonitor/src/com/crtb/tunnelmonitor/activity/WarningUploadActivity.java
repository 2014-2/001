package com.crtb.tunnelmonitor.activity;

import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.RpcCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WarningUploadActivity extends Activity {
	private static final String LOG_TAG = "WarningUploadActivity";
	
    private MenuPopupWindow menuWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning_upload);
        TextView title=(TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.upload_warning_data);
    }

    class MenuPopupWindow extends PopupWindow {
        public RelativeLayout chakan;
        public RelativeLayout shangchuan;
        private View mMenuView;
        private Intent intent;
        public Context c;
        AlertDialog dlg = null;

        public MenuPopupWindow(Activity context) {
            super(context);
            this.c = context;
            dlg = new AlertDialog.Builder(c).create();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mMenuView = inflater.inflate(R.layout.menu_warning_upload, null);
            chakan = (RelativeLayout) mMenuView.findViewById(R.id.menu_ck);
            shangchuan = (RelativeLayout) mMenuView.findViewById(R.id.menu_sc);

            chakan.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO：
                }
            });
            shangchuan.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                	uploadWarningData();
                }
            });
            setContentView(mMenuView);
            setWidth(LayoutParams.FILL_PARENT);
            setHeight(LayoutParams.WRAP_CONTENT);
            // 设置SelectPicPopupWindow弹出窗体可点击
            setFocusable(true);
            // 实例化一个ColorDrawable颜色为半透明
            ColorDrawable dw = new ColorDrawable(0xFF000000);
            // 设置SelectPicPopupWindow弹出窗体的背景
            setBackgroundDrawable(dw);
            setOutsideTouchable(true);
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                if (menuWindow == null) {
                    menuWindow = new MenuPopupWindow(this);
                }
                menuWindow.showAtLocation(new View(this), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                this.finish();
            }
        }
        return true;
    }
    
	private void uploadWarningData() {
		CrtbWebService.getInstance().uploadWarningData(null, new RpcCallback() {
			
			@Override
			public void onSuccess(Object[] data) {
				Log.d(LOG_TAG, "upload warning data success.");
				Toast.makeText(getApplicationContext(), "上传预警信息成功", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onFailed(String reason) {
				Log.d(LOG_TAG, "upload warning data failed.");
				Toast.makeText(getApplicationContext(), "上传预警信息失败", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
