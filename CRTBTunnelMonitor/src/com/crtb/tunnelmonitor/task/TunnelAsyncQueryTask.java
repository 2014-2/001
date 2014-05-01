package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.List;

import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

public class TunnelAsyncQueryTask extends AsyncQueryTask {

	public TunnelAsyncQueryTask(QueryLisenter lisenter) {
		super(lisenter);
	}

	@Override
	public List<RawSheetIndex> queryAllRawSheetIndex() {
		return RawSheetIndexDao.defaultDao().queryTunnelSectionRawSheetIndex();
	}

	@Override
	protected List<Section> queryAllSections(int sheetId, String sectionRowIds) {
		List<Section> sections = new ArrayList<Section>();
		List<TunnelCrossSectionIndex> sectionIndexList = TunnelCrossSectionIndexDao.defaultDao().querySectionByIds(sectionRowIds);
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
                List<TunnelSettlementTotalData> measurePoints = getUnUploadMeasurePoints(sheetId, sectionIndex.getID());
                if (measurePoints != null && measurePoints.size() > 0) {
                	 int measureNo = -1;
                     TunnelMeasureData measureData = null;
                     for(TunnelSettlementTotalData point : measurePoints) {
                         if (measureNo != point.getMEASNo()) {
                             measureNo = point.getMEASNo();
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
	
	private List<TunnelSettlementTotalData> getUnUploadMeasurePoints(int sheetId, int sectionId) {
        TunnelSettlementTotalDataDao pointDao = TunnelSettlementTotalDataDao.defaultDao();
        List<TunnelSettlementTotalData> unUploadMeasurePoints = new ArrayList<TunnelSettlementTotalData>();
        List<TunnelSettlementTotalData> pointList = pointDao.queryTunnelTotalDatas(sheetId, sectionId);
        if (pointList != null && pointList.size() > 0) {
            for(TunnelSettlementTotalData point : pointList) {
                // 1表示未上传, 2表示已上传
                if ("1".equals(point.getInfo())) {
                	unUploadMeasurePoints.add(point);
                }
            }
        }
        return unUploadMeasurePoints;
    }
	
}
