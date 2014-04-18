package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

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

import com.crtb.tunnelmonitor.widget.SubsidenceCrossSectionFragment;
import com.crtb.tunnelmonitor.widget.TunnelCrossSectionFragment;import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.RpcCallback;
public class DataUploadActivity extends FragmentActivity {
	private static final String LOG_TAG = "DataUploadActivity";
    private TextView mTopbarTitle;

    private ImageView cursor;

    private ViewPager mPager;

    private ArrayList<Fragment> mFragmentList;

    private TunnelCrossSectionFragment mTunnelFragment;

    private SubsidenceCrossSectionFragment mSinkFragment;

    private TextView mTunnelTab;

    private TextView mSinkTab;

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

        mTunnelFragment = new TunnelCrossSectionFragment();
        mSinkFragment = new SubsidenceCrossSectionFragment();

        mFragmentList.add(mTunnelFragment);
        mFragmentList.add(mSinkFragment);

        mPager.setAdapter(new UploadPagerAdapter(getSupportFragmentManager(), mFragmentList));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    private void initTab() {
        mTunnelTab = (TextView)findViewById(R.id.tunnel);
        mSinkTab = (TextView)findViewById(R.id.sink);

        mTunnelTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(0);
            }
        });

        mSinkTab.setOnClickListener(new OnClickListener() {
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
                    switch (mPager.getCurrentItem()) {
                       //隧道内断面
                        case 0:
                        	uploadSection();
                        	break;
                        case 1:
                        	break;
                            // TODO:地标下沉断面
                        default:
                        	break;
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
    
    //上传断面
    private void uploadSection() {
    	CrtbWebService.getInstance().uploadSection(null, new RpcCallback() {
			
			@Override
			public void onSuccess(Object[] data) {
				Log.d(LOG_TAG, "upload section success.");
			}
			
			@Override
			public void onFailed(String reason) {
				Log.d(LOG_TAG, "upload section faled: " + reason);
			}
		});
    }
    
    //上传测量数据
    private void uploadTestResult() {
    	CrtbWebService.getInstance().uploadTestResult(null, new RpcCallback() {
			
			@Override
			public void onSuccess(Object[] data) {
				Log.d(LOG_TAG, "upload test data success.");
}
			
			@Override
			public void onFailed(String reason) {
				Log.d(LOG_TAG, "upload test data failed.");
			}
		});
    }
}
