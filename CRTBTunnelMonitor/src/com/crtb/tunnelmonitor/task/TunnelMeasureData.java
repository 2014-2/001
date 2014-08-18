package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ICT.utils.RSACoder;
import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TotalStationInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.TotalStationIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.utils.AlertUtils;

public class TunnelMeasureData extends MeasureData {
	private static final String LOG_TAG = "TunnelMeasureData";
	private static final String POINT_TYPE_A = "A";
	private static final String POINT_TYPE_S1_1 = "S1-1";
	private static final String POINT_TYPE_S1_2 = "S1-2";
	private static final String POINT_TYPE_S2_1 = "S2-1";
	private static final String POINT_TYPE_S2_2 = "S2-2";
	private static final String POINT_TYPE_S3_1 = "S3-1";
	private static final String POINT_TYPE_S3_2 = "S3-2";
	private TunnelSurveyTimeComparator comparator = new TunnelSurveyTimeComparator();

	private List<TunnelSettlementTotalData> mMeasurePoints = new ArrayList<TunnelSettlementTotalData>();
	private String mMointorModel = null;
	private float mFaceDistance = 0f;

    private String mFaceDescription = null;

	public void addMeasurePoint(TunnelSettlementTotalData point) {
		mMeasurePoints.add(point);
	}

	@Override
	public Date getMeasureDate() {
		Date mesureDate = null;
		if (mMeasurePoints != null && mMeasurePoints.size() > 0) {
			Collections.sort(mMeasurePoints, comparator);
			TunnelSettlementTotalData point = mMeasurePoints.get(mMeasurePoints.size() - 1);
			mesureDate = point.getSurveyTime();
		}
		return mesureDate;
	}

