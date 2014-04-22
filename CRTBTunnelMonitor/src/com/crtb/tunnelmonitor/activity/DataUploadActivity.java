package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
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

    private int bmpW;

    private int offset = 0;

    private int currIndex = 0;

    private MenuPopupWindow menuWindow;

    private CounterListener mUploadListener = new CounterListener() {

        @Override
        public void done(final boolean success) {
            mProgressDialog.dismiss();
            if (success) {
                Toast.makeText(getApplicationContext(), "上传断面数据成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "上传断面数据失败", Toast.LENGTH_LONG).show();
            }
        }
    };

    private DataCounter mUploadCounter;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_upload);
        setTopbarTitle(getString(R.string.server_data_upload_title));
        initImageView();
        initPager();
        initTab();
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
                                int uploadSheetCount = 0;
                                if (sheetDataList != null || sheetDataList.size() > 0) {
                                    for(RawSheetData sheetData : sheetDataList) {
                                        if (sheetData.isChecked()) {
                                            uploadSheetCount++;
                                        }
                                    }
                                }
                                if (uploadSheetCount > 0) {
                                    uploadSectionList();
                                } else {
                                    Toast.makeText(getApplicationContext(), "请先选择要上传的记录单", Toast.LENGTH_LONG).show();
                                }
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
        TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
        List<TunnelCrossSectionIndex> sectionList = dao.queryUnUploadSections();
        if (sectionList != null && sectionList.size() > 0) {
            mProgressDialog = ProgressDialog.show(this, null, "正在上传数据", true, false);
            mUploadCounter = new DataCounter("SectionUpload", sectionList.size(), mUploadListener);
            for(TunnelCrossSectionIndex section : sectionList) {
                uploadSection(section);
            }
        }
    }

    //上传断面
    private void uploadSection(final TunnelCrossSectionIndex section) {
        SectionUploadParamter paramter = new SectionUploadParamter();
        CrtbUtils.fillSectionParamter(section, paramter);
        CrtbWebService.getInstance().uploadSection(paramter, new RpcCallback() {
            @Override
            public void onSuccess(Object[] data) {
                TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
                section.setInfo("2");
                dao.update(section);
                Log.d(LOG_TAG, "upload section success.");
                mUploadCounter.increase(true);
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "upload section faled: " + reason);
                mUploadCounter.increase(false);
            }
        });
    }

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
    private void uploadTestResult() {
        CrtbWebService.getInstance().uploadTestResult(null, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                CrtbWebService.getInstance().confirmSubmitData(new RpcCallback() {

                    @Override
                    public void onSuccess(Object[] data) {
                        Log.d(LOG_TAG, "upload test data success.");
                        showMessage(true);
                    }

                    @Override
                    public void onFailed(String reason) {
                        Log.d(LOG_TAG, "confirm test data failed: " + reason);
                        showMessage(false);
                    }
                });
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "upload test data failed.");
                showMessage(false);
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
}
