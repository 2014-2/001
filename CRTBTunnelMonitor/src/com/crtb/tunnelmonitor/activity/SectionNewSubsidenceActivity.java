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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.mydefine.CrtbDateDialogUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 新建地表下层断面
 *
 */
@InjectLayout(layout = R.layout.activity_sectionedit)
public class SectionNewSubsidenceActivity extends WorkFlowActivity implements OnClickListener {
	
	public static final String KEY_NEW_SUBSIDENCE_SECTION_OBJECT	= "_key_new_subsidence_section_object" ;
  
	@InjectView(id=R.id.vPager)
	private ViewPager mPager;
	
    private List<View> listViews = new ArrayList<View>() ;
    private ImageView cursor;
    private TextView t1, t2 ;
    private int offset = 0;
    private int currIndex = 0;
    private int bmpW;
    
    @InjectView(id=R.id.work_btn_queding,onClick="this")
	private Button section_btn_queding;
	
	@InjectView(id=R.id.work_btn_quxiao,onClick="this")
	private Button section_btn_quxiao;
    
    /////////////////////////////////base info////////////////////////
    @InjectView(layout=R.layout.layout_4)
    private LinearLayout mBaseInfoLayout ;
    
    @InjectView(id=R.id.section_new_et_chainage_prefix,parent="mBaseInfoLayout")
	private EditText section_new_et_prefix;
    
    @InjectView(id=R.id.section_new_et_Chainage,parent="mBaseInfoLayout")
    private EditText DSection_Chainage;
    
    @InjectView(id=R.id.section_new_et_name,parent="mBaseInfoLayout")
    private EditText DSection_name;
    
    @InjectView(id=R.id.section_new_et_calendar,parent="mBaseInfoLayout",onClick="this")
    private EditText DSection_createtime;
    
    @InjectView(id=R.id.section_new_et_width,parent="mBaseInfoLayout")
    private EditText DSection_Width;
    
    @InjectView(id=R.id.section_new_et_pc,parent="mBaseInfoLayout")
    private EditText DSection_PointCount;
    
    ////////////////////////////////deformation info//////////////////
    @InjectView(layout=R.layout.layout_2)
    private LinearLayout mDeformationInfoLayout ;
    
    @InjectView(id=R.id.DSection_Value1,parent="mDeformationInfoLayout")
    private EditText DSection_Value1;
    
    @InjectView(id=R.id.DSection_Value2,parent="mDeformationInfoLayout")
    private EditText DSection_Value2;
    
    @InjectView(id=R.id.DSection_SetTime,parent="mDeformationInfoLayout",onClick="this")
    private EditText DSection_SetTime;
    
    @InjectView(id=R.id.DSection_Info,parent="mDeformationInfoLayout")
    private EditText DSection_Info;

	private SubsidenceCrossSectionIndex subsidence = null;
	
