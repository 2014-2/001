package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ICT.utils.RSACoder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.PointUploadParameter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.SectionUploadParamter;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
import com.crtb.tunnelmonitor.widget.SectionSheetFragment;
import com.crtb.tunnelmonitor.widget.SectionSheetFragment.RawSheetData;
public class DataUploadActivity extends FragmentActivity {
    private static final String LOG_TAG = "DataUploadActivity";
    private TextView mTopbarTitle;

    private ImageView cursor;

    private ViewPager mPager;

    private ArrayList<Fragment> mFragmentList;

    private SectionSheetFragment mTunnelFragment;

    private SectionSheetFragment mSubsidenceFragment;

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

    private List<RawSheetData> mSelectedSheets;
    private DataCounter mSheetUploadCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_upload);
        setTopbarTitle(getString(R.string.server_data_upload_title));
        initImageView();
        initPager();
        initTab();
        initProgressOverlay();
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

        mTunnelFragment = new SectionSheetFragment(SectionSheetFragment.TUNNEL_CROSS);
        mSubsidenceFragment = new SectionSheetFragment(
                SectionSheetFragment.SUBSIDENCE_CROSS);

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

    private void uploadStatus(boolean isSuccess) {
        isUploading = false;
        mUploadStatusIcon.setVisibility(View.VISIBLE);
        if (isSuccess) {
            mUploadStatusIcon.setImageResource(R.drawable.success);
            mUploadStatusText.setText(R.string.data_upload_success);
        } else {
            mUploadStatusIcon.setImageResource(R.drawable.fail);
            mUploadStatusText.setText(R.string.data_upload_fail);
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
                        switch (mPager.getCurrentItem()) {
                            // 隧道内断面
                            case 0:
                                List<RawSheetData> sheetDataList = mTunnelFragment.getSheets();
                                mSelectedSheets = new ArrayList<RawSheetData>();
                                if (sheetDataList != null || sheetDataList.size() > 0) {
                                    for(RawSheetData sheetData : sheetDataList) {
                                        if (sheetData.isChecked()) {
                                            mSelectedSheets.add(sheetData);
                                        }
                                    }
                                }
                                uploadSectionList();
                                break;
                            case 1:
                                break;
                                // TODO:地标下沉断面
                            default:
                                break;
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请先打开工作面",
                                Toast.LENGTH_SHORT).show();
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

    //上传所有断面数据
    private void uploadSectionList() {
        if (mSelectedSheets == null || mSelectedSheets.size() == 0) {
        	Toast.makeText(getApplicationContext(), "请先选择要上传的记录单", Toast.LENGTH_LONG).show();
        	return ;
        } 
        showProgressOverlay();
        //mSectionUploadCounter = new DataCounter("SectionUpload", sectionList.size(), mSectionUploadListener);
        mSheetUploadCounter = new DataCounter("SheetUploadCounter", mSelectedSheets.size(), new CounterListener() {
			@Override
			public void done(boolean success) {
				if (success) {
	                mTunnelFragment.refreshUI();
	            }
	            uploadStatus(success);
			}
		});
        int uploadSectionCount = 0;
        for(RawSheetData sheetData: mSelectedSheets) {
        	List<TunnelCrossSectionIndex> sectionList = getUnUploadTunnelSections(sheetData.getSectionIds());
        	if (sectionList != null && sectionList.size() > 0) {
        		uploadSectionCount += sectionList.size();
	        	DataCounter sectionUploadCounter = new DataCounter("SectionUploadCounter", sectionList.size(), new CounterListener() {
					@Override
					public void done(boolean success) {
						mSheetUploadCounter.increase(success);
					}
				});
	        	for(TunnelCrossSectionIndex section : sectionList) {
	        		uploadSection(section, sheetData.getRowId(), sectionUploadCounter);
	        	}
        	}
        }
        if (uploadSectionCount == 0) {
        	for(int i =0 ; i < mSelectedSheets.size(); i++) {
        		mSheetUploadCounter.increase(false);
        	}
        }
    }

    //上传断面
    private void uploadSection(final TunnelCrossSectionIndex section, final int sheetRowId, final DataCounter sectionUploadCounter) {
        SectionUploadParamter paramter = new SectionUploadParamter();
        CrtbUtils.fillSectionParamter(section, paramter);
        CrtbWebService.getInstance().uploadSection(paramter, new RpcCallback() {
            @Override
            public void onSuccess(Object[] data) {
                TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
                section.setInfo("2");
                dao.update(section);
                DataCounter pointUploadCounter = new DataCounter("PointUploadCounter", 1, new CounterListener() {
					@Override
					public void done(boolean success) {
						sectionUploadCounter.increase(success);
					}
				});
                //查询测量点数据
                TunnelSettlementTotalDataDao  pointDao = TunnelSettlementTotalDataDao.defaultDao();
                TunnelSettlementTotalData point1 = pointDao.queryTunnelTotalData(sheetRowId, section.getID(), "A");
                TunnelSettlementTotalData point2 = pointDao.queryTunnelTotalData(sheetRowId, section.getID(), "S1-1");
                TunnelSettlementTotalData point3 = pointDao.queryTunnelTotalData(sheetRowId, section.getID(), "S1-2");
                Log.d(LOG_TAG, "upload section success: id = " + section.getID() + ", code = " + data[0]);
                final String sectionCode = (String) data[0];
                uploadTestResult(sectionCode, point1, point2, point3, pointUploadCounter);
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "upload section faled: " + reason);
                sectionUploadCounter.increase(false);
            }
        });
    }

    /**
     * 上传测量点数据
     * 
     * @param sheetDataList
     */
//    private void uploadPointList() {
//    	if (mSelectedSheets == null || mSelectedSheets.size() == 0) {
//        	Toast.makeText(getApplicationContext(), "请先选择要上传的记录单", Toast.LENGTH_LONG).show();
//        	return ;
//        }
//    	List<TunnelSettlementTotalData> pointList = new ArrayList<TunnelSettlementTotalData>();
//    	TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao();
//    	for(RawSheetData sheetData : mSelectedSheets) {
//    		pointList.addAll(dao.queryUnUploadTunnelTotalDataBySheet(sheetData.getRowId()));
//    	}
//    	if (pointList.size() > 0) {
//    		mPointUploadCounter = new DataCounter("PointUploadCounter", pointList.size(), mPointUploadListener);
//    		for(TunnelSettlementTotalData pointData : pointList) {
//    			uploadTestResult();
//    		}
//    	}
//    }
    
    private void updateSection() {
        CrtbWebService.getInstance().updateSection(null, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                Log.d(LOG_TAG, "update section status success.");
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "update section status failed.");
            }
        });
    }

    //上传测量数据
    private void uploadTestResult(String sectionCode, final TunnelSettlementTotalData point1, final TunnelSettlementTotalData point2, final TunnelSettlementTotalData point3, final DataCounter pointUploadCounter) {
        PointUploadParameter parameter = new PointUploadParameter();
        parameter.setSectionCode(sectionCode);
//		String pointCodeList = sectionCode + "GD01" + "/" + sectionCode
//				+ "SL01" + "#" + sectionCode + "SL02" + "/" + sectionCode
//				+ "SL03" + "#" + sectionCode + "SL04";
		String pointCodeList = sectionCode + "GD01" + "/" + sectionCode
				+ "SL01" + "#" + sectionCode + "SL02";
		parameter.setPointCodeList(pointCodeList);
		parameter.setTunnelFaceDistance(50.0f);
		parameter.setProcedure("02");
		parameter.setMonitorModel("xxx");
		parameter.setMeasureDate(new Date());
		String valueList = "50/141.4249";
		parameter.setPointValueList(valueList);
		String A = point1.getCoordinate().replace(",", "#");
		String S1_1 = point2.getCoordinate().replace(",", "#");
		String S1_2 = point3.getCoordinate().replace(",", "#");
		String coordinate = A + "/" + S1_1 + "#" + S1_2; //"50#50#50/100#200#300#200#300#301/100#200#300#200#300#301";
		String encncyptCoordinate = RSACoder.encnryptDes(coordinate, Constant.testDeskey);
		parameter.setPointCoordinateList(encncyptCoordinate);
		parameter.setSurveyorName("杨工");
		parameter.setSurveyorId("111");
		parameter.setRemark("yyy");
    	CrtbWebService.getInstance().uploadTestResult(parameter, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                CrtbWebService.getInstance().confirmSubmitData(new RpcCallback() {

                    @Override
                    public void onSuccess(Object[] data) {
                    	TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao();
                    	point1.setInfo("2");
                    	point2.setInfo("2");
                    	point3.setInfo("2");
                    	dao.update(point1);
                    	dao.update(point2);
                    	dao.update(point3);
                        Log.d(LOG_TAG, "upload test data success.");
                        pointUploadCounter.increase(true);
                    }

                    @Override
                    public void onFailed(String reason) {
                        Log.d(LOG_TAG, "confirm test data failed: " + reason);
                        pointUploadCounter.increase(false);
                    }
                });
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "upload test data failed.");
                pointUploadCounter.increase(false);
            }
        });
    }

    private void showMessage(boolean success) {
        if (success) {
            Toast.makeText(getApplicationContext(), "上传数据成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "上传数据失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 获取未上传隧道内断面列表
     * 
     * @param sectionRowIds 断面数据库ids
     * @return
     */
    public List<TunnelCrossSectionIndex> getUnUploadTunnelSections(String sectionRowIds) {
        TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
        List<TunnelCrossSectionIndex> unUploadSectionList = new ArrayList<TunnelCrossSectionIndex>();
        List<TunnelCrossSectionIndex> sectionList = dao.querySectionByIds(sectionRowIds);
        if (sectionList != null && sectionList.size() > 0) {
            for (TunnelCrossSectionIndex section : sectionList) {
                // 1表示未上传, 2表示已上传
                if ("1".equals(section.getInfo())) {
                    unUploadSectionList.add(section);
                }
            }
        }
        return unUploadSectionList;
    }
}
