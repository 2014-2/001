package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ICT.utils.RSACoder;
import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TotalStationInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionParameterDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.TotalStationIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionParameter;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.SectionInterActionManager;

public class TunnelMeasureData extends MeasureData {
	
	private List<TunnelSettlementTotalData> mtunnelTestPoints = new ArrayList<TunnelSettlementTotalData>();
	private TunnelSurveyTimeComparator comparator = new TunnelSurveyTimeComparator();	
	private String mMointorModel = null;
	private static final String LOG_TAG = "TunnelMeasureData";
	private static final String CROWN_PREFIX = "A";
	private float mFaceDistance = 0f;
    private String mFaceDescription = null;
    private SectionInterActionManager sectionInterActionManager;
    private int excavateMethod = -1;
    private String sectionCrossCode = "";
    private String sectionCrossGuid = "";
    
	public TunnelMeasureData(int _excavateMethod) {
		excavateMethod = _excavateMethod;
	}
	
	public TunnelMeasureData(String _sectionCrossCode){
		sectionCrossCode = _sectionCrossCode;
	}
	
	public TunnelMeasureData(String noused,String _sectionCrossGuid){
		sectionCrossGuid = _sectionCrossGuid;
	}
    
	@Override
	public String getPointCodeList(String sectionCode) {
		if(sectionCode == null || sectionCode.length() < 1){
			Log.i(LOG_TAG, "getPointCodeList + sectionCode ERROR");
			return "";
		}
		return getOneLineDetailsByPointType(sectionCode);
	}
	
    @Override
	public String getCoordinateList() {
    	if(mtunnelTestPoints == null || mtunnelTestPoints.size() < 1){
    		Log.i(LOG_TAG, "getCoordinateList + mtunnelTestPoints ERROR");
			return "";
    	}
    	return getSectionInterActionManager().getCoordinateList(mtunnelTestPoints);
	}

	@Override
	public String getValueList() {
    	if(mtunnelTestPoints == null || mtunnelTestPoints.size() < 1){
    		Log.i(LOG_TAG, "getValueList + mtunnelTestPoints ERROR");
			return "";
    	}
		return getSectionInterActionManager().getValueList(mtunnelTestPoints);
	}

	private String getOneLineDetailsByPointType(String sectionCode) {
		if(mtunnelTestPoints != null ){
			String pntType = mtunnelTestPoints.get(0).getPntType();
			if(pntType != null && !pntType.equals("")){
				if(pntType.contains("-")){
					String[] values = pntType.split("-");
					if(values != null && values.length > 0){
						pntType = values[0];
					}
				}
				return getSectionInterActionManager().getOneLineDetailsByPointType(sectionCode,pntType);
			}
		}
		return "";
	}
	public void addMeasurePoint(TunnelSettlementTotalData point) {
		mtunnelTestPoints.add(point);
	}

	@Override
	public Date getMeasureDate() {
		Date mesureDate = null;
		if (mtunnelTestPoints != null && mtunnelTestPoints.size() > 0) {
			Collections.sort(mtunnelTestPoints, comparator);
			TunnelSettlementTotalData point = mtunnelTestPoints.get(mtunnelTestPoints.size() - 1);
			mesureDate = point.getSurveyTime();
		}
		return mesureDate;
	}

	@Override
	public void markAsUploaded() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				//measureDataList
				
				TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao();
				for (TunnelSettlementTotalData point : mtunnelTestPoints) {				
					//point.setInfo("2");
					point.setUploadStatus(2); //表示该测点已上传
					dao.update(point);
				}
			}
		}).start();
	}
	
    public String getFaceDescription() {
        if (TextUtils.isEmpty(mFaceDescription)) {
            TunnelSettlementTotalData first = mtunnelTestPoints.size() > 0 ? mtunnelTestPoints.get(0)
                    : null;
            if (first != null) {
                RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneByGuid(first.getSheetId());
                if (sheet != null) {
                    mFaceDescription = sheet.getFACEDESCRIPTION();
                }
            }
        }
        return mFaceDescription;
    }

    public float getFaceDistance() {
        TunnelSettlementTotalData first = mtunnelTestPoints.size() > 0 ? mtunnelTestPoints.get(0) : null;
        if (first != null) {
            TunnelCrossSectionIndex section = TunnelCrossSectionIndexDao.defaultDao()
                    .querySectionByGuid(first.getChainageId());
            if (section != null) {
                double chainage = section.getChainage();
                RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneByGuid(first.getSheetId());
                if (sheet != null) {
                    double facedk = sheet.getFACEDK();
                    mFaceDistance = (float) (facedk - chainage);
                }
            }

        }
        return mFaceDistance;
    }

    public String getMonitorModel() {
        if (TextUtils.isEmpty(mMointorModel)) {
        	
        	// by wei.zhou 2014-05-27
        	String str = mtunnelTestPoints.size() > 0 ? mtunnelTestPoints.get(0).getStationId() : "-1";
        	int id = -1 ;
        	
        	try{
        		id = Integer.valueOf(str);
        	}catch(Exception e){
        		e.printStackTrace() ;
        	}
        	
            // int id = mtunnelTestPoints.size() > 0 ? mtunnelTestPoints.get(0).getStationId() : -1;
            
        	if (id >= 0) {
                TotalStationIndex station = TotalStationInfoDao.defaultDao().queryOneById(id);
                if (station != null) {
                    mMointorModel = station.getName();
                }
            }
        }
        return mMointorModel;
    }

	public String getSheetGuid() {
		String sheetGuid = "";
		TunnelSettlementTotalData first = mtunnelTestPoints.size() > 0 ? mtunnelTestPoints
				.get(0) : null;
		if (first != null) {
			sheetGuid = first.getSheetId();
		}
		return sheetGuid;
	}
	
	public List<MeasureData> getMeasureDataList(List<TunnelSettlementTotalData> tunnelTestPoints) {

		if(tunnelTestPoints == null){
			return null;
		}
		List<MeasureData> measureDataList = new ArrayList<MeasureData>();
		TunnelMeasureData measureData = null;
		TunnelSettlementTotalData p1;
		TunnelSettlementTotalData p2;
		ArrayList<String> crownList = getSectionInterActionManager().getCrownPointList();
		ArrayList<String> pointList = getSectionInterActionManager().getSurveyLinePointList();
		String pntType;
		TunnelSettlementTotalData testPoint;
		
		int count = tunnelTestPoints.size();
		
		for (int testPointindex = 0; testPointindex < count; testPointindex ++) {
			testPoint = tunnelTestPoints.get(testPointindex);
			pntType = testPoint.getPntType();
			
			if(crownList != null){
//YX A，A1
				//pointType:A2，或者A
				
				if (pntType.equals(CROWN_PREFIX)) {
					pntType += "1";		
				}
				if (crownList.contains(pntType)) {
					measureData = new TunnelMeasureData(null,testPoint.getChainageId());
					measureData.addMeasurePoint(testPoint);
					measureDataList.add(measureData);
					continue;
				} else if(pointList != null){
					if (pointList.contains(pntType)) {
						int index = pointList.indexOf(pntType);
						int p1Index;
						int p2Index;
						if (index % 2 == 0) {
							p1Index = index;
							p2Index = index + 1;
						} else {
							p1Index = index - 1;
							p2Index = index;
						}
						p1 = getPointByType(tunnelTestPoints, pointList.get(p1Index));
						p2 = getPointByType(tunnelTestPoints, pointList.get(p2Index));
						if (p1 != null && p2 != null) {
							measureData = new TunnelMeasureData(null,p1.getChainageId());
							measureData.addMeasurePoint(p1);
							measureData.addMeasurePoint(p2);
							measureDataList.add(measureData);
							testPointindex = testPointindex + 1;
							continue;
						}
					}
				} 
			}
		}
        return measureDataList;
    }

    private TunnelSettlementTotalData getPointByType(
            List<TunnelSettlementTotalData> tunnelTestPoints, String type) {
        TunnelSettlementTotalData result = null;
        for (TunnelSettlementTotalData point : tunnelTestPoints) {
            if (type.equals(point.getPntType())) {
                result = point;
            }
        }
        return result;
    }
    
    private SectionInterActionManager getSectionInterActionManager(){
    	if(sectionInterActionManager == null){
			if (excavateMethod != -1) {
				sectionInterActionManager = new SectionInterActionManager(excavateMethod);
			} else if(!sectionCrossGuid.equals("")){
				sectionInterActionManager = new SectionInterActionManager(null,sectionCrossGuid); 
			} else if(!sectionCrossCode.equals("")){
				sectionInterActionManager = new SectionInterActionManager(sectionCrossCode); 
			}
    	}
    	return sectionInterActionManager;
    }    
    

	class TunnelSurveyTimeComparator implements
			Comparator<TunnelSettlementTotalData> {

		private boolean ASC = true;

		public void setSortType(boolean isAsc) {
			ASC = isAsc;
		}

		@Override
		public int compare(TunnelSettlementTotalData lhs,
				TunnelSettlementTotalData rhs) {
			int compareValue = -2;
			boolean dataRight = false;

			if (lhs != null || rhs != null) {
				Date lSurveyTime = lhs.getSurveyTime();
				Date rSurveyTime = rhs.getSurveyTime();
				if (lSurveyTime != null && rSurveyTime != null) {
					if (ASC) {
						compareValue = lSurveyTime.compareTo(rSurveyTime);
					} else {
						compareValue = rSurveyTime.compareTo(lSurveyTime);
					}
					dataRight = true;
				}
			}

			if (!dataRight) {
				try {
					throw new Exception(
							"TunnelSurveyTimeComparator reference exception");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return compareValue;
		}
	}
}
