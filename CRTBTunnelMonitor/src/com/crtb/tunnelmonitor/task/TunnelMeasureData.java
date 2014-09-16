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
	
	public TunnelMeasureData(){
		
	}
	
    
	@Override
	public String getPointCodeList(String sectionCode) {
		if(sectionCode == null || sectionCode.length() < 1){
			Log.i(LOG_TAG, "getPointCodeList + sectionCode ERROR");
			return "";
		}
		if(mtunnelTestPoints != null ){
			String pntType = mtunnelTestPoints.get(0).getPntType();
			if(pntType != null && !pntType.equals("")){
				if(pntType.contains("-")){
					String[] values = pntType.split("-");
					if(values != null && values.length > 0){
						pntType = values[0];
					}
				}
				return SectionInterActionManager.getOneLineDetailsByPointType(sectionCode,pntType);
			}
		}
		
		return "";
	}
	
    @Override
	public String getCoordinateList() {
    	if(mtunnelTestPoints == null || mtunnelTestPoints.size() < 1){
    		Log.i(LOG_TAG, "getCoordinateList + mtunnelTestPoints ERROR");
			return "";
    	}
    	return SectionInterActionManager.getCoordinateList(mtunnelTestPoints);
	}

	@Override
	public String getValueList() {
    	if(mtunnelTestPoints == null || mtunnelTestPoints.size() < 1){
    		Log.i(LOG_TAG, "getValueList + mtunnelTestPoints ERROR");
			return "";
    	}
		return SectionInterActionManager.getValueList(mtunnelTestPoints);
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

	/**
	 * 清洗数据：把数据按拱顶和测线分组
	 * 获取测量数据列表，保证拱顶的数据在一TunnelMeasureData里，而测线在一个TunnelMeasureData里、且测线必须两个点都存在
	 * 
	 * @param tunnelTestPoints 未经分组的原始测量数据A1,A2,S1-2,S1-1,S2-2等数据
	 * @return 分组的测量数据
	 */
	public List<MeasureData> getMeasureDataList(List<TunnelSettlementTotalData> tunnelTestPoints) {

		if(tunnelTestPoints == null){
			return null;
		}
		List<MeasureData> measureDataList = new ArrayList<MeasureData>();
		TunnelMeasureData measureData = null;
		TunnelSettlementTotalData p;
		TunnelSettlementTotalData p1;
		TunnelSettlementTotalData p2;
		String pntType;
		String lineName;
		
		int count = tunnelTestPoints.size();
		//遍历数据进行分组：找到后就从列表中删除，可减少getPointByType中列表的比较次数
		for (int pIndex = 0; pIndex < count; pIndex ++) {
			p = tunnelTestPoints.get(pIndex);
			pntType = p.getPntType();
			
			//拱顶
			if(pntType.contains(CROWN_PREFIX)){
				measureData = new TunnelMeasureData();
				measureData.addMeasurePoint(p);
				measureDataList.add(measureData);
				tunnelTestPoints.remove(p);
				--pIndex;
				--count;
				continue;
			}
			//测线
			else{
				//格式S1-2,S12-2,S3-1
				lineName = pntType.substring(0,pntType.length() - 2);
				tunnelTestPoints.remove(p);
				count -= 1;
				pIndex -= 1;
				if(pntType.endsWith("1")){
					p1 = p;	
					p2 = getPointByType(tunnelTestPoints,lineName + "-2");
					if(p2 != null){
						tunnelTestPoints.remove(p2);
						count -= 1;
					}
				} else {
					p1 = getPointByType(tunnelTestPoints,lineName + "-1");
					p2 = p;
					if(p1 != null){
						tunnelTestPoints.remove(p1);
						count -= 1;
					}
				}
				
				if (p1 != null && p2 != null) {
					measureData = new TunnelMeasureData();
					measureData.addMeasurePoint(p1);
					measureData.addMeasurePoint(p2);
					measureDataList.add(measureData);
					//continue;
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
                break;
            }
        }
        return result;
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
