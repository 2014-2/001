package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.task.AsyncUpdateTask.UpdateListener;
import com.crtb.tunnelmonitor.task.AsyncUploadTask;
import com.crtb.tunnelmonitor.task.AsyncUploadTask.UploadListener;
import com.crtb.tunnelmonitor.task.AsyncUpdateTask;
import com.crtb.tunnelmonitor.task.SheetRecord;
import com.crtb.tunnelmonitor.task.SubsidenceAsyncQueryTask;
import com.crtb.tunnelmonitor.task.SubsidenceAsyncUploadTask;
import com.crtb.tunnelmonitor.task.AsyncQueryTask.QueryLisenter;
import com.crtb.tunnelmonitor.task.TunnelAsyncQueryTask;
import com.crtb.tunnelmonitor.task.TunnelAsyncUploadTask;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
import com.crtb.tunnelmonitor.widget.SubsidenceSectionSheetFragment;
import com.crtb.tunnelmonitor.widget.TunnelSectionSheetFragment;
import com.crtb.tunnelmonitor.network.CrtbWebService;


public class DataUploadActivity extends FragmentActivity {
    private static final String LOG_TAG = "DataUploadActivity";
    private TextView mTopbarTitle;
    private ImageView cursor;
    private ViewPager mPager;
    private ArrayList<Fragment> mFragmentList;
    private TunnelSectionSheetFragment mTunnelFragment;
    private SubsidenceSectionSheetFragment mSubsidenceFragment;
    private TextView mTunnelTab;
    private TextView mSubsidenceTab;
    private LinearLayout mProgressOverlay;
    private ProgressBar mUploadProgress;
    private ImageView mUploadStatusIcon;
    private TextView mUploadStatusText;
    private boolean isUploading = true;
    private int bmpW;
    private int offset = 0;
    private int currIndex = 0;
    private MenuPopupWindow menuWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_upload);
        setTopbarTitle(getString(R.string.server_data_upload_title));
        initImageView();
        initPager();
        initTab();
        initProgressOverlay();
        initCurWorkBinding();
    }

    @Override
    protected void onResume(){
    	super.onResume();
    	mTunnelFragment.refreshUI();
    	mSubsidenceFragment.refreshUI();
    }
    
    protected void setTopbarTitle(String title) {

        if (mTopbarTitle == null) {
            mTopbarTitle = (TextView)findViewById(R.id.tv_topbar_title);
        }

        mTopbarTitle.setText(title);
    }

    private void initImageView() {

        cursor = (ImageView)findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.heng).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 2 - bmpW) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        ViewGroup.LayoutParams lp = cursor.getLayoutParams();
        lp.width = screenW >> 1;
        lp.height = 4;
        cursor.setLayoutParams(lp);
        cursor.setImageMatrix(matrix);
    }

    private void initPager() {
        mPager = (ViewPager)findViewById(R.id.vPager);

        mFragmentList = new ArrayList<Fragment>();

        mTunnelFragment = new TunnelSectionSheetFragment();
        mSubsidenceFragment = new SubsidenceSectionSheetFragment();
        mFragmentList.add(mTunnelFragment);
        mFragmentList.add(mSubsidenceFragment);

        mPager.setAdapter(new UploadPagerAdapter(getSupportFragmentManager(), mFragmentList));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    private void initTab() {
        mTunnelTab = (TextView)findViewById(R.id.tunnel);
        mSubsidenceTab = (TextView)findViewById(R.id.sink);

        mTunnelTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(0);
            }
        });

        mSubsidenceTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(1);
            }
        });

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
        mUploadStatusText.setText(R.string.data_uploading);
        isUploading = true;
    }

    private void hideProgressOverlay() {
        mProgressOverlay.setVisibility(View.GONE);
    }

    private void updateStatus(boolean isSuccess, int code) {
        isUploading = false;
        mUploadStatusIcon.setVisibility(View.VISIBLE);
        mUploadProgress.setIndeterminate(false);
        mUploadProgress.setProgress(100);
        if (isSuccess) {
            mUploadStatusIcon.setImageResource(R.drawable.success);
            mUploadStatusText.setText(R.string.data_upload_success);
        } else {
            mUploadStatusIcon.setImageResource(R.drawable.fail);
            switch (code) {
			case AsyncUploadTask.CODE_NO_MEASURE_DATA:
				mUploadStatusText.setText(R.string.data_upload_fail_1);
				break;
			default:
				mUploadStatusText.setText(R.string.data_upload_fail);
				break;
			}
        }
    }

    class UploadPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mList;

        public UploadPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            mList = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mList.size();
        }

        @Override
        public Fragment getItem(int location) {
            // TODO Auto-generated method stub
            return mList.get(location);
        }

    }

    class MyOnPageChangeListener implements OnPageChangeListener {

        int one = offset * 2 + bmpW;

        int two = one * 2;

        @Override
        public void onPageSelected(int id) {

            Animation animation = null;

            switch (id) {
                case 0:
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, one, 0, 0);
                    }
                    break;

            }

            currIndex = id;
            animation.setFillAfter(true);
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    }

    class MenuPopupWindow extends PopupWindow {
        public RelativeLayout upload;
        private View mMenuView;
        private Intent intent;
        public Context c;
        AlertDialog dlg = null;

        public MenuPopupWindow(Activity context) {
            super(context);
            this.c = context;
            dlg = new AlertDialog.Builder(c).create();
            LayoutInflater inflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mMenuView = inflater.inflate(R.layout.menu_data_upload, null);
            upload = (RelativeLayout)mMenuView.findViewById(R.id.menu_upload);

            upload.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProjectIndex currentProject = ProjectIndexDao.defaultWorkPlanDao().queryEditWorkPlan();
                    if (currentProject != null) {
					    String siteCode = CrtbWebService.getInstance().getSiteCode();
					    if (siteCode == null || siteCode.trim() == "" ) {
						   Toast.makeText(getApplicationContext(), "请先关联工点", Toast.LENGTH_SHORT).show();
						} else {
                            switch (mPager.getCurrentItem()) {
                                // 隧道内断面
                                case 0:
                            	    uploadTunnelSheets();
                                    break;
                                case 1:
                            	    uploadSubsidenceSheets();
                                    break;
                                default:
                                    break;
                            }
						}
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
                menuWindow.showAtLocation(new View(this), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                this.finish();
            }
        }
        return true;
    }
    
	private void uploadTunnelSheets() {
		if (!mTunnelFragment.checkData()) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage(R.string.data_upload_promote_content);
			builder.setTitle(R.string.data_upload_promote_title);
			builder.setPositiveButton(R.string.data_upload_confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					doUploadTunnelSheets();
				}
			});
			builder.setNegativeButton(R.string.data_upload_deny, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					//Nothing need to do
				}
			});
			builder.create().show();
		} else {
			doUploadTunnelSheets();
		}
	}
    
	private void doUploadTunnelSheets() {
		List<RawSheetIndex> sheetRecords = mTunnelFragment.getUploadData();
		if (sheetRecords != null && sheetRecords.size() > 0) {
			TunnelAsyncQueryTask queryTask = new TunnelAsyncQueryTask(sheetRecords, new QueryLisenter() {

				@Override
				public void done(final List<SheetRecord> records) {
					if (records != null && records.size() > 0) {
						showProgressOverlay();
						AsyncUploadTask uploadTask = new TunnelAsyncUploadTask(new UploadListener() {
							@Override
							public void done(final boolean success, final int code) {
								AsyncUpdateTask updateTask = new AsyncUpdateTask(AsyncUpdateTask.TYPE_TUNNEL, records, new UpdateListener() {
									@Override
									public void done() {
										if (success) {
											mTunnelFragment.refreshUI();
										}
										updateStatus(success, code);
									}
								});
								updateTask.execute();
							}
						});
						uploadTask.execute(records);
					}
				}
			});
			queryTask.execute();
		} else {
			Toast.makeText(getApplicationContext(), "请先选择要上传的记录单", Toast.LENGTH_LONG).show();
		}
	}
	
    private void uploadSubsidenceSheets() {
    	if (!mSubsidenceFragment.checkData()) {
    		AlertDialog.Builder builder = new Builder(this);
			builder.setMessage(R.string.data_upload_promote_content);
			builder.setTitle(R.string.data_upload_promote_title);
			builder.setPositiveButton(R.string.data_upload_confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					doUploadSubsidenceSheets();
				}
			});
			builder.setNegativeButton(R.string.data_upload_deny, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					//Nothing need to do
				}
			});
			builder.create().show();
		} else {
			doUploadSubsidenceSheets();
		}
    }
    
    private void doUploadSubsidenceSheets() {
    	List<RawSheetIndex> sheetRecords = mSubsidenceFragment.getUploadData();
        if (sheetRecords != null && sheetRecords.size() > 0) {
        	SubsidenceAsyncQueryTask queryTask = new SubsidenceAsyncQueryTask(sheetRecords, new QueryLisenter() {
				
				@Override
				public void done(final List<SheetRecord> records) {
					if (records != null && records.size() > 0) {
			            showProgressOverlay();
			            AsyncUploadTask uploadTask  = new SubsidenceAsyncUploadTask(new UploadListener() {
			                @Override
			                public void done(final boolean success, final int code) {
			                	AsyncUpdateTask updateTask = new AsyncUpdateTask(AsyncUpdateTask.TYPE_SUBSIDENCE, records, new UpdateListener() {
									@Override
									public void done() {
										if (success) {
					                        mSubsidenceFragment.refreshUI();
					                    }
					                    updateStatus(success, code);
									}
								});
			                	updateTask.execute();
			                }
			            });
			            uploadTask.execute(records);
					}
				}
			});
        	queryTask.execute();
        } else {
            Toast.makeText(getApplicationContext(), "请先选择要上传的记录单", Toast.LENGTH_LONG).show();
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