	private ProjectIndex mCurrentWorkPlan;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sectionedit);
        
		// add by wei.zhou
		InjectCore.injectUIProperty(this);
		
		subsidence	= CommonObject.findObject(KEY_NEW_SUBSIDENCE_SECTION_OBJECT);

		// title
		setTopbarTitle(getString(R.string.section_new_subsidence_title));
		
		// init ViewPager
		initViewPager() ;
		
		mCurrentWorkPlan = CommonObject.findObject(KEY_CURRENT_WORKPLAN);
		
		// prefix
		section_new_et_prefix.setText(mCurrentWorkPlan.getChainagePrefix());
		
		// default
		String date = DateUtils.toDateString(DateUtils.getCurrtentTimes(), DateUtils.DATE_TIME_FORMAT) ;
		DSection_createtime.setText(date);
		DSection_SetTime.setText(date);
		
		DSection_Chainage.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				
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
				
				temp = edt.toString();
				
				if(!StringUtils.isEmpty(temp)){
					double f 	= CrtbUtils.formatDouble(temp);
					String pre 	= section_new_et_prefix.getEditableText().toString().trim();
					DSection_name.setText(CrtbUtils.formatSectionName(pre,f));
				} else {
					DSection_name.setText("");
				}
			}
		}) ;
		
		DSection_Width.addTextChangedListener(new TextWatcher() {
			
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
		}) ;
		
		//
		loadDefault();
    }

	private void loadDefault(){
		
		if(subsidence != null){
			
			setTopbarTitle("编辑地表下沉断面");
			
			section_new_et_prefix.setText(subsidence.getChainagePrefix());
			DSection_Chainage.setText(CrtbUtils.doubleToString(subsidence.getChainage()));
			DSection_name.setText(subsidence.getSectionName());
			DSection_createtime.setText(DateUtils.toDateString(subsidence.getInbuiltTime(),DateUtils.DATE_TIME_FORMAT));
			DSection_Width.setText(String.valueOf((int)subsidence.getWidth()));
			DSection_PointCount.setText(String.valueOf(subsidence.getSurveyPnts()));
			
			DSection_Chainage.setEnabled(false);
			DSection_Width.setEnabled(false);
			DSection_PointCount.setEnabled(false);
			
			DSection_Value1.setText(String.valueOf(subsidence.getDBU0()));
			DSection_Value2.setText(String.valueOf(subsidence.getDBLimitVelocity()));
			
			DSection_Value1.setEnabled(false);
			DSection_Value2.setEnabled(false);
			
			DSection_SetTime.setText(DateUtils.toDateString(subsidence.getDBU0Time(),DateUtils.DATE_TIME_FORMAT));
			DSection_Info.setText(subsidence.getDBU0Description());
		}
	}

	@Override
	public void onClick(View v) {
		
		Date curdate = null ;
		
		switch (v.getId()) {
		case R.id.work_btn_quxiao:
			Intent IntentCancel = new Intent();
			setResult(RESULT_CANCELED, IntentCancel);
			this.finish();// 关闭当前界面
			break;
		case R.id.section_new_et_calendar:
			
			/*curdate = DateUtils.toDate(DSection_createtime.getEditableText().toString().trim(), DateUtils.DATE_TIME_FORMAT);
			
			if(curdate == null){
				curdate	= DateUtils.getCurrtentTimes() ;
			}
			
			CrtbDateDialogUtils.setAnyDateDialog(this, DSection_createtime, curdate);*/
			break ;
		case R.id.DSection_SetTime:
			
			curdate = DateUtils.toDate(DSection_SetTime.getEditableText().toString().trim(), DateUtils.DATE_TIME_FORMAT);
			
			if(curdate == null){
				curdate	= DateUtils.getCurrtentTimes() ;
			}
			
			CrtbDateDialogUtils.setAnyDateDialog(this, DSection_SetTime, curdate);
			break ;
		case R.id.work_btn_queding: // 数据库
			
			final SubsidenceCrossSectionIndexDao dao = SubsidenceCrossSectionIndexDao.defaultDao() ;
			
			// base
			String prefix		= section_new_et_prefix.getEditableText().toString().trim() ;
			String chainage 	= DSection_Chainage.getEditableText().toString().trim();// 里程
			String name 		= DSection_name.getEditableText().toString().trim();
			String date 		= DSection_createtime.getEditableText().toString().trim();
			String width 		= DSection_Width.getEditableText().toString().trim();
			String point 		= DSection_PointCount.getEditableText().toString().trim();
						
			if (StringUtils.isEmpty(chainage)) {
				showText("断面里程不能为空");
				return;
			}

			if (StringUtils.isEmpty(date)) {
				showText("埋设时间不能为空");
				return;
			}
			
			if (StringUtils.isEmpty(point)) {
				showText("断面宽度不能为空");
				return;
			}
			
			double cv = CrtbUtils.formatDouble(chainage);
			
			if(cv < mCurrentWorkPlan.getStartChainage() || cv > mCurrentWorkPlan.getEndChainage()){
				showText("断面里程必须在工作面里程之内");
				return ;
			}

			if (StringUtils.isEmpty(width)) {
				showText("监测点不能为空");
				return;
			}
			
			int pc = 0 ;
			
			try{
				if(!StringUtils.isEmpty(point)){
					pc = Integer.valueOf(point);
				}
			}catch(Exception e){
				e.printStackTrace() ;
			}
			
			if(pc <= 0 || pc > 30){
				showText("监测点个数只能为: 1-30");
				return ;
			}
			
			// 变形阀值
			String dbu0 		= DSection_Value1.getEditableText().toString().trim();
			String dbl 			= DSection_Value2.getEditableText().toString().trim();
			String buildtime	= DSection_SetTime.getEditableText().toString().trim();
			String remark 		= DSection_Info.getEditableText().toString().trim();
			
			if (StringUtils.isEmpty(dbu0)) {
				showText("累计变形极限值不能为空");
				return;
			}
			
			if (StringUtils.isEmpty(dbl)) {
				showText("累计速率极限值不能为空");
				return;
			}
			
			// 是否存在相同里程的断面
			if(dao.querySectionIndexByChainage(cv) != null){
				showText("已经存在相同里程的断面");
				return ;
			}
			
			if(subsidence == null){
				
				subsidence = new SubsidenceCrossSectionIndex() ;
				
				///////////baase info//////////
				subsidence.setChainagePrefix(prefix);
				subsidence.setChainage(cv);
				subsidence.setWidth(CrtbUtils.formatDouble(width));
				subsidence.setInbuiltTime(DateUtils.toDate(date, DateUtils.PART_TIME_FORMAT));
				subsidence.setSurveyPnts(pc);
				
				//
				subsidence.setDBU0(CrtbUtils.formatFloat(dbu0));
				subsidence.setDBLimitVelocity(CrtbUtils.formatFloat(dbl));
				subsidence.setDBU0Time(DateUtils.toDate(buildtime,DateUtils.PART_TIME_FORMAT));
				//TODO: info被用来标记数据是否上传：1表示未上传, 2表示已上传
				subsidence.setInfo("1");
				subsidence.setUploadStatus(1); //表示该断面未上传
				
				// insert
				int code = dao.insert(subsidence);
				
				// 保存数据
				if(code == 100){
					showText("非注册用户,不能保存10个以上的断面");
					return ;
				} else if(code == ProjectIndexDao.DB_EXECUTE_FAILED){
					showText("保存失败");
					return ;
				}
			} else {
				
				subsidence.setChainagePrefix(prefix);
				subsidence.setChainage(cv);
				subsidence.setWidth(CrtbUtils.formatDouble(width));
				subsidence.setInbuiltTime(DateUtils.toDate(date, DateUtils.PART_TIME_FORMAT));
				subsidence.setSurveyPnts(pc);

				//
				subsidence.setDBU0(CrtbUtils.formatFloat(dbu0));
				subsidence.setDBLimitVelocity(CrtbUtils.formatFloat(dbl));
				subsidence.setDBU0Time(DateUtils.toDate(buildtime,DateUtils.PART_TIME_FORMAT));
				subsidence.setInfo(remark);

				// insert
				dao.update(subsidence);
			}
			
			setResult(RESULT_OK);
			finish();
			break;
		}

	}

	private void initViewPager() {
    	
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
				.getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 2 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		ViewGroup.LayoutParams lp = cursor.getLayoutParams() ;
		lp.width = screenW / 2;
		lp.height = 4 ;
		cursor.setLayoutParams(lp);
		cursor.setImageMatrix(matrix);

		t1 = (TextView) findViewById(R.id.tev);
		t2 = (TextView) findViewById(R.id.tex);

		t1.setOnClickListener(new MyOnClickListener(TAB_ONE));
		t2.setOnClickListener(new MyOnClickListener(TAB_TWO));

		listViews.add(mBaseInfoLayout);
		listViews.add(mDeformationInfoLayout);

		mPager.setAdapter(new MyPagerAdapter());
		mPager.setCurrentItem(TAB_ONE);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
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
