package com.crtb.tunnelmonitor.network;

import java.util.List;

import android.util.Log;

import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public class ProjectUploadTask {
	private static final String LOG_TAG = "ProjectUploadTask";
	
	public interface UploadListener {
		public void onUploadStart();
		public void onUpLoadFinished();
	}
	
	private int mSessionCount = 0;
	private UploadListener mListener;

	public void upload(UploadListener listener) {
		mListener = listener;
		uploadSectionList();
	}

	// 上传所有断面数据
	private void uploadSectionList() {
		TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
		List<TunnelCrossSectionIndex> sectionList = dao.queryAllSection();
		if (sectionList != null && sectionList.size() > 0) {
			mSessionCount = sectionList.size();
			for (TunnelCrossSectionIndex section : sectionList) {
				SectionUploadParamter paramter = new SectionUploadParamter();
				CrtbUtils.fillSectionParamter(section, paramter);
				uploadSection(paramter);
			}
		}
	}

	// 上传断面
	private void uploadSection(SectionUploadParamter paramter) {
		CrtbWebService.getInstance().uploadSection(paramter, new RpcCallback() {
			@Override
			public void onSuccess(Object[] data) {
				Log.d(LOG_TAG, "upload section success.");
				// uploadTestResult();
			}

			@Override
			public void onFailed(String reason) {
				Log.d(LOG_TAG, "upload section faled: " + reason);
			}
		});
	}

	// 上传测量数据
	private void uploadTestResult() {
		CrtbWebService.getInstance().uploadTestResult(null, new RpcCallback() {

			@Override
			public void onSuccess(Object[] data) {
				CrtbWebService.getInstance().confirmSubmitData(
						new RpcCallback() {

							@Override
							public void onSuccess(Object[] data) {
								Log.d(LOG_TAG, "upload test data success.");
							}

							@Override
							public void onFailed(String reason) {
								Log.d(LOG_TAG, "confirm test data failed: "
										+ reason);
							}
						});
			}

			@Override
			public void onFailed(String reason) {
				Log.d(LOG_TAG, "upload test data failed.");
			}
		});
	}
}
