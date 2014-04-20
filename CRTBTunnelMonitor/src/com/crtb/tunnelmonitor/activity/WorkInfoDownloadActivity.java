package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.SectionStatus;

public class WorkInfoDownloadActivity extends Activity {
    private static final String LOG_TAG = "WorkInfoDownloadActivity";
    private MenuPopupWindow menuWindow;

    private ListView mlvWorkInfos;

    private WorkPlanAdapter mAdapter;

    private List<ProjectIndex> mAllProjects;
    
    private int mPointCount = 0;
    private boolean mFlag = true;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workinfo_download);
        TextView title=(TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.download_work_data);
        init();
    }

    private void init() {
        mlvWorkInfos = (ListView)findViewById(R.id.lv_workinfo);

        mAllProjects = ProjectIndexDao.defaultWorkPlanDao().queryAllWorkPlan();
        if (mAllProjects == null) {
            mAllProjects = new ArrayList<ProjectIndex>();
        }

        mAdapter = new WorkPlanAdapter();
        mlvWorkInfos.setAdapter(mAdapter);
    }

    //下载断面编码数据
    private void downloadSectionCodeList(SectionStatus status) {
    	mPointCount = 0;
    	mFlag = true;
    	onDownLoadStarted();
        CrtbWebService.getInstance().getSectionCodeList(status, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                Log.d(LOG_TAG, "download section code list success.");
                List<String> sectionCodeList = Arrays.asList((String[])data);
                downloadSectionList(sectionCodeList);
            }

            @Override
            public void onFailed(String reason) {
            	mFlag = false;
            	onDownLoadFinished(mFlag);
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

    private void downloadSection(final String sectionCode) {
        CrtbWebService.getInstance().getSectionInfo(sectionCode, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                TunnelCrossSectionIndex[] sectionInfo = (TunnelCrossSectionIndex[])data;
                final TunnelCrossSectionIndex section = sectionInfo[0];
                new Thread(new Runnable() {
					@Override
					public void run() {
			             TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
			             dao.insert(section);
					}
				}).start();
                List<String> pointCodeList = Arrays.asList(section.getSurveyPntName().split(","));
                mPointCount += pointCodeList.size();
                downloadPointList(pointCodeList);
            }

            @Override
            public void onFailed(String reason) {
                // TODO Auto-generated method stub
                Log.d(LOG_TAG, "downloadSection failed: " + reason);
                mFlag = false;
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
            	final List<TunnelSettlementTotalData> pointTestDataList = Arrays.asList((TunnelSettlementTotalData[])data);
            	new Thread(new Runnable() {
					@Override
					public void run() {
						TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao();
		            	for(TunnelSettlementTotalData testPointData : pointTestDataList) {
		            		dao.insert(testPointData);
		            	}
					}
				}).start();
            	mPointCount--;
            	if (mPointCount == 0) {
            		onDownLoadFinished(mFlag);
            	}
                Log.d(LOG_TAG, "download point success.");
            }

            @Override
            public void onFailed(String reason) {
            	mFlag = false;
            	mPointCount--;
            	if (mPointCount == 0) {
            		onDownLoadFinished(mFlag);
            	}
                Log.d(LOG_TAG, "download point failed: " + reason);
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
                	ProjectIndex currentProject = ProjectIndexDao.defaultWorkPlanDao().queryEditWorkPlan();
                	if (currentProject != null) {
	                    downloadSectionCodeList(SectionStatus.VALID);
                	} else {
                		Toast.makeText(getApplicationContext(), "请先打开工作面", Toast.LENGTH_SHORT).show();
                	}
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

    class WorkPlanAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAllProjects.size();
        }

        @Override
        public Object getItem(int position) {
            return mAllProjects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mAllProjects.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.layout_workinfo_download_item, null);
            }
            ProjectIndex project = mAllProjects.get(position);
            TextView id = (TextView)convertView.findViewById(R.id.workplan_id);
            id.setText(String.valueOf(project.getId()));
            TextView name = (TextView)convertView.findViewById(R.id.workplan_name);
            name.setText(project.getProjectName());
            TextView isUpload = (TextView)convertView.findViewById(R.id.workplan_is_upload);
            //TODO: 当前暂时无法判断是否已上传
            isUpload.setText("已上传");
            return convertView;
        }
    }
    
    private void onDownLoadStarted() {
    	mProgressDialog = ProgressDialog.show(this, null, "正在下载数据", true, false);
    }
    
    private void onDownLoadFinished(boolean flag) {
    	mProgressDialog.dismiss();
    	String siteName = CrtbWebService.getInstance().getSiteName();
    	if (flag) {
    		Toast.makeText(getApplicationContext(), "下载" + siteName + "成功", Toast.LENGTH_SHORT).show();
    	} else {
    		Toast.makeText(getApplicationContext(), "下载" + siteName + "失败", Toast.LENGTH_SHORT).show();
    	}
    }
}