package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.TunnelCrossSectionDaoImpl;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;
import com.crtb.tunnelmonitor.entity.WorkInfos;

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
	
	@InjectView(layout=R.layout.section_new_xinxi)
	private LinearLayout mBaseInfoLayout ;
	
	@InjectView(layout=R.layout.section_new_kaiwa)
	private LinearLayout mExcavationInfoLayout ;
	
	@InjectView(layout=R.layout.section_new_yuzhi)
	private LinearLayout mDeformationInfoLayout ;

	private TextView section_new_tv_header;
	private EditText section_new_et_Chainage;
	private EditText section_new_et_calendar;
	private EditText section_new_et_name;
	private EditText section_new_et_width;

	private Spinner section_new_sp;
	private EditText section_new_et_a;
	private EditText section_new_et_s1;
	private EditText section_new_et_s2;
	private EditText section_new_et_s3;
	
	private EditText section_new_leiji1;
	private EditText section_new_leiji2;
	private EditText section_new_single1;
	private EditText section_new_single2;
	private EditText section_new_createtime1;
	private EditText section_new_createtime2;
	private EditText section_new_info1;
	private EditText section_new_info2;
	
	private TextView section_v_s3;
	
	private ImageView img_fangfa;
	private EditText et_a;
	/** 确定按钮 */
	private Button section_btn_queding;
	/** 取消按钮 */
	private Button section_btn_quxiao;
	private String sChainage = null;
	private TunnelCrossSectionInfo Edittsci = null;

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

//	// 初始化头标
//	private void InitTextView() {
//		t1 = (TextView) findViewById(R.id.text1);
//		t2 = (TextView) findViewById(R.id.text2);
////		section_new_tv_header = (TextView) findViewById(R.id.section_new_tv_header);
//		t1.setOnClickListener(new MyOnClickListener(0));
//		t2.setOnClickListener(new MyOnClickListener(1));
//
//		section_btn_queding = (Button) findViewById(R.id.work_btn_queding);
//		section_btn_quxiao = (Button) findViewById(R.id.work_btn_quxiao);
//
//		section_btn_queding.setOnClickListener(this);
//		section_btn_quxiao.setOnClickListener(this);
//		
//	}
	// 点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.work_btn_quxiao:
			Intent IntentCancel = new Intent();
			IntentCancel.putExtra(Constant.Select_SectionRowClickItemsName_Name,1);
			setResult(RESULT_CANCELED, IntentCancel);
			this.finish();// 关闭当前界面
			break;
		case R.id.work_btn_queding: // 数据库
			if(section_new_et_Chainage.getText().toString().trim().length() <= 0)
			{
				Toast.makeText(this, "请输入完整信息", 3000).show();
				return;
			}
			if(section_new_et_width.getText().toString().trim().length() <= 0)
			{
				Toast.makeText(this, "请输入完整信息", 3000).show();
				return;
			}

			AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
			WorkInfos Curw = CurApp.GetCurWork();
			TunnelCrossSectionInfo ts = new TunnelCrossSectionInfo();
			if (Edittsci != null) {
				ts.setId(Edittsci.getId());
			}
			ts.setChainage(Double.valueOf(section_new_et_Chainage.getText().toString().trim()));
			ts.setChainagePrefix(Curw.getChainagePrefix());
			ts.setInBuiltTime(section_new_et_calendar.getText().toString());
			ts.setWidth(Float.valueOf(section_new_et_width.getText().toString().trim()));
			if(section_new_sp.getSelectedItemPosition() == 0)
			{
				ts.setExcavateMethod(2);
			}
			else
				if(section_new_sp.getSelectedItemPosition() == 1)
			{
				ts.setExcavateMethod(0);
			}
			else
				if(section_new_sp.getSelectedItemPosition() == 2)
			{
				ts.setExcavateMethod(1);
			}
			else
			{
				ts.setExcavateMethod(0);
			}
			ts.setsExcavateMethod(section_new_sp.getSelectedItem().toString().trim());
			List<String> strAS = new ArrayList<String>();
			String strA = section_new_et_a.getText().toString().trim();
			String strS1 = section_new_et_s1.getText().toString().trim();
			String strS2 = section_new_et_s2.getText().toString().trim();
			String strS3 = section_new_et_s3.getText().toString().trim();
			strAS.add(strA);
			strAS.add(strS1);
			strAS.add(strS2);
			strAS.add(strS3);
			String sMix = AppCRTBApplication.GetExcavateMethodPoint(strAS);
			ts.setSurveyPntName(sMix);		
			if (section_new_info1 == null) {
				if (Edittsci == null) {
					ts.setInfo("");
				}
				else {
					ts.setInfo(Edittsci.getInfo());
				}
			}
			else {
				ts.setInfo(section_new_info1.getText().toString().trim());
			}
			ts.setChainageName(section_new_et_name.getText().toString().trim());
			if(!CurApp.IsValidTunnelCrossSectionInfo(ts))
			{
				Toast.makeText(this, "请输入完整信息", 3000).show();
				return;
			}
			if ((ts.getChainage().doubleValue() < Curw.getStartChainage().doubleValue()) ||
					(ts.getChainage().doubleValue() > Curw.getEndChainage().doubleValue())){
				String sStart = CurApp.GetSectionName(Curw.getStartChainage().doubleValue());
				String sEnd = CurApp.GetSectionName(Curw.getEndChainage().doubleValue());
				String sMsg = "请输入里程为"+sStart+"到"+sEnd+"之间的里程";
				Toast.makeText(this, sMsg, 3000).show();
				return;
			}
			List<TunnelCrossSectionInfo> infos = Curw.GetTunnelCrossSectionInfoList();
			if(infos == null)
			{
				Toast.makeText(this, "添加失败", 3000).show();
			}
			else
			{
				boolean bHave = false;
				for(int i=0;i<infos.size();i++)
				{
					TunnelCrossSectionInfo tmp = infos.get(i);
					if(tmp.getChainage().equals(ts.getChainage()))
					{
						bHave = true;
						break;
					}
				}
				if(bHave)
				{
					if(Edittsci == null)
					{
						Toast.makeText(this, "已存在", 3000).show();
						return;
					}
					else
					{
						TunnelCrossSectionDaoImpl impl = new TunnelCrossSectionDaoImpl(this,Curw.getProjectName());
						impl.UpdateSection(ts);
						Curw.UpdateTunnelCrossSectionInfo(ts);
						CurApp.UpdateWork(Curw);
						Toast.makeText(this, "编辑成功", 3000).show();
					}
				}
				else
				{
					TunnelCrossSectionDaoImpl impl = new TunnelCrossSectionDaoImpl(this,Curw.getProjectName());
					if(impl.InsertSection(ts))
					{
						infos.add(ts);
						CurApp.UpdateWork(Curw);
						Toast.makeText(this, "添加成功", 3000).show();
					}
					else
					{
						Toast.makeText(this, "添加失败", 3000).show();
					}
				}
			}
			Intent IntentOk = new Intent();
			IntentOk.putExtra(Constant.Select_SectionRowClickItemsName_Name,1);
			setResult(RESULT_OK, IntentOk);
			this.finish();
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
