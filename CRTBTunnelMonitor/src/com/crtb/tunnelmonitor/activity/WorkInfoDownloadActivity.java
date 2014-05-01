package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
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
import com.crtb.tunnelmonitor.utils.DataDownloadManager;
import com.crtb.tunnelmonitor.utils.DataDownloadManager.DownloadListener;

public class WorkInfoDownloadActivity extends Activity {
    private static final String LOG_TAG = "WorkInfoDownloadActivity";
    private MenuPopupWindow menuWindow;

    private ListView mlvWorkInfos;

    private WorkPlanAdapter mAdapter;

    private LinearLayout mProgressOverlay;

    private ProgressBar mDownloadProgress;

    private ImageView mDownloadStatusIcon;

    private TextView mDownloadStatusText;


    private List<ProjectIndex> mAllProjects;

    private int mPointCount = 0;
    private boolean mFlag = true;

    // private ProgressDialog mProgressDialog;

    private boolean isDownloading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workinfo_download);
        TextView title=(TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.download_work_site);
        init();
        initProgressOverlay();
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

    private void initProgressOverlay() {
        mProgressOverlay = (LinearLayout)findViewById(R.id.progress_overlay);
        mDownloadProgress = (ProgressBar)findViewById(R.id.progressbar);
        mDownloadStatusIcon = (ImageView)findViewById(R.id.download_status_icon);
        mDownloadStatusText = (TextView)findViewById(R.id.download_status_text);
        mProgressOverlay.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isDownloading) {
                    hideProgressOverlay();
                }
                return true;
            }
        });
    }

    private void showProgressOverlay() {
        mProgressOverlay.setVisibility(View.VISIBLE);
        mDownloadProgress.setIndeterminate(true);
        mDownloadStatusIcon.setVisibility(View.GONE);
        mDownloadStatusText.setText(R.string.data_downloading);
        isDownloading = true;
    }

    private void hideProgressOverlay() {
        mProgressOverlay.setVisibility(View.GONE);
    }

    private void updateStatus(boolean isSuccess) {
        isDownloading = false;
        mDownloadStatusIcon.setVisibility(View.VISIBLE);
        if (isSuccess) {
            mDownloadStatusIcon.setImageResource(R.drawable.success);
            mDownloadStatusText.setText(R.string.data_download_success);
        } else {
            mDownloadStatusIcon.setImageResource(R.drawable.fail);
            mDownloadStatusText.setText(R.string.data_download_fail);
        }
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
                    	showProgressOverlay();
                    	DataDownloadManager downloadManager = new DataDownloadManager();
                    	downloadManager.downloadData(new DownloadListener() {
							@Override
							public void done(boolean success) {
								updateStatus(success);
							}
						});
                    } else {
                        Toast.makeText(getApplicationContext(), "请先打开工作面", Toast.LENGTH_SHORT).show();
                    }
                    menuWindow.dismiss();
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
}