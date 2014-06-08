package com.crtb.tssurveyprovider;

import android.content.ContentValues;
import android.text.TextUtils;

import com.crtb.log.CRTBLog;
import com.crtb.utils.CUtils;

public class Coordinate3D {
	public double E = 0;
	public double N = 0;
	public double H = 0;
	public boolean mMeas = false; // 是否是测量值

	private final String TAG = "TS03D";
	private final String MTIME = "mtime";
	private final String XYZS = "xyzs";
	public int ErrCode = 0; // 仪器错误代码

	public Coordinate3D(String ret) {
		setCoordinate(ret, false);
	}

	// 解析坐标值
	public boolean setCoordinate(String ret, boolean bMeas) {
		mMeas = bMeas;
		if (TextUtils.isEmpty(ret)) return false;
		
		CRTBLog.d(TAG, "parse->" + ret);
		String[] rets = ret.split(",");
		// compare GRC_OK
		if (rets[2].endsWith(":0") && rets.length > 5) {
			E = Double.parseDouble(rets[3]);
			N = Double.parseDouble(rets[4]);
			H = Double.parseDouble(rets[5]);
			// CoordTime = Long.parseLong(rets[6]);
			ErrCode = 1;
			return true;
		}
		else
		{
			// 仪器返回其他错误代码
			if (rets.length > 3) {
				String[] retCodes = rets[2].split(":");
				ErrCode = Integer.parseInt(retCodes[1]);
			}
			else ErrCode = -4;
		}

		return false;
	}

	public ContentValues toContentValues() {
		if (!mMeas) return null;

		ContentValues values = new ContentValues();
		String xyzs = String.format("%.4f#%.4f#%.4f", E, N, H);
		values.put(XYZS, xyzs);
		values.put(MTIME, CUtils.formatDate(new java.util.Date()));
		return values;
	}
	
}
