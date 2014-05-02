package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ICT.utils.RSACoder;

import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;

public class SubsidenceMeasureData extends MeasureData {
	private List<SubsidenceTotalData> mMeasurePoints = new ArrayList<SubsidenceTotalData>();

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
