package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.crtb.tunnelmonitor.entity.RawSheetIndex;

public abstract class AsyncQueryTask extends AsyncTask<Void, Void, List<SheetRecord> >{
	public interface QueryLisenter {
		/**
		 * 
		 * @param records
		 */
		public void done(List<SheetRecord> records);
	}
	
	private List<RawSheetIndex> mSheets;
	private QueryLisenter mLisenter;
	
	AsyncQueryTask(List<RawSheetIndex> sheets, QueryLisenter lisenter) {
		mSheets = sheets;
		mLisenter = lisenter;
	}
	
	@Override
	protected List<SheetRecord> doInBackground(Void... params) {
		 List<SheetRecord> sheetRecords = new ArrayList<SheetRecord>();
		 List<RawSheetIndex> rawSheets = getAllRawSheetIndex();
		 if (rawSheets != null && rawSheets.size() > 0) {
			 for(RawSheetIndex sheet : rawSheets) {
				 SheetRecord record = new SheetRecord();
				 record.setRawSheet(sheet);
				 List<Section> uploadSections = queryAllSections(sheet.getGuid(), sheet.getCrossSectionIDs());
				 record.setUnUpLoadSection(uploadSections);
				 sheetRecords.add(record);
			 }
		 }
		return sheetRecords;
	}

	@Override
	protected void onPostExecute(List<SheetRecord> records) {
		if (mLisenter != null) {
			mLisenter.done(records);
		}
	}
	
	/**
	 * 查询所有的记录单
	 * @return
	 */
	protected List<RawSheetIndex> getAllRawSheetIndex() {
		return mSheets;
	}
	
	/**
	 * 查询所有的断面
	 * 
	 * @param sectionRowIds
	 * @param sheetId
	 * @return
	 */
	protected abstract List<Section> queryAllSections(String sheetGuid, String sectionGuids);
	
}
