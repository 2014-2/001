package com.crtb.tunnelmonitor.activity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.dao.impl.v2.AlertListDao;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.task.WarningDataManager;
import com.crtb.tunnelmonitor.task.WarningDataManager.UploadWarningData;
import com.crtb.tunnelmonitor.task.WarningDataManager.WarningLoadListener;
import com.crtb.tunnelmonitor.task.WarningDataManager.WarningUploadListener;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public class WarningUploadActivity extends Activity {
    private static final String LOG_TAG = "WarningUploadActivity";
    private MenuPopupWindow menuWindow;
    private ListView mlvWarningList;
    private LinearLayout mProgressOverlay;
    private ProgressBar mUploadProgress;
    private ImageView mUploadStatusIcon;
    private TextView mUploadStatusText;
    private boolean isUploading = true;
    private WarningUploadAdapter mAdapter;

    private Random ran = new Random();
    private String s[] = new String[20];
    private String ss[] = {"拱顶", "测线S1", "测线S2"};
    private String sss[] = {"开","正在处理","已消警"};
    private String ssss[] = {"", "", "", ""};
    private String s2[] = {"拱顶的累计沉降值超限", "拱顶的单次下沉速率超限", "累计收敛值超限",
            "地表的累计沉降值超限","地表的单次下沉速率超限" ,"单次收敛速率超限"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning_upload);
        TextView title=(TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.upload_warning_data);
        initB();
        init();
        initProgressOverlay();
        initCurWorkBinding();
    }

    private void init() {
        mlvWarningList = (ListView) findViewById(R.id.warning_list);
        mlvWarningList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setCheckStatus(position);
            }
        });
        mAdapter = new WarningUploadAdapter();
        mlvWarningList.setAdapter(mAdapter);
        loadData();
    }

    private void initProgressOverlay() {
        mProgressOverlay = (LinearLayout)findViewById(R.id.progress_overlay);
        mUploadProgress = (ProgressBar)findViewById(R.id.progressbar);
        mUploadStatusIcon = (ImageView)findViewById(R.id.upload_status_icon);
        mUploadStatusText = (TextView)findViewById(R.id.upload_status_text);
        mProgressOverlay.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isUploading) {
                    hideProgressOverlay();
                }
                return true;
            }
        });
    }

    private void showProgressOverlay() {
        mProgressOverlay.setVisibility(View.VISIBLE);
        mUploadProgress.setIndeterminate(true);
        mUploadStatusIcon.setVisibility(View.GONE);
        mUploadStatusText.setText(R.string.data_uploading_alert);
        isUploading = true;
    }

    private void hideProgressOverlay() {
        mProgressOverlay.setVisibility(View.GONE);
    }

    private void updateStatus(boolean isSuccess) {
        isUploading = false;
        mUploadStatusIcon.setVisibility(View.VISIBLE);
        mUploadProgress.setIndeterminate(false);
        mUploadProgress.setProgress(100);
        if (isSuccess) {
            mUploadStatusIcon.setImageResource(R.drawable.success);
            mUploadStatusText.setText(R.string.data_upload_alert_success);

            List<WarningUploadData> warningDataList = mAdapter.getWarningData();
            if (warningDataList != null && warningDataList.size() > 0) {
                for (WarningUploadData uploadData : warningDataList) {
                    if (uploadData.isChecked()) {
                        UploadWarningData uW = uploadData.getUploadWarningData();
                        if (uW != null) {
                            ArrayList<AlertInfo> ais = uW.getAlertInfos();
                            if (ais != null && ais.size() > 0) {
                                for (AlertInfo ai : ais) {
                                    if (ai != null) {
                                        int alertId = ai.getAlertId();
                                        AlertList bean = AlertListDao.defaultDao().queryOneById(alertId);
                                        if (bean != null) {
                                            bean.setUploadStatus(2);
                                            AlertListDao.defaultDao().update(bean);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            loadData();
        } else {
            mUploadStatusIcon.setImageResource(R.drawable.fail);
            mUploadStatusText.setText(R.string.data_upload_alert_fail);
        }

    }

    private void loadData() {
        WarningDataManager dataManager = new WarningDataManager();
        dataManager.loadData(new WarningLoadListener() {
            @Override
            public void done(List<UploadWarningData> uploadDataList) {
                List<WarningUploadData> warningDataList = new ArrayList<WarningUploadData>();
                for(UploadWarningData uploadWarningData : uploadDataList) {
                    WarningUploadData warningData = new WarningUploadData();
                    warningData.setUploadWarningData(uploadWarningData);
                    warningData.setChecked(false);
                    warningDataList.add(warningData);
                }
                mAdapter.setWarningData(warningDataList);
            }
        });
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
                	List<AlertInfo> checkedData = getCheckedAlertInfo();
                    if (checkedData != null && checkedData.size() > 0) {
                        Intent intent = new Intent(WarningUploadActivity.this,ReviewWarningActivity.class);
                        intent.putExtra("alert_info", (Serializable)checkedData);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "请选中预警信息再点击查看",Toast.LENGTH_SHORT).show();
                    }
                    menuWindow.dismiss();
                }
            });
            shangchuan.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
					String siteCode = CrtbWebService.getInstance()
							.getSiteCode();
					if (siteCode == null || siteCode.trim() == "") {
						Toast.makeText(getApplicationContext(), "请先关联工点",
								Toast.LENGTH_SHORT).show();
					} else {
						WarningDataManager dataManager = new WarningDataManager();
						List<UploadWarningData> uploadWarningDataList = new ArrayList<UploadWarningData>();
						List<WarningUploadData> warningDataList = mAdapter
								.getWarningData();
						if (warningDataList != null
								&& warningDataList.size() > 0) {
							for (WarningUploadData uploadData : warningDataList) {
								if (uploadData.isChecked()) {
									uploadWarningDataList.add(uploadData
											.getUploadWarningData());
								}
							}
							if (uploadWarningDataList.size() > 0) {
								UploadWarningData warningData = uploadWarningDataList
										.get(0);
								if (AlertUtils
										.mergedAlertCanBeUploaded(warningData
												.getMergedAlert())) {
									showProgressOverlay();
									dataManager.uploadData(
											uploadWarningDataList,
											new WarningUploadListener() {
												@Override
												public void done(
														final boolean success) {
													runOnUiThread(new Runnable() {
														@Override
														public void run() {
															updateStatus(success);
														}
													});
												}
											});
								} else {
									Toast.makeText(WarningUploadActivity.this,
											R.string.alert_upload_promote,
											Toast.LENGTH_LONG).show();
								}
							}
						}
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

    private List<AlertInfo> getCheckedAlertInfo() {
        List<AlertInfo> checkedData = new ArrayList<AlertInfo>();
        List<WarningUploadData> allData = mAdapter.getWarningData();
        for (WarningUploadData data : allData) {
            if (data.isChecked()) {
                UploadWarningData d = data.getUploadWarningData();
                if (d != null) {
                    AlertInfo leiji = d.getLeijiAlert();
                    if (leiji != null) {
                        checkedData.add(leiji);
                    }
                    AlertInfo sulv = d.getSulvAlert();
                    if (sulv != null) {
                        checkedData.add(sulv);
                    }
                    
                }
            }
        }
        return checkedData;
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
        showProgressOverlay();
        CrtbWebService.getInstance().uploadWarningData(null, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                Log.d(LOG_TAG, "upload warning data success.");
                // Toast.makeText(getApplicationContext(), "上传预警信息成功",
                // Toast.LENGTH_SHORT).show();
                updateStatus(true);
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "upload warning data failed.");
                // Toast.makeText(getApplicationContext(), "上传预警信息失败",
                // Toast.LENGTH_SHORT).show();
                updateStatus(false);
            }
        });
    }

    public void initB() {
        for (int i = 0; i < s.length; i++) {
            s[i] = "DK+"
                    + (10 + ran.nextInt(10))
                    + ((Math.round(ran.nextDouble() + 100) / 100.0) + (ran
                            .nextInt(100) + 100));
        }
    }

    public String getdate() {
        SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return simp.format(new Date());
    }

    private static class ViewHolder {
        TextView mWarningTime;
        TextView mWarningState;
        TextView mWarningUpload;
        ImageView mWarningCheck;
    }

    private class WarningUploadAdapter extends BaseAdapter {
        private List<WarningUploadData> mWarningDataList;
        private int mCheckedPosition = -1;

        WarningUploadAdapter() {
            mWarningDataList = new ArrayList<WarningUploadData>();
        }

        public void setWarningData(List<WarningUploadData> warningDataList) {
            if (warningDataList != null && warningDataList.size() > 0) {
				for (WarningUploadData warningData : warningDataList) {
					warningData.setChecked(false);
				}
            	mCheckedPosition = -1;
                mWarningDataList = warningDataList;
                notifyDataSetChanged();
            }
        }

        public List<WarningUploadData> getWarningData() {
            return mWarningDataList;
        }

        public void setCheckStatus(int position) {
			if (mCheckedPosition != -1) {
				WarningUploadData oldWarningData = mWarningDataList.get(mCheckedPosition);
				oldWarningData.setChecked(false);
				if (mCheckedPosition != position) {
					WarningUploadData newWarningData = mWarningDataList.get(position);
					newWarningData.setChecked(true);
					mCheckedPosition = position;
				} else {
					mCheckedPosition = -1;
				}
			} else {
				WarningUploadData warningData = mWarningDataList.get(position);
				warningData.setChecked(true);
				mCheckedPosition = position;
			}
			notifyDataSetChanged();
		}

        @Override
        public int getCount() {
            return mWarningDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mWarningDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_warning_upload_item, null);
                ViewHolder holder = new ViewHolder();
                holder.mWarningTime = (TextView)convertView.findViewById(R.id.warning_id);
                holder.mWarningState = (TextView)convertView.findViewById(R.id.warning_state);
                holder.mWarningUpload = (TextView)convertView.findViewById(R.id.warning_is_uploaded);
                holder.mWarningCheck = (ImageView)convertView.findViewById(R.id.checked_switch);
                convertView.setTag(holder);
            }
            bindView(mWarningDataList.get(position), convertView);
            return convertView;
        }

        private void bindView(WarningUploadData warningData, View convertView) {
            ViewHolder holder = (ViewHolder)convertView.getTag();
            AlertInfo ai = warningData.getUploadWarningData().getAlertInfo();
            if (ai != null) {
                holder.mWarningTime.setText(ai.getDate());
                holder.mWarningState.setText(ai.getAlertStatusMsg());
            }
            if (warningData.isUploaded()) {
                holder.mWarningUpload.setText("已上传");
            } else {
                holder.mWarningUpload.setText("未上传");
            }
            if (warningData.isChecked()) {
                holder.mWarningCheck.setImageResource(R.drawable.yes);
            } else {
                holder.mWarningCheck.setImageResource(R.drawable.no);
            }
        }
    }

    public class WarningUploadData {
        private UploadWarningData mUploadWarningData;
        private boolean mIsChecked;

        public void setUploadWarningData(UploadWarningData uploadWarningData) {
            mUploadWarningData = uploadWarningData;
        }

        public UploadWarningData getUploadWarningData() {
            return mUploadWarningData;
        }

        public boolean isUploaded() {
            if (mUploadWarningData != null) {
                AlertInfo ai = mUploadWarningData.getAlertInfo();
                if (ai != null) {
                    return ai.getUploadStatus() == 2;
                }
            }
            return false;
        }

        public void setChecked(boolean checked) {
            mIsChecked = checked;
        }

        public boolean isChecked() {
            return mIsChecked;
        }
    }
    
    
    private void initCurWorkBinding() {
    	TextView curProject = (TextView)findViewById(R.id.cur_project_name);
    	TextView curWork = (TextView)findViewById(R.id.cur_work_name);
    	String[] list = CrtbUtils.getWorkSiteInfo();
    	if(curWork != null && curProject != null && list != null && list.length == 2){
    		curProject.setText(list[0]);
    		curWork.setText(list[1]);
    	}	
	}
}
