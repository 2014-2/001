package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;
import org.zw.android.framework.util.DateUtils;
import org.zw.android.framework.util.StringUtils;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tssurveyprovider.Coordinate3D;
import com.crtb.tssurveyprovider.ISurveyProvider;
import com.crtb.tssurveyprovider.TSSurveyProvider;
import com.crtb.tunnelmonitor.AppConfig;
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
import com.crtb.tunnelmonitor.utils.AlertUtils;
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
	
    private static int DEBUG_TEST_NUM = 0;//TODO: remove, just for debug
    private static double DEBUG_x = 2001.67;//TODO: remove, just for debug
    private static double DEBUG_y = 3422.35;//TODO: remove, just for debug
    private static double DEBUG_z = 4368.26;//TODO: remove, just for debug

	public static final String KEY_TEST_SECTION_TYPE	= "_key_test_section_type" ;
	
	public static final String KEY_TEST_OBJECT	= "_key_test_object" ;
	
	@InjectView(id=R.id.test_bottom_layout)
	private LinearLayout mBottomLayout ;
	
	@InjectView(id=R.id.test_bnt_left_section)
	private RelativeLayout mLeftBnt ;
	
	@InjectView(id=R.id.test_bnt_next_section)
	private RelativeLayout mNextBnt ;
	
	@InjectView(id=R.id.test_container_layout)
	private LinearLayout mContainerLayout ;
	
	private int sectionType ;
	private RawSheetIndex rawSheet ;
	private final List<TunnelCrossSectionIndex> tunnelSectionList = new ArrayList<TunnelCrossSectionIndex>();
	private final List<SubsidenceCrossSectionIndex> subsidenceSectionList = new ArrayList<SubsidenceCrossSectionIndex>();
	
	private int sectionIndex ;
	private TunnelCrossSectionIndex tunnelSection ;
	private SubsidenceCrossSectionIndex subsidenceSection ;
	
	private TunnelSettlementTotalData pS1,pS2 ;
	
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
			
			if(tunnelSectionList.size() > 0){
				
				sectionIndex = 0 ;
				mBottomLayout.setVisibility(View.VISIBLE);
				mLeftBnt.setOnClickListener(this);
				mNextBnt.setOnClickListener(this);
				
				// 当前断面
				tunnelSection	= tunnelSectionList.get(sectionIndex);
				
				// 当前测量信息
				loadSectionTestData() ;
			}
			
			// 是否存在下一个
			if(tunnelSectionList.size() == 1){
				mBottomLayout.setVisibility(View.GONE);
			}
		} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
			
			SubsidenceCrossSectionIndexDao dao = SubsidenceCrossSectionIndexDao.defaultDao() ;
			
			for(String id : ids){
				
				SubsidenceCrossSectionIndex bean = dao.querySectionIndex(id);
				
				if(bean != null){
					subsidenceSectionList.add(bean);
				}
			}
			
			if(subsidenceSectionList.size() > 0){
				
				sectionIndex = 0 ;
				mBottomLayout.setVisibility(View.VISIBLE);
				mLeftBnt.setOnClickListener(this);
				mNextBnt.setOnClickListener(this);
				
				subsidenceSection	= subsidenceSectionList.get(sectionIndex);
				
				// 当前测量信息
				loadSectionTestData() ;
			}
			
			// 是否存在下一个
			if (subsidenceSectionList.size() == 1) {
				mBottomLayout.setVisibility(View.GONE);
			}
		}
	}
	
	private void addTestPoint(View view){
		mContainerLayout.addView(view);
	}
	
	private View createTunnelTestPointView(TunnelSettlementTotalData bean,final String type){
		
		final TestPointHolder holder 	= new TestPointHolder() ;
		View view = InjectCore.injectOriginalObject(holder);
		
		if(bean != null){
			
			// 存在的测量数据
			String coord = bean.getCoordinate() != null ? bean.getCoordinate() : "";
			
			// 默认值
			String[] str = coord.split(",");
			if (str.length == 3) {
				holder.mPointX.setText(str[0]);
				holder.mPointY.setText(str[1]);
				holder.mPointZ.setText(str[2]);
			}
			
			// 测试时间
			holder.mPointTime.setText(DateUtils.toDateString(bean.getSurveyTime(), DateUtils.PART_TIME_FORMAT));
		}
		
		// 测点类型
		holder.mPointType.setText(type);
		
		// 测量
		holder.mPointTestBnt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
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
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				
				String x = String.format("%1$.4f", point.N);
				String y = String.format("%1$.4f", point.E);
				String z = String.format("%1$.4f", point.H);
				String time = Time.getDateEN() ;
				
				holder.mPointX.setText(x);
				holder.mPointY.setText(y);
				holder.mPointZ.setText(z);
				holder.mPointTime.setText(time);
				
				TunnelSettlementTotalData obj = new TunnelSettlementTotalData() ;
				obj.setStationId(0x000001);
				obj.setChainageId(tunnelSection.getID());
				obj.setSheetId(rawSheet.getID());
				obj.setPntType(type); // 测量点类型
				obj.setSurveyorID(Integer.valueOf(rawSheet.getCertificateID()));// 测量人员id
				
				TunnelSettlementTotalDataDao dao 	= TunnelSettlementTotalDataDao.defaultDao() ;
				
				// 存在的测量点信息
				TunnelSettlementTotalData old = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(),type);
				
				if(old != null){
					obj.setMEASNo(old.getMEASNo() + 1);
				} else {
					obj.setMEASNo(1);
				}
				
				obj.setCoordinate(z + "," + y + "," + z);
				obj.setSurveyTime(DateUtils.toDate(time, DateUtils.DATE_TIME_FORMAT));
				obj.setDataStatus(0);
				
				// 插入
				if(dao.insert(obj) == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
					
					if(type.equals(AppConfig.POINT_A)){
						doWarring(holder, AlertUtils.getPointSubsidenceExceedMsg(obj));
					} else {
						
						if(type.equals(AppConfig.POINT_S1_1)
								|| type.equals(AppConfig.POINT_S2_1)
								|| type.equals(AppConfig.POINT_S3_1)){
							pS1	= obj ;
						} else {
							pS2	= obj ;
						}
						
						if(pS1 != null && pS2 != null){
							doWarringLine(holder);
						}
					}
					
					showText("保存成功");
				} else {
					showText("保存失败");
				}
			}
		}) ;
		
		return view ;
	}
	
	private void doWarringLine(TestPointHolder holder){
		
	    String[] list = AlertUtils.getLineConvergenceExceedMsg(pS1,pS2);

		doWarring(holder, list);

		pS1 = null;
		pS2 = null;
	}
	
	private void doWarring(TestPointHolder holder, String[] list){
		
		if(list == null || list.length == 0){
			System.out.println("zhouwei : 检查拱顶 ------ 没有错误");
			return ;
		}
		
		holder.warringLayout.removeAllViews() ;
		holder.warringLayout.setVisibility(View.VISIBLE);
		
		for(String msg : list){
			
			TextView tv = new TextView(this);
			tv.setTextColor(Color.RED);
			tv.setTextSize(20);
			
			if(!StringUtils.isEmpty(msg)){
				tv.setText(msg);
				holder.warringLayout.addView(tv);
			}
		}
	}
	
	private View createSubsidenceTestPointView(SubsidenceTotalData bean,final String type){
		
		final TestPointHolder holder 	= new TestPointHolder() ;
		
		View view = InjectCore.injectOriginalObject(holder);
		
		// 测点类型
		holder.mPointType.setText(type);
		
		if(bean != null){
			
			String coord = bean.getCoordinate() != null ? bean.getCoordinate() : "";
			
			// 默认值
			String[] str = coord.split(",");
			if (str.length == 3) {
				holder.mPointX.setText(str[0]);
				holder.mPointY.setText(str[1]);
				holder.mPointZ.setText(str[2]);
			}

			// 测试时间
			holder.mPointTime.setText(DateUtils.toDateString(bean.getSurveyTime(), DateUtils.PART_TIME_FORMAT));
		}
		
		// 测量
		holder.mPointTestBnt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
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
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				
				String x = String.format("%1$.4f", point.N);
				String y = String.format("%1$.4f", point.E);
				String z = String.format("%1$.4f", point.H);
				String time = Time.getDateEN() ;
				
				holder.mPointX.setText(x);
				holder.mPointY.setText(y);
				holder.mPointZ.setText(z);
				holder.mPointTime.setText(time);
				
				SubsidenceTotalDataDao dao 	= SubsidenceTotalDataDao.defaultDao() ;
				
				// 存在的测量点信息
				SubsidenceTotalData old = dao.queryTunnelTotalData(rawSheet.getID(),subsidenceSection.getID(),type);
				
				final SubsidenceTotalData obj = new SubsidenceTotalData() ;
				obj.setStationId(0x000001);
				obj.setChainageId(subsidenceSection.getID());
				obj.setSheetId(rawSheet.getID());
				obj.setPntType(type); // 测量点类型
				obj.setSurveyorID(Integer.valueOf(rawSheet.getCertificateID()));// 测量人员id
				
				if(old != null){
					obj.setMEASNo(old.getMEASNo() + 1);
				} else {
					obj.setMEASNo(1);
				}
				
				obj.setCoordinate(z + "," + y + "," + z);
				obj.setSurveyTime(DateUtils.toDate(time, DateUtils.DATE_TIME_FORMAT));
				obj.setDataStatus(0);
				
				// 插入
				if(dao.insert(obj) == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
					showText("保存成功");
				} else {
					showText("保存失败");
				}
			}
		}) ;
		
		return view ;
	}
	
	private void loadSectionTestData(){
		
		// 删除所以的测量点view
		mContainerLayout.removeAllViews() ;
		
		if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
			
			TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao();
			String method 	= tunnelSection.getExcavateMethod() ;
			int type 		= CrtbUtils.getExcavateMethod(CrtbUtils.getExcavateMethodByStr(method));
			pS1				= null ;
			pS2				= null ;
			
			if(type == -1){
				showText("无效开挖类型: " + method);
				return ;
			}
			
			TunnelSettlementTotalData bean = null ;
			
			// A
			bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_A);
			addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_A));
			
			// 全断面法(A,S1(1,2))
			if(type == 0){
				
				// S1-1
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S1_1);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S1_1));
				
				// S1-2
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S1_2);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S1_2));
			} 
			// 台阶法(A,S1(1,2),S2(1,2))
			else if(type == 1){
				
				// S1-1
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S1_1);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S1_1));
				
				// S1-2
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S1_2);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S1_2));
				
				// S2-1
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S2_1);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S2_1));
				
				// S2-2
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S2_2);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S2_2));
			} 
			// 三台阶法(A,S1(1,2),S2(1,2),S3(1,2))
			else if(type == 2){
				
				// S1-1
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S1_1);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S1_1));
				
				// S1-2
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S1_2);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S1_2));
				
				// S2-1
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S2_1);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S2_1));
				
				// S2-2
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S2_2);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S2_2));
				
				// S3-1
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S3_1);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S3_1));
				
				// S3-2
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S3_2);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S3_2));
			} 
			// 双侧壁法(A,S1(1,2),S2(1,2),S3(1,2))
			else if(type == 3){
				
				// S1-1
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S1_1);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S1_1));
				
				// S1-2
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S1_2);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S1_2));
				
				// S2-1
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S2_1);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S2_1));
				
				// S2-2
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S2_2);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S2_2));
				
				// S3-1
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S3_1);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S3_1));
				
				// S3-2
				bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(), AppConfig.POINT_S3_2);
				addTestPoint(createTunnelTestPointView(bean,AppConfig.POINT_S3_2));
			}
		} 
		// 地表下沉断面测量---临时值
		else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
			
			String points = subsidenceSection.getSurveyPnts();
			int size = 0 ;
			
			try{
				if(!StringUtils.isEmpty(points)){
					size = Integer.valueOf(points) ;
				}
			} catch(Exception e){
				e.printStackTrace() ;
				showText("地表下沉隧道测量点错误");
			}
			
			SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao();
			SubsidenceTotalData bean = null ;
			
			for(int id = 0 ; id < size ; id++){
				
				String index = String.valueOf(id + 1) ;
				
				bean = dao.queryTunnelTotalData(rawSheet.getID(),subsidenceSection.getID(),index);
				addTestPoint(createSubsidenceTestPointView(bean,index));
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
		
	@InjectLayout(layout=R.layout.test_record_point_layout)
	final class TestPointHolder {
		
		@InjectView(id=R.id.test_point_type)
		TextView mPointType ;
		
		@InjectView(id=R.id.test_point_x)
		TextView mPointX ;
		
		@InjectView(id=R.id.test_point_y)
		TextView mPointY ;
		
		@InjectView(id=R.id.test_point_z)
		TextView mPointZ ;
		
		@InjectView(id=R.id.test_point_time)
		TextView mPointTime ;
		
		@InjectView(id=R.id.bnt_test)
		Button 	mPointTestBnt ;
		
		@InjectView(id=R.id.test_warring_layout)
		LinearLayout warringLayout ;
	}
}
