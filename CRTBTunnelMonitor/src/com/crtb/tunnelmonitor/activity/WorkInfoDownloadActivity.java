
package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SiteProjectMappingDao;
import com.crtb.tunnelmonitor.dao.impl.v2.WorkSiteIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.SiteProjectMapping;
import com.crtb.tunnelmonitor.entity.WorkSiteIndex;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.task.DataDownloadManager;
import com.crtb.tunnelmonitor.task.DataDownloadManager.DownloadListener;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
import com.crtb.tunnelmonitor.widget.CrtbProgressOverlay;

public class WorkInfoDownloadActivity extends WorkFlowActivity {
    private static final String LOG_TAG = "WorkInfoDownloadActivity";
    private MenuPopupWindow menuWindow;

    private ListView mlvWorkInfos;

    private WorkSitesAdapter mAdapter;

    private int longPressedItemPosition = -1;

    private int curProjectId = -1;
    
    private CrtbProgressOverlay progressOverlay;

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
        //使用自定义的弹出对话框
        //registerForContextMenu(mlvWorkInfos);
        loadMenuInfo();
        loadData();
    }

    private void loadMenuInfo() {
    	mlvWorkInfos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
		        longPressedItemPosition = position;
		        String workSiteName = "";
		        WorkSiteIndex item = null;
		        if (mAdapter != null) {
		            item = (WorkSiteIndex) mAdapter.getItem(longPressedItemPosition);
		            if (item != null) {
		                workSiteName = item.getSiteName();
		            }
		        }			
				final String[] menus = new String[]{getString(R.string.work_site_down_down),getString(R.string.work_site_down_bind)};
				showListActionMenu(workSiteName, menus, item);
			}
		}) ;
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
	protected void onListItemSelected(Object obj, int position, String menu) {
		if (obj == null || menu == null) {
			return;
		}

		final WorkSiteIndex workSite = (WorkSiteIndex) obj;

		if (menu.equals(getString(R.string.work_site_down_down))) {
			downWorkSite(workSite);
		} else if (menu.equals(getString(R.string.work_site_down_bind))) {
			bindWorkSite(workSite);
		}
    }
    
    private void bindWorkSite(final WorkSiteIndex workSite){
    	CrtbWebService.getInstance().setZoneCode(workSite.getZoneCode());
		CrtbWebService.getInstance().setSiteCode(workSite.getSiteCode());
		SiteProjectMappingDao.defaultDao().insertOrUpdate(curProjectId, workSite.getID());
		loadData();
    }
    
    private void downWorkSite(final WorkSiteIndex workSite){
    	bindWorkSite(workSite);
    	Toast.makeText(this, "下载功能暂时关闭", Toast.LENGTH_LONG).show();
//    	showProgressOverlay();
//		DataDownloadManager downloadManager = new DataDownloadManager();
//		downloadManager.downloadWorkSite(workSite, new DownloadListener() {
//			@Override
//			public void done(boolean success) {
//				updateStatus(success);
//				if (success) {
//					new UpdateTask().execute(workSite);
//				}
//			}
//		});
    }
    
    private void initProgressOverlay() {
    	progressOverlay = new CrtbProgressOverlay(this, CrtbUtils.getProgressLayout(this));
    }

    private void showProgressOverlay() {
        progressOverlay.showProgressOverlay(getString(R.string.data_downloading));
    }

    private void updateStatus(boolean isSuccess) {
    	String notice = getString(R.string.data_download_success);
        if (!isSuccess) {
        	notice = getString(R.string.data_download_fail);
        }
		progressOverlay.uploadFinish(isSuccess, notice);
    }

    class MenuPopupWindow extends PopupWindow {
        public RelativeLayout xiazai;
        private View mMenuView;

        public MenuPopupWindow(Activity context) {
            super(context);
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
            	if(!progressOverlay.isUploading()){
					if (progressOverlay != null && progressOverlay.showing()) {
						progressOverlay.hideProgressOverlay();
					} else{
						this.finish();						
					}
            	}
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

    private class UpdateTask extends AsyncTask<WorkSiteIndex, Void, Void> {

        @Override
        protected Void doInBackground(WorkSiteIndex... params) {
            WorkSiteIndex workSite = params[0];
            workSite.setDownloadFlag(2);
            WorkSiteIndexDao.defaultDao().update(workSite);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            loadData();
        }
    }
}