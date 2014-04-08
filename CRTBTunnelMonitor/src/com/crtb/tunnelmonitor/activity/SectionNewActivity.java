package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.activity.WorkNewActivity.MyOnClickListener;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.TunnelCrossSectionDaoImpl;
import com.crtb.tunnelmonitor.dao.impl.TunnelCrossSectionExDaoImpl;
import com.crtb.tunnelmonitor.dao.impl.WorkDaoImpl;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;
import com.crtb.tunnelmonitor.entity.WorkInfos;
import com.crtb.tunnelmonitor.utils.SearchWather;
import com.crtb.tunnelmonitor.utils.Time;
import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.activity.R.drawable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 新建断面
 * 
 */
public class SectionNewActivity extends Activity implements OnClickListener {
	private ViewPager mPager;// 页卡内容

	private List<View> listViews; // Tab页面列表

	private ImageView cursor;// 动画图片

	private TextView t1, t2;// 页卡头标

	private int offset = 0;// 动画图片偏移量

	private int currIndex = 0;// 当前页卡编号

	private int bmpW;// 动画图片宽度

	/***/
	private int num;
	// 控件名
	/***/
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

		setContentView(R.layout.activity_section_new);
		sChainage = getIntent().getExtras().getString(Constant.Select_SectionRowClickItemsName_Name);
		double dChainage = AppCRTBApplication.StrToDouble(sChainage, -1);
		InitImageView();
		InitTextView();
		InitViewPager();
		InitMyTextView();

