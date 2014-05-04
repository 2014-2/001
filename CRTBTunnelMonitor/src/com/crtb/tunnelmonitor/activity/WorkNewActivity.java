package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;
import org.zw.android.framework.util.DateUtils;
import org.zw.android.framework.util.StringUtils;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.mydefine.CrtbDateDialogUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

@InjectLayout(layout=R.layout.activity_work_new)
public class WorkNewActivity extends WorkFlowActivity implements OnClickListener {
	
	public static final String KEY_WORKPLAN_OBJECT		= "_key_workplan_object" ;
	
	@InjectView(id=R.id.vPager)
	private ViewPager mPager;
	
	@InjectView(id=R.id.cursor)
	private ImageView cursor;
	
	private TextView t1, t2;
	
	private int offset = 0;
	private int currIndex = 0;
	
	private int bmpW;
	
	@InjectView(layout=R.layout.work_new_msg)
	private FrameLayout tabOneView ;
	
	@InjectView(id=R.id.ed_work_new_name,parent="tabOneView")
	private EditText mWorkPlanName;
	
	@InjectView(id=R.id.ed_work_new_calendar,parent="tabOneView",onClick="this")
	private EditText mWorkPlanCalendar;
	
	@InjectView(id=R.id.ed_work_new_unit,parent="tabOneView")
	private EditText  mWorkPlanUnit;
	
	@InjectView(id=R.id.ed_work_new_prefix,parent="tabOneView")
	private EditText mWorkPlanPrefix;
	
	@InjectView(id=R.id.ed_work_new_start,parent="tabOneView")
	private EditText mWorkPlanStart;
	
	@InjectView(id=R.id.ed_work_new_end,parent="tabOneView")
	private EditText mWorkPlanEnd;
	
	/////////////////////////////////////////////
	
	@InjectView(layout=R.layout.work_new_variantvalue_layout)
	LinearLayout tabTwoView ;
	
	///////////////////////拱顶/////////////////////
	
	@InjectView(id=R.id.vault_trans_max,parent="tabTwoView")
	private EditText mVaultTransMax;
	
	@InjectView(id=R.id.vault_trans_velocity,parent="tabTwoView")
	private EditText mVaultTransVelocity;
	
	@InjectView(id=R.id.vault_trans_date,parent="tabTwoView",onClick="this")
	private EditText mVaultTransDate;
	
	@InjectView(id=R.id.vault_trans_remark,parent="tabTwoView")
	private EditText mVaultTransRemark;
	
	////////////////////////周边收敛/////////////////////////
	
	@InjectView(id=R.id.circum_astringe_max,parent="tabTwoView")
	private EditText mAstringeMax;
	
	@InjectView(id=R.id.circum_astringe_velocity,parent="tabTwoView")
	private EditText mAstringevelocity;
	
	@InjectView(id=R.id.circum_astringe_date,parent="tabTwoView",onClick="this")
	private EditText mAstringeDate;
	
	@InjectView(id=R.id.circum_astringe_remark,parent="tabTwoView")
	private EditText mAstringeRemark;
	
	/////////////////////////地表下沉/////////////////////////
	@InjectView(id=R.id.surface_sink_max,parent="tabTwoView")
	private EditText mSurfaceSinkMax ;
	
	private Button mBntConfirm;
	
	private Button mBntCancel;
	
