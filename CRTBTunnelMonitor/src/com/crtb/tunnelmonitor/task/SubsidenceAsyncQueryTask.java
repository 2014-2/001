package com.crtb.tunnelmonitor.task;

import java.util.List;

import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;

public abstract class SubsidenceAsyncQueryTask extends AsyncQueryTask {

	public SubsidenceAsyncQueryTask(QueryLisenter lisenter) {
		super(lisenter);
	}

	@Override
	public List<RawSheetIndex> queryAllRawSheetIndex() {
		return RawSheetIndexDao.defaultDao().queryAllSubsidenceSectionRawSheetIndex();
	}

	@Override
	protected List<Section> queryAllSections(int sheetId, String sectionRowIds) {
		// TODO Auto-generated method stub
		return null;
	}

}
