package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.List;

import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

public class TunnelAsyncQueryTask extends AsyncQueryTask {
	
	public TunnelAsyncQueryTask(List<RawSheetIndex> sheets, QueryLisenter queryLisenter) {
		super(sheets, queryLisenter);
		
	}

	@Override
	protected List<Section> queryAllSections(String sheetGuid, String sectionRowIds) {
		List<Section> sections = new ArrayList<Section>();
		List<TunnelCrossSectionIndex> sectionIndexList = TunnelCrossSectionIndexDao.defaultDao().querySectionByGuids(sectionRowIds);
		if (sectionIndexList != null && sectionIndexList.size() > 0) {
			for(TunnelCrossSectionIndex sectionIndex : sectionIndexList) {
				TunnelSection section = new TunnelSection();
				section.setSection(sectionIndex);
				if (section.isUpload()) {
					TunnelCrossSectionExIndexDao sectionExIndexDao = TunnelCrossSectionExIndexDao.defaultDao();
                	TunnelCrossSectionExIndex sectionExIndex = sectionExIndexDao.querySectionById(sectionIndex.getID());
                	if (sectionExIndex != null) {
                		section.setSectionCode(sectionExIndex.getSECTCODE());
                	}
				}
				//断面的未上传测量数据
                List<MeasureData> measureDataList = new ArrayList<MeasureData>();
                List<TunnelSettlementTotalData> measurePoints = getUnUploadMeasurePoints(sheetGuid, sectionIndex.getGuid());
                if (measurePoints != null && measurePoints.size() > 0) {
                	 int measureNo = -1;
                     TunnelMeasureData measureData = null;
                     for(TunnelSettlementTotalData point : measurePoints) {
                           // Yongdong: The same sheet should test at the same time. And MeasNo is always 1 now.
//                         if (measureNo != point.getMEASNo()) {
//                             measureNo = point.getMEASNo();
//                             measureData = new TunnelMeasureData();
//                             measureDataList.add(measureData);
//                         }
                         if (measureData == null) {
                             measureData = new TunnelMeasureData();
                             measureDataList.add(measureData);
                         }
                         measureData.addMeasurePoint(point);
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
	
	private List<TunnelSettlementTotalData> getUnUploadMeasurePoints(String sheetId, String sectionId) {
        TunnelSettlementTotalDataDao pointDao = TunnelSettlementTotalDataDao.defaultDao();
        List<TunnelSettlementTotalData> unUploadMeasurePoints = new ArrayList<TunnelSettlementTotalData>();
        List<TunnelSettlementTotalData> pointList = pointDao.queryTunnelTotalDatas(sheetId, sectionId);
        if (pointList != null && pointList.size() > 0) {
            for(TunnelSettlementTotalData point : pointList) {
            	// 1表示未上传, 2表示已上传
            	if (1 == point.getUploadStatus()) {
            		unUploadMeasurePoints.add(point);
            	}
            }
        }
        return unUploadMeasurePoints;
    }
}
