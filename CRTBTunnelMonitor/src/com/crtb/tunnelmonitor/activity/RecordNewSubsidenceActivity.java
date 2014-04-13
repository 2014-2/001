package com.crtb.tunnelmonitor.activity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.adapter.RecordSubsidenceCrossSectionInfoAdapter;
import com.crtb.tunnelmonitor.adapter.RecordTunnelCrossSectionInfoAdapter;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.RecordDaoImpl;
import com.crtb.tunnelmonitor.dao.impl.SubsidenceCrossSectionDaoImpl;
import com.crtb.tunnelmonitor.dao.impl.TunnelCrossSectionDaoImpl;
import com.crtb.tunnelmonitor.entity.RecordInfo;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionInfo;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;
import com.crtb.tunnelmonitor.entity.WorkInfos;
import com.crtb.tunnelmonitor.entity.WorkPlan;
import com.crtb.tunnelmonitor.utils.Time;

/**
 * 新建地表下沉记录单
 *
 */
@InjectLayout(layout = R.layout.activity_record_new)
public class RecordNewSubsidenceActivity extends WorkFlowActivity implements OnPageChangeListener, OnClickListener {
   
	@InjectView(id=R.id.vPager)
	private ViewPager mPager;
	
	@InjectView(id=R.id.cursor)
	private ImageView cursor;
	
    ArrayList<View> list = new ArrayList<View>();
    
    private TextView t1, t2;
    private int offset = 0;
    private int currIndex = 0;
    private int bmpW;
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
	
	@InjectView(id=R.id.record_dotype,parent="mBaseInfoLayout")
	private EditText record_dotype;
	
	////////////////////////////section info///////////////////////
	@InjectView(layout=R.layout.record_new_xinxi)
	private LinearLayout mSectionListLayout ;
    
    LinearLayout xin;
    private int num,itype;
    private TextView record_new_tv_header;
    
    private List<TunnelCrossSectionInfo> infos = null;
    private List<SubsidenceCrossSectionInfo> infos1 = null;

    private RecordTunnelCrossSectionInfoAdapter adapter = null;
    private RecordSubsidenceCrossSectionInfoAdapter adapter1 = null;
    
	private RecordInfo editInfo = null;
	private AppCRTBApplication CurApp = null;
	
