package com.crtb.tunnelmonitor.utils;

import java.util.ArrayList;
import java.util.List;

import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

import android.os.AsyncTask;
import android.util.Log;


public class DataUploadManager {
	private static final String LOG_TAG = "UploadDataLoader";
	
	public interface DataLoadListener {
		/**
		 * 数据加载完毕
		 * 
		 * @param uploadDataList 等待上传的数据
		 */
		public void done(List<UploadSheetData> uploadDataList);
	}
	
	private DataLoadListener mListener;
	
	public void loadData(DataLoadListener listener) {
		mListener = listener;
		new DataLoadTask().execute();
	}
	
	private class DataLoadTask extends AsyncTask<Void, Void, List<UploadSheetData>> {

		@Override
		protected List<UploadSheetData> doInBackground(Void... params) {
			List<UploadSheetData> uploadSheetDatas = new ArrayList<UploadSheetData>();
			//获取隧道内断面记录单
			List<RawSheetIndex> rawSheets = RawSheetIndexDao.defaultDao().queryTunnelSectionRawSheetIndex();
			if (rawSheets != null && rawSheets.size() > 0) {
				for(RawSheetIndex sheet : rawSheets) {
					UploadSheetData uploadSheetData = new UploadSheetData();
					uploadSheetDatas.add(uploadSheetData);
					uploadSheetData.setRawSheet(sheet);
					List<UploadSectionData> uploadSectionDatas = new ArrayList<UploadSectionData>();
					List<TunnelCrossSectionIndex>  unUploadSections = getUnUploadTunnelSections(sheet.getCrossSectionIDs());
					for(TunnelCrossSectionIndex section : unUploadSections) {
						UploadSectionData uploadSectionData = new UploadSectionData();
						uploadSectionDatas.add(uploadSectionData);
						uploadSectionData.setSection(section);
						//断面的测量数据
						List<UploadPointData> pointDatas = new ArrayList<UploadPointData>();
						List<TunnelSettlementTotalData> unUploadPoints = getUnUploadTunnelSettlementTotalData(sheet.getID(), section.getID());
						int measureNo = -1;
						UploadPointData measureData = null;
						for(TunnelSettlementTotalData point : unUploadPoints) {
							if (measureNo != point.getMEASNo()) {
								measureData = new UploadPointData();
								pointDatas.add(measureData);
							}
							measureData.addPoint(point);
					    }
						uploadSectionData.setUnUploadPointDatas(pointDatas);
					}
					uploadSheetData.setUnUpLoadSection(uploadSectionDatas);
				}
			}
			return uploadSheetDatas;
		}
		
		@Override
		protected void onPostExecute(List<UploadSheetData> result) {
			if (mListener != null) {
				mListener.done(result);
			}
		}
	}
	
    /**
     * 获取未上传隧道内断面列表
     * 
     * @param sectionRowIds 断面数据库ids
     * @return
     */
    public List<TunnelCrossSectionIndex> getUnUploadTunnelSections(String sectionRowIds) {
        TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
        List<TunnelCrossSectionIndex> unUploadSectionList = new ArrayList<TunnelCrossSectionIndex>();
        List<TunnelCrossSectionIndex> sectionList = dao.querySectionByIds(sectionRowIds);
        if (sectionList != null && sectionList.size() > 0) {
            for (TunnelCrossSectionIndex section : sectionList) {
                // 1表示未上传, 2表示已上传
                if ("1".equals(section.getInfo())) {
                    unUploadSectionList.add(section);
                }
            }
        }
        return unUploadSectionList;
    }
    
