package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.MergedAlert;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.WarningUploadParameter;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
import com.crtb.tunnelmonitor.utils.SectionInterActionManager;

public class WarningDataManager {
	private static final String LOG_TAG = "WarningDataManager";
	public interface WarningLoadListener {
		/**
		 * 数据加载完毕
		 * 
		 * @param uploadDataList
		 *            等待上传的数据
		 */
		public void done(List<UploadWarningData> uploadDataList);
	}

	public interface WarningUploadListener {
		/**
		 * 
		 * @param success
		 */
		public void done(boolean success);
	}

	private WarningLoadListener mLoadListener;
	private WarningUploadListener mUploadListener;
	public void loadData(WarningLoadListener loadListener) {
		mLoadListener = loadListener;
		new DataLoadTask().execute();
	}

	public void uploadData(List<UploadWarningData> warningDataList, WarningUploadListener uploadListener) {
		mUploadListener = uploadListener;
		new DataUploadTask().execute(warningDataList);
	}

	private class DataLoadTask extends AsyncTask<Void, Void, List<UploadWarningData>> {

		@Override
		protected List<UploadWarningData> doInBackground(Void... params) {
			List<UploadWarningData> uploadWarningDataList = new ArrayList<UploadWarningData>();
//			ArrayList<AlertInfo> alertInfoList = AlertUtils.getAlertInfoList();
//			if (alertInfoList != null && alertInfoList.size() > 0) {
//				for(AlertInfo alertInfo : alertInfoList) {
//					UploadWarningData warningData = new UploadWarningData();
//					warningData.setAlertInfo(alertInfo);
//					warningData.setSectionCode(alertInfo.getSECTCODE());
//					uploadWarningDataList.add(warningData);
//				}
//			}
			ArrayList<MergedAlert> mal = AlertUtils.getMergedAlerts();
			Collections.sort(mal);
			if (mal != null && mal.size() > 0) {
			    for (MergedAlert ma : mal) {
//			        boolean can = AlertUtils.mergedAlertCanBeUploaded(ma);
			    	UploadWarningData warningData = new UploadWarningData();
			        warningData.setLeijiAlert(ma.getLeijiAlert());
			        warningData.setSulvAlert(ma.getSulvAlert());
					warningData.setSectionCode(warningData.getAlertInfo().getSECTCODE());
					uploadWarningDataList.add(warningData);
			    }
			}
			
//			if (uploadWarningDataList.size() == 0) {
//				UploadWarningData fakeWarningData = new UploadWarningData();
//				AlertInfo fakeAlertInfo = new AlertInfo();
//				fakeAlertInfo.setPntType("A");
//				fakeAlertInfo.setAlertStatus(0);
//				fakeAlertInfo.setAlertStatusMsg("已销警");
//				fakeAlertInfo.setDate(CrtbUtils.formatDate(new Date()));
//				fakeWarningData.setAlertInfo(fakeAlertInfo);
//				fakeWarningData.setSectionCode("XPCL01SD00010001");
//				uploadWarningDataList.add(fakeWarningData);
//			}
			return uploadWarningDataList;
		}

		@Override
		protected void onPostExecute(List<UploadWarningData> result) {
			if (mLoadListener != null) {
				mLoadListener.done(result);
			}
		}

	}

//	private String getSectionCodeById(int sectionId) {
//		String sectionCode = "";
//		TunnelCrossSectionExIndexDao sectionExIndexDao = TunnelCrossSectionExIndexDao.defaultDao();
//		TunnelCrossSectionExIndex sectionExIndex = sectionExIndexDao.querySectionById(sectionId);
//		if (sectionExIndex != null) {
//			sectionCode = sectionExIndex.getSECTCODE();
//		}
//		return sectionCode;
//	}
	
	private class DataUploadTask extends AsyncTask<List<UploadWarningData>, Void, Void> {
		
		@Override
		protected Void doInBackground(List<UploadWarningData>... params) {
			if (params != null && params.length > 0) {
				List<UploadWarningData> warningDataList = params[0];
				if (warningDataList != null && warningDataList.size() > 0) {
					DataCounter warningUploadCounter = new DataCounter("WarningUploadCounter", warningDataList.size(), new CounterListener() {
						@Override
						public void done(boolean success) {
							if (mUploadListener != null) {
								mUploadListener.done(success);
							}
						}
					});
					for(UploadWarningData warningData : warningDataList) {
						uploadWarningData(warningData, warningUploadCounter);
					}
				}
			}
			return null;
		}
	}

