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
import android.text.Editable;
import android.text.TextWatcher;
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
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.mydefine.CrtbDateDialogUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 新建隧道内断面
 * 
 */
@InjectLayout(layout = R.layout.activity_section_new)
public class SectionNewActivity extends WorkFlowActivity implements OnClickListener {
	
	public static final String KEY_NEW_TUNNEL_SECTION_OBJECT	= "_key_new_tunnel_section_object" ;
	
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
	
	@InjectView(id=R.id.img_fangfa,parent="mExcavationInfoLayout")
	private ImageView section_method ;
	
	@InjectView(id=R.id.section_new_sp,parent="mExcavationInfoLayout",onClick="this")
	private Spinner section_new_sp;
	
	@InjectView(id=R.id.section_new_et_a,parent="mExcavationInfoLayout")
	private EditText section_new_et_a;
	
	@InjectView(id=R.id.section_new_et_s1,parent="mExcavationInfoLayout")
	private EditText section_new_et_s1;
	
	@InjectView(id=R.id.section_new_et_s2_label,parent="mExcavationInfoLayout")
	private TextView section_new_et_s2_label;
	
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
	
	private TunnelCrossSectionIndex sectionInfo ;
	
	private ProjectIndex mCurrentWorkPlan;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_section_new);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);

		// title
		setTopbarTitle(getString(R.string.section_new_title));
		
		sectionInfo	= CommonObject.findObject(KEY_NEW_TUNNEL_SECTION_OBJECT);
		
		// init ViewPager
		initViewPager();
		
		mCurrentWorkPlan = CommonObject.findObject(KEY_CURRENT_WORKPLAN);
		
		// prefix
		section_new_et_prefix.setText(mCurrentWorkPlan.getChainagePrefix());
		
		// spinner
		section_new_sp.setAdapter(ArrayAdapter.createFromResource(this, R.array.section_excavation,  
                android.R.layout.simple_list_item_single_choice)) ;
		section_new_sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(position == 0){
					section_new_et_s2_label.setVisibility(View.INVISIBLE);
					section_new_et_s2.setVisibility(View.INVISIBLE);
					section_new_et_s3_label.setVisibility(View.INVISIBLE);
					section_new_et_s3.setVisibility(View.INVISIBLE);
					section_method.setBackgroundResource(R.drawable.ic_full_face);
				} else if(position == 1){
					section_new_et_s2_label.setVisibility(View.VISIBLE);
					section_new_et_s2.setVisibility(View.VISIBLE);
					section_new_et_s3_label.setVisibility(View.INVISIBLE);
					section_new_et_s3.setVisibility(View.INVISIBLE);
					section_method.setBackgroundResource(R.drawable.ic_step_method);
				} else if(position == 2){
					section_new_et_s2_label.setVisibility(View.VISIBLE);
					section_new_et_s2.setVisibility(View.VISIBLE);
					section_new_et_s3_label.setVisibility(View.VISIBLE);
					section_new_et_s3.setVisibility(View.VISIBLE);
					section_method.setBackgroundResource(R.drawable.ic_three_step);
				} else if(position == 3){
					section_new_et_s2_label.setVisibility(View.VISIBLE);
					section_new_et_s2.setVisibility(View.VISIBLE);
					section_new_et_s3_label.setVisibility(View.VISIBLE);
					section_new_et_s3.setVisibility(View.VISIBLE);
					section_method.setBackgroundResource(R.drawable.ic_dual_slope_method);
				} 
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		}) ;
		
		String date = DateUtils.toDateString(DateUtils.getCurrtentTimes(), DateUtils.DATE_TIME_FORMAT) ;
		section_new_et_calendar.setText(date);
		section_new_createtime1.setText(date);
		section_new_createtime2.setText(date);
		
		section_new_et_Chainage.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable edt) {
				
				String temp = edt.toString();
				
				if(!StringUtils.isEmpty(temp)){
					float f 	= Float.valueOf(temp);
					String pre 	= section_new_et_prefix.getEditableText().toString().trim();
					section_new_et_name.setText(CrtbUtils.formatSectionName(pre,f));
				} else {
					section_new_et_name.setText("");
				}
			}
		}) ;
		
		loadDefault();
	}
	
	private void loadDefault(){
		
		if(sectionInfo != null){
			
			setTopbarTitle("编辑隧道内断面");
			
			section_new_et_prefix.setText(sectionInfo.getChainagePrefix());
			section_new_et_Chainage.setText(String.valueOf(sectionInfo.getChainage()));
			section_new_et_name.setText(sectionInfo.getSectionName());
			section_new_et_calendar.setText(DateUtils.toDateString(sectionInfo.getInBuiltTime()));
			section_new_et_width.setText(String.valueOf(sectionInfo.getWidth()));
			
			section_new_sp.setSelection(CrtbUtils.getExcavateMethod(sectionInfo.getExcavateMethod()));
			
			section_new_et_a.setText(sectionInfo.getPointAName());
			section_new_et_s1.setText(sectionInfo.getPointS1Name());
			section_new_et_s2.setText(sectionInfo.getPointS2Name());
			section_new_et_s3.setText(sectionInfo.getPointS3Name());
			
			section_new_leiji_gd.setText(String.valueOf(sectionInfo.getGDU0()));
			section_new_leiji_sl.setText(String.valueOf(sectionInfo.getSLU0()));
			section_new_single_gd.setText(String.valueOf(sectionInfo.getGDVelocity()));
			section_new_single_sl.setText(String.valueOf(sectionInfo.getSLLimitVelocity()));
			
			section_new_createtime1.setText(DateUtils.toDateString(sectionInfo.getGDU0Time()));
			section_new_createtime2.setText(DateUtils.toDateString(sectionInfo.getSLU0Time()));
			
			section_new_remark_gd.setText(sectionInfo.getGDU0Description());
			section_new_remark_sl.setText(sectionInfo.getSLU0Description());
		}
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
			//CrtbDateDialogUtils.setAnyDateDialog(this, section_new_et_calendar, DateUtils.getCurrtentTimes());
			break ;
		case R.id.section_new_createtime_gd :
			//CrtbDateDialogUtils.setAnyDateDialog(this, section_new_createtime1, DateUtils.getCurrtentTimes());
			break ;
		case R.id.section_new_createtime_sl :
			CrtbDateDialogUtils.setAnyDateDialog(this, section_new_createtime2, DateUtils.getCurrtentTimes());
			break ;
		case R.id.work_btn_queding: // 数据库
			
			// base
			String prefix		= section_new_et_prefix.getEditableText().toString().trim() ;
			String chainage 	= section_new_et_Chainage.getEditableText().toString().trim();// 里程
			String name 		= section_new_et_name.getEditableText().toString().trim();
			String date 		= section_new_et_calendar.getEditableText().toString().trim();
			String width 		= section_new_et_width.getEditableText().toString().trim();
			
			if(StringUtils.isEmpty(chainage)){
				showText("断面里程不能为空");
				return ;
			}
			
			if(StringUtils.isEmpty(date)){
				showText("埋设时间不能为空");
				return ;
			}
			
			if(StringUtils.isEmpty(width)){
				showText("断面宽度必须大于0");
				return ;
			}
			
			String pointA	= section_new_et_a.getText().toString().trim() ;
			String pointS1	= section_new_et_s1.getText().toString().trim() ;
			String pointS2	= section_new_et_s2.getText().toString().trim() ;
			String pointS3	= section_new_et_s3.getText().toString().trim() ;
			
			String gdlj	= section_new_leiji_gd.getEditableText().toString().trim() ;
			String zblj	= section_new_leiji_sl.getEditableText().toString().trim() ;
			String gdsl	= section_new_single_gd.getEditableText().toString().trim() ;
			String zbsl	= section_new_single_sl.getEditableText().toString().trim() ;
			
			String gdtime	= section_new_createtime1.getEditableText().toString().trim() ;
			String zbtime	= section_new_createtime2.getEditableText().toString().trim() ;
			
			String gddes	= section_new_remark_gd.getEditableText().toString().trim() ;
			String zbdes	= section_new_remark_sl.getEditableText().toString().trim() ;
			
			if(StringUtils.isEmpty(gdlj)){
				showText("拱顶极限值必须大于0");
				return ;
			}
			
			if(StringUtils.isEmpty(zblj)){
				showText("周边收敛极限值必须大于0");
				return ;
			}
			
			if(StringUtils.isEmpty(gdsl)){
				showText("拱顶下沉变形极限值必须大于0");
				return ;
			}
			
			if(StringUtils.isEmpty(zbsl)){
				showText("周边收敛变形极限值必须大于0");
				return ;
			}
			
			if(sectionInfo == null){
				
				sectionInfo = new TunnelCrossSectionIndex() ;
				
				// base info
				sectionInfo.setChainagePrefix(prefix);
				sectionInfo.setChainage(Float.valueOf(chainage));
				sectionInfo.setSectionName(name);
				sectionInfo.setInBuiltTime(DateUtils.toDate(date, DateUtils.PART_TIME_FORMAT));
				sectionInfo.setWidth(Float.valueOf(width));
				//TODO: 表示数据未上传
				sectionInfo.setInfo("1"); 
				
				sectionInfo.setPointAName(pointA);
				sectionInfo.setPointS1Name(pointS1);
				sectionInfo.setPointS2Name(pointS2);
				sectionInfo.setPointS3Name(pointS3);
				
				sectionInfo.setGDU0(Float.valueOf(gdlj));
				sectionInfo.setGDVelocity(Float.valueOf(gdsl));
				sectionInfo.setGDU0Time(DateUtils.toDate(gdtime, DateUtils.PART_TIME_FORMAT));
				sectionInfo.setGDU0Description(gddes);
				
				sectionInfo.setSLU0(Float.valueOf(zblj));
				sectionInfo.setSLLimitVelocity(Float.valueOf(zbsl));
				sectionInfo.setSLU0Time(DateUtils.toDate(zbtime, DateUtils.PART_TIME_FORMAT));
				sectionInfo.setSLU0Description(zbdes);
				
				// excavation
				sectionInfo.setExcavateMethod((String)section_new_sp.getSelectedItem());
				
				int code = TunnelCrossSectionIndexDao.defaultDao().insert(sectionInfo);
				
				// 保存数据
				if(code == 100){
					showText("非注册用户,不能保存10个以上的断面");
					return ;
				} else if(code == ProjectIndexDao.DB_EXECUTE_FAILED){
					showText("保存失败");
					return ;
				}
			} else {
				
				// base info
				sectionInfo.setChainagePrefix(prefix);
				sectionInfo.setChainage(Float.valueOf(chainage));
				sectionInfo.setSectionName(name);
				sectionInfo.setInBuiltTime(DateUtils.toDate(date, DateUtils.PART_TIME_FORMAT));
				sectionInfo.setWidth(Float.valueOf(width));
				
				// excavation
				sectionInfo.setExcavateMethod((String)section_new_sp.getSelectedItem());
				
				sectionInfo.setPointAName(pointA);
				sectionInfo.setPointS1Name(pointS1);
				sectionInfo.setPointS2Name(pointS2);
				sectionInfo.setPointS3Name(pointS3);
				
				sectionInfo.setGDU0(Float.valueOf(gdlj));
				sectionInfo.setGDVelocity(Float.valueOf(gdsl));
				sectionInfo.setGDU0Time(DateUtils.toDate(gdtime, DateUtils.PART_TIME_FORMAT));
				sectionInfo.setGDU0Description(gddes);
				
				sectionInfo.setSLU0(Float.valueOf(zblj));
				sectionInfo.setSLLimitVelocity(Float.valueOf(zbsl));
				sectionInfo.setSLU0Time(DateUtils.toDate(zbtime, DateUtils.PART_TIME_FORMAT));
				sectionInfo.setSLU0Description(zbdes);
				
				TunnelCrossSectionIndexDao.defaultDao().update(sectionInfo);
			}
			
			setResult(RESULT_OK);
			finish();
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
