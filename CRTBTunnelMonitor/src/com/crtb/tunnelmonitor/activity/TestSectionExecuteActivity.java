package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;
import org.zw.android.framework.util.DateUtils;
import org.zw.android.framework.util.StringUtils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tssurveyprovider.Coordinate3D;
import com.crtb.tssurveyprovider.ISurveyProvider;
import com.crtb.tssurveyprovider.TSSurveyProvider;
import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
import com.crtb.tunnelmonitor.utils.Time;

/**
 * 开始测量
 * 
 * @author zhouwei
 *
 */
@InjectLayout(layout=R.layout.activity_testrecord_execute)
public class TestSectionExecuteActivity extends WorkFlowActivity implements View.OnClickListener {
	
	public static final String KEY_TEST_SECTION_TYPE	= "_key_test_section_type" ;
	
	public static final String KEY_TEST_OBJECT	= "_key_test_object" ;
	
	@InjectView(id=R.id.test_bottom_layout)
	private LinearLayout mBottomLayout ;
	
	@InjectView(id=R.id.test_bnt_left_section)
	private RelativeLayout mLeftBnt ;
	
	@InjectView(id=R.id.test_bnt_next_section)
	private RelativeLayout mNextBnt ;
	
	/////////////////////////A///////////////////////
	@InjectView(id=R.id.test_a_x)
	private TextView mTvAX ;
	
	@InjectView(id=R.id.test_a_y)
	private TextView mTvAY ;
	
	@InjectView(id=R.id.test_a_z)
	private TextView mTvAZ ;
	
	@InjectView(id=R.id.test_a_time)
	private TextView mTvATime ;
	
	/////////////////////////S1///////////////////////
	@InjectView(id=R.id.test_s1_1_x)
	private TextView mTvS1X ;
	
	@InjectView(id=R.id.test_s1_1_y)
	private TextView mTvS1Y ;
	
	@InjectView(id=R.id.test_s1_1_z)
	private TextView mTvS1Z ;
	
	@InjectView(id=R.id.test_s1_1_time)
	private TextView mTvS1Time ;
	
	/////////////////////////S1-2///////////////////////
	@InjectView(id=R.id.test_s1_2_x)
	private TextView mTvS2X ;
	
	@InjectView(id=R.id.test_s1_2_y)
	private TextView mTvS2Y ;
	
	@InjectView(id=R.id.test_s1_2_z)
	private TextView mTvS2Z ;
	
	@InjectView(id=R.id.test_s1_2_time)
	private TextView mTvS2Time ;
	
	private int sectionType ;
	private RawSheetIndex rawSheet ;
	private final List<TunnelCrossSectionIndex> tunnelSectionList = new ArrayList<TunnelCrossSectionIndex>();
	private final List<SubsidenceCrossSectionIndex> subsidenceSectionList = new ArrayList<SubsidenceCrossSectionIndex>();
	
	private int sectionIndex ;
	private TunnelCrossSectionIndex tunnelSection ;
	private SubsidenceCrossSectionIndex subsidenceSection ;
	