	private WorkPlan mCurrentWorkPlan;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_record_new);
        
		// add by wei.zhou
		InjectCore.injectUIProperty(this);

		// title
		setTopbarTitle(getString(R.string.record_new_section_subisdence_title));
		
		// init ViewPager
		initViewPager();
		
		mCurrentWorkPlan = CommonObject.findObject(KEY_CURRENT_WORKPLAN);
		
		// prefix
		section_new_et_prefix.setText(mCurrentWorkPlan.getMileagePrefix());
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
//			if(record_Chainage.getText().toString().trim().length() <= 0)
//			{
//				Toast.makeText(this, "请输入完整信息", 3000).show();
//				return;
//			}
//			WorkInfos Curw = CurApp.GetCurWork();
//			RecordInfo ts = new RecordInfo();
//			if (editInfo != null) {
//				ts.setId(editInfo.getId());
//			}
//			ts.setFacedk(Double.valueOf(record_Chainage.getText().toString().trim()));
//			ts.setCrossSectionType(itype);
//			// 获取手机的当前时间
//			final String time = Time.getDateEN();
//			ts.setCreateTime(Timestamp.valueOf(time));
//			ts.setFacedescription(record_dotype.getText().toString().trim());
//			ts.setTemperature(Double.valueOf(record_C.getText().toString().trim()));
//			ts.setInfo("");
//			if (itype == 1) {
//				ts.setCrossSectionIDs(AppCRTBApplication.GetSectionIDArrayForTunnelCrossArray(infos));
//			}
//			else {
//				ts.setCrossSectionIDs(AppCRTBApplication.GetSectionIDArrayForSubCrossArray(infos1));
//			}
//			ts.setChainageName(CurApp.GetSectionName(ts.getFacedk().doubleValue()));
//			if(!CurApp.IsValidRecordInfo(ts))
//			{
//				Toast.makeText(this, "请输入完整信息", 3000).show();
//				return;
//			}
//			if ((ts.getFacedk().doubleValue() < Curw.getStartChainage().doubleValue()) ||
//					(ts.getFacedk().doubleValue() > Curw.getEndChainage().doubleValue())){
//				String sStart = CurApp.GetSectionName(Curw.getStartChainage().doubleValue());
//				String sEnd = CurApp.GetSectionName(Curw.getEndChainage().doubleValue());
//				String sMsg = "请输入里程为"+sStart+"到"+sEnd+"之间的里程";
//				Toast.makeText(this, sMsg, 3000).show();
//				return;
//			}
//			List<RecordInfo> rinfos = null;
//			if (itype == 1) {
//				rinfos = Curw.getTcsirecordList();
//			}
//			else {
//				rinfos = Curw.getScsirecordList();
//			}
//			if(rinfos == null)
//			{
//				Toast.makeText(this, "添加失败", 3000).show();
//			}
//			else
//			{
//				if(editInfo == null)
//				{
//					RecordDaoImpl impl = new RecordDaoImpl(this,Curw.getProjectName());
//					if(impl.AddRecord(ts))
//					{
//						rinfos.add(ts);
//						CurApp.UpdateWork(Curw);
//						Toast.makeText(this, "添加成功", 3000).show();
//					}
//					else
//					{
//						Toast.makeText(this, "添加失败", 3000).show();
//					}
//				}
//				else
//				{
//					RecordDaoImpl impl = new RecordDaoImpl(this,Curw.getProjectName());
//					impl.UpdateRecord(ts);
//					Curw.UpdateRecordInfo(itype,ts);
//					CurApp.UpdateWork(Curw);
//					Toast.makeText(this, "编辑成功", 3000).show();
//				}
//			}
//			Intent IntentOk = new Intent();
//			IntentOk.putExtra(Constant.Select_SectionRowClickItemsName_Name,itype);
//			setResult(RESULT_OK, IntentOk);
//			this.finish();
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

//	public void setdata(int type) {
//    	AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
//		WorkInfos CurW = CurApp.GetCurWork();
//		if(CurW == null)
//		{
//			return;
//		}
//		if (type == 1) {
//			if(infos == null)
//			{
//				infos = new ArrayList<TunnelCrossSectionInfo>();
//			}
//			TunnelCrossSectionDaoImpl impl = new TunnelCrossSectionDaoImpl(this, CurW.getProjectName());
//			impl.GetTunnelCrossSectionList(infos);
//			if (editInfo != null) {
//				String sSels = editInfo.getCrossSectionIDs();
//				List<Integer> iSels = AppCRTBApplication.GetSectionIDArray(sSels);
//				for (int i = 0; i < infos.size(); i++) {
//					for (int j = 0; j < iSels.size(); j++) {
//						if (infos.get(i).getId() == iSels.get(j)) {
//							infos.get(i).setbUse(true);
//							break;
//						}
//					}
//				}
//			}
//		}
//		else {
//			if(infos1 == null)
//			{
//				infos1 = new ArrayList<SubsidenceCrossSectionInfo>();
//			}
//			SubsidenceCrossSectionDaoImpl impl = new SubsidenceCrossSectionDaoImpl(this, CurW.getProjectName());
//			impl.GetSubsidenceCrossSectionList(infos1);
//			if (editInfo != null) {
//				String sSels = editInfo.getCrossSectionIDs();
//				List<Integer> iSels = AppCRTBApplication.GetSectionIDArray(sSels);
//				for (int i = 0; i < infos1.size(); i++) {
//					for (int j = 0; j < iSels.size(); j++) {
//						if (infos1.get(i).getId() == iSels.get(j)) {
//							infos1.get(i).setbUse(true);
//							break;
//						}
//					}
//				}
//			}
//		}
//	}    

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        int single = (int) (b.getWidth() + offSet * 2);
        TranslateAnimation ta = new TranslateAnimation(currIndex * single,
                single * arg0, 0, 0);
        ta.setFillAfter(true);
        ta.setDuration(200);
        cursor.startAnimation(ta);
        currIndex = arg0;
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
