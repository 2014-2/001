package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.List;

import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;

public class SubsidenceAsyncQueryTask extends AsyncQueryTask {
	private static final String LOG_TAG = "SubsidenceAsyncQueryTask";
	
	public SubsidenceAsyncQueryTask(List<RawSheetIndex> sheets, QueryLisenter lisenter) {
		super(sheets, lisenter);
	}

	@Override
	protected List<Section> queryAllSections(String sheetGuid, String sectionRowIds) {
		List<Section> sections = new ArrayList<Section>();
		List<SubsidenceCrossSectionIndex>  sectionIndexList = SubsidenceCrossSectionIndexDao.defaultDao().querySectionByGuids(sectionRowIds);
		if (sectionIndexList != null && sectionIndexList.size() > 0) {
			for(SubsidenceCrossSectionIndex sectionIndex : sectionIndexList) {
				SubsidenceSection section = new SubsidenceSection();
				section.setSection(sectionIndex);
				if (section.isUpload()) {
					SubsidenceCrossSectionExIndexDao sectionExIndexDao = SubsidenceCrossSectionExIndexDao.defaultDao();
                	SubsidenceCrossSectionExIndex sectionExIndex = sectionExIndexDao.querySectionById(sectionIndex.getID());
                	if (sectionExIndex != null) {
                		section.setSectionCode(sectionExIndex.getSECTCODE());
                	}
				}
				//断面的未上传测量数据
                List<MeasureData> measureDataList = new ArrayList<MeasureData>();
                List<SubsidenceTotalData> measurePoints = getUnUploadMeasurePoints(sheetGuid, sectionIndex.getGuid());
                if (measurePoints != null && measurePoints.size() > 0) {
                	 int measureNo = -1;
                     SubsidenceMeasureData measureData = null;
                     for(SubsidenceTotalData point : measurePoints) {
                           // Yongdong: The same sheet should test at the same time. And MeasNo is always 1 now.
//                         if (measureNo != point.getMEASNo()) {
//                             measureNo = point.getMEASNo();
//                             measureData = new SubsidenceMeasureData();
//                             measureDataList.add(measureData);
//                         }
//                         if (measureData == null) {
//                             measureData = new SubsidenceMeasureData();
//                             measureDataList.add(measureData);
//                         }
                    	 measureData = new SubsidenceMeasureData();
                    	 measureData.addMeasurePoint(point);
                         measureDataList.add(measureData);
                     }
                     section.setMeasureData(measureDataList);
                }
				if (section.needUpload()) {
					sections.add(section);
				}
			}
		}
		return sections;
	}

	 public List<SubsidenceTotalData> getUnUploadMeasurePoints(String sheetGuid, String sectionGuid) {
	    	SubsidenceTotalDataDao pointDao = SubsidenceTotalDataDao.defaultDao();
	        List<SubsidenceTotalData> measurePoints = new ArrayList<SubsidenceTotalData>();
	        List<SubsidenceTotalData> pointList = pointDao.querySubsidenceTotalDatas(sheetGuid, sectionGuid);
	        if (pointList != null && pointList.size() > 0) {
	            for(SubsidenceTotalData point : pointList) {
	            	// 1表示未上传, 2表示已上传
	            	if (1 == point.getUploadStatus()) {
	            		measurePoints.add(point);
	            	}
	            }
	        }
	        return measurePoints;
	    }
}
