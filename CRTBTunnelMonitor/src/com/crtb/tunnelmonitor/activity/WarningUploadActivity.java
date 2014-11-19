package com.crtb.tunnelmonitor.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.activity.WarningShowSortBySectionActivity.SectionWarning;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertHandlingInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertListDao;
import com.crtb.tunnelmonitor.entity.AlertHandlingList;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.WaringUploadDataContainer;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.task.WarningDataManager;
import com.crtb.tunnelmonitor.infors.UploadWarningEntity;
import com.crtb.tunnelmonitor.task.WarningDataManager.WarningLoadListener;
import com.crtb.tunnelmonitor.task.WarningDataManager.WarningUploadListener;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
import com.crtb.tunnelmonitor.widget.CrtbProgressOverlay;

public class WarningUploadActivity extends Activity {
    private static final String LOG_TAG = "WarningUploadActivity";
    private MenuPopupWindow menuWindow;
    private ListView mlvWarningList;
    private CrtbProgressOverlay progressOverlay = null;
    private WarningDataManager dataManager = null;
    private WarningUploadAdapter mAdapter;
    private WaringUploadDataContainer dataContainer;
    private boolean needQueryData = false;
    public static final String CURRENT_EDIT_SECTION = "current_edit_section";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning_upload);
        TextView title=(TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.upload_warning_data);
        init();
        initProgressOverlay();
        initCurWorkBinding();

        dataContainer = (WaringUploadDataContainer)CommonObject.findObject(WaringUploadDataContainer.KEY);
        CommonObject.remove(WaringUploadDataContainer.KEY);
        loadData();
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
    }

    private void initProgressOverlay() {
    	  progressOverlay = new CrtbProgressOverlay(this,CrtbUtils.getProgressLayout(this));
    }

    private void showProgressOverlay() {
    	progressOverlay.showProgressOverlay(getString(R.string.data_uploading_alert));
    }

    private void updateStatus(boolean isSuccess) {
        if (isSuccess) {
        	progressOverlay.uploadFinish(true,getString(R.string.data_upload_alert_success));
            List<WarningUploadData> warningDataList = mAdapter.getWarningData();
            if (warningDataList != null && warningDataList.size() > 0) {
                for (WarningUploadData uploadData : warningDataList) {
                    if (uploadData.isChecked()) {
                        UploadWarningEntity uW = uploadData.getUploadWarningData();
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
            needQueryData = true;
            loadData();
        } else {
            progressOverlay.uploadFinish(false,dataManager.getNotice());
        }

    }

    private void loadData() {
		if (!needQueryData) {
			mAdapter.setWarningData(dataContainer.getWaringDataList());
			return;
		}

		needQueryData = false;
		String sectionGuid = dataContainer.getCurSectionGuid();
		WarningDataManager dataManager = new WarningDataManager();
		dataManager.loadDataSortBySectionGuid(sectionGuid, new WarningLoadListener() {
			@Override
			public void done(List<UploadWarningEntity> uploadDataList) {
				List<WarningUploadData> waringDataList = new ArrayList<WarningUploadData>();
				for (UploadWarningEntity uploadWarningData : uploadDataList) {

					WarningUploadData warningData = new WarningUploadData();
					warningData.setUploadWarningData(uploadWarningData);
					warningData.setChecked(false);
					waringDataList.add(warningData);
				}
				dataContainer.setWaringDataList(waringDataList);
				CommonObject.putObject(WarningUploadActivity.CURRENT_EDIT_SECTION,waringDataList);
				mAdapter.setWarningData(dataContainer.getWaringDataList());
			}
		});		
    }

    class MenuPopupWindow extends PopupWindow {
        public RelativeLayout chakan;
        public RelativeLayout shangchuan;
        private View mMenuView;

        public MenuPopupWindow(final Activity context) {
            super(context);
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
						Toast.makeText(getApplicationContext(), "请先关联工点",Toast.LENGTH_SHORT).show();
					} else {
						List<UploadWarningEntity> uploadWarningDataList = new ArrayList<UploadWarningEntity>();
						List<WarningUploadData> warningDataList = mAdapter
								.getWarningData();
						if (warningDataList != null
								&& warningDataList.size() > 0) {
							for (WarningUploadData uploadData : warningDataList) {
								if (uploadData.isChecked()) {
									uploadWarningDataList.add(uploadData.getUploadWarningData());
								}
							}
							if (uploadWarningDataList.size() > 0) {
								UploadWarningEntity warningData = uploadWarningDataList.get(0);
								AlertInfo ai = warningData.getLeijiAlert();
								//更新累积报警值
								if(ai != null){
								    AlertUtils.getAlertTransfiniteInfo(ai);
								}
								
								if (!AlertUtils.hasUnhandledPreviousWarningData(ai)) {
									CrtbUtils.showDialogWithYes(context, getString(R.string.upload_warning_data), getString(R.string.alert_upload_promote));
									return;
								}
								
								showProgressOverlay();
								//默认同一时刻只能选择一条数据上传
								uploadWarning(warningData);
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
    
    private void uploadWarning(UploadWarningEntity originalData){
    	AlertInfo alertInfo = originalData.getAlertInfo();
    	int alertId = alertInfo.getAlertId();
		dataManager = new WarningDataManager();
		List<AlertHandlingList> handlings = AlertHandlingInfoDao.defaultDao().queryByAlertIdOrderByHandlingTimeAscAndNoUpload(alertId);
		if(handlings == null || handlings.size() < 1){
			AlertHandlingList ahl = AlertHandlingInfoDao.defaultDao().queryNoHandlingInfoByAlertId(alertInfo);
			if (ahl != null) {
				handlings = new ArrayList<AlertHandlingList>();
				handlings.add(ahl);
			} else {
				// 所有处理详情已经上传了，且没有没有处理详情的情况
				//YX 此处的处理和数据上传里的预警处理不一致，是由于数据处理里面要精确的判断个数
				updateStatus(false);
				return;
			}
    	}
		
       	if(handlings.size() > 1){
       		//排除第一条处理详情为null的数据
       		String handlingInfo = handlings.get(0).getInfo();
       		if(handlingInfo == null || handlingInfo.equals("")){
       			handlings.remove(0);
       		}
       	}
       	
		dataManager.uploadWarningData(originalData,handlings,new WarningUploadListener() {
			@Override
			public void done(final boolean success,boolean hasData) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateStatus(success);
					}
				});
			}
		});
    }

    private List<AlertInfo> getCheckedAlertInfo() {
        List<AlertInfo> checkedData = new ArrayList<AlertInfo>();
        List<WarningUploadData> allData = mAdapter.getWarningData();
        for (WarningUploadData data : allData) {
            if (data.isChecked()) {
                UploadWarningEntity d = data.getUploadWarningData();
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

	public static class WarningUploadData implements Serializable{
		private static final long serialVersionUID = 1L;
		private UploadWarningEntity mUploadWarningData;
		private boolean mIsChecked;

		public void setUploadWarningData(UploadWarningEntity uploadWarningData) {
			mUploadWarningData = uploadWarningData;
		}

		public UploadWarningEntity getUploadWarningData() {
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
