
package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
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
import com.crtb.tunnelmonitor.dao.impl.v2.SiteProjectMappingDao;
import com.crtb.tunnelmonitor.dao.impl.v2.WorkSiteIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.SiteProjectMapping;
import com.crtb.tunnelmonitor.entity.WorkSiteIndex;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.task.DataDownloadManager;
import com.crtb.tunnelmonitor.task.DataDownloadManager.DownloadListener;

public class WorkInfoDownloadActivity extends Activity {
    private static final String LOG_TAG = "WorkInfoDownloadActivity";
    private MenuPopupWindow menuWindow;

    private ListView mlvWorkInfos;

    private WorkSitesAdapter mAdapter;

    private LinearLayout mProgressOverlay;

    private ProgressBar mDownloadProgress;

    private ImageView mDownloadStatusIcon;

    private TextView mDownloadStatusText;

    private boolean isDownloading = true;
    private int longPressedItemPosition = -1;
    private static final int CONTEXT_MENU_DOWNLOAD_WORKSITE = 0;

    private int curProjectId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workinfo_download);
        TextView title = (TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.download_work_site);

        ProjectIndex curProject = ProjectIndexDao.defaultWorkPlanDao().queryEditWorkPlan();
        curProjectId = curProject.getId();

        init();
        initProgressOverlay();
    }

    private void init() {
        mlvWorkInfos = (ListView) findViewById(R.id.lv_workinfo);
        mAdapter = new WorkSitesAdapter();
        mlvWorkInfos.setAdapter(mAdapter);
        registerForContextMenu(mlvWorkInfos);
        loadData();
    }

	private void loadData() {
		List<WorkSiteIndex> workSites = WorkSiteIndexDao.defaultDao().queryAllWorkSite();
		if (workSites == null) {
			workSites = new ArrayList<WorkSiteIndex>();
		}
		if (workSites != null && workSites.size() > 0) {
			mAdapter.setData(workSites);
		}
	}
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        longPressedItemPosition = info.position;
        String workSiteName = "";
        if (mAdapter != null) {
            WorkSiteIndex item = (WorkSiteIndex) mAdapter.getItem(longPressedItemPosition);
            if (item != null) {
                workSiteName = item.getSiteName();
            }
        }
        menu.setHeaderTitle(workSiteName);
        menu.add(0, CONTEXT_MENU_DOWNLOAD_WORKSITE, 0, "下载");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CONTEXT_MENU_DOWNLOAD_WORKSITE) {
            if (longPressedItemPosition >= 0 && mAdapter != null) {
                WorkSiteIndex workSite = (WorkSiteIndex) mAdapter.getItem(longPressedItemPosition);
                if (item != null) {
                	CrtbWebService.getInstance().setZoneCode(workSite.getZoneCode());
                	CrtbWebService.getInstance().setSiteCode(workSite.getSiteCode());
                	showProgressOverlay();
                    DataDownloadManager downloadManager = new DataDownloadManager();
                    downloadManager.downloadWorkSite(workSite, new DownloadListener() {
						@Override
						public void done(boolean success) {
							updateStatus(success);
						}
					});
                }
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        longPressedItemPosition = -1;
        super.onContextMenuClosed(menu);
    }

    private void initProgressOverlay() {
        mProgressOverlay = (LinearLayout) findViewById(R.id.progress_overlay);
        mDownloadProgress = (ProgressBar) findViewById(R.id.progressbar);
        mDownloadStatusIcon = (ImageView) findViewById(R.id.download_status_icon);
        mDownloadStatusText = (TextView) findViewById(R.id.download_status_text);
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
                    ProjectIndex currentProject = ProjectIndexDao.defaultWorkPlanDao()
                            .queryEditWorkPlan();
                    if (currentProject != null) {
                        showProgressOverlay();
                        DataDownloadManager downloadManager = new DataDownloadManager();
                        downloadManager.downloadWorkSiteList(new DownloadListener() {
                            @Override
                            public void done(boolean success) {
                                updateStatus(success);
                                loadData();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "请先打开工作面", Toast.LENGTH_SHORT)
                                .show();
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
                menuWindow.showAtLocation(new View(this), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                this.finish();
            }
        }
        return true;
    }

    class WorkSitesAdapter extends BaseAdapter {
    	private List<WorkSiteIndex> mWorkSites;
    	
    	WorkSitesAdapter() {
    		mWorkSites = new ArrayList<WorkSiteIndex>();
    	}
    	
    	public void setData(List<WorkSiteIndex> workSites) {
    		if (workSites != null) {
    			mWorkSites = workSites;
    			notifyDataSetChanged();
    		}
    	}
    	
        @Override
        public int getCount() {
            return mWorkSites.size();
        }

        @Override
        public Object getItem(int position) {
            return mWorkSites.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mWorkSites.get(position).getID();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.layout_workinfo_download_item, null);
            }
            WorkSiteIndex workSite = mWorkSites.get(position);
            int workSiteId = workSite.getID();
            TextView id = (TextView) convertView.findViewById(R.id.workplan_id);
            id.setText(String.valueOf(workSiteId));
            TextView name = (TextView) convertView.findViewById(R.id.workplan_name);
            name.setText(workSite.getSiteName());
            TextView isDownload = (TextView) convertView.findViewById(R.id.workplan_is_upload);
            String flagStr = null;
            int flag = workSite.getDownloadFlag();
            if (flag == 1) {
                flagStr = "未下载";
            } else if (flag == 2) {
                flagStr = "已下载";
            }
            isDownload.setText(flagStr);

            boolean mappedToCurProject = false;
            SiteProjectMapping mapping = SiteProjectMappingDao.defaultDao().queryOneByWorkSiteId(
                    workSiteId);
            if (mapping != null) {
                if (mapping.getProjectId() == curProjectId) {
                    mappedToCurProject = true;
                }
            }

            convertView.setBackgroundColor(getResources().getColor(
                    mappedToCurProject ? R.color.blue : android.R.color.transparent));
            return convertView;
        }
    }
}