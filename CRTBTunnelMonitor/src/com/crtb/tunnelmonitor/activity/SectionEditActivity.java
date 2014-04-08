package com.crtb.tunnelmonitor.activity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.SubsidenceCrossSectionDaoImpl;
import com.crtb.tunnelmonitor.dao.impl.TunnelCrossSectionDaoImpl;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionInfo;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;
import com.crtb.tunnelmonitor.entity.WorkInfos;
import com.crtb.tunnelmonitor.utils.Time;
import com.crtb.tunnelmonitor.activity.R;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 编辑地表下层断面
 *
 */
public class SectionEditActivity extends Activity implements OnClickListener
 {
    private ViewPager mPager;// 页卡内容
    private List<View> listViews; // Tab页面列表
    private ImageView cursor;// 动画图片
    private TextView t1, t2, t3, textView1;// 页卡头标
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    private View View3;
    private TextView section_new_tv_diheader;  
    private int num;
    
    private EditText DSection_Chainage;
    private EditText DSection_name;
    private EditText DSection_createtime;
    private EditText DSection_Width;
    private EditText DSection_PointCount;
    private EditText DSection_Value1;
    private EditText DSection_Value2;
    private EditText DSection_SetTime;
    private EditText DSection_Info;

	/** 确定按钮 */
	private Button section_btn_queding;
	/** 取消按钮 */
	private Button section_btn_quxiao;
	
	private String sChainage = null;
	private SubsidenceCrossSectionInfo editInfo = null;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sectionedit);
        num = getIntent().getExtras().getInt("name");
        InitImageView();
        InitTextView();
        InitViewPager();
        InitMyTextView();
        
		sChainage = getIntent().getExtras().getString(Constant.Select_SectionRowClickItemsName_Name);
		double dChainage = AppCRTBApplication.StrToDouble(sChainage, -1);
		InitImageView();
		InitTextView();
		InitViewPager();
		InitMyTextView();

		if (sChainage.length() > 0) {
        	section_new_tv_diheader.setText("编辑地表下层断面");
			AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
			WorkInfos Curw = CurApp.GetCurWork();
			List<SubsidenceCrossSectionInfo> infos = Curw.getScsiList();
			for(int i=0;i<infos.size();i++)
			{
				SubsidenceCrossSectionInfo tmp = infos.get(i);
				if(tmp.getChainage().equals(dChainage))
				{
					editInfo = tmp;
					break;
				}
			}
		}
    }

    /**
     * 初始化动画
     */
    private void InitImageView() {
        cursor = (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
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
     * 初始化头标
     */
    private void InitTextView() {
        t1 = (TextView) findViewById(R.id.tev);
        t2 = (TextView) findViewById(R.id.tex);
        section_new_tv_diheader = (TextView) findViewById(R.id.section_new_tv_diheader);
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
			IntentCancel.putExtra(Constant.Select_SectionRowClickItemsName_Name,2);
			setResult(RESULT_CANCELED, IntentCancel);
			this.finish();// 关闭当前界面
			break;
		case R.id.work_btn_queding: // 数据库
			if(DSection_Chainage.getText().toString().trim().length() <= 0)
			{
				Toast.makeText(this, "请输入完整信息", 3000).show();
				return;
			}
			if(DSection_Width.getText().toString().trim().length() <= 0)
			{
				Toast.makeText(this, "请输入完整信息", 3000).show();
				return;
			}
			if(DSection_PointCount.getText().toString().trim().length() <= 0)
			{
				Toast.makeText(this, "请输入完整信息", 3000).show();
				return;
			}

			AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
			WorkInfos Curw = CurApp.GetCurWork();
			SubsidenceCrossSectionInfo ts = new SubsidenceCrossSectionInfo();
			if (editInfo != null) {
				ts.setId(editInfo.getId());
			}
			ts.setChainage(Double.valueOf(DSection_Chainage.getText().toString().trim()));
			ts.setChainagePrefix(Curw.getChainagePrefix());
			ts.setInbuiltTime(Timestamp.valueOf(DSection_createtime.getText().toString()));
			ts.setWidth(Integer.valueOf(DSection_Width.getText().toString().trim()));
			ts.setSurveyPnts(DSection_PointCount.getText().toString().trim());
			ts.setInfo(DSection_Info.getText().toString().trim());
			ts.setChainageName(CurApp.GetSectionName(ts.getChainage().doubleValue()));
			if(!CurApp.IsValidSubsidenceTunnelCrossSectionInfo(ts))
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
			List<SubsidenceCrossSectionInfo> infos = Curw.getScsiList();
			if(infos == null)
			{
				Toast.makeText(this, "添加失败", 3000).show();
			}
			else
			{
				boolean bHave = false;
				for(int i=0;i<infos.size();i++)
				{
					SubsidenceCrossSectionInfo tmp = infos.get(i);
					if(tmp.getChainage().equals(ts.getChainage()))
					{
						bHave = true;
						break;
					}
				}
				if(bHave)
				{
					if(editInfo == null)
					{
						Toast.makeText(this, "已存在", 3000).show();
						return;
					}
					else
					{
						SubsidenceCrossSectionDaoImpl impl = new SubsidenceCrossSectionDaoImpl(this,Curw.getProjectName());
						impl.UpdateSubsidenceCrossSection(ts);
						Curw.UpdateSubsidenceCrossSectionInfo(ts);
						CurApp.UpdateWork(Curw);
						Toast.makeText(this, "编辑成功", 3000).show();
					}
				}
				else
				{
					SubsidenceCrossSectionDaoImpl impl = new SubsidenceCrossSectionDaoImpl(this,Curw.getProjectName());
					if(impl.InsertSubsidenceCrossSection(ts))
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
			IntentOk.putExtra(Constant.Select_SectionRowClickItemsName_Name,2);
			setResult(RESULT_OK, IntentOk);
			this.finish();
			break;
		default:
			break;
		}

	}

    /**
     * 初始化ViewPager
     */
    private void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager);
        listViews = new ArrayList<View>();
        LayoutInflater mInflater = getLayoutInflater();
        listViews.add(mInflater.inflate(R.layout.layout_4, null));
        listViews.add(mInflater.inflate(R.layout.layout_2, null));
        mPager.setAdapter(new MyPagerAdapter(listViews));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     * 小尛龙
     */
    public void InitMyTextView() {
     
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
			
			DSection_Chainage = (EditText) findViewById(R.id.DSection_Chainage);
			DSection_name = (EditText) findViewById(R.id.DSection_name);
			DSection_createtime = (EditText) findViewById(R.id.DSection_createtime);
			DSection_Width = (EditText) findViewById(R.id.DSection_Width);
			DSection_PointCount = (EditText) findViewById(R.id.DSection_PointCount);

			DSection_Value1 = (EditText) findViewById(R.id.DSection_Value1);
			DSection_Value2 = (EditText) findViewById(R.id.DSection_Value2);
			DSection_SetTime = (EditText) findViewById(R.id.DSection_SetTime);
			DSection_Info = (EditText) findViewById(R.id.DSection_Info);
			
			DSection_Chainage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					//if(!hasFocus)
					{
						String sChainage = DSection_Chainage.getText().toString().trim();
						if (sChainage.length() > 0) {
							AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
							DSection_name.setText(CurApp.GetSectionName(Double.valueOf(sChainage).doubleValue()));
						}
						else {
							DSection_name.setText("");
						}
					}
				}
			});

			// 获取手机的当前时间
			final String time = Time.getDateEN();
			// 设置文本框里面的字体大小
			DSection_createtime.setTextSize(11);
			if (arg1 == 0) {
				if (editInfo != null) {
					DSection_Chainage.setText(editInfo.getChainage().toString());
					DSection_Chainage.setFocusableInTouchMode(false);
					DSection_name.setText(CurApp.GetSectionName(editInfo.getChainage().doubleValue()));
					DSection_name.setFocusableInTouchMode(false);
					DSection_createtime.setText(editInfo.getInbuiltTime().toString());
					DSection_createtime.setFocusableInTouchMode(false);
					DSection_Width.setText(Integer.toString(editInfo.getWidth()));
					DSection_Width.setFocusableInTouchMode(false);
					DSection_PointCount.setText(editInfo.getSurveyPnts());
					DSection_PointCount.setFocusableInTouchMode(false);
				}
				else {
					// 跟改文本框赋值时间
					DSection_createtime.setText(time);
				}
			}
			else
			if (arg1 == 1) {
				DSection_Value1.setText(Curw.getGDLimitTotalSettlement().toString());
				DSection_Value2.setText(Curw.getGDLimitVelocity().toString());
				DSection_SetTime.setTextSize(11);
				DSection_SetTime.setText(time);
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
