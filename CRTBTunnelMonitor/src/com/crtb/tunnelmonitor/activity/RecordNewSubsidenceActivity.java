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

import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
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
	
	@InjectView(id=R.id.record_buildtime,parent="mBaseInfoLayout")
	private EditText record_buildtime;
	
	@InjectView(id=R.id.record_dotype,parent="mBaseInfoLayout")
	private EditText record_dotype;
	
	////////////////////////////section info///////////////////////
	@InjectView(layout=R.layout.record_new_subsidence_layout)
	private LinearLayout mSectionListLayout ;
	
	@InjectView(id = R.id.section_use_list, parent = "mSectionListLayout")
	private CrtbRecordSubsidenceSectionInfoListView sectionListView;
    
	private RawSheetIndex recordInfo = null;
	private boolean editRawSheet , editSection ;
	
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
					if (temp.length() - posDot - 1 > 4) {
						edt.delete(posDot + 5, posDot + 6);
					}
				}
				
				temp = edt.toString();	

				if (!StringUtils.isEmpty(temp)) {
					sectionListView.setChainage(Float.valueOf(temp));
				}
			}
		});

		String str = record_Chainage.getText().toString().trim();
		if (!StringUtils.isEmpty(str)) {
			sectionListView.setChainage(Float.valueOf(str));
		}
    }
    
    private void loadDefault(){
    	
    	if(recordInfo != null){
    		
    		editRawSheet	= true ;
    		
    		setTopbarTitle("编辑地表下沉断面记录单");
    		sectionListView.setSectionIds(recordInfo.getCrossSectionIDs());
			
    		section_new_et_prefix.setText(recordInfo.getPrefix());
			record_Chainage.setText(String.valueOf(recordInfo.getFACEDK()));
			record_Person.setText(recordInfo.getSurveyer());
			record_Card.setText(recordInfo.getCertificateID());
			record_C.setText(String.valueOf(recordInfo.getTEMPERATURE()));
			record_dotype.setText(recordInfo.getFACEDESCRIPTION());
			
			record_buildtime.setText(DateUtils.toDateString(recordInfo.getCreateTime(),DateUtils.PART_TIME_FORMAT)) ;
    	
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
		case R.id.work_btn_queding: // 数据库
			
			if(editRawSheet && !editSection){
				showText("你不能保存,请进入选择断面");
				return ;
			}
						
			// base
			String prefix		= section_new_et_prefix.getEditableText().toString().trim() ;
			String chainage 	= record_Chainage.getEditableText().toString().trim();// 里程
			String person 		= record_Person.getEditableText().toString().trim();
			String idcard 		= record_Card.getEditableText().toString().trim();
			String temperature 	= record_C.getEditableText().toString().trim();
			String descr 		= record_dotype.getEditableText().toString().trim();
			String currentTime 	= record_buildtime.getEditableText().toString().trim() ;

			if (StringUtils.isEmpty(chainage)) {
				showText("撑子面里程不能为空");
				return;
			}

			if (StringUtils.isEmpty(person)) {
				showText("测量员不能为空");
				return;
			}

			if (StringUtils.isEmpty(idcard)) {
				showText("身份证号不能为空");
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
				recordInfo.setPrefix(prefix);
				recordInfo.setFACEDK(CrtbUtils.formatDouble(chainage));
				recordInfo.setCreateTime(DateUtils.toDate(currentTime,DateUtils.PART_TIME_FORMAT));
				recordInfo.setSurveyer(person);
				recordInfo.setCertificateID(idcard);
				recordInfo.setTEMPERATURE(temp);
				recordInfo.setFACEDESCRIPTION(descr);
				recordInfo.setCrossSectionIDs(sections);
				
				RawSheetIndexDao.defaultDao().insert(recordInfo);
				
			} else {
				
				// 基本信息
				recordInfo.setCrossSectionType(RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES);
				recordInfo.setPrefix(prefix);
				recordInfo.setFACEDK(CrtbUtils.formatDouble(chainage));
				recordInfo.setCreateTime(DateUtils.toDate(currentTime,DateUtils.PART_TIME_FORMAT));
				recordInfo.setSurveyer(person);
				recordInfo.setCertificateID(idcard);
				recordInfo.setTEMPERATURE(temp);
				recordInfo.setFACEDESCRIPTION(descr);
				recordInfo.setCrossSectionIDs(sections);
				
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
