package com.crtb.tunnelmonitor.task;

import java.util.Arrays;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.dao.impl.v2.WorkSiteIndexDao;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.entity.WorkSiteIndex;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.SectionStatus;
import com.crtb.tunnelmonitor.utils.CrtbUtils;


public class DataDownloadManager {
	private static final String LOG_TAG = "DataDownloadManager";
	
	public interface DownloadListener {
		/**
		 * 
		 * @param success
		 */
		public void done(boolean success);
	}

	private DownloadListener mListener;
	private Handler mHandler;
    protected String mSectionPrefix;
	
	public DataDownloadManager() {
		mHandler = new Handler(Looper.getMainLooper());
		mSectionPrefix = CrtbUtils.getSectionPrefix();
	}
	
	public void downloadWorkSite(WorkSiteIndex workSite, DownloadListener listener) {
		mListener = listener;
		downloadSectionCodeList(workSite.getSiteCode(), SectionStatus.VALID);
	}
	
	public void downloadWorkSiteList(final DownloadListener listener) {
		CrtbWebService.getInstance().getZoneAndSiteCode(new RpcCallback() {
			@Override
			public void onSuccess(Object[] data) {
				WorkZone[] workZoneList = (WorkZone[]) data;
				if (workZoneList != null && workZoneList.length > 0) {
					for(WorkZone workZone : workZoneList) {
						List<WorkSite> workSites = workZone.getWorkSites();
						for(WorkSite workSite : workSites) {
							WorkSiteIndex site = new WorkSiteIndex();
							site.setZoneCode(workZone.getZoneCode());
							site.setZoneName(workZone.getZoneName());
							site.setSiteCode(workSite.getSiteCode());
							site.setSiteName(workSite.getSiteName());
							site.setDownloadFlag(1);
							site.setProjectId(-1);
							WorkSiteIndexDao dao = WorkSiteIndexDao.defaultDao();
							dao.insert(site);
						}
					}
				}
				if (listener != null) {
					listener.done(true);
				}
			}
			
			@Override
			public void onFailed(String reason) {
				if (listener != null) {
					listener.done(false);
				}
			}
		});
	}
	
	
    //下载断面编码数据
    private void downloadSectionCodeList(String siteCode, SectionStatus status) {
    	//3 = 断面编码下载 + 断面下载 
    	final DataCounter downloadCounter = new DataCounter("DownloadCounter)", 2, new CounterListener() {
			@Override
			public void done(final boolean success) {
				if (mListener != null) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mListener.done(success);
						}
					});
				}
			}
		});
        CrtbWebService.getInstance().getSectionCodeList(siteCode, status, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                Log.d(LOG_TAG, "download section code list success.");
                List<String> sectionCodeList = Arrays.asList((String[])data);
                if (sectionCodeList != null && sectionCodeList.size() > 0) {
                	//标识断面编码列表下载完毕
                	downloadCounter.increase(true, "SecitonCodeList");
                	DataCounter sectionDownloadCounter = new DataCounter("SectionDownloadCounter", sectionCodeList.size(), new CounterListener() {
						@Override
						public void done(boolean success) {
							//标识断面数据下载完毕
							downloadCounter.increase(success, "SectionList");
						}
					});
                	for(String sectionCode : sectionCodeList) {
                        downloadSection(sectionCode, sectionDownloadCounter);
                    }
                } else {
                	downloadCounter.finish("section code list is empty");
                }
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "download section code list failed.");
                downloadCounter.finish(reason);
            }
        });
    }
    
    private void downloadSection(final String sectionCode, final DataCounter sectionDownloadCounter) {
        CrtbWebService.getInstance().getSectionInfo(sectionCode, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                TunnelCrossSectionIndex[] sectionInfo = (TunnelCrossSectionIndex[])data;
                final TunnelCrossSectionIndex section = sectionInfo[0];
                if (section != null) {
                    if (TextUtils.isEmpty(section.getChainagePrefix())) {
                        section.setChainagePrefix(mSectionPrefix);
                    }
                    storeTunnelSection(sectionCode, section);
                    List<String> pointCodeList = Arrays.asList(section.getSurveyPntName().split(","));
                    if (pointCodeList != null && pointCodeList.size() > 0) {
                        DataCounter pointDownloadCounter = new DataCounter("PointDownloadCounter", pointCodeList.size(), new CounterListener() {
                            @Override
                            public void done(boolean success) {
                                sectionDownloadCounter.increase(success, "SectionList");
                            }
                        });
                        for(String pointCode : pointCodeList) {
                            downloadPoint(sectionCode, pointCode, pointDownloadCounter);
                        }
                    } else {
                        sectionDownloadCounter.increase(true, "SectionList");
                    }
                }
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "downloadSection failed: " + reason);
                sectionDownloadCounter.increase(false, "SectionList");
            }
        });
    }
    
    private void downloadPoint(final String sectionCode, final String pointCode, final DataCounter pointDownloadCounter) {
        CrtbWebService.getInstance().getPointInfo(pointCode, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
            	if (data != null && data.length > 0) {
            		final List<TunnelSettlementTotalData> pointTestDataList = Arrays.asList((TunnelSettlementTotalData[])data);
            		storeTunnelPoints(pointTestDataList);
            	}
                pointDownloadCounter.increase(true, sectionCode);
                Log.d(LOG_TAG, "download point success.");
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "download point failed: " + reason);
                pointDownloadCounter.increase(false, sectionCode);
            }
        });
    }
    
    private void storeTunnelSection(final String sectionCode, final TunnelCrossSectionIndex section) {
    	 new Thread(new Runnable() {
             @Override
             public void run() {
                 TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
                 int id = dao.insertOrUpdate(section);
                 if (id >=0) {
                     TunnelCrossSectionExIndexDao.defaultDao().insertIfNotExist(id, sectionCode);
                 }
             }
         }).start();
    }
    
    private void storeTunnelPoints(final List<TunnelSettlementTotalData> pointTestDataList) {
    	new Thread(new Runnable() {
            @Override
            public void run() {
                TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao();
                for(TunnelSettlementTotalData testPointData : pointTestDataList) {
                    dao.insert(testPointData);
                }
            }
        }).start();
    }
    
    private void storeSubsidenceSection(final String sectionCode, final SubsidenceCrossSectionIndex section) {
   	 new Thread(new Runnable() {
            @Override
            public void run() {
                SubsidenceCrossSectionIndexDao dao = SubsidenceCrossSectionIndexDao.defaultDao();
                int id = dao.insertOrUpdate(section);
                if (id >= 0) {
                    SubsidenceCrossSectionExIndexDao.defaultDao().insertIfNotExist(id, sectionCode);
                }
            }
        }).start();
   }
    
    private void storeSubsidencePoints(final List<SubsidenceTotalData> pointTestDataList) {
    	new Thread(new Runnable() {
            @Override
            public void run() {
            	SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao();
                for(SubsidenceTotalData testPointData : pointTestDataList) {
                    dao.insert(testPointData);
                }
            }
        }).start();
    }
    
    private void storeWorkSite(final WorkSiteIndex site, final DataCounter workSiteCounter) {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				WorkSiteIndexDao dao = WorkSiteIndexDao.defaultDao();
				dao.insert(site);
				workSiteCounter.increase(true);
			}
		}).start();
    }
}
