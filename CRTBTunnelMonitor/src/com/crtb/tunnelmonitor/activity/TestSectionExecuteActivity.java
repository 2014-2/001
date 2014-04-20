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
	
	private int sectionType ;
	private RawSheetIndex rawSheet ;
	private final List<TunnelCrossSectionIndex> tunnelSectionList = new ArrayList<TunnelCrossSectionIndex>();
	private final List<SubsidenceCrossSectionIndex> subsidenceSectionList = new ArrayList<SubsidenceCrossSectionIndex>();
	
	private int sectionIndex ;
	private TunnelCrossSectionIndex tunnelSection ;
	private SubsidenceCrossSectionIndex subsidenceSection ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);
		
		Object bean = CommonObject.findObject(KEY_TEST_OBJECT);
		sectionType = CommonObject.findObject(KEY_TEST_SECTION_TYPE);
		
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
			
			// 
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
				
				tunnelSection	= tunnelSectionList.get(sectionIndex);
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
			}
		}
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
				((TextView) findViewById(R.id.test_a_x)).setText(x);

				y = String.format("%1$.4f", point.E); // y
				((TextView) findViewById(R.id.test_a_y)).setText(y);

				z = String.format("%1$.4f", point.H); // z
				((TextView) findViewById(R.id.test_a_z)).setText(z);
				
				time = Time.getDateEN() ;

				((TextView) findViewById(R.id.test_a_time)).setText(time); // time
				
				// 保存A
				if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
					
					TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao() ;
					
					TunnelSettlementTotalData bean = new TunnelSettlementTotalData() ;
					bean.setStationId(0x000001);
					bean.setChainageId(tunnelSection.getID());
					bean.setSheetId(rawSheet.getID());
					bean.setCoordinate(z + "," + y + "," + z);
					bean.setPntType("A"); // 测量点类型
					bean.setSurveyTime(DateUtils.toDate(time, DateUtils.DATE_TIME_FORMAT));
					bean.setMEASNo(1);
					bean.setSurveyorID(Integer.valueOf(rawSheet.getCertificateID()));// 测量人员id
					bean.setDataStatus(0);
					
					if(dao.insert(bean) == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
						showText("保存成功");
					} else {
						showText("保存失败");
					}
				} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
					
					SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao() ;
					
					SubsidenceTotalData bean = new SubsidenceTotalData() ;
					bean.setStationId(0x000001);
					bean.setChainageId(subsidenceSection.getID());
					bean.setSheetId(rawSheet.getID());
					bean.setCoordinate(z + "," + y + "," + z);
					bean.setPntType("A"); // 测量点类型
					bean.setSurveyTime(DateUtils.toDate(time, DateUtils.DATE_TIME_FORMAT));
					bean.setMEASNo(1);
					bean.setSurveyorID(Integer.valueOf(rawSheet.getCertificateID()));// 测量人员id
					bean.setDataStatus(0);
					
					if(dao.insert(bean) == SubsidenceTotalDataDao.DB_EXECUTE_SUCCESS){
						showText("保存成功");
					} else {
						showText("保存失败");
					}
				}
				
				break;
			case R.id.bnt_test_s1_1:
				
				x = String.format("%1$.4f", point.N); // x
				((TextView) findViewById(R.id.test_s1_1_x)).setText(x);

				y = String.format("%1$.4f", point.E); // y
				((TextView) findViewById(R.id.test_s1_1_y)).setText(y);

				z = String.format("%1$.4f", point.H); // z
				((TextView) findViewById(R.id.test_s1_1_z)).setText(z);
				
				time = Time.getDateEN() ;

				((TextView) findViewById(R.id.test_s1_1_time)).setText(time); // time
				
				// 保存S1
				if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
					
					TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao() ;
					
					TunnelSettlementTotalData bean = new TunnelSettlementTotalData() ;
					bean.setStationId(0x000001);
					bean.setChainageId(tunnelSection.getID());
					bean.setSheetId(rawSheet.getID());
					bean.setCoordinate(z + "," + y + "," + z);
					bean.setPntType("S1"); // 测量点类型
					bean.setSurveyTime(DateUtils.toDate(time, DateUtils.DATE_TIME_FORMAT));
					bean.setMEASNo(1);
					bean.setSurveyorID(Integer.valueOf(rawSheet.getCertificateID()));// 测量人员id
					bean.setDataStatus(0);
					
					if(dao.insert(bean) == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
						showText("保存成功");
					} else {
						showText("保存失败");
					}
				} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
					
					SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao() ;
					
					SubsidenceTotalData bean = new SubsidenceTotalData() ;
					bean.setStationId(0x000001);
					bean.setChainageId(subsidenceSection.getID());
					bean.setSheetId(rawSheet.getID());
					bean.setCoordinate(z + "," + y + "," + z);
					bean.setPntType("S1"); // 测量点类型
					bean.setSurveyTime(DateUtils.toDate(time, DateUtils.DATE_TIME_FORMAT));
					bean.setMEASNo(1);
					bean.setSurveyorID(Integer.valueOf(rawSheet.getCertificateID()));// 测量人员id
					bean.setDataStatus(0);
					
					if(dao.insert(bean) == SubsidenceTotalDataDao.DB_EXECUTE_SUCCESS){
						showText("保存成功");
					} else {
						showText("保存失败");
					}
				}
				
				break;
			case R.id.bnt_test_s1_2:
				x = String.format("%1$.4f", point.N); // x
				((TextView) findViewById(R.id.test_s1_2_x)).setText(x);

				y = String.format("%1$.4f", point.E); // y
				((TextView) findViewById(R.id.test_s1_2_y)).setText(y);

				z = String.format("%1$.4f", point.H); // z
				((TextView) findViewById(R.id.test_s1_2_z)).setText(z);

				time = Time.getDateEN() ;
				
				((TextView) findViewById(R.id.test_s1_2_time)).setText(time); // time
				
				// 保存S2
				if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
					
					TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao() ;
					
					TunnelSettlementTotalData bean = new TunnelSettlementTotalData() ;
					bean.setStationId(0x000001);
					bean.setChainageId(tunnelSection.getID());
					bean.setSheetId(rawSheet.getID());
					bean.setCoordinate(z + "," + y + "," + z);
					bean.setPntType("S2"); // 测量点类型
					bean.setSurveyTime(DateUtils.toDate(time, DateUtils.DATE_TIME_FORMAT));
					bean.setMEASNo(1);
					bean.setSurveyorID(Integer.valueOf(rawSheet.getCertificateID()));// 测量人员id
					bean.setDataStatus(0);
					
					if(dao.insert(bean) == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
						showText("保存成功");
					} else {
						showText("保存失败");
					}
				} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
					
					SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao() ;
					
					SubsidenceTotalData bean = new SubsidenceTotalData() ;
					bean.setStationId(0x000001);
					bean.setChainageId(subsidenceSection.getID());
					bean.setSheetId(rawSheet.getID());
					bean.setCoordinate(z + "," + y + "," + z);
					bean.setPntType("S2"); // 测量点类型
					bean.setSurveyTime(DateUtils.toDate(time, DateUtils.DATE_TIME_FORMAT));
					bean.setMEASNo(1);
					bean.setSurveyorID(Integer.valueOf(rawSheet.getCertificateID()));// 测量人员id
					bean.setDataStatus(0);
					
					if(dao.insert(bean) == SubsidenceTotalDataDao.DB_EXECUTE_SUCCESS){
						showText("保存成功");
					} else {
						showText("保存失败");
					}
				}
				break;
			}
		}
	};

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
				
			} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
				sectionIndex-- ;
				subsidenceSection = subsidenceSectionList.get(sectionIndex);
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
				}
				
			} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
				
				if(sectionIndex >= subsidenceSectionList.size() - 1){
					showText("已经是最后一条数据");
					return ;
				}
				
				if(sectionIndex + 1 < subsidenceSectionList.size()){
					
					sectionIndex++ ;
					subsidenceSection = subsidenceSectionList.get(sectionIndex);
				}
			}
			
			break ;
		}
	}
		
}
