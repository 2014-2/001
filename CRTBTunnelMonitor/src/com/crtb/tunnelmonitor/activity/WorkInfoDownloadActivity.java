package com.crtb.tunnelmonitor.activity;

import java.util.Arrays;
import java.util.List;

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

import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.PointStatus;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.SectionStatus;

public class WorkInfoDownloadActivity extends Activity {
    private static final String LOG_TAG = "WorkInfoDownloadActivity";
    private MenuPopupWindow menuWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workinfo_download);
        TextView title=(TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.download_work_data);
    }

    //下载断面编码数据
    private void downloadSectionCodeList(SectionStatus status) {
        CrtbWebService.getInstance().getSectionCodeList(status, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                Log.d(LOG_TAG, "download section code list success.");
                downloadSectionList(Arrays.asList((String[])data));
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "download section code list failed.");
            }
        });
    }

    //下载断面详细数据
    private void downloadSectionList(List<String> codeList) {
        for(String sectionCode : codeList) {
            downloadSection(sectionCode);
        }
    }

    private void downloadSection(String sectionCode) {
        CrtbWebService.getInstance().getSectionInfo(sectionCode, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                TunnelCrossSectionInfo[] sectionInfo = (TunnelCrossSectionInfo[])data;
                TunnelCrossSectionInfo section = sectionInfo[0];
                TunnelCrossSectionDao dao = TunnelCrossSectionDao.defaultDao();
                dao.insert(section);
                List<String> pointCodeList = Arrays.asList(section.getSurveyPntName().split(","));
                downloadPointList(pointCodeList);
            }

            @Override
            public void onFailed(String reason) {
                // TODO Auto-generated method stub
                Log.d(LOG_TAG, "downloadSection failed: " + reason);
            }
        });
    }

    //TODO:  It's not work yet.
    private void downloadPointList(List<String> pointCodeList) {
    	for(String pointCode : pointCodeList) {
    		downloadPoint(pointCode);
    	}
    }

    private void downloadPoint(String pointCode) {
        CrtbWebService.getInstance().getPointInfo(pointCode, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                Log.d(LOG_TAG, "download point success.");
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "download point failed.");
            }
        });
    }

    class MenuPopupWindow extends PopupWindow {
        public RelativeLayout xiazai;
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
            mMenuView = inflater.inflate(R.layout.menu_workinfo_download, null);
            xiazai = (RelativeLayout) mMenuView.findViewById(R.id.menu_xz);

            xiazai.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                	downloadSectionCodeList(SectionStatus.VALID);
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
}