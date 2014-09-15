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
                List<TunnelSettlementTotalData> measurePoints = getUnUploadMeasurePoints(sheetGuid, sectionIndex.getGuid());
                if (measurePoints != null && measurePoints.size() > 0) {
                    
                	//YX 获取上传数据                	 
                	TunnelMeasureData tunnelMeasureData = new TunnelMeasureData(sectionIndex.getExcavateMethod());
                	section.setMeasureData(tunnelMeasureData.getMeasureDataList(measurePoints));
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
