package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;
import com.crtb.tunnelmonitor.entity.WorkPlan;
import com.crtb.tunnelmonitor.mydefine.CrtbDateDialogUtils;

/**
 * 新建隧道内断面
 * 
 */
@InjectLayout(layout = R.layout.activity_section_new)
public class SectionNewActivity extends WorkFlowActivity implements OnClickListener {
	
	private List<View> listViews = new ArrayList<View>();

	@InjectView(id=R.id.vPager)
	private ViewPager mPager;
	
	@InjectView(id=R.id.cursor)
	private ImageView cursor;

	private TextView t1, t2, t3;// 页卡头标

	private int offset = 0;// 动画图片偏移量

	private int currIndex = 0;// 当前页卡编号

	private int bmpW;// 动画图片宽度
	
	@InjectView(id=R.id.work_btn_queding,onClick="this")
	private Button section_btn_queding;
	
	@InjectView(id=R.id.work_btn_quxiao,onClick="this")
	private Button section_btn_quxiao;
	
	///////////////////////////////base info//////////////////////////////
	@InjectView(layout=R.layout.section_new_xinxi)
	private LinearLayout mBaseInfoLayout ;
	
	@InjectView(id=R.id.section_new_et_chainage_prefix,parent="mBaseInfoLayout")
	private EditText section_new_et_prefix;
	
	@InjectView(id=R.id.section_new_et_Chainage,parent="mBaseInfoLayout")
	private EditText section_new_et_Chainage;
	
	@InjectView(id=R.id.section_new_et_name,parent="mBaseInfoLayout")
	private EditText section_new_et_name;
	
	@InjectView(id=R.id.section_new_et_calendar,parent="mBaseInfoLayout",onClick="this")
	private EditText section_new_et_calendar;
	
	@InjectView(id=R.id.section_new_et_width,parent="mBaseInfoLayout")
	private EditText section_new_et_width;

	///////////////////////////////excavation info//////////////////////////////
	@InjectView(layout=R.layout.section_new_kaiwa)
	private LinearLayout mExcavationInfoLayout ;
	
	@InjectView(id=R.id.section_new_sp,parent="mExcavationInfoLayout",onClick="this")
	private Spinner section_new_sp;
	
	@InjectView(id=R.id.section_new_et_a,parent="mExcavationInfoLayout")
	private EditText section_new_et_a;
	
	@InjectView(id=R.id.section_new_et_s1,parent="mExcavationInfoLayout")
	private EditText section_new_et_s1;
	
	@InjectView(id=R.id.section_new_et_s2,parent="mExcavationInfoLayout")
	private EditText section_new_et_s2;
	
	@InjectView(id=R.id.section_new_et_s3_label,parent="mExcavationInfoLayout")
	private TextView section_new_et_s3_label;
	
	@InjectView(id=R.id.section_new_et_s3,parent="mExcavationInfoLayout")
	private EditText section_new_et_s3;
	
	////////////////////////////////Deformation info ///////////////////////////
	@InjectView(layout=R.layout.section_new_yuzhi)
	private LinearLayout mDeformationInfoLayout ;
	
	@InjectView(id=R.id.section_new_leiji_gd,parent="mDeformationInfoLayout")
	private EditText section_new_leiji_gd;
	
	@InjectView(id=R.id.section_new_leiji_sl,parent="mDeformationInfoLayout")
	private EditText section_new_leiji_sl;
	
	@InjectView(id=R.id.section_new_single_gd,parent="mDeformationInfoLayout")
	private EditText section_new_single_gd;
	
	@InjectView(id=R.id.section_new_single_sl,parent="mDeformationInfoLayout")
	private EditText section_new_single_sl;
	
	@InjectView(id=R.id.section_new_createtime_gd,parent="mDeformationInfoLayout",onClick="this")
	private EditText section_new_createtime1;
	
	@InjectView(id=R.id.section_new_createtime_sl,parent="mDeformationInfoLayout",onClick="this")
	private EditText section_new_createtime2;
	
	@InjectView(id=R.id.section_new_remark_gd,parent="mDeformationInfoLayout")
	private EditText section_new_remark_gd;
	
	@InjectView(id=R.id.section_new_remark_sl,parent="mDeformationInfoLayout")
	private EditText section_new_remark_sl;
	
	private TunnelCrossSectionInfo sectionInfo ;
	
