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
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crtb.tssurveyprovider.Coordinate3D;
import com.crtb.tssurveyprovider.ISurveyProvider;
import com.crtb.tssurveyprovider.TSSurveyProvider;
import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.AppConfig;
import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SurveyerInformationDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogConnecting;
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

    private static boolean DEBUG = false;//DEBUG FLAG, set to true to generate fake data
    private static int COUNT = 0;//TODO: REMOVE: JUST FOR DEBUG
    private static double X = 2.3614D;
    private static double Y = 3.7607D;
    private static double Z = 1378.1012D;

    // 测量列表
	public static final String KEY_TEST_RAWSHEET_LIST	= "_key_test_rawsheet_list" ;
	static final int MSG_ERROR_CONNECT	= 1 ;
	static final int MSG_TEST_ERROR		= 2 ;
	static final int MSG_TEST_SUCCESS	= 3 ;
	static final int MSG_ERROR_NOT_TEST	= 4 ;
	
	@InjectView(id=R.id.test_bottom_layout)
	private LinearLayout mBottomLayout ;
	
	@InjectView(id=R.id.test_bnt_left_section)
	private RelativeLayout mLeftBnt ;
	
	@InjectView(id=R.id.test_bnt_next_section)
	private RelativeLayout mNextBnt ;
	
	@InjectView(id=R.id.test_container_layout)
	private LinearLayout mContainerLayout ;
	
	private int					rawSheetIndex;// 当前测量单索引
	private RawSheetIndex		rawSheetBean;// 当前测量单
	private List<RawSheetIndex> rawSheets ; // 测量数据
	private boolean 			rawSheetCanTest ; // 是否能够测量
	private boolean 			showNextHint;
	private boolean 			showLastHint;
	
	private int sectionIndex ;
	private TunnelCrossSectionIndex tunnelSection ;
	private SubsidenceCrossSectionIndex subsidenceSection ;
	private final List<TunnelCrossSectionIndex> tunnelSectionList = new ArrayList<TunnelCrossSectionIndex>();
	private final List<SubsidenceCrossSectionIndex> subsidenceSectionList = new ArrayList<SubsidenceCrossSectionIndex>();
	
	// 测量临时数据
	private final List<TunnelSettlementTotalData> tempTunnelData = new ArrayList<TunnelSettlementTotalData>();
	private final List<SubsidenceTotalData> tempSubsidenceData = new ArrayList<SubsidenceTotalData>();
	
	private TunnelSettlementTotalData pS1,pS2 ;
	
	private CrtbDialogConnecting connectDialog ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);
		
		Object bean = CommonObject.findObject(KEY_TEST_RAWSHEET_LIST);
		
		rawSheetIndex	= 0 ;
		rawSheetBean	= null ;
		showNextHint 	= true;
		showLastHint 	= true;
		tempTunnelData.clear() ;
		tempSubsidenceData.clear() ;
		
		// 测量数据
		if(bean instanceof List<?>){
			rawSheets 		= (List<RawSheetIndex>) bean ;
			rawSheetBean	= rawSheets.get(rawSheetIndex);
		}
		
		// 加载测量数据
		loadRawSheetIndexInfo(rawSheetBean);
	}
	
	// 下一个测量单
	private boolean loadNextRawSheetIndex(){
		
		if(rawSheets == null 
				|| rawSheets.isEmpty() 
				|| rawSheetIndex + 1 >= rawSheets.size()){
			
			if(showNextHint){
				showText("已经是最后一个断面");
				showNextHint = false ;
			}
			
			return false ;
		}
		
		// 下一条数据
		rawSheetIndex += 1;
		
		rawSheetBean 	= rawSheets.get(rawSheetIndex);
		sectionIndex	= 0 ;
		tunnelSection	= null ;
		subsidenceSection = null ;
		tunnelSectionList.clear() ;
		subsidenceSectionList.clear() ;
		rawSheetCanTest	= false  ;
		showNextHint	= true ;
		
		return true ;
	}
	
	// 上一个测量单
	private boolean loadPreRawSheetIndex(){
		
		if(rawSheets == null 
				|| rawSheets.isEmpty() 
				|| rawSheetIndex <= 0){
			
			if(showLastHint){
				showText("已经是第一个断面");
				showLastHint = false ;
			}
			return false ;
		}
		
		// 下一条数据
		rawSheetIndex-- ;
		
		rawSheetBean 	= rawSheets.get(rawSheetIndex);
		sectionIndex	= 0 ;
		tunnelSection	= null ;
		subsidenceSection = null ;
		tunnelSectionList.clear() ;
		subsidenceSectionList.clear() ;
		rawSheetCanTest	= false  ;
		showLastHint = true ;
		
		return true ;
	}
	
	private void loadRawSheetIndexInfo(RawSheetIndex bean){
		
		rawSheetCanTest	= false ;
		
		if(bean == null){
			showText("没有测量数据");
			return ;
		}
		
		rawSheetCanTest		= RawSheetIndexDao.defaultDao().isNewestRawSheetIndex(bean);
		int type			= bean.getCrossSectionType() ;
		String sectionIds 	= bean.getCrossSectionIDs() ;
		
		if(StringUtils.isEmpty(sectionIds)){
			showText("没有断面信息");
		} else {
			
			String[] ids = sectionIds.split(",");
			
			// 加载断面信息
			loadSections(ids,type);
		}
	}
	
	private void loadSections(String[] ids,int sectionType){
		
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
	
	/*private TunnelSettlementTotalData findTunnelData(String ptype){
		
		if(tunnelSection == null || ptype == null){
			return null ;
		}
		
		int sid = tunnelSection.getID() ;
		
		for(TunnelSettlementTotalData bean : tempTunnelData){
			
			String type = bean.getPntType() ;
			
			if(bean.getChainageId() == sid && type != null && type.equals(ptype)){
				return bean ;
			}
		}
		
		return null ;
	}
	
	private SubsidenceTotalData findSubsidenceData(String ptype){
		
		if(subsidenceSection == null || ptype == null){
			return null ;
		}
		
		int sid = subsidenceSection.getID() ;
		
		for(SubsidenceTotalData bean : tempSubsidenceData){
			
			String type = bean.getPntType() ;
			
			if(bean.getChainageId() == sid && type != null && type.equals(ptype)){
				return bean ;
			}
		}
		
		return null ;
	}*/
	
	private TestPointHolder createTunnelTestPointView(TunnelSettlementTotalData bean,final String type,String typeName,String suffix){
		
		final TestPointHolder holder 	= new TestPointHolder() ;
		View view						= InjectCore.injectOriginalObject(holder);
		holder.mItemView				= view ;
		
		// 从已经测量的数据中查找
		// TunnelSettlementTotalData bean = findTunnelData(type);
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
		if(StringUtils.isEmpty(typeName)){
			holder.mPointType.setText(type);
		} else {
			if(StringUtils.isEmpty(suffix)){
				holder.mPointType.setText(typeName);
			} else {
				holder.mPointType.setText(typeName + "-" + suffix);
			}
		}
		
		// 测量
		holder.mPointTestBnt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				connectSurveyProvider(holder,type,RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL);
			}
		}) ;
		
		// 清除
		holder.mPointResetBnt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				TunnelSettlementTotalDataDao dao 	= TunnelSettlementTotalDataDao.defaultDao() ;
				TunnelSettlementTotalData obj 		= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(),type);
				
				if(obj != null){
					
					dao.reset(obj);
					
					holder.mPointX.setText("");
					holder.mPointY.setText("");
					holder.mPointZ.setText("");
					holder.mPointTime.setText("");
					holder.warringLayout.setVisibility(View.INVISIBLE);
				}
			}
		}) ;
		
		return holder ;
	}
	
	// 异步测量
	private void connectSurveyProvider(final TestPointHolder holder,final String type,final int sectionType){
		
		// 暂时去掉优化
		/*ExecuteAsyncTaskImpl.defaultSyncExecutor().executeTask(new BaseAsyncTask(mHanlder){
			
			@Override
			public void process() {
				
				
			}
		}) ;*/
		
		if(!rawSheetCanTest){
			mHanlder.sendMessage(MSG_ERROR_NOT_TEST);
			return ;
		}

		String x, y, z;

		if (!DEBUG) {
		    ISurveyProvider ts = TSSurveyProvider.getDefaultAdapter();
		    
		    if (ts == null) {
		        mHanlder.sendMessage(MSG_ERROR_CONNECT);
		        return;
		    }
		    
		    Coordinate3D point = new Coordinate3D(null);
		    
		    try {
		        int nret = ts.GetCoord(0, 0, point);
		        
		        if (nret != 1) {
		            mHanlder.sendMessage(MSG_TEST_ERROR);
		            return;
		        }
		        
		        if(point.N == 0d 
		                || point.E == 0d 
		                || point.H == 0d){
		            mHanlder.sendMessage(MSG_ERROR_CONNECT);
		            return ;
		        }
		        
		    } catch (InterruptedException e) {
		        e.printStackTrace();
		        return;
		    }

		    x = String.format("%1$.4f", point.N);
		    y = String.format("%1$.4f", point.E);
		    z = String.format("%1$.4f", point.H);
		} else {
		    x = String.format("%1$.4f", X - COUNT * 0.05);
		    y = String.format("%1$.4f", Y - COUNT * 0.05);
		    z = String.format("%1$.4f", Z - COUNT * 0.05);
		    COUNT++;
		}

		String time = Time.getDateEN() ;
		
		TestInfo info = new TestInfo() ;
		info.holder	= holder ;
		info.type	= type ;// 测点类型
		info.sectionType	= sectionType ;// 断面类型
		
		info.x 		= x ;
		info.y		= y ;
		info.z		= z ;
		info.time	= time ;
		
		mHanlder.sendMessage(MSG_TEST_SUCCESS,info);
	}
	
	@Override
	protected AppHandler getHandler() {
		return new AppHandler(this){

			@Override
			protected void dispose(Message msg) {
				
				switch(msg.what){
				case MSG_TASK_START :
					
					if(connectDialog == null){
						connectDialog	= new CrtbDialogConnecting(TestSectionExecuteActivity.this);
					}
					
					if(!connectDialog.isShowing()){
						connectDialog.showDialog("正在从全站仪获取数据!") ;
					}
					break ;
				case MSG_TASK_END :
					
					if(connectDialog != null){
						connectDialog.dismiss() ;
					}
					
					break ;
				case MSG_ERROR_CONNECT :
					showText("请先连接全站仪");
					break ;
				case MSG_TEST_ERROR :
					showText("测量失败");
					break ;
				case MSG_ERROR_NOT_TEST :
					showText("该记录不是最新记录单,不能测量");
					break ;
				case MSG_TEST_SUCCESS :
					
					TestInfo info = (TestInfo)msg.obj ;
					
					info.holder.mPointX.setText(info.x);
					info.holder.mPointY.setText(info.y);
					info.holder.mPointZ.setText(info.z);
					info.holder.mPointTime.setText(info.time);
					
					// 隧道内断面
					if(info.sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
						
						TunnelSettlementTotalData obj = new TunnelSettlementTotalData() ;
						obj.setStationId(AppCRTBApplication.getInstance().getCurUsedStationId());
						obj.setChainageId(String.valueOf(tunnelSection.getID()));
						obj.setSheetId(String.valueOf(rawSheetBean.getID()));
						obj.setPntType(info.type); // 测量点类型
						obj.setSurveyorID(String.valueOf(SurveyerInformationDao.defaultDao().getRowIdByCertificateID(
						        rawSheetBean.getCertificateID())));// 测量人员id
						obj.setInfo("1");

						TunnelSettlementTotalDataDao dao 	= TunnelSettlementTotalDataDao.defaultDao() ;
						
						obj.setCoordinate(info.x + "," + info.y + "," + info.z);
						obj.setSurveyTime(DateUtils.toDate(info.time, DateUtils.DATE_TIME_FORMAT));
						
						int err = TunnelSettlementTotalDataDao.DB_EXECUTE_FAILED;
						boolean update = false;
						// 存在的测量点信息
						TunnelSettlementTotalData old = dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(),info.type);
						if (old != null) {
						    //UPDATE
						    obj.setMEASNo(old.getMEASNo());
						    obj.setID(old.getID());
						    obj.setDataStatus(old.getDataStatus());
						    err = dao.update(obj);
						    update = true;
						} else {
						    //INSERT NEW
						    obj.setDataStatus(0);
						    int lastMEASNo = 0;
						    List<TunnelSettlementTotalData> l = dao.queryAllOrderByMEASNoDesc(info.type);
						    if(l != null && l.size() > 0) {
						        TunnelSettlementTotalData last = l.get(0);
						        lastMEASNo = last.getMEASNo();
						    }
						    obj.setMEASNo(lastMEASNo + 1);
						    err = dao.insert(obj);
						    update = false;
						}
						
						if(err == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
							
							// 保存测量数据
							// tempTunnelData.add(obj);
							
							if(info.type.equals(AppConfig.POINT_A)){
								doWarning(info.holder, AlertUtils.getPointSubsidenceExceedMsg(obj));
							} else {
								
								if(info.type.equals(AppConfig.POINT_S1_1)
										|| info.type.equals(AppConfig.POINT_S2_1)
										|| info.type.equals(AppConfig.POINT_S3_1)){
									pS1	= obj ;
								} else {
									pS2	= obj ;
								}
								
								if(pS1 != null && pS2 != null){
									doWarningLine(info.holder,pS1,pS2);
								}
							}
							
							showText((update ? "更新" : "保存") + "成功");
						} else {
							showText((update ? "更新" : "保存") + "失败");
						}
					} 
					// 地表下沉
					else {
						
						SubsidenceTotalDataDao dao 	= SubsidenceTotalDataDao.defaultDao() ;
						
						// 存在的测量点信息
						SubsidenceTotalData old = dao.querySubsidenceTotalData(rawSheetBean.getID(),subsidenceSection.getID(),info.type);
						
						final SubsidenceTotalData obj = new SubsidenceTotalData() ;
						obj.setStationId(AppCRTBApplication.getInstance().getCurUsedStationId());
						obj.setChainageId(String.valueOf(subsidenceSection.getID()));
						obj.setSheetId(String.valueOf(rawSheetBean.getID()));
						obj.setPntType(info.type); // 测量点类型
						obj.setSurveyorID(String.valueOf(SurveyerInformationDao.defaultDao().getRowIdByCertificateID(
						        rawSheetBean.getCertificateID())));// 测量人员id

						obj.setCoordinate(info.x + "," + info.y + "," + info.z);
						obj.setSurveyTime(DateUtils.toDate(info.time, DateUtils.DATE_TIME_FORMAT));
						obj.setInfo("1");

						int err = TunnelSettlementTotalDataDao.DB_EXECUTE_FAILED;
						boolean update = false;

                        if (old != null) {
                            //UPDATE
                            obj.setMEASNo(old.getMEASNo());
                            obj.setID(old.getID());
                            obj.setDataStatus(old.getDataStatus());
                            err = dao.update(obj);
                            update = true;
                        } else {
                            //INSERT NEW
                            obj.setDataStatus(0);
                            int lastMEASNo = 0;
                            List<SubsidenceTotalData> l = dao.queryAllOrderByMEASNoDesc(info.type);
                            if (l != null && l.size() > 0) {
                                SubsidenceTotalData last = l.get(0);
                                lastMEASNo = last.getMEASNo();
                            }
                            obj.setMEASNo(lastMEASNo + 1);
                            err = dao.insert(obj);
                            update = false;
                        }
						
						if(err == TunnelSettlementTotalDataDao.DB_EXECUTE_SUCCESS){
							
							// 保存临时数据
							// tempSubsidenceData.add(obj);
							
						    doWarning(info.holder, AlertUtils.getPointSubsidenceExceedMsg(obj));
							showText((update ? "更新" : "保存") + "成功");
							
						} else {
							showText((update ? "更新" : "保存") + "失败");
						}
					}
					
					break ;
				}
			}
			
		};
	}

	private void doWarningLine(TestPointHolder holder, TunnelSettlementTotalData p1, TunnelSettlementTotalData p2){
		
	    String[] list = AlertUtils.getLineConvergenceExceedMsg(p1,p2);

		doWarning(holder, list);

		pS1 = null;
		pS2 = null;
	}
	
	private void doWarning(TestPointHolder holder, String[] list){
		
		if(list == null || list.length == 0){
			System.out.println("zhouwei : 检查拱顶 ------ 没有错误");
			return ;
		}
		
		holder.warringLayout.removeAllViews() ;
		holder.warringLayout.setVisibility(View.VISIBLE);
		
		for(String msg : list){
			
			TextView tv = new TextView(this);
			tv.setTextColor(Color.RED);
			tv.setTextSize(12);
			
			if(!StringUtils.isEmpty(msg)){
				tv.setText(msg);
				holder.warringLayout.addView(tv);
			}
		}
	}
	
	private View createSubsidenceTestPointView(final SubsidenceTotalData bean,final String type){
		
		final TestPointHolder holder 	= new TestPointHolder() ;
		
		View view = InjectCore.injectOriginalObject(holder);
		
		// 测点类型
		holder.mPointType.setText(type);
		
		// 从已经测量的数据中查找
		// SubsidenceTotalData bean = findSubsidenceData(type);
		
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
				connectSurveyProvider(holder,type,RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES);
			}
		}) ;
		
		// 清除
		holder.mPointResetBnt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				SubsidenceTotalDataDao dao 	= SubsidenceTotalDataDao.defaultDao() ;
				SubsidenceTotalData obj = dao.querySubsidenceTotalData(rawSheetBean.getID(),subsidenceSection.getID(),type);

				if(obj != null){
					
					dao.reset(obj);
					
					holder.mPointX.setText("");
					holder.mPointY.setText("");
					holder.mPointZ.setText("");
					holder.mPointTime.setText("");
					holder.warringLayout.setVisibility(View.INVISIBLE);
				}
			}
		});

		return view ;
	}
	
	// 得到测量点名称: 0 A, 1 S1, 2 S2, 3 S3
	private String getPntName(int index){
		
		if(index < 0 || index > 3 || tunnelSection == null){
			return "" ;
		}
		
		String str = tunnelSection.getSurveyPntName() ;
		
		if(StringUtils.isEmpty(str)){
			return "" ;
		}
		
		String[] array = str.split(",");
		
		if(index == 0 && array.length >= 1){
			return array[0];
		} else if(index == 1 && array.length >=2 ){
			return array[1];
		} else if(index == 2 && array.length >= 3){
			return array[2];
		} else if(index == 3 && array.length >= 4){
			return array[3];
		}
		
		return "" ;
	}
	
	private void loadSectionTestData(){
		
		// 删除所以的测量点view
		mContainerLayout.removeAllViews() ;
		
		if(rawSheetBean == null){
			return ;
		}
		
		int sectionType = rawSheetBean.getCrossSectionType() ;
		
		if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
			
			TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao();
			String method 	= tunnelSection.getExcavateMethod() ;
			int type 		= CrtbUtils.getExcavateMethod(CrtbUtils.getExcavateMethodByStr(method));
			pS1				= null ;
			pS2				= null ;
			TestPointHolder holder = null ;
			
			if(type == -1){
				showText("无效开挖类型: " + method);
				return ;
			}
			
			// title
			setTopbarTitle(tunnelSection.getSectionName() + " (" + (sectionIndex + 1) + "/" + tunnelSectionList.size() + ")");
			
			TunnelSettlementTotalData bean = null ;
			TunnelSettlementTotalData p1 = null , p2 = null ;
			
			// A
			bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_A);
			holder 	= createTunnelTestPointView(bean,AppConfig.POINT_A,getPntName(0),"");
			addTestPoint(holder.mItemView);
			
			// A 报警
			if(bean != null){
				doWarning(holder, AlertUtils.getPointSubsidenceExceedMsg(bean));
			}
			
			// 全断面法(A,S1(1,2))
			if(type == 0){
				
				// S1-1
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S1_1);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S1_1,getPntName(1),"1") ;
				addTestPoint(holder.mItemView);
				p1 = bean ;
				
				// S1-2
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S1_2);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S1_2,getPntName(1),"2");
				addTestPoint(holder.mItemView);
				p2 = bean ;
				
				if(p1 != null && p2 != null){
					doWarningLine(holder,p1,p2);
				}
			} 
			// 台阶法(A,S1(1,2),S2(1,2))
			else if(type == 1){
				
				// S1-1
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S1_1);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S1_1,getPntName(1),"1") ;
				p1 		= bean ;
				addTestPoint(holder.mItemView);
				
				// S1-2
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S1_2);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S1_2,getPntName(1),"2") ;
				p2		= bean ;
				addTestPoint(holder.mItemView);
				
				if(p1 != null && p2 != null){
					doWarningLine(holder,p1,p2);
				}
				
				p1	= p2 = null ;
				
				// S2-1
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S2_1);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S2_1,getPntName(2),"1") ;
				p1 		= bean ;
				addTestPoint(holder.mItemView);
				
				// S2-2
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S2_2);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S2_2,getPntName(2),"2") ;
				p2 		= bean ;
				addTestPoint(holder.mItemView);
				
				if(p1 != null && p2 != null){
					doWarningLine(holder,p1,p2);
				}
			} 
			// 三台阶法(A,S1(1,2),S2(1,2),S3(1,2))
			else if(type == 2){
				
				// S1-1
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S1_1);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S1_1,getPntName(1),"1") ;
				p1		= bean ;
				addTestPoint(holder.mItemView);
				
				// S1-2
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S1_2);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S1_2,getPntName(1),"2") ;
				p2		= bean ;
				addTestPoint(holder.mItemView);
				
				if(p1 != null && p2 != null){
					doWarningLine(holder,p1,p2);
				}
				
				p1	= p2 = null ;
				
				// S2-1
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S2_1);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S2_1,getPntName(2),"1") ;
				p1		= bean ;
				addTestPoint(holder.mItemView);
				
				// S2-2
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S2_2);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S2_2,getPntName(2),"2") ;
				p2		= bean ;
				addTestPoint(holder.mItemView);
				
				if(p1 != null && p2 != null){
					doWarningLine(holder,p1,p2);
				}
				
				p1	= p2 = null ;
				
				// S3-1
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S3_1);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S3_1,getPntName(3),"1") ;
				p1		= bean ;
				addTestPoint(holder.mItemView);
				
				// S3-2
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S3_2);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S3_2,getPntName(3),"2") ;
				p2		= bean ;
				addTestPoint(holder.mItemView);
				
				if(p1 != null && p2 != null){
					doWarningLine(holder,p1,p2);
				}
			} 
			// 双侧壁法(A,S1(1,2),S2(1,2),S3(1,2))
			else if(type == 3){
				
				// S1-1
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S1_1);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S1_1,getPntName(1),"1") ;
				p1		= bean ;
				addTestPoint(holder.mItemView);
				
				// S1-2
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S1_2);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S1_2,getPntName(1),"2") ;
				p2		= bean ;
				addTestPoint(holder.mItemView);
				
				if(p1 != null && p2 != null){
					doWarningLine(holder,p1,p2);
				}
				
				p1	= p2 = null ;
				
				// S2-1
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S2_1);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S2_1,getPntName(2),"1") ;
				p1		= bean ;
				addTestPoint(holder.mItemView);
				
				// S2-2
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S2_2);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S2_2,getPntName(2),"2") ;
				p2		= bean ;
				addTestPoint(holder.mItemView);
				
				if(p1 != null && p2 != null){
					doWarningLine(holder,p1,p2);
				}
				
				p1	= p2 = null ;
				
				// S3-1
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S3_1);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S3_1,getPntName(3),"1") ;
				p1		= bean ;
				addTestPoint(holder.mItemView);
				
				// S3-2
				bean 	= dao.queryTunnelTotalData(rawSheetBean.getID(),tunnelSection.getID(), AppConfig.POINT_S3_2);
				holder 	= createTunnelTestPointView(bean,AppConfig.POINT_S3_2,getPntName(3),"2") ;
				p2		= bean ;
				addTestPoint(holder.mItemView);
				
				if(p1 != null && p2 != null){
					doWarningLine(holder,p1,p2);
				}
			}
		} 
		// 地表下沉断面测量---临时值
		else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
			
			int size = subsidenceSection.getSurveyPnts();
			
			// title
			setTopbarTitle(subsidenceSection.getSectionName() + " (" + (sectionIndex + 1) + "/" + subsidenceSectionList.size() + ")");
			
			SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao();
			SubsidenceTotalData bean = null ;
			
			for(int id = 0 ; id < size ; id++){
				
				String index = String.valueOf(id + 1) ;
				
				bean = dao.querySubsidenceTotalData(rawSheetBean.getID(),subsidenceSection.getID(),index);
				addTestPoint(createSubsidenceTestPointView(bean,index));
			}
		}
	}

	@Override
	public void onClick(View v) {
		
		int sectionType = 0 ;
		
		switch(v.getId()){
		case R.id.test_bnt_left_section :
			
			if(rawSheetBean == null){
				return ;
			}
			
			// 当前测量单类型
			sectionType = rawSheetBean.getCrossSectionType() ; 
			
			if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
				
				// 第一个断面
				if(sectionIndex <= 0){
					
					// 上一个测量单
					if(loadPreRawSheetIndex()){
						loadRawSheetIndexInfo(rawSheetBean);
					}
				} else {
					
					// 上一个断面
					sectionIndex-- ;
					tunnelSection = tunnelSectionList.get(sectionIndex);
					loadSectionTestData();
				}
			} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
				
				if(sectionIndex <= 0){
					
					// 上一个测量单
					if(loadPreRawSheetIndex()){
						loadRawSheetIndexInfo(rawSheetBean);
					}
				} else {
					// 上一个断面
					sectionIndex-- ;
					subsidenceSection = subsidenceSectionList.get(sectionIndex);
					loadSectionTestData();
				}
			}
			
			break ;
		case R.id.test_bnt_next_section :
			
			if(rawSheetBean == null){
				return ;
			}
			
			// 当前测量单类型
			sectionType = rawSheetBean.getCrossSectionType() ; 
			
			if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
				
				// 最后一个断面
				if(sectionIndex >= tunnelSectionList.size() - 1){
					
					// 下一个测量单
					if(loadNextRawSheetIndex()){
						loadRawSheetIndexInfo(rawSheetBean);
					}
				} else {
					
					// 当前测量单的下一个断面
					if(sectionIndex + 1 < tunnelSectionList.size()){
						
						sectionIndex++ ;
						tunnelSection = tunnelSectionList.get(sectionIndex);
						loadSectionTestData();
					}
				}
			} else if(sectionType == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
				
				// 最后一个断面
				if(sectionIndex >= subsidenceSectionList.size() - 1){
					
					// 下一个测量单
					if(loadNextRawSheetIndex()){
						loadRawSheetIndexInfo(rawSheetBean);
					}
				} else {
					
					// 下一个断面
					if(sectionIndex + 1 < subsidenceSectionList.size()){
						
						sectionIndex++ ;
						subsidenceSection = subsidenceSectionList.get(sectionIndex);
						loadSectionTestData();
					}
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
		
		@InjectView(id=R.id.bnt_reset)
		Button 	mPointResetBnt ;
		
		@InjectView(id=R.id.test_warring_layout)
		LinearLayout warringLayout ;
		
		// view
		View mItemView ;
	}
	
	final class TestInfo {
		
		TestPointHolder holder ;
		String type ;
		int sectionType;
		
		String x ;
		String y ;
		String z ;
		String time ;
	}
}
