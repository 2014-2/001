package com.crtb.tunnelmonitor.task;

import java.util.List;

import com.crtb.tunnelmonitor.network.DataCounter;

public class SubsidenceAsyncUploadTask extends AsyncUploadTask {

	public SubsidenceAsyncUploadTask(UploadListener listener) {
		super(listener);
	}

	@Override
	protected void uploadSection(Section section, int sheetId, DataCounter sectionUploadCounter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void uploadMeasureDataList(String sectionCode, List<MeasureData> measureDataList,
			DataCounter sectionUploadCounter) {
		// TODO Auto-generated method stub
		
	}


}
