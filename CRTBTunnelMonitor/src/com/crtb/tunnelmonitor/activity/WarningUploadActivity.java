package com.crtb.tunnelmonitor.activity;

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

import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.utils.WarningDataManager;
import com.crtb.tunnelmonitor.utils.WarningDataManager.WarningLoadListener;

public class WarningUploadActivity extends Activity {
    private static final String LOG_TAG = "WarningUploadActivity";
    private MenuPopupWindow menuWindow;
    private ListView mlvWarningList;
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
    }

    private void init() {
        mlvWarningList = (ListView) findViewById(R.id.warning_list);
        mlvWarningList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mAdapter.revertCheck(position);
			}
		});
        mAdapter = new WarningUploadAdapter();
        mlvWarningList.setAdapter(mAdapter);
        loadData();
    }

    private void loadData() {
    	WarningDataManager dataManager = new WarningDataManager();
        dataManager.loadData(new WarningLoadListener() {
			@Override
			public void done(List<AlertInfo> uploadDataList) {
				List<WarningUploadData> warningDataList = new ArrayList<WarningUploadData>();
				for(AlertInfo alertInfo : uploadDataList) {
					WarningUploadData warningData = new WarningUploadData();
					warningData.setAlertInfo(alertInfo);
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
    	
    	WarningUploadAdapter() {
    		mWarningDataList = new ArrayList<WarningUploadData>();
    	}
    	
    	public void setWarningData(List<WarningUploadData> warningDataList) {
    		if (warningDataList != null) {
    			mWarningDataList = warningDataList;
    			notifyDataSetChanged();
    		}
    	}
    	
    	public void revertCheck(int position) {
    		WarningUploadData warningUploadData = mWarningDataList.get(position);
    		warningUploadData.setChecked(!warningUploadData.isChecked());
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
        	holder.mWarningTime.setText(warningData.getAlertInfo().getDate());
        	holder.mWarningState.setText(warningData.getAlertInfo().getAlertStatusMsg());
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
    
    private class WarningUploadData {
    	private AlertInfo mAlertInfo;
    	private boolean mIsChecked;
    	
    	public void setAlertInfo(AlertInfo alertInfo) {
    		mAlertInfo = alertInfo;
    	}
    	
    	public AlertInfo getAlertInfo() {
    		return mAlertInfo;
    	}
    	
    	public boolean isUploaded() {
    		return false;
    	}
    	
    	public void setChecked(boolean checked) {
            mIsChecked = checked;
        }

        public boolean isChecked() {
            return mIsChecked;
        }
    }
}
