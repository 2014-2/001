package com.crtb.tunnelmonitor.task;

import java.util.List;

import android.os.Handler;
import android.os.Looper;

import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

public class AsyncUpdateTask {
	public static final int TYPE_TUNNEL = 1;
	public static final int TYPE_SUBSIDENCE = 2;
	
	public interface UpdateListener {
		/**
		 * called when the update finished.
		 */
		public void done();
	}

	private List<SheetRecord> mSheets;
	private UpdateListener mListener;
	private int mType;
	private Handler mHandler;
	
	public AsyncUpdateTask(final int type, List<SheetRecord> sheets, UpdateListener listener) {
		mType = type;
		mSheets = sheets;
		mListener = listener;
		mHandler = new Handler(Looper.getMainLooper());
		
	}
	
	public void execute() {
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				switch (mType) {
				case TYPE_TUNNEL:
					new Thread(new Runnable() {
						@Override
						public void run() {
							updateTunnelSheet();
							notifyDone();
						}
					}).start();
					
					break;
				case TYPE_SUBSIDENCE:
					new Thread(new Runnable() {
						@Override
						public void run() {
							updateSubsidenceSheet();
							notifyDone();
						}
					}).start();
					break;
				default:
					break;
				}
			}
		}, 1000);
	}
	
	private void updateTunnelSheet() {
		if (mSheets != null && mSheets.size() > 0) {
			for(SheetRecord record : mSheets) {
				updateTunnelSheetStatus(record);
			}
		}
	}
	
	private void updateSubsidenceSheet() {
		if (mSheets != null && mSheets.size() > 0) {
			for(SheetRecord record : mSheets) {
				updateSubsidenceSheetStatus(record);
			}
		}
	}
	
	private void updateTunnelSheetStatus(SheetRecord record) {
		RawSheetIndex sheetIndex = record.getRawSheet();
		int totalSections = 0, uploadedSections = 0, unUploadSections = 0;
		int totalPoints = 0,  uploadedPoints = 0, unUploadedPoints = 0;
		List<TunnelCrossSectionIndex> sectionIndexList = TunnelCrossSectionIndexDao.defaultDao().querySectionByGuids(sheetIndex.getCrossSectionIDs());
		if (sectionIndexList != null && sectionIndexList.size() > 0) {
			totalSections = sectionIndexList.size();
			for(TunnelCrossSectionIndex sectionIndex : sectionIndexList) {
				switch (sectionIndex.getUploadStatus()) {
				case 1:
					unUploadSections++;
					break;
				case 2:
					uploadedSections++;
				default:
					break;
				}
				//断面相关测点查询
				List<TunnelSettlementTotalData> pointList = TunnelSettlementTotalDataDao.defaultDao().queryTunnelTotalDatas(sheetIndex.getID(), sectionIndex.getID());
				if (pointList != null && pointList.size() > 0) {
					totalPoints += pointList.size();
					for(TunnelSettlementTotalData point : pointList) {
						switch (point.getUploadStatus()) {
						case 1:
							unUploadedPoints++;
							break;
						case 2:
							uploadedPoints++;
						default:
							break;
						}
					}
				}
			}
		}
		int uploadStatus;
		if ((uploadedSections == 0) && (uploadedPoints == 0)){//所有数据均未上传
			uploadStatus = 1;
		} else if ((totalSections == uploadedSections) && (totalPoints == uploadedPoints)) { // 所有数据均已上传
			uploadStatus = 2;
		} else {
			uploadStatus = 3; // 有一部分上传
		}
		sheetIndex.setUploadStatus(uploadStatus);
		RawSheetIndexDao.defaultDao().update(sheetIndex);
	}
	
	private void updateSubsidenceSheetStatus(SheetRecord record) {
		RawSheetIndex sheetIndex = record.getRawSheet();
		int totalSections = 0, uploadedSections = 0, unUploadSections = 0;
		int totalPoints = 0,  uploadedPoints = 0, unUploadedPoints = 0;
		List<SubsidenceCrossSectionIndex>  sectionIndexList = SubsidenceCrossSectionIndexDao.defaultDao().querySectionByGuids(sheetIndex.getCrossSectionIDs());
		if (sectionIndexList != null && sectionIndexList.size() > 0) {
			totalSections = sectionIndexList.size();
			for(SubsidenceCrossSectionIndex sectionIndex : sectionIndexList) {
				switch (sectionIndex.getUploadStatus()) {
				case 1:
					unUploadSections++;
					break;
				case 2:
					uploadedSections++;
				default:
					break;
				}
				//断面相关测点查询
		        List<SubsidenceTotalData> pointList =  SubsidenceTotalDataDao.defaultDao().querySubsidenceTotalDatas(sheetIndex.getID(), sectionIndex.getID());
		        if (pointList != null && pointList.size() > 0) {
					totalPoints += pointList.size();
					for(SubsidenceTotalData point : pointList) {
						switch (point.getUploadStatus()) {
						case 1:
							unUploadedPoints++;
							break;
						case 2:
							uploadedPoints++;
						default:
							break;
						}
					}
				}
			}
		}
		//
		int uploadStatus;
		if ((uploadedSections == 0) && (uploadedPoints == 0)){//所有数据均未上传
			uploadStatus = 1;
		} else if ((totalSections == uploadedSections) && (totalPoints == uploadedPoints)) { // 所有数据均已上传
			uploadStatus = 2;
		} else {
			uploadStatus = 3; // 有一部分上传
		}
		sheetIndex.setUploadStatus(uploadStatus);
		RawSheetIndexDao.defaultDao().update(sheetIndex);
	}
	
	private void notifyDone() {
		if (mListener != null) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					mListener.done();
				}
			});
		}
	}
}
