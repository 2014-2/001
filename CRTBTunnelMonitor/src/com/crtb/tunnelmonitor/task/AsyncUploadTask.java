package com.crtb.tunnelmonitor.task;

import java.util.List;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;

public abstract class AsyncUploadTask extends AsyncTask<List<SheetRecord>, Void, Void> {
	private static final String LOG_TAG = "AsyncUploadTask";
	
	public interface UploadListener {
		/**
		 * 
		 * @param success
		 */
		public void done(boolean success);
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
            	//上传记录单
                final DataCounter sheetUploadCounter = new DataCounter("SheetUploadCounter", sheetRecords.size(), new CounterListener() {
                    @Override
                    public void done(boolean success) {
                        notifyDone(success);
                    }
                });
                for(SheetRecord record : sheetRecords) {
                    List<Section> sections = record.getUnUploadSections();
                    if (sections != null && sections.size() > 0) {
                        DataCounter sectionUploadCounter = new DataCounter("SectionUploadCounter", sections.size(), new CounterListener() {
                            @Override
                            public void done(boolean success) {
                                sheetUploadCounter.increase(success);
                            }
                        });
                        for(Section section : sections) {
                        	if (!section.isUpload()) {
                        		uploadSection(section, record.getRawSheet().getID(), sectionUploadCounter);
                        	} else {
                        		Log.d(LOG_TAG, "section is already uploaded: section_code: " + section.getSectionCode());
                        		uploadMeasureDataList(section.getSectionCode(), section.getMeasureData(), sectionUploadCounter);
                        	}
                        }
                    } else {
                        // 如果没有未上传的断面数据，则直接认为上传成功
                        sheetUploadCounter.increase(true);
                    }
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
	protected abstract void uploadSection(Section section, int sheetId, DataCounter sectionUploadCounter);
	
	/**
	 * 上传断面的测量数据
	 * @param section
	 * @param sectionUploadCounter
	 */
	protected abstract void uploadMeasureDataList(String sectionCode, List<MeasureData> measureDataList, DataCounter sectionUploadCounter);
	
	private void notifyDone(final boolean flag) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mListener != null) {
					mListener.done(flag);
				}
			}
		});
	}
}
