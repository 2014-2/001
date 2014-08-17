package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;
import org.zw.android.framework.util.DateUtils;
import org.zw.android.framework.util.StringUtils;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;
import com.crtb.tunnelmonitor.task.AsyncUpdateTask;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
import com.crtb.tunnelmonitor.widget.CrtbRecordSubsidenceSectionInfoListView;

/**
 * 新建地表下沉记录单
 *
 */
@InjectLayout(layout = R.layout.activity_record_new)
public class RecordNewSubsidenceActivity extends WorkFlowActivity implements OnPageChangeListener, OnClickListener {
   
	public static final String KEY_RECORD_SUBSIDENCE_OBJECT		= "_key_record_subsidence_object" ;
	
	@InjectView(id=R.id.vPager)
	private ViewPager mPager;
	
	@InjectView(id=R.id.cursor)
	private ImageView cursor;
	
	//@InjectView(id=R.id.bottom_layout)
	//private RelativeLayout bottomLayout ;
	
    ArrayList<View> list = new ArrayList<View>();
    
    private TextView t1, t2;
    private int offset = 0;
    private int currIndex = 0;
    int disPlayWidth, offSet;
    Bitmap b;
    
    @InjectView(id=R.id.work_btn_queding,onClick="this")
	private Button section_btn_queding;
	
	@InjectView(id=R.id.work_btn_quxiao,onClick="this")
	private Button section_btn_quxiao;
    
	////////////////////////////base info//////////////////////////
	@InjectView(layout=R.layout.lrecord_new_xuanze)
	private LinearLayout mBaseInfoLayout ;
	
	@InjectView(id=R.id.section_new_et_chainage_prefix,parent="mBaseInfoLayout")
	private EditText section_new_et_prefix;
	
	@InjectView(id=R.id.section_new_et_Chainage,parent="mBaseInfoLayout")
	private EditText record_Chainage;
	
	@InjectView(id=R.id.record_Person,parent="mBaseInfoLayout")
	private EditText record_Person;
	
	@InjectView(id=R.id.record_Card,parent="mBaseInfoLayout")
	private EditText record_Card;
	
	@InjectView(id=R.id.record_C,parent="mBaseInfoLayout")
	private EditText record_C;
	
	@InjectView(id=R.id.record_buildtime,parent="mBaseInfoLayout",onClick="this")
	private EditText record_buildtime;
	
	@InjectView(id=R.id.record_dotype,parent="mBaseInfoLayout")
	private EditText record_dotype;
	
	////////////////////////////section info///////////////////////
	@InjectView(layout=R.layout.record_new_subsidence_layout)
	private LinearLayout mSectionListLayout ;
	
	@InjectView(id = R.id.section_use_list, parent = "mSectionListLayout")
	private CrtbRecordSubsidenceSectionInfoListView sectionListView;
    
	private RawSheetIndex recordInfo 		= null;
	private SurveyerInformation surveyer 	= null ;
	private boolean editRawSheet , editSection ;
	private String	sectionGuis				= null ;
	
