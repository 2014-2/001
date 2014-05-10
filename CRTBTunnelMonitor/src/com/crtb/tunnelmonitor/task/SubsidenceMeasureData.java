package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ICT.utils.RSACoder;
import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TotalStationInfoDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TotalStationIndex;

public class SubsidenceMeasureData extends MeasureData {
	private List<SubsidenceTotalData> mMeasurePoints = new ArrayList<SubsidenceTotalData>();
    private String mMointorModel = null;
    private float mFaceDistance = 0f;
    private String mFaceDescription = null;

	public void addMeasurePoint(SubsidenceTotalData point) {
		mMeasurePoints.add(point);
	}

	@Override
	public Date getMeasureDate() {
		Date mesureDate = null;
		if (mMeasurePoints != null && mMeasurePoints.size() > 0) {
			SubsidenceTotalData point = mMeasurePoints.get(0);
			mesureDate = point.getSurveyTime();
		}
		return mesureDate;
	}

	@Override
	public String getPointCodeList(String sectionCode) {
		StringBuilder sb = new StringBuilder();
		final int totalCount = mMeasurePoints.size();
		for (int i = 0; i < totalCount; i++) {
			sb.append(sectionCode + "DB" + String.format("%02d", i) + "/");
		}
		sb.deleteCharAt(sb.lastIndexOf("/"));

		return sb.toString();
	}

	@Override
	public String getCoordinateList() {
		final int pointCount = mMeasurePoints.size();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pointCount; i++) {
			sb.append(mMeasurePoints.get(i).getCoordinate().replace(",", "#") + "/");
		}
		sb.deleteCharAt(sb.lastIndexOf("/"));
		Log.d("SubsidenceMeasureData", sb.toString());
		String coordinate = sb.toString();
		return RSACoder.encnryptDes(coordinate, Constant.testDeskey);
	}

	@Override
	public String getValueList() {
		final int pointCount = mMeasurePoints.size();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pointCount; i++) {
			sb.append(mMeasurePoints.get(i).getCoordinate().split(",")[2] + "/");
		}
		sb.deleteCharAt(sb.lastIndexOf("/"));
		return sb.toString();
	}

    public String getFaceDescription() {
        if (TextUtils.isEmpty(mFaceDescription)) {
            SubsidenceTotalData first = mMeasurePoints.size() > 0 ? mMeasurePoints.get(0)
                    : null;
            if (first != null) {
                int sheetId = first.getSheetId();
                RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneById(sheetId);
                if (sheet != null) {
                    mFaceDescription = sheet.getFACEDESCRIPTION();
                }
            }
        }
        return mFaceDescription;
    }

    public float getFaceDistance() {
        SubsidenceTotalData first = mMeasurePoints.size() > 0 ? mMeasurePoints.get(0) : null;
        if (first != null) {
            int chainageId = first.getChainageId();
            SubsidenceCrossSectionIndex section = SubsidenceCrossSectionIndexDao.defaultDao()
                    .querySectionById(chainageId);
            if (section != null) {
                double chainage = section.getChainage();

                int sheetId = first.getSheetId();
                RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneById(sheetId);
                if (sheet != null) {
                    double facedk = sheet.getFACEDK();
                    mFaceDistance  = (float) (facedk - chainage);
                }
            }
        }
        return mFaceDistance;
    }

    public String getMonitorModel() {
        if (TextUtils.isEmpty(mMointorModel)) {
            int id = mMeasurePoints.size() > 0 ? mMeasurePoints.get(0).getStationId() : -1;
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
				SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao();
				for (SubsidenceTotalData point : mMeasurePoints) {
					point.setInfo("2");
					dao.update(point);
				}
			}
		}).start();
	}

}
