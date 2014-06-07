package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;

public abstract class AsyncUploadTask extends AsyncTask<List<SheetRecord>, Void, Void> {
	private static final String LOG_TAG = "AsyncUploadTask";
	/** 数据上传成功 */
	public static final int CODE_SUCCESS = 100;
	/** 记录单中无有效的观测数据 */
	public static final int CODE_NO_MEASURE_DATA = 101;

	public interface UploadListener {
		/**
		 * 
		 * @param success
		 */
		public void done(boolean success, int code);
	}

	private UploadListener mListener;
	private Handler mHandler;

	public AsyncUploadTask(UploadListener listener) {
		mListener = listener;
		mHandler = new Handler(Looper.getMainLooper());
	}

	@Override
	protected Void doInBackground(List<SheetRecord>... params) {
		if (params != null && params.length > 0) {
			List<SheetRecord> sheetRecords = params[0];
			if (sheetRecords != null && sheetRecords.size() > 0) {
				List<Section> allUploadSection = new ArrayList<Section>();
				for (SheetRecord record : sheetRecords) {
					List<Section> sections = record.getUnUploadSections();
					if (sections != null && sections.size() > 0) {
						for (Section section : sections) {
							int loc = contains(allUploadSection, section);
							if (loc == -1) {
								allUploadSection.add(section);
							} else {
								allUploadSection.get(loc).addMeasureData(section.getMeasureData());
							}
						}
					}
				}
				if (allUploadSection.size() > 0) {
					DataCounter sectionUploadCounter = new DataCounter("SectionUploadCounter", allUploadSection.size(),
							new CounterListener() {
								@Override
								public void done(boolean success) {
									notifyDone(success, CODE_SUCCESS);
								}
							});
					for (Section section : allUploadSection) {
						if (!section.isUpload()) {
							uploadSection(section, sectionUploadCounter);
						} else {
							Log.d(LOG_TAG, "section is already uploaded: section_code: " + section.getSectionCode());
							uploadMeasureDataList(section.getSectionCode(), section.getMeasureData(),
									sectionUploadCounter);
						}
					}
				} else {
					notifyDone(false, CODE_NO_MEASURE_DATA);
				}
			} else {
				Log.w(LOG_TAG, "empty data.");
			}
		}
		return null;
	}

	/**
	 * 上传断面数据
	 * 
	 * @param section
	 * @param sheetId
	 * @param sectionUploadCounter
	 */
	protected abstract void uploadSection(Section section, DataCounter sectionUploadCounter);

	/**
	 * 上传断面的测量数据
	 * 
	 * @param section
	 * @param sectionUploadCounter
	 */
	protected abstract void uploadMeasureDataList(String sectionCode, List<MeasureData> measureDataList,
			DataCounter sectionUploadCounter);

	private void notifyDone(final boolean flag, final int code) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mListener != null) {
					mListener.done(flag, code);
				}
			}
		});
	}

	private static int contains(List<Section> sections, Section section) {
		int result = -1;
		if (section != null && sections != null && sections.size() > 0) {
			for (int i = 0; i < sections.size(); i++) {
				Section s = sections.get(i);
				if (s.getRowId() == section.getRowId()) {
					result = i;
					break;
				}
			}
		}
		return result;
	}

}
