package com.crtb.tssurveyprovider;

import java.io.IOException;

import android.text.TextUtils;

import com.crtb.log.CRTBLog;

class TSLeicaGSI8 extends TSLeicaGSIBase implements ITSCoordinate {
	private final String TAG = "GSI8";

	public TSLeicaGSI8()
	{
		mPat = "(\\d{2}....)(\\+|\\-)(\\S{8})\\s";
	}

	public byte[] DoMeasureCommandString() {
		return "GET/M/WI81/WI82/WI83\r\n".getBytes();
	}

	public byte[] GetCoordCommandString() {
		return null;
	}

	@Override
	public byte[] GetCoordRETString() {
		return "81..00+17562758 82..00-39529788 83..06+05037248 ".getBytes();
	}

	public String ParseRETString(String text) {
		Coordinate3D testObject = new Coordinate3D(null);
		gsiLine (text,testObject);
		// "%R1P,0,0:0,101.12345,101,101,3,101.12345,101,101,3";
		return "%R1P,0,0:0," + testObject.E +"," +testObject.N+","+testObject.H+",3,101.12345,101,101,3";
	}

	public byte[] ClearMeasureCommandString() {
		return "c\r\n".getBytes();
	}

	public byte[] TestTSConnectString ()
	{
		return ClearMeasureCommandString ();
	}

	@Override
	public int measure(Coordinate3D testObject) throws IOException, InterruptedException {
		int nret = -1;
		final char[] buffer = new char[1024];
		
		nret = TSSurveyProvider.DoMeasure(buffer, ClearMeasureCommandString());
		if (nret < 0) return nret;
		nret = TSSurveyProvider.ReadMeasureString2(buffer);
		if (nret < 0) return nret;

		nret = TSSurveyProvider.DoMeasure(buffer, DoMeasureCommandString());
		if (nret < 0) return nret;
		nret = TSSurveyProvider.ReadMeasureString2(buffer);
		if (nret < 0) return nret;

		String coordRet = ParseRETString(String.valueOf(buffer));
		testObject.setCoordinate(coordRet.trim(), true);
		nret = testObject.ErrCode;
		return 0;
	}

	@Override
	public int TestTSConnect() throws IOException, InterruptedException {
		int nret = -1;
		final char[] buffer = new char[1024];
		
		nret = TSSurveyProvider.DoMeasure(buffer, TestTSConnectString());
		if (nret < 0) return nret;
		
		nret = TSSurveyProvider.ReadMeasureString2(buffer);
		if (nret < 0) return nret;

		String sLine = String.valueOf(buffer).trim();
		if (TextUtils.isEmpty(sLine)) return -2;

		CRTBLog.d(TAG, "parse->" + sLine);
		
		if (!sLine.contains("?")) return -3;
		return nret;
	}

}
