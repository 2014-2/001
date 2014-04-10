package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputFilter;
import android.text.method.NumberKeyListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.WorkDaoImpl;
import com.crtb.tunnelmonitor.entity.WorkInfos;
import com.crtb.tunnelmonitor.utils.Time;

/**
 * 新建工作面和编辑工作界面
 * 
 */
public class WorkNewActivity extends WorkFlowActivity implements OnClickListener {
	
	/** 页卡内容 */
	private ViewPager mPager;
	/** Tab页面列表 */
	private List<View> listViews;
	/** 动画图片 */
	private ImageView cursor;
	/** 页卡头标 */
	private TextView t1, t2;
	/** 动画图片偏移量 */
	private int offset = 0;
	/** 当前页卡编号 */
	private int currIndex = 0;
	/** 动画图片宽度 */
	private int bmpW;
	/** 视图 */
	private View View3;
	/** 获取当前时间按钮 */
	/** 新建工作面信息 */
	private ImageView img_calendar;
	/** 工作面 */
	private EditText work_new_et_name;
	/** 设置时间 */
	private EditText work_new_et_calendar;
	/** 施工单位 */
	private EditText work_new_et_unit;
	/** 前缀 */
	private EditText work_new_et_prefix;
	/** 起始里程 */
	private EditText work_new_et_start;
	/** 终止里程 */
	private EditText work_new_et_end;
	// 阈值
	/** 变形速率极限值 */
	private EditText work_new_gongding1;
	/** 累计变形极限值 */
	private EditText work_new_gongding2;
	/** 设置时间 */
	private EditText work_new_gongding3;
	/** 备注 */
	private EditText work_new_gongding4;
	//
	/** 变形速率极限值 */
	private EditText work_new_zhoubian1;
	/** 累计变形极限值 */
	private EditText work_new_zhoubian2;
	/** 设置时间 */
	private EditText work_new_zhoubian3;
	/** 备注 */
	private EditText work_new_zhoubian4;
	//
	/** 变形速率极限值 */
	private EditText work_new_dibiao1;
	/** 累计变形极限值 */
	private EditText work_new_dibiao2;
	/** 设置时间 */
	private EditText work_new_dibiao3;
	/** 备注 */
	private EditText work_new_dibiao4;

	/** 确定按钮 */
	private Button work_btn_queding;
	/** 取消按钮 */
	private Button work_btn_quxiao;

