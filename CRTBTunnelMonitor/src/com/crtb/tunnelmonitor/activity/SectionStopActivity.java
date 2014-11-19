package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.task.AsyncStopTask;
import com.crtb.tunnelmonitor.task.AsyncStopTask.StopListener;
import com.crtb.tunnelmonitor.task.SectionStopEntity;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
import com.crtb.tunnelmonitor.widget.CrtbProgressOverlay;
import com.crtb.tunnelmonitor.widget.SubSectionStopFragment;
import com.crtb.tunnelmonitor.widget.TunnelSectionStopFragment;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SectionStopActivity extends FragmentActivity {
	private static final String LOG_TAG = "DataUploadActivity";
	private TextView mTopbarTitle;
	private ImageView cursor;
	private ViewPager mPager;
	private ArrayList<Fragment> mFragmentList;
	private TunnelSectionStopFragment mTunnelFragment;
	private SubSectionStopFragment mSubsidenceFragment;
	private TextView mTunnelTab;
	private TextView mSubsidenceTab;
	private int bmpW;
	private int offset = 0;
	private int currIndex = 0;
	private MenuPopupWindow menuWindow;
	private String title ;
	private CrtbProgressOverlay progressOverlay = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_section_stop);
		title = getString(R.string.section_stop);
		setTopbarTitle(title);
		initImageView();
		initPager();
		initTab();
		initProgressOverlay();
		initCurWorkBinding();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mTunnelFragment.refreshUI();
		mSubsidenceFragment.refreshUI();
	}

	protected void setTopbarTitle(String title) {

		if (mTopbarTitle == null) {
			mTopbarTitle = (TextView) findViewById(R.id.tv_topbar_title);
		}

		mTopbarTitle.setText(title);
	}

	private void initImageView() {

		cursor = (ImageView) findViewById(R.id.cursor);
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
		mPager = (ViewPager) findViewById(R.id.vPager);

		mFragmentList = new ArrayList<Fragment>();

		mTunnelFragment = new TunnelSectionStopFragment();
		mSubsidenceFragment = new SubSectionStopFragment();
		mFragmentList.add(mTunnelFragment);
		mFragmentList.add(mSubsidenceFragment);

		mPager.setAdapter(new UploadPagerAdapter(getSupportFragmentManager(), mFragmentList));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void initTab() {
		mTunnelTab = (TextView) findViewById(R.id.tunnel);
		mSubsidenceTab = (TextView) findViewById(R.id.sink);

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
		progressOverlay = new CrtbProgressOverlay(this,CrtbUtils.getProgressLayout(this));
	}

	private void showProgressOverlay() {
		progressOverlay.showProgressOverlay(getString(R.string.data_uploading_alert));
	}

	private void updateStatus(boolean isSuccess, String reason) {
		if (isSuccess) {
			progressOverlay.uploadFinish(true, "断面封存成功");
		} else {
			if (reason == null) {
				reason = "断面封存失败";
			}
			progressOverlay.uploadFinish(true, reason);
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

		public MenuPopupWindow(Activity context) {
			super(context);
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mMenuView = inflater.inflate(R.layout.menu_section_stop, null);
			upload = (RelativeLayout) mMenuView.findViewById(R.id.menu_upload);

			upload.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					uploadWrapper();
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

		private void uploadWrapper() {			
			ProjectIndex currentProject = ProjectIndexDao.defaultWorkPlanDao().queryEditWorkPlan();
			if (currentProject != null) {
				String siteCode = CrtbWebService.getInstance().getSiteCode();
				if (siteCode == null || siteCode.trim() == "") {
					Toast.makeText(getApplicationContext(), "请先关联工点", Toast.LENGTH_SHORT).show();
				} else {
					CrtbUtils.showDialogWithYesOrNo(SectionStopActivity.this,"断面封存","该断面封存后就不能再测量，是否继续？",new CrtbUtils.UploadCall(){

						@Override
						public void ok() {
							switch (mPager.getCurrentItem()) {
							// 隧道内断面
							case 0:
								doUploadTunnelSection();
								break;
							case 1:
								doUploadSubsidenceSection();
								break;
							default:
								break;
							}
						}});
				}
			} else {
				Toast.makeText(getApplicationContext(), "请先打开工作面", Toast.LENGTH_SHORT).show();
			}
			menuWindow.dismiss();
		}
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

	@SuppressWarnings("unchecked")
	private void doUploadTunnelSection() {
		TunnelCrossSectionIndex tunnel = mTunnelFragment.getChooseData();
		if (tunnel == null) {
			CrtbUtils.showDialogWithYes(this,title, "请先选择数据再操作");
			return;
		}
		
		if (!mTunnelFragment.getUploadState()) {
			CrtbUtils.showDialogWithYes(this,title, "该断面还未上传至工管中心，不能封存");
			return;
		}
		
		if (mTunnelFragment.getSectionStopState()) {
			CrtbUtils.showDialogWithYes(this,title, "该断面已经封存");
			return;
		}
		
		if(!mTunnelFragment.canStop()){
			CrtbUtils.showDialogWithYes(this,title, "最新记录单中正在使用该断面，不能进行封存");
			return;
		}
		showProgressOverlay();

		AsyncStopTask stopTask = new AsyncStopTask(new StopListener() {

			@Override
			public void done(boolean success, String reason) {
				if (success) {
					mTunnelFragment.refreshUI();
				}
				updateStatus(success, reason);
			}
		});

		SectionStopEntity sectionStop = new SectionStopEntity();
		sectionStop.tunnel = tunnel;
		TunnelCrossSectionExIndexDao handler = TunnelCrossSectionExIndexDao.defaultDao();
		sectionStop.sectionCode = handler.querySectionById(sectionStop.tunnel.getID()).getSECTCODE();
		stopTask.execute(sectionStop);
	}

	private void doUploadSubsidenceSection() {
		SubsidenceCrossSectionIndex sub = mSubsidenceFragment.getChooseData();
		if (sub == null) {
			CrtbUtils.showDialogWithYes(this,title, "请先选择数据再操作");
			return;
		}
		
		if (!mSubsidenceFragment.getUploadState()) {
			CrtbUtils.showDialogWithYes(this,title, "该断面还未上传至工管中心，不能封存");
			return;
		}
		
		if (mSubsidenceFragment.getSectionStopState()) {
			CrtbUtils.showDialogWithYes(this,title, "该断面已经封存");
			return;
		}
		
		if(!mSubsidenceFragment.canStop()){
			CrtbUtils.showDialogWithYes(this,title, "最新记录单中正在使用该断面，不能进行封存");
			return;
		}
		
		showProgressOverlay();
		AsyncStopTask stopTask = new AsyncStopTask(new StopListener() {

			@Override
			public void done(boolean success, String reason) {
				if (success) {
					mSubsidenceFragment.refreshUI();
				}
				updateStatus(success, reason);
			}
		});

		SectionStopEntity sectionStop = new SectionStopEntity();
		sectionStop.sub = sub;
		SubsidenceCrossSectionExIndexDao handler = SubsidenceCrossSectionExIndexDao.defaultDao();
		sectionStop.sectionCode = handler.querySectionById(sectionStop.sub.getID()).getSECTCODE();
		stopTask.execute(sectionStop);
	}

	private void initCurWorkBinding() {
		TextView curProject = (TextView) findViewById(R.id.cur_project_name);
		TextView curWork = (TextView) findViewById(R.id.cur_work_name);
		String[] list = CrtbUtils.getWorkSiteInfo();
		if (curWork != null && curProject != null && list != null && list.length == 2) {
			curProject.setText(list[0]);
			curWork.setText(list[1]);
		}
	}
}