	private WorkPlan mCurrentWorkPlan;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_section_new);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);

		// title
		setTopbarTitle(getString(R.string.section_new_title));
		
		// init ViewPager
		initViewPager();
		
		mCurrentWorkPlan = CommonObject.findObject(KEY_CURRENT_WORKPLAN);
		
		// prefix
		section_new_et_prefix.setText(mCurrentWorkPlan.getMileagePrefix());
		
		// spinner
		section_new_sp.setAdapter(ArrayAdapter.createFromResource(this, R.array.section_excavation,  
                android.R.layout.simple_list_item_single_choice)) ;
		section_new_sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		}) ;
		
		String date = DateUtils.toDateString(DateUtils.getCurrtentTimes(), DateUtils.DATE_TIME_FORMAT) ;
		section_new_et_calendar.setText(date);
		section_new_createtime1.setText(date);
		section_new_createtime2.setText(date);
	}

	private void initViewPager() {
		
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);
		
		listViews.add(mBaseInfoLayout);
		listViews.add(mExcavationInfoLayout);
		listViews.add(mDeformationInfoLayout);
		
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.heng)
				.getWidth();
		int screenW = mDisplayMetrics.widthPixels ;
		offset = (screenW / 3 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		ViewGroup.LayoutParams lp = cursor.getLayoutParams() ;
		lp.width = screenW / 3;
		lp.height = 4 ;
		cursor.setLayoutParams(lp);
		cursor.setImageMatrix(matrix);
		
		t1.setOnClickListener(new MyOnClickListener(TAB_ONE));
		t2.setOnClickListener(new MyOnClickListener(TAB_TWO));
		t3.setOnClickListener(new MyOnClickListener(TAB_THREE));
		
		mPager.setAdapter(new MyPagerAdapter());
		mPager.setCurrentItem(TAB_ONE);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.work_btn_quxiao:
			
			Intent IntentCancel = new Intent();
			setResult(RESULT_CANCELED, IntentCancel);
			this.finish();
			
			break;
		case R.id.section_new_et_calendar :
			CrtbDateDialogUtils.setAnyDateDialog(this, section_new_et_calendar, DateUtils.getCurrtentTimes());
			break ;
		case R.id.section_new_createtime_gd :
			CrtbDateDialogUtils.setAnyDateDialog(this, section_new_createtime1, DateUtils.getCurrtentTimes());
			break ;
		case R.id.section_new_createtime_sl :
			CrtbDateDialogUtils.setAnyDateDialog(this, section_new_createtime2, DateUtils.getCurrtentTimes());
			break ;
		case R.id.work_btn_queding: // 数据库
			
			// base
			String chainage 	= section_new_et_Chainage.getEditableText().toString().trim();// 里程
			String name 		= section_new_et_name.getEditableText().toString().trim();
			String date 		= section_new_et_calendar.getEditableText().toString().trim();
			String width 		= section_new_et_width.getEditableText().toString().trim();
			
			if(StringUtils.isEmpty(chainage)){
				showText("断面里程不能为空");
				return ;
			}
			
			//if(StringUtils.isEmpty(name)){
			//	showText("断面名称不能为空");
			//	return ;
			//}
			
			if(StringUtils.isEmpty(date)){
				showText("埋设时间不能为空");
				return ;
			}
			
			if(StringUtils.isEmpty(width)){
				showText("埋设时间不能为空");
				return ;
			}
			
			sectionInfo = new TunnelCrossSectionInfo() ;
			sectionInfo.setChainage(Float.valueOf(chainage));
			sectionInfo.setChainageName(name);
			sectionInfo.setInBuiltTime(date);
			sectionInfo.setWidth(Float.valueOf(width));
			
			TunnelCrossSectionDao.defaultDao().insert(sectionInfo);
			
//			if(section_new_et_Chainage.getText().toString().trim().length() <= 0)
//			{
//				Toast.makeText(this, "请输入完整信息", 3000).show();
//				return;
//			}
//			if(section_new_et_width.getText().toString().trim().length() <= 0)
//			{
//				Toast.makeText(this, "请输入完整信息", 3000).show();
//				return;
//			}
//
//			AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
//			WorkInfos Curw = CurApp.GetCurWork();
//			TunnelCrossSectionInfo ts = new TunnelCrossSectionInfo();
//			if (Edittsci != null) {
//				ts.setId(Edittsci.getId());
//			}
//			ts.setChainage(Double.valueOf(section_new_et_Chainage.getText().toString().trim()));
//			ts.setChainagePrefix(Curw.getChainagePrefix());
//			ts.setInBuiltTime(section_new_et_calendar.getText().toString());
//			ts.setWidth(Float.valueOf(section_new_et_width.getText().toString().trim()));
//			if(section_new_sp.getSelectedItemPosition() == 0)
//			{
//				ts.setExcavateMethod(2);
//			}
//			else
//				if(section_new_sp.getSelectedItemPosition() == 1)
//			{
//				ts.setExcavateMethod(0);
//			}
//			else
//				if(section_new_sp.getSelectedItemPosition() == 2)
//			{
//				ts.setExcavateMethod(1);
//			}
//			else
//			{
//				ts.setExcavateMethod(0);
//			}
//			ts.setsExcavateMethod(section_new_sp.getSelectedItem().toString().trim());
//			List<String> strAS = new ArrayList<String>();
//			String strA = section_new_et_a.getText().toString().trim();
//			String strS1 = section_new_et_s1.getText().toString().trim();
//			String strS2 = section_new_et_s2.getText().toString().trim();
//			String strS3 = section_new_et_s3.getText().toString().trim();
//			strAS.add(strA);
//			strAS.add(strS1);
//			strAS.add(strS2);
//			strAS.add(strS3);
//			String sMix = AppCRTBApplication.GetExcavateMethodPoint(strAS);
//			ts.setSurveyPntName(sMix);		
////			if (section_new_info1 == null) {
////				if (Edittsci == null) {
////					ts.setInfo("");
////				}
////				else {
////					ts.setInfo(Edittsci.getInfo());
////				}
////			}
////			else {
////				//ts.setInfo(section_new_info1.getText().toString().trim());
////			}
//			ts.setChainageName(section_new_et_name.getText().toString().trim());
//			if(!CurApp.IsValidTunnelCrossSectionInfo(ts))
//			{
//				Toast.makeText(this, "请输入完整信息", 3000).show();
//				return;
//			}
//			if ((ts.getChainage().doubleValue() < Curw.getStartChainage().doubleValue()) ||
//					(ts.getChainage().doubleValue() > Curw.getEndChainage().doubleValue())){
//				String sStart = CurApp.GetSectionName(Curw.getStartChainage().doubleValue());
//				String sEnd = CurApp.GetSectionName(Curw.getEndChainage().doubleValue());
//				String sMsg = "请输入里程为"+sStart+"到"+sEnd+"之间的里程";
//				Toast.makeText(this, sMsg, 3000).show();
//				return;
//			}
//			List<TunnelCrossSectionInfo> infos = Curw.GetTunnelCrossSectionInfoList();
//			if(infos == null)
//			{
//				Toast.makeText(this, "添加失败", 3000).show();
//			}
//			else
//			{
//				boolean bHave = false;
//				for(int i=0;i<infos.size();i++)
//				{
//					TunnelCrossSectionInfo tmp = infos.get(i);
//					if(tmp.getChainage().equals(ts.getChainage()))
//					{
//						bHave = true;
//						break;
//					}
//				}
//				if(bHave)
//				{
//					if(Edittsci == null)
//					{
//						Toast.makeText(this, "已存在", 3000).show();
//						return;
//					}
//					else
//					{
//						TunnelCrossSectionDaoImpl impl = new TunnelCrossSectionDaoImpl(this,Curw.getProjectName());
//						impl.UpdateSection(ts);
//						Curw.UpdateTunnelCrossSectionInfo(ts);
//						CurApp.UpdateWork(Curw);
//						Toast.makeText(this, "编辑成功", 3000).show();
//					}
//				}
//				else
//				{
//					TunnelCrossSectionDaoImpl impl = new TunnelCrossSectionDaoImpl(this,Curw.getProjectName());
//					if(impl.InsertSection(ts))
//					{
//						infos.add(ts);
//						CurApp.UpdateWork(Curw);
//						Toast.makeText(this, "添加成功", 3000).show();
//					}
//					else
//					{
//						Toast.makeText(this, "添加失败", 3000).show();
//					}
//				}
//			}
//			Intent IntentOk = new Intent();
//			IntentOk.putExtra(Constant.Select_SectionRowClickItemsName_Name,1);
//			setResult(RESULT_OK, IntentOk);
//			this.finish();
			break;
		}
	}

	public class MyPagerAdapter extends PagerAdapter {
		
		public MyPagerAdapter() {
			
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(listViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
			
		}

		@Override
		public int getCount() {
			return listViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(listViews.get(arg1), 0);
			return listViews.get(arg1);
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
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
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
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
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
