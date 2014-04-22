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
		}
	}
	
	private void addTestPoint(View view){
		mContainerLayout.addView(view);
	}
	
	private View createTunnelTestPointView(TunnelSettlementTotalData bean,String type){
		
		TunnelSettlementTotalData obj 	= bean ;
		final TestPointHolder holder 	= new TestPointHolder() ;
		
		if(obj == null){
			holder.insert	= true ;
			obj = new TunnelSettlementTotalData() ;
			obj.setStationId(0x000001);
			obj.setChainageId(tunnelSection.getID());
			obj.setSheetId(rawSheet.getID());
			obj.setPntType(type); // 测量点类型
			obj.setMEASNo(1);
			obj.setSurveyorID(Integer.valueOf(rawSheet.getCertificateID()));// 测量人员id
		} else {
			holder.insert = false ;
		}
		
		String coord = obj.getCoordinate() != null ? obj.getCoordinate() : "";
		
		View view = InjectCore.injectOriginalObject(holder);
		
		// 测点类型
		holder.mPointType.setText(type);
		
		// 默认值
		String[] str = coord.split(",");
		if (str.length == 3) {
			holder.mPointX.setText(str[0]);
			holder.mPointY.setText(str[1]);
			holder.mPointZ.setText(str[2]);
		}

		// 测试时间
		holder.mPointTime.setText(DateUtils.toDateString(obj.getSurveyTime(), DateUtils.PART_TIME_FORMAT));
		// 测量对象
		final TunnelSettlementTotalData item = obj ;
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
				
				TunnelSettlementTotalDataDao dao 	= TunnelSettlementTotalDataDao.defaultDao() ;
				
				if(holder.insert){
					item.setMEASNo(1);
				} else {
					item.setMEASNo(item.getMEASNo() + 1);
				}
				
				item.setCoordinate(z + "," + y + "," + z);
				item.setSurveyTime(DateUtils.toDate(time, DateUtils.DATE_TIME_FORMAT));
				item.setDataStatus(0);
				
				ArrayList<Integer> list = AlertUtils.checkPointSubsidenceExceed(item);
				
				if(list == null){
					
				}
				
				// 插入
				if(holder.insert){
					if(dao.insert(item) == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
						showText("保存成功");
					} else {
						showText("保存失败");
					}
				} 
				// 更新
				else {
					if(dao.update(item) == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
						showText("更新成功");
					} else {
						showText("更新失败");
					}
				}
			}
		}) ;
		
		return view ;
	}
	
	private View createSubsidenceTestPointView(SubsidenceTotalData bean,String type){
		
		SubsidenceTotalData obj 	= bean ;
		final TestPointHolder holder 	= new TestPointHolder() ;
		
		if(obj == null){
			holder.insert	= true ;
			obj = new SubsidenceTotalData() ;
			obj.setStationId(0x000001);
			obj.setChainageId(subsidenceSection.getID());
			obj.setSheetId(rawSheet.getID());
			obj.setPntType(type); // 测量点类型
			obj.setMEASNo(1);
			obj.setSurveyorID(Integer.valueOf(rawSheet.getCertificateID()));// 测量人员id
		} else {
			holder.insert = false ;
		}
		
		String coord = obj.getCoordinate() != null ? obj.getCoordinate() : "";
		
		View view = InjectCore.injectOriginalObject(holder);
		
		// 测点类型
		holder.mPointType.setText(type);
		
		// 默认值
		String[] str = coord.split(",");
		if (str.length == 3) {
			holder.mPointX.setText(str[0]);
			holder.mPointY.setText(str[1]);
			holder.mPointZ.setText(str[2]);
		}

		// 测试时间
		holder.mPointTime.setText(DateUtils.toDateString(obj.getSurveyTime(), DateUtils.PART_TIME_FORMAT));
		// 测量对象
		final SubsidenceTotalData item = obj ;
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
				
				if(holder.insert){
					item.setMEASNo(1);
				} else {
					item.setMEASNo(item.getMEASNo() + 1);
				}
				
				item.setCoordinate(z + "," + y + "," + z);
				item.setSurveyTime(DateUtils.toDate(time, DateUtils.DATE_TIME_FORMAT));
				item.setDataStatus(0);
				
				// 插入
				if(holder.insert){
					if(dao.insert(item) == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
						showText("保存成功");
					} else {
						showText("保存失败");
					}
				} 
				// 更新
				else {
					if(dao.update(item) == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
						showText("更新成功");
					} else {
						showText("更新失败");
					}
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
			int type 		= CrtbUtils.getExcavateMethod(method);
			
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
			
			SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao();
			SubsidenceTotalData bean = null ;
			
			// A
			bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(),"2");
			addTestPoint(createSubsidenceTestPointView(bean,"2"));
			
			// s1-1
			bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(),"3");
			addTestPoint(createSubsidenceTestPointView(bean,"3"));
			
			// s1-2
			bean = dao.queryTunnelTotalData(rawSheet.getID(),tunnelSection.getID(),"4");
			addTestPoint(createSubsidenceTestPointView(bean,"4"));
		}
	}

//	private OnClickListener mMeasListener = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			
//			// 从全站仪得到数据
//			
//			ISurveyProvider ts = TSSurveyProvider.getDefaultAdapter();
//			
//			if (ts == null) {
//				Toast.makeText(TestSectionExecuteActivity.this, "请先连接全站仪",
//						Toast.LENGTH_SHORT).show();
//				return;
//			}
//
//			Coordinate3D point = new Coordinate3D(null);
//			try {
//				int nret = ts.GetCoord(0, 0, point);
//				if (nret != 1) {
//					Toast.makeText(TestSectionExecuteActivity.this, "测量失败",
//							Toast.LENGTH_SHORT).show();
//					return;
//				}
//				// String text = String.format("%1$s,%2$s,%3$s",
//				// point.N,point.E,point.H);
//				// tv.setText(text);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//				return;
//			}
//			
//			String text = "" , x = "" , y = "" ,z = "", time = Time.getDateEN() ;
//		}
//	};
	
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
		
		// 是否新数据
		boolean insert = false ;
		
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
		
		@InjectView(id=R.id.test_warring_line_1)
		TextView mWarringLine1 ;
		
		@InjectView(id=R.id.test_warring_line_2)
		TextView mWarringLine2 ;
	}
}
