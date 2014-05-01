package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ICT.utils.RSACoder;
import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.utils.AlertUtils;

public class TunnelMeasureData extends MeasureData {
	private static final String LOG_TAG = "TunnelMeasureData";

	private List<TunnelSettlementTotalData> mMeasurePoints = new ArrayList<TunnelSettlementTotalData>();

	public void addMeasurePoint(TunnelSettlementTotalData point) {
		mMeasurePoints.add(point);
	}

	@Override
	public Date getMeasureDate() {
		Date mesureDate = null;
		if (mMeasurePoints != null && mMeasurePoints.size() > 0) {
			TunnelSettlementTotalData point = mMeasurePoints.get(0);
			mesureDate = point.getSurveyTime();
		}
		return mesureDate;
	}

	@Override
	public String getPointCodeList(String sectionCode) {
		String pointCodeList = "";
		final int pointCount = mMeasurePoints.size();
		switch (pointCount) {
		// 全断面法
		case 3:
			pointCodeList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#" + sectionCode + "SL02";
			break;
		// 台阶法
		case 5:
			pointCodeList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#" + sectionCode + "SL02" + "/"
					+ sectionCode + "SL03" + "#" + sectionCode + "SL04";
			break;
		// 三台阶法或双侧壁法
		case 7:
			pointCodeList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#" + sectionCode + "SL02" + "/"
					+ sectionCode + "SL03" + "#" + sectionCode + "SL04" + "/" + sectionCode + "SL05" + "#"
					+ sectionCode + "SL06";
			break;
		default:
			Log.d(LOG_TAG, "未知的开挖方法: 测点数目=" + pointCount);
			break;
		}
		return pointCodeList;
	}

	@Override
	public String getCoordinateList() {
		String coordinateList = "";
		final int pointCount = mMeasurePoints.size();
		TunnelSettlementTotalData pointA, pointS1_1, pointS1_2, pointS2_1, pointS2_2, pointS3_1, pointS3_2;
		String A, S1_1, S1_2, S2_1, S2_2, S3_1, S3_2, coordinate;
		switch (pointCount) {
		// 全断面法
		case 3:
			pointA = getPointByType("A");
			pointS1_1 = getPointByType("S1-1");
			pointS1_2 = getPointByType("S1-2");
			A = pointA.getCoordinate().replace(",", "#");
			S1_1 = pointS1_1.getCoordinate().replace(",", "#");
			S1_2 = pointS1_2.getCoordinate().replace(",", "#");
			coordinate = A + "/" + S1_1 + "#" + S1_2;
			coordinateList = RSACoder.encnryptDes(coordinate, Constant.testDeskey);
			break;
		// 台阶法
		case 5:
			pointA = getPointByType("A");
			pointS1_1 = getPointByType("S1-1");
			pointS1_2 = getPointByType("S1-2");
			pointS2_1 = getPointByType("S2-1");
			pointS2_2 = getPointByType("S2-2");
			A = pointA.getCoordinate().replace(",", "#");
			S1_1 = pointS1_1.getCoordinate().replace(",", "#");
			S1_2 = pointS1_2.getCoordinate().replace(",", "#");
			S2_1 = pointS2_1.getCoordinate().replace(",", "#");
			S2_2 = pointS2_2.getCoordinate().replace(",", "#");
			coordinate = A + "/" + S1_1 + "#" + S1_2 + "/" + S2_1 + "#" + S2_2;
			coordinateList = RSACoder.encnryptDes(coordinate, Constant.testDeskey);
			break;
		// 三台阶法或双侧壁法
		case 7:
			pointA = getPointByType("A");
			pointS1_1 = getPointByType("S1-1");
			pointS1_2 = getPointByType("S1-2");
			pointS2_1 = getPointByType("S2-1");
			pointS2_2 = getPointByType("S2-2");
			pointS3_1 = getPointByType("S3-1");
			pointS3_2 = getPointByType("S3-2");
			A = pointA.getCoordinate().replace(",", "#");
			S1_1 = pointS1_1.getCoordinate().replace(",", "#");
			S1_2 = pointS1_2.getCoordinate().replace(",", "#");
			S2_1 = pointS2_1.getCoordinate().replace(",", "#");
			S2_2 = pointS2_2.getCoordinate().replace(",", "#");
			S3_1 = pointS3_1.getCoordinate().replace(",", "#");
			S3_2 = pointS3_2.getCoordinate().replace(",", "#");
			coordinate = A + "/" + S1_1 + "#" + S1_2 + "/" + S2_1 + "#" + S2_2 + "/" + S3_1 + "#" + S3_2;
			coordinateList = RSACoder.encnryptDes(coordinate, Constant.testDeskey);
			break;
		default:
			Log.d(LOG_TAG, "未知的开挖方法: 测点数目=" + pointCount);
			break;
		}
		return coordinateList;
	}

	@Override
	public String getValueList() {
		String valueList = "";
		final int pointCount = mMeasurePoints.size();
		TunnelSettlementTotalData pointA, pointS1_1, pointS1_2, pointS2_1, pointS2_2, pointS3_1, pointS3_2;
		String[] cA;
		switch (pointCount) {
		// 全断面法
		case 3:
			pointA = getPointByType("A");
			pointS1_1 = getPointByType("S1-1");
			pointS1_2 = getPointByType("S1-2");
			cA = pointA.getCoordinate().split(",");
			valueList = cA[2] + "/" + AlertUtils.getLineLength(pointS1_1, pointS1_2);
			break;
		// 台阶法
		case 5:
			pointA = getPointByType("A");
			pointS1_1 = getPointByType("S1-1");
			pointS1_2 = getPointByType("S1-2");
			pointS2_1 = getPointByType("S2-1");
			pointS2_2 = getPointByType("S2-2");
			cA = pointA.getCoordinate().split(",");
			valueList = cA[2] + "/" + AlertUtils.getLineLength(pointS1_1, pointS1_2) + "/"
					+ AlertUtils.getLineLength(pointS2_1, pointS2_2);
			break;
		// 三台阶法或双侧壁法
		case 7:
			pointA = getPointByType("A");
			pointS1_1 = getPointByType("S1-1");
			pointS1_2 = getPointByType("S1-2");
			pointS2_1 = getPointByType("S2-1");
			pointS2_2 = getPointByType("S2-2");
			pointS3_1 = getPointByType("S3-1");
			pointS3_2 = getPointByType("S3-2");
			cA = pointA.getCoordinate().split(",");
			valueList = cA[2] + "/" + AlertUtils.getLineLength(pointS1_1, pointS1_2) + "/"
					+ AlertUtils.getLineLength(pointS2_1, pointS2_2) + "/"
					+ AlertUtils.getLineLength(pointS3_1, pointS3_2);
			break;
		default:
			Log.d(LOG_TAG, "未知的开挖方法: 测点数目=" + pointCount);
			break;
		}
		return valueList;
	}

	@Override
	public void markAsUploaded() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao();
				for (TunnelSettlementTotalData point : mMeasurePoints) {
					point.setInfo("2");
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
}