	private List<View> listViews = new ArrayList<View>();
	private ProjectIndex mWorkPlanBean ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_work_new);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);
		
		setTopbarTitle(getString(R.string.work_plan_create_title));
		
		initView();
		
		InitImageView();
		
		InitViewPager();
		
		mWorkPlanBean = CommonObject.findObject(WorkNewActivity.KEY_WORKPLAN_OBJECT);
		
		if(mWorkPlanBean != null){
			
			setTopbarTitle("编辑工作面");
			
			loadDefaultData(mWorkPlanBean);
		} else {
			// default 
			String date = DateUtils.toDateString(DateUtils.getCurrtentTimes(), DateUtils.DATE_TIME_FORMAT) ;
			mWorkPlanCalendar.setText(date);
			mVaultTransDate.setText(date);
			mAstringeDate.setText(date);
		}
		
		TextWatcher watcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable edt) {
				
				String temp = edt.toString();
				
				int posDot = temp.indexOf(".");
				
				if(posDot >= 0){
					if (temp.length() - posDot - 1 > 4) {
						edt.delete(posDot + 5, posDot + 6);
					}
				}
			}
		};
		
		mWorkPlanStart.addTextChangedListener(watcher) ;
		mWorkPlanEnd.addTextChangedListener(watcher) ;
	}
	
	private void loadDefaultData(ProjectIndex bean){
		
		mWorkPlanName.setText(bean.getProjectName());
		mWorkPlanCalendar.setText(DateUtils.toDateString(bean.getCreateTime(),DateUtils.PART_TIME_FORMAT));
		mWorkPlanUnit.setText(bean.getConstructionFirm());
		mWorkPlanPrefix.setText(bean.getChainagePrefix());
		mWorkPlanStart.setText(String.valueOf(bean.getStartChainage()));
		mWorkPlanEnd.setText(String.valueOf(bean.getEndChainage()));
		
		// 拱顶
		mVaultTransMax.setText(String.valueOf(bean.getGDLimitTotalSettlement()));
		mVaultTransVelocity.setText(String.valueOf(bean.getGDLimitVelocity()));
		mVaultTransDate.setText(DateUtils.toDateString(bean.getGDCreateTime(),DateUtils.PART_TIME_FORMAT));
		mVaultTransRemark.setText(bean.getGDInfo());
		
		// 收敛
		mAstringeMax.setText(String.valueOf(bean.getSLLimitTotalSettlement()));
		mAstringevelocity.setText(String.valueOf(bean.getSLLimitVelocity()));
		mAstringeDate.setText(DateUtils.toDateString(bean.getSLCreateTime(),DateUtils.PART_TIME_FORMAT));
		mAstringeRemark.setText(bean.getSLInfo());
		
		// 地表
		mSurfaceSinkMax.setText(String.valueOf(bean.getDBLimitTotalSettlement()));
	}

	private void InitViewPager() {
		
		listViews.add(tabOneView);
		listViews.add(tabTwoView);
		
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void initView() {
		
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		mBntConfirm = (Button) findViewById(R.id.work_btn_queding);
		mBntCancel = (Button) findViewById(R.id.work_btn_quxiao);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		mBntConfirm.setOnClickListener(this);
		mBntCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		Date curdate = null ;
		
		switch (v.getId()) {
		case R.id.work_btn_quxiao:
			Intent IntentCancel = new Intent();
			setResult(RESULT_CANCELED, IntentCancel);
			this.finish();
			break;
		case R.id.work_btn_queding:
			
			// base
			String name 		= mWorkPlanName.getEditableText().toString().trim() ;
			String date 		= mWorkPlanCalendar.getEditableText().toString().trim() ;
			String unit 		= mWorkPlanUnit.getEditableText().toString().trim() ;
			String pref 		= mWorkPlanPrefix.getEditableText().toString().trim() ;
			String start 		= mWorkPlanStart.getEditableText().toString().trim() ;
			String end 			= mWorkPlanEnd.getEditableText().toString().trim() ;
			
			if(StringUtils.isEmpty(name)){
				showText("工作面名称不能为空");
				return ;
			}
			
			if(StringUtils.isEmpty(date)){
				showText("日期不能为空");
				return ;
			}
			
			if(StringUtils.isEmpty(unit)){
				showText("施工单位不能为空");
				return ;
			}
			
			if(StringUtils.isEmpty(pref)){
				showText("前缀不能为空");
				return ;
			}
			
			if(StringUtils.isEmpty(start)){
				showText("开始里程不能为空");
				return ;
			}
			
			if(StringUtils.isEmpty(end)){
				showText("开始里程不能为空");
				return ;
			}
			
			double sm	= CrtbUtils.formatDouble(start);
			double em	= CrtbUtils.formatDouble(end);
			
			if(em <= sm){
				showText("结束里程必须大于开始里程");
				return ;
			}
			
			// 拱顶
			String vaultMax 	= mVaultTransMax.getEditableText().toString().trim() ;
			String vaultVel 	= mVaultTransVelocity.getEditableText().toString().trim() ;
			String vaultDate 	= mVaultTransDate.getEditableText().toString().trim() ;
			String vaultRemark 	= mVaultTransRemark.getEditableText().toString().trim() ;
			
			if(StringUtils.isEmpty(vaultMax)){
				showText("拱顶累计变形极限值不能为空");
				return ;
			}
			
			if(StringUtils.isEmpty(vaultVel)){
				showText("拱顶变形速率极限值不能为空");
				return ;
			}
			
			// 收敛
			String circumMax 	= mAstringeMax.getEditableText().toString().trim() ;
			String circumVel 	= mAstringevelocity.getEditableText().toString().trim() ;
			String circumDate 	= mAstringeDate.getEditableText().toString().trim() ;
			String circumRemark = mAstringeRemark.getEditableText().toString().trim() ;
			
			if(StringUtils.isEmpty(circumMax)){
				showText("周边累计收敛极限值不能为空");
				return ;
			}
			
			if(StringUtils.isEmpty(circumVel)){
				showText("周边收敛速率极限值不能为空");
				return ;
			}
			
			// surface
			String surfaceMax	= mSurfaceSinkMax.getEditableText().toString().trim() ;
			
			if(StringUtils.isEmpty(circumVel)){
				showText("地表累计收敛极限值不能为空");
				return ;
			}
			
			float gdlimt= 0f ,gdv = 0f,sllimt= 0f ,sldv = 0f,dblimt = 0f,dbv = 0f ;
			
			try{
				
				gdlimt	= Float.valueOf(vaultMax);
				gdv		= Float.valueOf(vaultVel);
				sllimt	= Float.valueOf(circumMax);
				sldv	= Float.valueOf(circumVel);
				dblimt	= Float.valueOf(surfaceMax);
				
			} catch(Exception e){
				e.printStackTrace() ;
			}
			
			if(mWorkPlanBean != null){
				
				mWorkPlanBean.setProjectName(name);
				mWorkPlanBean.setCreateTime(DateUtils.toDate(date));
				mWorkPlanBean.setConstructionFirm(unit);
				mWorkPlanBean.setChainagePrefix(pref);
				mWorkPlanBean.setStartChainage(sm);
				mWorkPlanBean.setEndChainage(em);
				
				mWorkPlanBean.setGDLimitTotalSettlement(gdlimt);
				mWorkPlanBean.setGDLimitVelocity(gdv);
				mWorkPlanBean.setGDCreateTime(DateUtils.toDate(vaultDate, DateUtils.PART_TIME_FORMAT));
				mWorkPlanBean.setGDInfo(vaultRemark);
				
				mWorkPlanBean.setSLLimitTotalSettlement(sllimt);
				mWorkPlanBean.setSLLimitVelocity(sldv);
				mWorkPlanBean.setSLCreateTime(DateUtils.toDate(circumDate, DateUtils.PART_TIME_FORMAT));
				mWorkPlanBean.setSLInfo(circumRemark);
				
				mWorkPlanBean.setDBLimitTotalSettlement(dblimt);
				
				if(ProjectIndexDao.defaultWorkPlanDao().update(mWorkPlanBean) == ProjectIndexDao.DB_EXECUTE_FAILED){
					showText("更新工作面失败");
					return ;
				}
			} else {
				
				ProjectIndex info = new ProjectIndex() ;
				info.setProjectName(name);
				info.setCreateTime(DateUtils.toDate(date));
				info.setConstructionFirm(unit);
				info.setChainagePrefix(pref);
				info.setStartChainage(sm);
				info.setEndChainage(em);
				
				// 第一次初始化数据库名称
				if(StringUtils.isEmpty(info.getDbName())){
					info.setDbName(ProjectIndexDao.getDbUniqueName(name)) ;
				}
				
				info.setGDLimitTotalSettlement(gdlimt);
				info.setGDLimitVelocity(gdv);
				info.setGDCreateTime(DateUtils.toDate(vaultDate, DateUtils.PART_TIME_FORMAT));
				info.setGDInfo(vaultRemark);
				
				info.setSLLimitTotalSettlement(sllimt);
				info.setSLLimitVelocity(sldv);
				info.setSLCreateTime(DateUtils.toDate(circumDate, DateUtils.PART_TIME_FORMAT));
				info.setSLInfo(circumRemark);
				
				info.setDBLimitTotalSettlement(dblimt);
				
				int code = ProjectIndexDao.defaultWorkPlanDao().insert(info) ;
				
				// 保存数据
				if(code == 100){
					showText("非注册用户，只能创建2个工作面");
					return ;
				} else if(code == ProjectIndexDao.DB_EXECUTE_FAILED){
					showText("保存失败");
					return ;
				}
			}
			
			setResult(RESULT_CANCELED);
			finish();
			
			break;
		case R.id.ed_work_new_calendar :
			
			curdate = DateUtils.toDate(mWorkPlanCalendar.getEditableText().toString().trim(), DateUtils.PART_TIME_FORMAT);
			
			if(curdate == null){
				curdate	= DateUtils.getCurrtentTimes() ;
			}
			
			CrtbDateDialogUtils.setAnyDateDialog(this, mWorkPlanCalendar, curdate);
			
			break ;
		case R.id.vault_trans_date :
			
			curdate = DateUtils.toDate(mVaultTransDate.getEditableText().toString().trim(), DateUtils.PART_TIME_FORMAT);
			
			if(curdate == null){
				curdate	= DateUtils.getCurrtentTimes() ;
			}
			
			CrtbDateDialogUtils.setAnyDateDialog(this, mVaultTransDate, curdate);
			
			break;
			
		case R.id.circum_astringe_date :
			
			curdate = DateUtils.toDate(mAstringeDate.getEditableText().toString().trim(), DateUtils.PART_TIME_FORMAT);
			
			if(curdate == null){
				curdate	= DateUtils.getCurrtentTimes() ;
			}
			
			CrtbDateDialogUtils.setAnyDateDialog(this, mAstringeDate, curdate);
			
			break;
		}

	}

	private void InitImageView() {
		
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.heng)
				.getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 2 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		ViewGroup.LayoutParams lp = cursor.getLayoutParams() ;
		lp.width = screenW >> 1 ;
		lp.height = 4 ;
		cursor.setLayoutParams(lp);
		cursor.setImageMatrix(matrix);
	}

	public class MyPagerAdapter extends PagerAdapter {
		
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int index) {
			((ViewPager) arg0).addView(mListViews.get(index));
			return mListViews.get(index);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			
		}
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {

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
}