	private ProjectIndex mCurrentWorkPlan;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_record_new);
        
		// add by wei.zhou
		InjectCore.injectUIProperty(this);
		
		// find cache
		recordInfo = CommonObject.findObject(KEY_RECORD_SUBSIDENCE_OBJECT);
		
		// title
		setTopbarTitle(getString(R.string.record_new_section_subisdence_title));
		
		// init ViewPager
		initViewPager();
		
		mCurrentWorkPlan = CommonObject.findObject(KEY_CURRENT_WORKPLAN);
		
		// prefix
		section_new_et_prefix.setText(mCurrentWorkPlan.getChainagePrefix());
		
		// load default
		loadDefault();
		
		// chainage change
		record_Chainage.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

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
					if (temp.length() - posDot - 1 > 3) {
						edt.delete(posDot + 4, posDot + 5);
					}
				}
				
				temp = edt.toString();	

				if (!StringUtils.isEmpty(temp)) {
					sectionListView.setChainage(CrtbUtils.formatDouble(temp));
				}
			}
		});

		String str = record_Chainage.getText().toString().trim();
		if (!StringUtils.isEmpty(str)) {
			sectionListView.setChainage(CrtbUtils.formatDouble(str));
		}
    }
    
    private void loadDefault(){
    	
        record_Person.setEnabled(true);
        record_Card.setEnabled(true);
        
        AppCRTBApplication app = AppCRTBApplication.getInstance();
        
        if (!app.isbLocaUser()) {
            SurveyerInformation p =  app.getCurPerson();
            if (p != null) {
                record_Person.setText(p.getSurveyerName());
                record_Card.setText(p.getCertificateID());
                record_Person.setEnabled(false);
                record_Card.setEnabled(false);
            }
        }

    	if(recordInfo != null){
    		
    		editRawSheet	= true ;
    		
    		setTopbarTitle("编辑地表下沉断面记录单");
    		sectionGuis	= recordInfo.getCrossSectionIDs() ;
    		sectionListView.setSectionIds(recordInfo.getGuid(),sectionGuis);
    		
    		surveyer = RawSheetIndexDao.defaultDao().querySurveyerBySheetIndexGuid(recordInfo.getGuid());
			
    		section_new_et_prefix.setText(mCurrentWorkPlan.getChainagePrefix());
			record_Chainage.setText(CrtbUtils.doubleToString(recordInfo.getFACEDK()));
			record_Person.setText(surveyer.getSurveyerName());
			record_Card.setText(surveyer.getCertificateID());
			record_C.setText(String.valueOf(recordInfo.getTEMPERATURE()));
			record_dotype.setText(recordInfo.getFACEDESCRIPTION());
			
			record_buildtime.setText(DateUtils.toDateString(recordInfo.getCreateTime(),DateUtils.PART_TIME_FORMAT)) ;
			
			record_Chainage.setEnabled(false);
			record_Person.setEnabled(false);
			record_Card.setEnabled(false);
			
			// 是否上传
			if (recordInfo.getUploadStatus() == 2) {
				record_C.setEnabled(false);
				record_dotype.setEnabled(false);
				record_buildtime.setEnabled(false);
				
				// bottomLayout.setVisibility(View.INVISIBLE);
			}
    	
    	} else {
    		editRawSheet	= false ;
    		record_buildtime.setText(DateUtils.toDateString(DateUtils.getCurrtentTimes(),DateUtils.PART_TIME_FORMAT)) ;
    	}
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.work_btn_quxiao:
			Intent IntentCancel = new Intent();
			setResult(RESULT_CANCELED, IntentCancel);
			this.finish();// 关闭当前界面
			break;
		case R.id.record_buildtime :
			
			/*Date curdate = DateUtils.toDate(record_buildtime.getEditableText().toString().trim(), DateUtils.PART_TIME_FORMAT);
			
			if(curdate == null){
				curdate	= DateUtils.getCurrtentTimes() ;
			}
			
			CrtbDateDialogUtils.setAnyDateDialog(this, record_buildtime, curdate);*/
			break ;
		case R.id.work_btn_queding: // 数据库
			
			if(editRawSheet && !editSection){
				showText("你不能保存,请进入选择断面");
				return ;
			}
			
			// base
			String chainage 	= record_Chainage.getEditableText().toString().trim();// 里程
			String person 		= record_Person.getEditableText().toString().trim();
			String idcard 		= record_Card.getEditableText().toString().trim();
			String temperature 	= record_C.getEditableText().toString().trim();
			String descr 		= record_dotype.getEditableText().toString().trim();
			String currentTime 	= record_buildtime.getEditableText().toString().trim() ;

			ArrayList<Double> chainages = sectionListView.getChainages();
			if (StringUtils.isEmpty(chainage)) {
				showText("掌子面里程不能为空");
				return;
			}
			
            for (Double c : chainages) {
                if (Math.abs(c - CrtbUtils.formatDouble(chainage)) >= 500) {
                    showText("距掌子面距离需小于500米,否则无法上传至工管中心");
                    return;
                }
            }

			if (StringUtils.isEmpty(person)) {
				showText("测量员不能为空");
				return;
			}

			if (!isCertificateID(idcard)) {
				showText("身份证号码无效");
				return;
			}
			
			float temp = 0f ;
			try{
				temp	= Float.valueOf(temperature) ;
			}catch(Exception e){
				
			}
			
			String sections = sectionListView.getSelectedSection() ;
			
			if(StringUtils.isEmpty(sections)){
				showText("请选择断面");
				return;
			}
			
			if(recordInfo == null){
				
				recordInfo = new RawSheetIndex();
				// 基本信息
				recordInfo.setCrossSectionType(RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES);
				recordInfo.setFACEDK(CrtbUtils.formatDouble(chainage));
				recordInfo.setCreateTime(DateUtils.toDate(currentTime,DateUtils.PART_TIME_FORMAT));
				recordInfo.setTEMPERATURE(temp);
				recordInfo.setFACEDESCRIPTION(descr);
				recordInfo.setCrossSectionIDs(sections);
				recordInfo.setUploadStatus(1); //表示记录单未上传
				
				RawSheetIndexDao.defaultDao().insert(recordInfo);
				
				surveyer = new SurveyerInformation() ;
				surveyer.setSurveyerName(person);
				surveyer.setCertificateID(idcard);
				surveyer.setProjectID(recordInfo.getGuid());
				
				// 保存测量人员
				RawSheetIndexDao.defaultDao().insertSurveyer(surveyer);
				
			} else {
				
				// 是否需要改为部分上传状态
//				if(recordInfo.getUploadStatus() == 2 && sectionGuis != null){
//					
//					if(!sectionGuis.equals(sections)){
//						recordInfo.setUploadStatus(3);
//					}
//				}
				int uploadStatus = AsyncUpdateTask.getSubsidenceSheetStatus(recordInfo);
				recordInfo.setUploadStatus(uploadStatus);
				
				// 基本信息
				recordInfo.setCrossSectionType(RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES);
				recordInfo.setFACEDK(CrtbUtils.formatDouble(chainage));
				recordInfo.setCreateTime(DateUtils.toDate(currentTime,DateUtils.PART_TIME_FORMAT));
				recordInfo.setTEMPERATURE(temp);
				recordInfo.setFACEDESCRIPTION(descr);
				recordInfo.setCrossSectionIDs(sections);
				
				// 更新测量人员
				
				// 跟新记录单
				RawSheetIndexDao.defaultDao().update(recordInfo);
			}

			setResult(RESULT_OK);
			finish();
			break;
		}
	}
	
	private void initViewPager(){
		
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);

		t1.setOnClickListener(tv_Listener);
		t2.setOnClickListener(tv_Listener);

		disPlayWidth	= mDisplayMetrics.widthPixels ;
		b = BitmapFactory.decodeResource(this.getResources(), R.drawable.heng);
		offSet = ((disPlayWidth / 4) - b.getWidth() / 2);
		
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		ViewGroup.LayoutParams lp = cursor.getLayoutParams() ;
		lp.width = disPlayWidth / 2;
		lp.height = 4 ;
		cursor.setLayoutParams(lp);
		cursor.setImageMatrix(matrix);

		list.add(mBaseInfoLayout);
		list.add(mSectionListLayout);
		
		PagerAdapter pa = new PagerAdapter() {

			@Override
			public void destroyItem(View arg0, int arg1, Object arg2) {
				((ViewPager) arg0).removeView((View) list.get(arg1));

			}

			@Override
			public void finishUpdate(View arg0) {

			}

			@Override
			public int getCount() {
				return list.size();
			}

			@Override
			public Object instantiateItem(View arg0, int arg1) {
				((ViewPager) arg0).addView((View) list.get(arg1), 0);
				return (View) list.get(arg1);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
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
		};
		
		mPager.setAdapter(pa);
		mPager.setCurrentItem(TAB_ONE);
		mPager.setOnPageChangeListener(this);
	}

	@Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int index) {
        int single = (int) (b.getWidth() + offSet * 2);
        TranslateAnimation ta = new TranslateAnimation(currIndex * single,
                single * index, 0, 0);
        ta.setFillAfter(true);
        ta.setDuration(200);
        cursor.startAnimation(ta);
        currIndex = index;
        
        if(index == 1){
        	sectionListView.onResume() ;
        	editSection	= true ;
        }
    }

    public OnClickListener tv_Listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int single = (int) (b.getWidth() + offSet * 2);
            switch (v.getId()) {
            case R.id.text1:
                mPager.setCurrentItem(0);
                if (currIndex != 0) {
                    TranslateAnimation ta = new TranslateAnimation(
                            (currIndex * single), 0, 0, 0);
                    ta.setFillAfter(true);
                    ta.setDuration(200);
                    cursor.startAnimation(ta);
                }
                currIndex = 0;
                break;
            case R.id.text2:
                mPager.setCurrentItem(1);
                if (currIndex != 1) {
                    TranslateAnimation ta = new TranslateAnimation(currIndex
                            * single, single, 0, 0);
                    ta.setFillAfter(true);
                    ta.setDuration(200);
                    cursor.startAnimation(ta);
                }
                currIndex = 1;
                break;
            default:
                break;
            }
        }
    };
}