		if (sChainage.length() > 0) {
			section_new_tv_header.setText("编辑隧道内断面");
			AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
			WorkInfos Curw = CurApp.GetCurWork();
			List<TunnelCrossSectionInfo> infos = Curw.GetTunnelCrossSectionInfoList();
			for(int i=0;i<infos.size();i++)
			{
				TunnelCrossSectionInfo tmp = infos.get(i);
				if(tmp.getChainage().equals(dChainage))
				{
					Edittsci = tmp;
					break;
				}
			}
		}

	}

	private void InitMyTextView() {

	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();

		listViews.add(mInflater.inflate(R.layout.section_new_xinxi, null));
		listViews.add(mInflater.inflate(R.layout.section_new_kaiwa, null));
		listViews.add(mInflater.inflate(R.layout.section_new_yuzhi, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	// 初始化头标
	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		section_new_tv_header = (TextView) findViewById(R.id.section_new_tv_header);
		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));

		section_btn_queding = (Button) findViewById(R.id.work_btn_queding);
		section_btn_quxiao = (Button) findViewById(R.id.work_btn_quxiao);

		section_btn_queding.setOnClickListener(this);
		section_btn_quxiao.setOnClickListener(this);
		
	}
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
		default:
			break;
		}

	}

	private void InitImageView() {

		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.heng)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置

	}

	/**
	 * ViewPager适配器
	 */
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
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);

			AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
			WorkInfos Curw = CurApp.GetCurWork();
			
			section_new_et_Chainage = (EditText) findViewById(R.id.section_new_et_Chainage);
			section_new_et_calendar = (EditText) findViewById(R.id.section_new_et_calendar);
			section_new_et_name = (EditText) findViewById(R.id.section_new_et_name);
			section_new_et_width = (EditText) findViewById(R.id.section_new_et_width);

			section_new_sp = (Spinner) findViewById(R.id.section_new_sp);
			img_fangfa = (ImageView) findViewById(R.id.img_fangfa);
			section_new_et_a = (EditText) findViewById(R.id.section_new_et_a);
			section_new_et_s1 = (EditText) findViewById(R.id.section_new_et_s1);
			section_new_et_s2 = (EditText) findViewById(R.id.section_new_et_s2);
			section_new_et_s3 = (EditText) findViewById(R.id.section_new_et_s3);
			
			section_new_leiji1 = (EditText) findViewById(R.id.section_new_leiji1);
			section_new_leiji2 = (EditText) findViewById(R.id.section_new_leiji2);
			section_new_single1 = (EditText) findViewById(R.id.section_new_single1);
			section_new_single2 = (EditText) findViewById(R.id.section_new_single2);
			section_new_createtime1 = (EditText) findViewById(R.id.section_new_createtime1);
			section_new_createtime2 = (EditText) findViewById(R.id.section_new_createtime2);
			section_new_info1 = (EditText) findViewById(R.id.section_new_info1);
			section_new_info2 = (EditText) findViewById(R.id.section_new_info2);
			
			section_v_s3 = (TextView)findViewById(R.id.section_v_s3);
			
			//section_new_info2.setVisibility(View.GONE);
			section_new_et_Chainage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					//if(!hasFocus)
					{
						String sChainage = section_new_et_Chainage.getText().toString().trim();
						if (sChainage.length() > 0) {
							AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
							section_new_et_name.setText(CurApp.GetSectionName(Double.valueOf(sChainage).doubleValue()));
						}
						else {
							section_new_et_name.setText("");
						}
					}
				}
			});
			List<String> testList = new ArrayList<String>();
			testList.add("台阶法");
			testList.add("全断面法");
			testList.add("双侧壁导坑法");
			section_new_sp = (Spinner) findViewById(R.id.section_new_sp);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					SectionNewActivity.this,
					android.R.layout.simple_spinner_item, testList);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (arg1 == 0) {
				// 获取手机的当前时间
				final String time = Time.getDateEN();
				// 设置文本框里面的字体大小
				section_new_et_calendar.setTextSize(11);
				if (Edittsci != null) {
					section_new_et_Chainage.setFocusable(false);
					section_new_et_Chainage.setFocusableInTouchMode(false);
					section_new_et_name.setFocusableInTouchMode(false);
					section_new_et_width.setFocusableInTouchMode(false);
					section_new_et_calendar.setFocusableInTouchMode(false);
					section_new_et_Chainage.setText(Edittsci.getChainage().toString());
					section_new_et_name.setText(CurApp.GetSectionName(Edittsci.getChainage().doubleValue()));
					section_new_et_calendar.setText(Edittsci.getInBuiltTime());
					section_new_et_width.setText(Float.toString(Edittsci.getWidth()));
				}
				else
				{
					// 跟改文本框赋值时间
					section_new_et_calendar.setText(time);
				}
			}
			else
			if(arg1 == 1)
			{
				section_new_sp.setAdapter(adapter);
				section_new_sp
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						if (Edittsci != null) {
							position = TunnelCrossSectionDaoImpl.GetExcavateMethodInt(Edittsci);
						}
						if (position == 0) {
							img_fangfa
									.setBackgroundResource(R.drawable.stepmethod);
							section_v_s3.setVisibility(View.GONE);
							section_new_et_s3.setVisibility(View.GONE);
							System.out.println(section_new_sp
									.getSelectedItem().toString());
						} else if (position == 1) {
							System.out.println(section_new_sp
									.getSelectedItem().toString());
							img_fangfa
									.setBackgroundResource(R.drawable.totalcrosssection);
							section_v_s3.setVisibility(View.GONE);
							section_new_et_s3.setVisibility(View.GONE);
						} else if (position == 2) {
							System.out.println(section_new_sp
									.getSelectedItem().toString());
							img_fangfa
									.setBackgroundResource(R.drawable.dualslopemethod);
							section_v_s3.setVisibility(View.VISIBLE);
							section_new_et_s3.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});
				if (Edittsci != null) {
					section_new_sp.setSelection(TunnelCrossSectionDaoImpl.GetExcavateMethodUi(Edittsci.getExcavateMethod()));
					section_new_sp.setSelected(false);
					List<String> strAS = AppCRTBApplication.GetExcavateMethodPointArray(Edittsci.getSurveyPntName());
					section_new_et_a.setText(strAS.get(0));
					section_new_et_s1.setText(strAS.get(1));
					section_new_et_s2.setText(strAS.get(2));
					section_new_et_s3.setText(strAS.get(3));
				}
			}
			else
			if (arg1 == 2) {
				
				section_new_leiji1.setText(Curw.getGDLimitTotalSettlement().toString());
				section_new_leiji2.setText(Curw.getSLLimitTotalSettlement().toString());
				section_new_single1.setText(Curw.getGDLimitVelocity().toString());
				section_new_single2.setText(Curw.getSLLimitVelocity().toString());
				section_new_createtime1.setText(section_new_et_calendar.getText().toString().trim());
				section_new_createtime2.setText(section_new_et_calendar.getText().toString().trim());

				if (Edittsci != null) {
					section_new_info1.setText(Edittsci.getInfo());
				}
//				et_a = (EditText) findViewById(R.id.section_new_et_a);
//				SearchWather s = new SearchWather(et_a);
			}
			

			return mListViews.get(arg1);
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

	/**
	 * 头标点击监听
	 */
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

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

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