	@Override
	public String getPointCodeList(String sectionCode) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		if (getPointByType(POINT_TYPE_A) != null) {
			sb.append(sectionCode + "GD01");
			first = false;
		}
		TunnelSettlementTotalData s1_1 = getPointByType(POINT_TYPE_S1_1);
		TunnelSettlementTotalData s1_2 = getPointByType(POINT_TYPE_S1_2);
		if (s1_1 != null && s1_2 != null) {
			if (first) {
				sb.append(sectionCode + "SL01" + "#" + sectionCode + "SL02");
				first = false;
			} else {
				sb.append("/" + sectionCode + "SL01" + "#" + sectionCode + "SL02");
			}
		}
		TunnelSettlementTotalData s2_1 = getPointByType(POINT_TYPE_S2_1);
		TunnelSettlementTotalData s2_2 = getPointByType(POINT_TYPE_S2_2);
		if (s2_1 != null && s2_2 != null) {
			if (first) {
				sb.append(sectionCode + "SL03" + "#" + sectionCode + "SL04");
				first = false;
			} else {
				sb.append("/" + sectionCode + "SL03" + "#" + sectionCode + "SL04");
			}
		}
		TunnelSettlementTotalData s3_1 = getPointByType(POINT_TYPE_S3_1);
		TunnelSettlementTotalData s3_2 = getPointByType(POINT_TYPE_S3_2);
		if (s3_1 != null && s3_2 != null) {
			if (first) {
				sb.append(sectionCode + "SL05" + "#" + sectionCode + "SL06");
				first = false;
			} else {
				sb.append("/" + sectionCode + "SL05" + "#" + sectionCode + "SL06");
			}
		}
		return sb.toString();
	}

	@Override
	public String getCoordinateList() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		TunnelSettlementTotalData pointA = getPointByType(POINT_TYPE_A);
		if (pointA != null) {
			sb.append(pointA.getCoordinate().replace(",", "#"));
			first = false;
		}
		TunnelSettlementTotalData  pointS1_1 = getPointByType(POINT_TYPE_S1_1);
		TunnelSettlementTotalData  pointS1_2 = getPointByType(POINT_TYPE_S1_2);
		if (pointS1_1 != null && pointS1_2 != null) {
			String S1_1 = pointS1_1.getCoordinate().replace(",", "#");
			String S1_2 = pointS1_2.getCoordinate().replace(",", "#");
			if (first) {
				sb.append(S1_1 + "#" + S1_2);
				first = false;
			} else {
				sb.append("/" + S1_1 + "#" + S1_2);
			}
		}
		TunnelSettlementTotalData pointS2_1 = getPointByType(POINT_TYPE_S2_1);
		TunnelSettlementTotalData pointS2_2 = getPointByType(POINT_TYPE_S2_2);
		if (pointS2_1 != null && pointS2_2 != null) {
			String S2_1 = pointS2_1.getCoordinate().replace(",", "#");
			String S2_2 = pointS2_2.getCoordinate().replace(",", "#");
			if (first) {
				sb.append(S2_1 + "#" + S2_2);
				first = false;
			} else {
				sb.append("/" + S2_1 + "#" + S2_2);
			}
		}
		TunnelSettlementTotalData pointS3_1 = getPointByType(POINT_TYPE_S3_1);
		TunnelSettlementTotalData pointS3_2 = getPointByType(POINT_TYPE_S3_2);
		if (pointS3_1 != null && pointS3_2 != null) {
			String S3_1 = pointS3_1.getCoordinate().replace(",", "#");
			String S3_2 = pointS3_2.getCoordinate().replace(",", "#");
			if (first) {
				sb.append(S3_1 + "#" + S3_2);
				first = false;
			} else {
				sb.append("/" + S3_1 + "#" + S3_2);
			}
		}
		String coordinate = sb.toString();
		Log.d(LOG_TAG, "coordinate: " + coordinate);
		return RSACoder.encnryptDes(coordinate, Constant.testDeskey);
	}

	@Override
	public String getValueList() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		TunnelSettlementTotalData pointA = getPointByType(POINT_TYPE_A);
		if (pointA != null) {
			String[] cA = pointA.getCoordinate().split(",");
			sb.append(cA[2]);
			first = false;
		}
		TunnelSettlementTotalData pointS1_1 = getPointByType(POINT_TYPE_S1_1);
		TunnelSettlementTotalData pointS1_2 = getPointByType(POINT_TYPE_S1_2);
		if (pointS1_1 != null && pointS1_2 != null) {
			if (first) {
				sb.append(AlertUtils.getLineLength(pointS1_1, pointS1_2));
				first = false;
			} else {
				sb.append("/" + AlertUtils.getLineLength(pointS1_1, pointS1_2));
			}
		}
		TunnelSettlementTotalData pointS2_1 = getPointByType(POINT_TYPE_S2_1);
		TunnelSettlementTotalData pointS2_2 = getPointByType(POINT_TYPE_S2_2);
		if (pointS2_1 != null && pointS2_2 != null) {
			if (first) {
				sb.append(AlertUtils.getLineLength(pointS2_1, pointS2_2));
				first = false;
			} else {
				sb.append("/" + AlertUtils.getLineLength(pointS2_1, pointS2_2));
			}
		}
		TunnelSettlementTotalData pointS3_1 = getPointByType(POINT_TYPE_S3_1);
		TunnelSettlementTotalData pointS3_2 = getPointByType(POINT_TYPE_S3_2);
		if (pointS3_1 != null && pointS3_2 != null) {
			if (first) {
				sb.append(AlertUtils.getLineLength(pointS3_1, pointS3_2));
				first = false;
			} else {
				sb.append("/" + AlertUtils.getLineLength(pointS3_1, pointS3_2));
			}
		}
		return sb.toString();
	}

    public String getFaceDescription() {
        if (TextUtils.isEmpty(mFaceDescription)) {
            TunnelSettlementTotalData first = mMeasurePoints.size() > 0 ? mMeasurePoints.get(0)
                    : null;
            if (first != null) {
                RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneByGuid(first.getSheetId());
                if (sheet != null) {
                    mFaceDescription = sheet.getFACEDESCRIPTION();
                }
            }
        }
        return mFaceDescription;
    }

    public float getFaceDistance() {
        TunnelSettlementTotalData first = mMeasurePoints.size() > 0 ? mMeasurePoints.get(0) : null;
        if (first != null) {
            TunnelCrossSectionIndex section = TunnelCrossSectionIndexDao.defaultDao()
                    .querySectionByGuid(first.getChainageId());
            if (section != null) {
                double chainage = section.getChainage();
                RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneByGuid(first.getSheetId());
                if (sheet != null) {
                    double facedk = sheet.getFACEDK();
                    mFaceDistance = (float) (facedk - chainage);
                }
            }

        }
        return mFaceDistance;
    }

    public String getMonitorModel() {
        if (TextUtils.isEmpty(mMointorModel)) {
        	
        	// by wei.zhou 2014-05-27
        	String str = mMeasurePoints.size() > 0 ? mMeasurePoints.get(0).getStationId() : "-1";
        	int id = -1 ;
        	
        	try{
        		id = Integer.valueOf(str);
        	}catch(Exception e){
        		e.printStackTrace() ;
        	}
        	
            // int id = mMeasurePoints.size() > 0 ? mMeasurePoints.get(0).getStationId() : -1;
            
        	if (id >= 0) {
                TotalStationIndex station = TotalStationInfoDao.defaultDao().queryOneById(id);
                if (station != null) {
                    mMointorModel = station.getName();
                }
            }
        }
        return mMointorModel;
    }

	@Override
	public void markAsUploaded() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao();
				for (TunnelSettlementTotalData point : mMeasurePoints) {
                    if (POINT_TYPE_S1_1.equals(point.getPntType())) {
                        TunnelSettlementTotalData pointS1_2 = getPointByType(POINT_TYPE_S1_2);
                        if (pointS1_2 == null) {
                            continue;
                        }
                    } else if (POINT_TYPE_S1_2.equals(point.getPntType())) {
                        TunnelSettlementTotalData pointS1_1 = getPointByType(POINT_TYPE_S1_1);
                        if (pointS1_1 == null) {
                            continue;
                        }
                    } else if (POINT_TYPE_S2_1.equals(point.getPntType())) {
                        TunnelSettlementTotalData pointS2_2 = getPointByType(POINT_TYPE_S2_2);
                        if (pointS2_2 == null) {
                            continue;
                        }
                    } else if (POINT_TYPE_S2_2.equals(point.getPntType())) {
                        TunnelSettlementTotalData pointS2_1 = getPointByType(POINT_TYPE_S2_1);
                        if (pointS2_1 == null) {
                            continue;
                        }
                    } else if (POINT_TYPE_S3_1.equals(point.getPntType())) {
                        TunnelSettlementTotalData pointS3_2 = getPointByType(POINT_TYPE_S3_2);
                        if (pointS3_2 == null) {
                            continue;
                        }
                    } else if (POINT_TYPE_S3_2.equals(point.getPntType())) {
                        TunnelSettlementTotalData pointS3_1 = getPointByType(POINT_TYPE_S3_1);
                        if (pointS3_1 == null) {
                            continue;
                        }
                    }

//					point.setInfo("2");
					point.setUploadStatus(2); //表示该测点已上传
					dao.update(point);
				}
			}
		}).start();
	}

	private TunnelSettlementTotalData getPointByType(String type) {
		TunnelSettlementTotalData result = null;
		for (TunnelSettlementTotalData point : mMeasurePoints) {
			if (type.equals(point.getPntType())) {
				result = point;
			}
		}
		return result;
	}
	
	public String getSheetGuid() {
		String sheetGuid = "";
		TunnelSettlementTotalData first = mMeasurePoints.size() > 0 ? mMeasurePoints
				.get(0) : null;
		if (first != null) {
			sheetGuid = first.getSheetId();
		}
		return sheetGuid;
	}
	
	public static List<MeasureData> createMeasureData(
            List<TunnelSettlementTotalData> measurePoints) {
        List<MeasureData> measureDataList = new ArrayList<MeasureData>();
        TunnelSettlementTotalData a = getPointByType(measurePoints, POINT_TYPE_A);
        TunnelSettlementTotalData s1_1 = getPointByType(measurePoints, POINT_TYPE_S1_1);
        TunnelSettlementTotalData s1_2 = getPointByType(measurePoints, POINT_TYPE_S1_2);
        TunnelSettlementTotalData s2_1 = getPointByType(measurePoints, POINT_TYPE_S2_1);
        TunnelSettlementTotalData s2_2 = getPointByType(measurePoints, POINT_TYPE_S2_2);
        TunnelSettlementTotalData s3_1 = getPointByType(measurePoints, POINT_TYPE_S3_1);
        TunnelSettlementTotalData s3_2 = getPointByType(measurePoints, POINT_TYPE_S3_2);

        if (a != null) {
            TunnelMeasureData measureData = new TunnelMeasureData();
            measureData.addMeasurePoint(a);
            measureDataList.add(measureData);
        }

        if (s1_1 != null && s1_2 != null) {
            TunnelMeasureData measureData = new TunnelMeasureData();
            measureData.addMeasurePoint(s1_1);
            measureData.addMeasurePoint(s1_2);
            measureDataList.add(measureData);
        }

        if (s2_1 != null && s2_2 != null) {
            TunnelMeasureData measureData = new TunnelMeasureData();
            measureData.addMeasurePoint(s2_1);
            measureData.addMeasurePoint(s2_2);
            measureDataList.add(measureData);
        }

        if (s3_1 != null && s3_2 != null) {
            TunnelMeasureData measureData = new TunnelMeasureData();
            measureData.addMeasurePoint(s3_1);
            measureData.addMeasurePoint(s3_2);
            measureDataList.add(measureData);
        }

        return measureDataList;
    }

    private static TunnelSettlementTotalData getPointByType(
            List<TunnelSettlementTotalData> measurePoints, String type) {
        TunnelSettlementTotalData result = null;
        for (TunnelSettlementTotalData point : measurePoints) {
            if (type.equals(point.getPntType())) {
                result = point;
            }
        }
        return result;
    }
    
	class TunnelSurveyTimeComparator implements
			Comparator<TunnelSettlementTotalData> {

		private boolean ASC = true;

		public void setSortType(boolean isAsc) {
			ASC = isAsc;
		}

		@Override
		public int compare(TunnelSettlementTotalData lhs,
				TunnelSettlementTotalData rhs) {
			int compareValue = -2;
			boolean dataRight = false;

			if (lhs != null || rhs != null) {
				Date lSurveyTime = lhs.getSurveyTime();
				Date rSurveyTime = rhs.getSurveyTime();
				if (lSurveyTime != null && rSurveyTime != null) {
					if (ASC) {
						compareValue = lSurveyTime.compareTo(rSurveyTime);
					} else {
						compareValue = rSurveyTime.compareTo(lSurveyTime);
					}
					dataRight = true;
				}
			}

			if (!dataRight) {
				try {
					throw new Exception(
							"TunnelSurveyTimeComparator reference exception");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return compareValue;
		}
	}
}