    private void uploadWarningData(UploadWarningData warningData, final DataCounter warningUploadCounter) {
    	final AlertInfo alertInfo = warningData.getAlertInfo();
    	WarningUploadParameter parameter = new WarningUploadParameter();
    	parameter.setSectionCode(warningData.getSectionCode());
    	parameter.setPointCode(warningData.getPointCode());
    	int level = alertInfo.getAlertLevel();
		if ((level == 2) || (level == 3)) {
			parameter.setWarningLevel(1);
		} else if (level == 1) {
			parameter.setWarningLevel(2);
		} else {
			parameter.setWarningLevel(1);
		}
		AlertInfo sulvalert = warningData.getSulvAlert();
    	parameter.setTransformSpeed(sulvalert != null ? (float)sulvalert.getUValue() : 0.001f);
//    	String originalID = alertInfo.getOriginalDataID();
//    	List<Integer> ids = new ArrayList<Integer>();
//        if (originalID.contains(AlertUtils.ORIGINAL_ID_DIVIDER)) {
//            String[] idStrs = originalID.split(AlertUtils.ORIGINAL_ID_DIVIDER);
//            for (String idStr : idStrs) {
//                ids.add(Integer.valueOf(idStr));
//            }
//        } else {
//            ids.add(Integer.valueOf(originalID));
//        }

//        float pointValue = 0.0f;
//        TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao();
//        if (ids.size() == 1) {
//        	TunnelSettlementTotalData point = dao.queryOneById(ids.get(0));
//        	pointValue = Float.parseFloat(point.getCoordinate().split(",")[2]);
//        } else {
//        	TunnelSettlementTotalData point1 = dao.queryOneById(ids.get(0));
//        	TunnelSettlementTotalData point2 = dao.queryOneById(ids.get(1));
//        	pointValue = (float) AlertUtils.getLineLength(point1, point2);
//        }
        AlertInfo leijiAlert = warningData.getLeijiAlert();
    	parameter.setWarningPointValue((float) (leijiAlert != null ? leijiAlert.getUValue() : 0.001f));
    	parameter.setWarningDate(CrtbUtils.parseDate(alertInfo.getDate()));
    	String duePerson = alertInfo.getDuePerson();
    	if (TextUtils.isEmpty(duePerson)) {
    	    duePerson = AppCRTBApplication.mUserName;
    	}
    	parameter.setWarningPerson(duePerson);
    	parameter.setWarningDescription(alertInfo.getHandling());
    	parameter.setWarningEndTime(CrtbUtils.parseDate(alertInfo.getHandlingTime()));
    	// On website, 1 means alert, 0 means closed. but local database & windows has the different define.
    	parameter.setWarningResult(alertInfo.getAlertStatus() == 1 ? 0 : 1);
    	parameter.setRemark(alertInfo.getHandling());
        CrtbWebService.getInstance().uploadWarningData(parameter, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
            	warningUploadCounter.increase(true);
                Log.d(LOG_TAG, "upload warning data success.");
            }

            @Override
            public void onFailed(String reason) {
            	warningUploadCounter.increase(false);
                Log.d(LOG_TAG, "upload warning data failed.");
            }
        });
    }
    
    public class UploadWarningData {
    	private AlertInfo mLeijiAlert;
    	private AlertInfo mSulvAlert;
    	private String mSectionCode;
    	
    	public ArrayList<AlertInfo> getAlertInfos() {
    	    ArrayList<AlertInfo> as = new ArrayList<AlertInfo>();
    	    as.add(mLeijiAlert);
    	    as.add(mSulvAlert);
    	    return as;
    	}

    	public AlertInfo getAlertInfo() {
    	    if (mLeijiAlert != null) {
    	        return mLeijiAlert;
    	    } else {
    	        return mSulvAlert;
    	    }
    	}
	
    	public void setLeijiAlert(AlertInfo alertInfo) {
    		mLeijiAlert = alertInfo;
    	}
    	
    	public AlertInfo getLeijiAlert() {
    		return mLeijiAlert;
    	}
    	
    	public void setSectionCode(String sectionCode) {
    		mSectionCode = sectionCode;
    	}
    	
    	public String getSectionCode() {
    		return mSectionCode;
    	}
    	
    	public AlertInfo getSulvAlert() {
            return mSulvAlert;
        }

        public void setSulvAlert(AlertInfo sulvAlert) {
            mSulvAlert = sulvAlert;
        }

        public MergedAlert getMergedAlert() {
            MergedAlert ma = new MergedAlert();
            ma.setLeijiAlert(mLeijiAlert);
            ma.setSulvAlert(mSulvAlert);
            return ma;
        }

    	private TunnelCrossSectionIndexDao sectionIndexDao = null;
        
        public String getPointCode() {
        	//YX  根据开挖方法，获取点或线对应的测点上传序列    	
        	AlertInfo ai = getAlertInfo();
        	return SectionInterActionManager.getOneLineDetailsByPointType(ai.getSECTCODE(),ai.getPntType());
    	}
    }
}