	// 获取传来的值
	private String workName = null;
	private WorkInfos editWork = null;
	//
	private int num;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_new);
		// 获取从工作界面传来的值
		workName = "" ;//getIntent().getExtras().getString(Constant.Select_WorkRowClickItemsName_Name);
		if(workName.length() > 0)
		{
			AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
			List<WorkInfos> infos = CurApp.GetWorkList();
			for(int i=0;i<infos.size();i++)
			{
				WorkInfos tmp = infos.get(i);
				if(workName.equals(tmp.getProjectName()))
				{
					editWork = tmp;
					break;
				}
			}
		}

		initView();
		InitImageView();
		InitViewPager();

		setTopbarTitle("新建工作面");
	}

	/** 初始化ViewPager */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.work_new_msg, null));
		listViews.add(mInflater.inflate(R.layout.work_new_variantvalue, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

	}

	/** 初始化 */
	private void initView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		work_btn_queding = (Button) findViewById(R.id.work_btn_queding);
		work_btn_quxiao = (Button) findViewById(R.id.work_btn_quxiao);

		// 点击事件
		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		work_btn_queding.setOnClickListener(this);
		work_btn_quxiao.setOnClickListener(this);

	}

	// 点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.work_btn_quxiao:
			Intent IntentCancel = new Intent();
			setResult(RESULT_CANCELED, IntentCancel);
			this.finish();// 关闭当前界面
			break;
		case R.id.work_btn_queding: // 数据库
			if(work_new_et_start.getText().toString().trim().length() <= 0)
			{
				Toast.makeText(WorkNewActivity.this, "请输入完整信息", 3000).show();
				return;
			}
			if(work_new_et_end.getText().toString().trim().length() <= 0)
			{
				Toast.makeText(WorkNewActivity.this, "请输入完整信息", 3000).show();
				return;
			}

			WorkInfos w = new WorkInfos();
			w.setProjectName(work_new_et_name.getText().toString().trim());
			w.setCreateTime(work_new_et_calendar.getText().toString().trim());
			w.setChainagePrefix(work_new_et_prefix.getText().toString().trim());
			w.setStartChainage(Double.valueOf(work_new_et_start.getText().toString().trim()));
			w.setEndChainage(Double.valueOf(work_new_et_end.getText().toString().trim()));
			w.setConstructionFirm(work_new_et_unit.getText().toString().trim());
			w.setGDLimitVelocity(Float.valueOf(work_new_gongding2.getText().toString().trim()));
			w.setGDLimitTotalSettlement(Float.valueOf(work_new_gongding1.getText().toString().trim()));
			w.setSLLimitVelocity(Float.valueOf(work_new_zhoubian2.getText().toString().trim()));
			w.setSLLimitTotalSettlement(Float.valueOf(work_new_zhoubian1.getText().toString().trim()));
			w.setDBLimitVelocity(Float.valueOf(work_new_dibiao2.getText().toString().trim()));
			w.setDBLimitTotalSettlement(work_new_dibiao1.getText().toString().trim());
			w.setInfo(work_new_dibiao4.getText().toString().trim());
			w.setLimitedTotalSubsidenceTime(work_new_dibiao3.getText().toString().trim());
			AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
			if(!CurApp.IsValidWork(w))
			{
				Toast.makeText(WorkNewActivity.this, "请输入完整信息", 3000).show();
				return;
			}
			List<WorkInfos> infos = CurApp.GetWorkList();
			if(infos == null)
			{
				Toast.makeText(WorkNewActivity.this, "添加失败", 3000).show();
			}
			else
			{
				boolean bHave = false;
				for(int i=0;i<infos.size();i++)
				{
					if(infos.get(i).getProjectName().equals(w.getProjectName()))
					{
						bHave = true;
						break;
					}
				}
				if(bHave)
				{
					if(editWork == null)
					{
						Toast.makeText(WorkNewActivity.this, "已存在", 3000).show();
					}
					else
					{
						CurApp.getDatabase().UpdateWork(w);
						WorkDaoImpl d = new WorkDaoImpl(WorkNewActivity.this, w.getProjectName());
						d.UpdateWork(w);
						CurApp.UpdateWork(w);
						Toast.makeText(WorkNewActivity.this, "编辑成功", 3000).show();
					}
				}
				else
				{
					if(CurApp.getDatabase().InsertWork(w))
					{
						infos.add(w);
						WorkDaoImpl d = new WorkDaoImpl(WorkNewActivity.this, w.getProjectName());
						d.InsertWork(w);
						Toast.makeText(WorkNewActivity.this, "添加成功", 3000).show();
					}
					else
					{
						Toast.makeText(WorkNewActivity.this, "添加失败", 3000).show();
					}
				}
			}
			Intent IntentOk = new Intent();
			setResult(RESULT_OK, IntentOk);
			this.finish();
			break;
		default:
			break;
		}

	}

	/** 滑动效果 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.heng)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
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
			((ViewPager) arg0).addView(mListViews.get(arg1));

			// 初始化
			img_calendar = (ImageView) findViewById(R.id.img_calendar);
			work_new_et_calendar = (EditText) findViewById(R.id.work_new_et_calendar);
			img_calendar = (ImageView) findViewById(R.id.img_calendar);
			work_new_et_name = (EditText) findViewById(R.id.work_new_et_name);
			work_new_et_calendar = (EditText) findViewById(R.id.work_new_et_calendar);
			work_new_et_unit = (EditText) findViewById(R.id.work_new_et_unit);
			work_new_et_prefix = (EditText) findViewById(R.id.work_new_et_prefix);
			work_new_et_start = (EditText) findViewById(R.id.work_new_et_start);
			work_new_et_end = (EditText) findViewById(R.id.work_new_et_end);
			work_new_gongding1 = (EditText) findViewById(R.id.work_new_gongding1);
			work_new_gongding3 = (EditText) findViewById(R.id.work_new_gongding3);
			//work_new_gongding4 = (EditText) findViewById(R.id.work_new_gongding4);
			work_new_gongding2 = (EditText) findViewById(R.id.work_new_gongding2);
			work_new_zhoubian1 = (EditText) findViewById(R.id.work_new_zhoubian1);
			work_new_zhoubian2 = (EditText) findViewById(R.id.work_new_zhoubian2);
			work_new_zhoubian3 = (EditText) findViewById(R.id.work_new_zhoubian3);
			//work_new_zhoubian4 = (EditText) findViewById(R.id.work_new_zhoubian4);
			work_new_dibiao1 = (EditText) findViewById(R.id.work_new_dibiao1);
			work_new_dibiao2 = (EditText) findViewById(R.id.work_new_dibiao2);
			work_new_dibiao3 = (EditText) findViewById(R.id.work_new_dibiao3);
			work_new_dibiao4 = (EditText) findViewById(R.id.work_new_dibiao4);
			
			work_new_et_prefix.setKeyListener(new NumberKeyListener() {

				@Override
				public int getInputType() {
					return 0;
				}

				@Override
				protected char[] getAcceptedChars() {
					return new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
							'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
							's', 't', 'u', 'v', 'w', 'x', 'v', 'z', 'Q', 'W',
							'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S',
							'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C',
							'V', 'B', 'N', 'M', '1', '2', '3', '4', '5', '6',
							'7', '8', '9' };
				}
			});
			work_new_et_prefix.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
			work_new_et_start.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
			work_new_et_end.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
			// 获取手机的当前时间
			final String time = Time.getDateEN();
			if (arg1 == 0) {
				// 设置文本框里面的字体大小
				work_new_et_calendar.setTextSize(11);
				
				// 判断传过来的是不是编辑，设置文本框的是否可用
				if(editWork != null) {
					
					// 更改标题
					setTopbarTitle("编辑工作面");
					
					// 设置文本框不可更改
					work_new_et_name.setFocusableInTouchMode(false);
					work_new_et_calendar.setFocusableInTouchMode(false);
					work_new_et_prefix.setFocusableInTouchMode(false);
					work_new_et_start.setFocusableInTouchMode(false);
					work_new_et_end.setFocusableInTouchMode(false);
					
					work_new_et_name.setText(editWork.getProjectName());
					work_new_et_calendar.setText(editWork.getCreateTime());
					work_new_et_unit.setText(editWork.getConstructionFirm());
					work_new_et_prefix.setText(editWork.getChainagePrefix());
					work_new_et_start.setText(String.valueOf(editWork.getStartChainage()));
					work_new_et_end.setText(String.valueOf(editWork.getEndChainage()));

				}
				else {
					// 跟改文本框赋值时间
					work_new_et_calendar.setText(time);
				}
			}
			else
			if (arg1 == 1) {
				// 设置文本框的悲剧为灰色
				// 新建是阈值里面的前三个文本框不能更改
				work_new_gongding1.setFocusableInTouchMode(false);
				work_new_gongding2.setFocusableInTouchMode(false);
				work_new_gongding3.setFocusableInTouchMode(false);
				work_new_gongding3.setText(time);
				work_new_gongding3.setTextSize(11);
				work_new_zhoubian3.setText(time);
				work_new_zhoubian3.setTextSize(11);
				work_new_dibiao3.setText(time);
				work_new_dibiao3.setTextSize(11);
				if ((editWork != null)) {
					work_new_gongding1.setText(String.valueOf(editWork.getGDLimitTotalSettlement()));
					work_new_gongding2.setText(String.valueOf(editWork.getGDLimitVelocity()));
					work_new_gongding3.setText(time);
					work_new_zhoubian1.setText(String.valueOf(editWork.getSLLimitTotalSettlement()));
					work_new_zhoubian2.setText(String.valueOf(editWork.getSLLimitVelocity()));
					work_new_zhoubian3.setText(time);
					work_new_dibiao1.setText(String.valueOf(editWork.getDBLimitTotalSettlement()));
					work_new_dibiao2.setText(String.valueOf(editWork.getDBLimitVelocity()));
					work_new_dibiao3.setText(time);
					work_new_dibiao4.setText(editWork.getInfo());
				}
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
		int two = one * 2; // 页卡1 -> 页卡3 偏移量

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
