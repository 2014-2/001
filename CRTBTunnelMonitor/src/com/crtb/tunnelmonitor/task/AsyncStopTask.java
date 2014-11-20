package com.crtb.tunnelmonitor.task;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.CrossSectionStopSurveyingDao;
import com.crtb.tunnelmonitor.entity.CrossSectionStopSurveying;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.CrossSectionStopSurveying.CrossSectionEnum;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.RpcCallback;

public class AsyncStopTask extends AsyncTask<SectionStopEntity, Void, Void> {
	private static final String TAG = "AsyncStopTask: ";

	public interface StopListener {
		/**
		 * 
		 * @param success
		 */
		public void done(boolean success,String reason);
	}

	private StopListener mListener;
	private Handler mHandler;

	public AsyncStopTask(StopListener listener) {
		mListener = listener;
		mHandler = new Handler(Looper.getMainLooper());
	}

	@Override
	protected Void doInBackground(SectionStopEntity... param) {		
		if (param == null) {
			Log.e(Constant.LOG_TAG_ACTIVITY, TAG +"doInBackground:"+ " empty SectionStopEntity.");
			done(false, null);
			return null;
		}
		
		final SectionStopEntity entity = param[0];
		CrtbWebService handler = CrtbWebService.getInstance();
		entity.workAreaCode = handler.getZoneCode();
	    entity.workSiteCode = handler.getSiteCode();
		handler.stopSection(entity, new RpcCallback() {

			@Override
			public void onSuccess(Object[] data) {
				int state = -1;
				CrossSectionStopSurveying sectionStopSurvey = null;
				if(entity.tunnel != null){
					sectionStopSurvey = new CrossSectionStopSurveying();
					sectionStopSurvey.setCrossSectionChainage(entity.tunnel.getChainage());
					sectionStopSurvey.setCrossSectionId(entity.tunnel.getGuid());
					sectionStopSurvey.setCrossSectionType(CrossSectionEnum.Tunnel);

				} else if(entity.sub != null){
					sectionStopSurvey = new CrossSectionStopSurveying();
					sectionStopSurvey.setCrossSectionChainage(entity.sub.getChainage());
					sectionStopSurvey.setCrossSectionId(entity.sub.getGuid());
					sectionStopSurvey.setCrossSectionType(CrossSectionEnum.Sub);
				}
				state = CrossSectionStopSurveyingDao.defaultDao().insert(sectionStopSurvey);
				if(state != CrossSectionStopSurveyingDao.defaultDao().DB_EXECUTE_SUCCESS){
					Log.e(Constant.LOG_TAG_ACTIVITY, TAG +"doInBackground:"+" insert CrossSectionStopSurveying failed.");
				}
				done(true, null);
			}

			@Override
			public void onFailed(String reason) {
				done(false, reason);
			}
		});
		return null;
	}
	
	private void done(final boolean success,final String reason){
		mHandler.post(new Runnable() {
 			@Override
 			public void run() {
 				if (mListener != null) {
 					mListener.done(success,reason);
 				}
 			}
 		});
	}
}