    /**
     * 获取未上传隧道内断面测点数据列表
     * 
     * @param sheetId
     * @param chainageId
     * @return
     */
    public List<TunnelSettlementTotalData> getUnUploadTunnelSettlementTotalData(int sheetId, int chainageId) {
    	TunnelSettlementTotalDataDao pointDao = TunnelSettlementTotalDataDao.defaultDao();
    	List<TunnelSettlementTotalData> unUploadPointList = new ArrayList<TunnelSettlementTotalData>();
    	List<TunnelSettlementTotalData> pointList = pointDao.queryTunnelTotalDatas(sheetId, chainageId);
    	if (pointList != null && pointList.size() > 0) {
    		for(TunnelSettlementTotalData point : pointList) {
    			// 1表示未上传, 2表示已上传
    			if ("1".equals(point.getInfo())) {
    				unUploadPointList.add(point);
    			}
    		}
    	}
    	return unUploadPointList;
    }
	

	public class UploadSheetData {
		private RawSheetIndex mRawSheet;
		private List<UploadSectionData> mUnUploadSections;
		
		UploadSheetData() {
			mUnUploadSections = new ArrayList<UploadSectionData>();
		}
		
		public void setRawSheet(RawSheetIndex rawSheet) {
			mRawSheet = rawSheet;
		}
		
		public RawSheetIndex getRawSheet() {
			return mRawSheet;
		}
		
		public void setUnUpLoadSection(List<UploadSectionData> unUploadSections) {
			mUnUploadSections = unUploadSections;
		}
		
		public List<UploadSectionData> getUnUploadSections() {
			return mUnUploadSections;
		}
		
		public boolean needUpload() {
			boolean result = false;
			if (mUnUploadSections != null && mUnUploadSections.size() > 0) {
				result = true;
			}
			return result;
		}
	}
	
	public class UploadSectionData {
		private TunnelCrossSectionIndex mSection;
		private List<UploadPointData> mUnUploadPointDatas;
		
		public void setSection(TunnelCrossSectionIndex section) {
			mSection = section;
		}
		
		public TunnelCrossSectionIndex getSection() {
			return mSection;
		}
		
		public void setUnUploadPointDatas(List<UploadPointData> unUploadPointDatas) {
			mUnUploadPointDatas = unUploadPointDatas;
		}
		
		public List<UploadPointData> getUnUploadPointDatas() {
			return mUnUploadPointDatas;
		}
		
		public boolean needUpload() {
			boolean result = false;
			if ("1".equals(mSection.getInfo())) {
				result = true;
			}
			if (mUnUploadPointDatas != null && mUnUploadPointDatas.size() > 0) {
				result = true;
			}
			return result;
		}
		
	}
	
	public class UploadPointData {
    	private List<TunnelSettlementTotalData> mMeasurePoints = new ArrayList<TunnelSettlementTotalData>();
    	
    	public void addPoint(TunnelSettlementTotalData point) {
    		mMeasurePoints.add(point);
    	}
    	
    	public List<TunnelSettlementTotalData> getPoints() {
    		return mMeasurePoints;
    	}
    	
    	public String getPointCodeList(String sectionCode) {
    		String pointCodeList = "";
    		final int pointCount = mMeasurePoints.size();
    		switch (pointCount) {
    		//全断面法
			case 3:
				pointCodeList = sectionCode + "GD01" + "/" + sectionCode
				+ "SL01" + "#" + sectionCode + "SL02";
				break;
			//台阶法
			case 5:
				pointCodeList = sectionCode + "GD01" + "/" + sectionCode
				+ "SL01" + "#" + sectionCode + "SL02" + "/" + sectionCode
				+ "SL03" + "#" + sectionCode + "SL04";
				break;
			//三台阶法或双侧壁法
			case 7:
				pointCodeList = sectionCode + "GD01" + "/" + sectionCode
				+ "SL01" + "#" + sectionCode + "SL02" + "/" + sectionCode
				+ "SL03" + "#" + sectionCode + "SL04" + "/" + sectionCode
				+ "SL05" + "#" + sectionCode + "SL06";
				break;
			default:
				Log.d(LOG_TAG, "未知的开挖方法: 测点数目=" + pointCount);
				break;
			}
    		return pointCodeList;
    	}
    }
}