	private TunnelSettlementTotalData tunnelBeanA,tunnelBeanS1,tunnelBeanS2 ;// 隧道内断面测量数据
	private SubsidenceTotalData subsidenceBeanA,subsidenceBeanS1,subsidenceBeanS2 ; // 下沉断面测量数据
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);
		
		Object bean = CommonObject.findObject(KEY_TEST_OBJECT);
		sectionType = CommonObject.findInteger(KEY_TEST_SECTION_TYPE);
		
		// 测量数据
		rawSheet = (RawSheetIndex)bean ;
		
		// set title
		setTopbarTitle(CrtbUtils.formatSectionName(rawSheet.getPrefix(), (float)rawSheet.getFACEDK()));
		
		tunnelSectionList.clear() ;
		subsidenceSectionList.clear() ;
		
		// 测量按钮事件
		findViewById(R.id.bnt_test_a).setOnClickListener(mMeasListener);
		findViewById(R.id.bnt_test_s1_1).setOnClickListener(mMeasListener);
		findViewById(R.id.bnt_test_s1_2).setOnClickListener(mMeasListener);
		
		String sectionIds = rawSheet.getCrossSectionIDs() ;
		
		if(StringUtils.isEmpty(sectionIds)){
			showText("没有断面信息");
		} else {
			
			String[] ids = sectionIds.split(",");
			
			// 加载断面信息
			loadSections(ids);
		}
	}
	
	private void loadSections(String[] ids){
		
		if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
			
			TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao() ;
			
			for(String id : ids){
				
				TunnelCrossSectionIndex bean = dao.querySectionIndex(id);
				
				if(bean != null){
					tunnelSectionList.add(bean);
				}
			}
			
			if(tunnelSectionList.size() > 1){
				
				sectionIndex = 0 ;
				mBottomLayout.setVisibility(View.VISIBLE);
				mLeftBnt.setOnClickListener(this);
				mNextBnt.setOnClickListener(this);
				
				// 当前断面
				tunnelSection	= tunnelSectionList.get(sectionIndex);
				
				// 当前测量信息
				loadSectionTestData() ;
			}
		} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
			
			SubsidenceCrossSectionIndexDao dao = SubsidenceCrossSectionIndexDao.defaultDao() ;
			
			for(String id : ids){
				
				SubsidenceCrossSectionIndex bean = dao.querySectionIndex(id);
				
				if(bean != null){
					subsidenceSectionList.add(bean);
				}
			}
			
			if(subsidenceSectionList.size() > 1){
				
				sectionIndex = 0 ;
				mBottomLayout.setVisibility(View.VISIBLE);
				mLeftBnt.setOnClickListener(this);
				mNextBnt.setOnClickListener(this);
				
				subsidenceSection	= subsidenceSectionList.get(sectionIndex);
				
				// 当前测量信息
				loadSectionTestData() ;
			}
		}
	}
	
	private void loadSectionTestData(){
		
		clearData() ;
		
		if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
			
			// 是否已经存在A测量点信息
			tunnelBeanA = TunnelSettlementTotalDataDao.defaultDao()
					.queryTunnelTotalData(rawSheet.getID(),
							tunnelSection.getID(), "A");

			if (tunnelBeanA != null) {

				String coord = tunnelBeanA.getCoordinate() != null ? tunnelBeanA
						.getCoordinate() : "";

				String[] str = coord.split(",");

				if (str.length == 3) {
					mTvAX.setText(str[0]);
					mTvAY.setText(str[1]);
					mTvAZ.setText(str[2]);
				}

				mTvATime.setText(DateUtils.toDateString(
						tunnelBeanA.getSurveyTime(), DateUtils.PART_TIME_FORMAT));
			}
			
			// 是否已经存在A测量点信息
			tunnelBeanS1 = TunnelSettlementTotalDataDao.defaultDao()
					.queryTunnelTotalData(rawSheet.getID(),
							tunnelSection.getID(), "S1");

			if (tunnelBeanS1 != null) {

				String coord = tunnelBeanS1.getCoordinate() != null ? tunnelBeanS1
						.getCoordinate() : "";

				String[] str = coord.split(",");

				if (str.length == 3) {
					mTvS1X.setText(str[0]);
					mTvS1Y.setText(str[1]);
					mTvS1Z.setText(str[2]);
				}

				mTvS1Time.setText(DateUtils.toDateString(
						tunnelBeanS1.getSurveyTime(),
						DateUtils.PART_TIME_FORMAT));
			}
			
			// 是否已经存在A测量点信息
			tunnelBeanS2 = TunnelSettlementTotalDataDao.defaultDao()
					.queryTunnelTotalData(rawSheet.getID(),
							tunnelSection.getID(), "S2");

			if (tunnelBeanS2 != null) {

				String coord = tunnelBeanS2.getCoordinate() != null ? tunnelBeanS2
						.getCoordinate() : "";

				String[] str = coord.split(",");

				if (str.length == 3) {
					mTvS2X.setText(str[0]);
					mTvS2Y.setText(str[1]);
					mTvS2Z.setText(str[2]);
				}

				mTvS2Time.setText(DateUtils.toDateString(
						tunnelBeanS2.getSurveyTime(),
						DateUtils.PART_TIME_FORMAT));
			}
		} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
			
			
			// 是否已经存在A测量点信息
			subsidenceBeanA = SubsidenceTotalDataDao.defaultDao()
					.queryTunnelTotalData(rawSheet.getID(),
							tunnelSection.getID(), "A");

			if (subsidenceBeanA != null) {

				String coord = subsidenceBeanA.getCoordinate() != null ? subsidenceBeanA
						.getCoordinate() : "";

				String[] str = coord.split(",");

				if (str.length == 3) {
					mTvAX.setText(str[0]);
					mTvAY.setText(str[1]);
					mTvAZ.setText(str[2]);
				}

				mTvATime.setText(DateUtils.toDateString(
						subsidenceBeanA.getSurveyTime(), DateUtils.PART_TIME_FORMAT));
			}

			// 是否已经存在A测量点信息
			subsidenceBeanS1 = SubsidenceTotalDataDao.defaultDao()
					.queryTunnelTotalData(rawSheet.getID(),
							tunnelSection.getID(), "S1");

			if (subsidenceBeanS1 != null) {

				String coord = subsidenceBeanS1.getCoordinate() != null ? subsidenceBeanS1
						.getCoordinate() : "";

				String[] str = coord.split(",");

				if (str.length == 3) {
					mTvS1X.setText(str[0]);
					mTvS1Y.setText(str[1]);
					mTvS1Z.setText(str[2]);
				}

				mTvS1Time.setText(DateUtils.toDateString(
						subsidenceBeanS1.getSurveyTime(),
						DateUtils.PART_TIME_FORMAT));
			}

			// 是否已经存在A测量点信息
			subsidenceBeanS2 = SubsidenceTotalDataDao.defaultDao()
					.queryTunnelTotalData(rawSheet.getID(),
							tunnelSection.getID(), "S2");

			if (subsidenceBeanS2 != null) {

				String coord = subsidenceBeanS2.getCoordinate() != null ? subsidenceBeanS2
						.getCoordinate() : "";

				String[] str = coord.split(",");

				if (str.length == 3) {
					mTvS2X.setText(str[0]);
					mTvS2Y.setText(str[1]);
					mTvS2Z.setText(str[2]);
				}

				mTvS2Time.setText(DateUtils.toDateString(
						subsidenceBeanS2.getSurveyTime(),
						DateUtils.PART_TIME_FORMAT));
			}
		}
	}

	private void clearData() {

		mTvAX.setText("");
		mTvAY.setText("");
		mTvAZ.setText("");
		mTvATime.setText("");

		mTvS1X.setText("");
		mTvS1Y.setText("");
		mTvS1Z.setText("");
		mTvS1Time.setText("");

		mTvS2X.setText("");
		mTvS2Y.setText("");
		mTvS2Z.setText("");
		mTvS2Time.setText("");
	}

	private OnClickListener mMeasListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			// 从全站仪得到数据
			
			ISurveyProvider ts = TSSurveyProvider.getDefaultAdapter();
			
			if (ts == null) {
				Toast.makeText(TestSectionExecuteActivity.this, "请先连接全站仪",
						Toast.LENGTH_SHORT).show();
				return;
			}

			Coordinate3D point = new Coordinate3D(null);
			try {
				int nret = ts.GetCoord(0, 0, point);
				if (nret != 1) {
					Toast.makeText(TestSectionExecuteActivity.this, "测量失败",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// String text = String.format("%1$s,%2$s,%3$s",
				// point.N,point.E,point.H);
				// tv.setText(text);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}

			String text = "" , x = "" , y = "" ,z = "", time = Time.getDateEN() ;

			Button btn = (Button) v;
			switch (btn.getId()) {
			case R.id.bnt_test_a:
				
				x = String.format("%1$.4f", point.N); // x
				mTvAX.setText(x);

				y = String.format("%1$.4f", point.E); // y
				mTvAY.setText(y);

				z = String.format("%1$.4f", point.H); // z
				mTvAZ.setText(z);
				
				time = Time.getDateEN() ;

				mTvATime.setText(time); // time
				
				// 保存A
				insertTestData("A",x,y,z,time);
				
				break;
			case R.id.bnt_test_s1_1:
				
				x = String.format("%1$.4f", point.N); // x
				mTvS1X.setText(x);

				y = String.format("%1$.4f", point.E); // y
				mTvS1Y.setText(y);

				z = String.format("%1$.4f", point.H); // z
				mTvS1Z.setText(z);
				
				time = Time.getDateEN() ;

				mTvS1Time.setText(time); // time
				
				// 保存S1
				insertTestData("S1",x,y,z,time);
				break;
			case R.id.bnt_test_s1_2:
				
				x = String.format("%1$.4f", point.N); // x
				mTvS2X.setText(x);

				y = String.format("%1$.4f", point.E); // y
				mTvS2Y.setText(y);

				z = String.format("%1$.4f", point.H); // z
				mTvS2Z.setText(z);

				time = Time.getDateEN() ;
				
				mTvS2Time.setText(time); // time
				
				// 保存S2
				insertTestData("S2",x,y,z,time);
				
				break;
			}
		}
	};
	
	private void insertTestData(String type,String x,String y,String z,String time){
		
		// 保存S2
		if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
			
			TunnelSettlementTotalDataDao dao 	= TunnelSettlementTotalDataDao.defaultDao() ;
			TunnelSettlementTotalData bean 		= null ;
			boolean insert = false ;
			
			if(type.equals("A") && tunnelBeanA != null){
				tunnelBeanA.setMEASNo(tunnelBeanA.getMEASNo() + 1);
				bean		= tunnelBeanA ;
			} else if(type.equals("S1") && tunnelBeanS1 != null){
				tunnelBeanS1.setMEASNo(tunnelBeanS1.getMEASNo() + 1);
				bean		= tunnelBeanS1 ;
			} else if(type.equals("S2") && tunnelBeanS2 != null){
				tunnelBeanS2.setMEASNo(tunnelBeanS2.getMEASNo() + 1);
				bean		= tunnelBeanS2 ;
			} 
			
			// 是否新建数据
			if(bean == null){
				insert 	= true ;
				bean	= new TunnelSettlementTotalData() ;
				bean.setStationId(0x000001);
				bean.setChainageId(tunnelSection.getID());
				bean.setSheetId(rawSheet.getID());
				bean.setPntType(type); // 测量点类型
				bean.setMEASNo(1);
				bean.setSurveyorID(Integer.valueOf(rawSheet.getCertificateID()));// 测量人员id
			}
			
			bean.setCoordinate(z + "," + y + "," + z);
			bean.setSurveyTime(DateUtils.toDate(time, DateUtils.DATE_TIME_FORMAT));
			bean.setDataStatus(0);
			
			// 插入
			if(insert){
				if(dao.insert(bean) == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
					showText("保存成功");
				} else {
					showText("保存失败");
				}
			} 
			// 更新
			else {
				if(dao.update(bean) == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
					showText("更新成功");
				} else {
					showText("更新失败");
				}
			}
			
		} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
			
			SubsidenceTotalDataDao dao 	= SubsidenceTotalDataDao.defaultDao() ;
			SubsidenceTotalData bean 	= null ;
			boolean insert = false ;
			
			if(type.equals("A") && subsidenceBeanA != null){
				subsidenceBeanA.setMEASNo(subsidenceBeanA.getMEASNo() + 1);
				bean		= subsidenceBeanA ;
			} else if(type.equals("S1") && subsidenceBeanS1 != null){
				subsidenceBeanS1.setMEASNo(subsidenceBeanS1.getMEASNo() + 1);
				bean		= subsidenceBeanS1 ;
			} else if(type.equals("S2") && subsidenceBeanS2 != null){
				subsidenceBeanS2.setMEASNo(subsidenceBeanS2.getMEASNo() + 1);
				bean		= subsidenceBeanS2 ;
			} 
			
			if(bean == null){
				insert 	= true ;
				bean 	= new SubsidenceTotalData() ;
				bean.setStationId(0x000001);
				bean.setChainageId(subsidenceSection.getID());
				bean.setSheetId(rawSheet.getID());
				bean.setPntType(type); // 测量点类型
				bean.setMEASNo(1);
				bean.setSurveyorID(Integer.valueOf(rawSheet.getCertificateID()));// 测量人员id
			}
			
			bean.setCoordinate(z + "," + y + "," + z);
			bean.setSurveyTime(DateUtils.toDate(time, DateUtils.DATE_TIME_FORMAT));
			bean.setDataStatus(0);
			
			if(insert){
				if(dao.insert(bean) == SubsidenceTotalDataDao.DB_EXECUTE_SUCCESS){
					showText("保存成功");
				} else {
					showText("保存失败");
				}
			} else {
				if(dao.update(bean) == SubsidenceTotalDataDao.DB_EXECUTE_SUCCESS){
					showText("更新成功");
				} else {
					showText("更新失败");
				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.test_bnt_left_section :
			
			if(sectionIndex <= 0){
				showText("已经是第一条数据");
				return ;
			}
			
			if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
				
				sectionIndex-- ;
				tunnelSection = tunnelSectionList.get(sectionIndex);
				
				loadSectionTestData();
			} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
				sectionIndex-- ;
				subsidenceSection = subsidenceSectionList.get(sectionIndex);
				
				loadSectionTestData();
			}
			break ;
		case R.id.test_bnt_next_section :
			
			if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
				
				if(sectionIndex >= tunnelSectionList.size() - 1){
					showText("已经是最后一条数据");
					return ;
				}
				
				if(sectionIndex + 1 < tunnelSectionList.size()){
					sectionIndex++ ;
					tunnelSection = tunnelSectionList.get(sectionIndex);
					
					loadSectionTestData();
				}
				
			} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
				
				if(sectionIndex >= subsidenceSectionList.size() - 1){
					showText("已经是最后一条数据");
					return ;
				}
				
				if(sectionIndex + 1 < subsidenceSectionList.size()){
					
					sectionIndex++ ;
					subsidenceSection = subsidenceSectionList.get(sectionIndex);
					
					loadSectionTestData();
				}
			}
			
			break ;
		}
	}
		
}
