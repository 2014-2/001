package com.crtb.tssurveyprovider;

import java.io.IOException;

import android.text.TextUtils;

import com.crtb.log.CRTBLog;

/**
 * ASCII-Request: %R1Q,2082:WaitTime[long],Mode[long]
 * ASCII-Response: %R1P,0,0:RC,E[double],N[double],H[double],CoordTime[long],
 *                 E-Cont[double],N-Cont[double],H-Cont[double],CoordContTime[long]
 * @author CMo
 *
 */
class TSLeicaGeoCOM implements ITSCoordinate {
	private final String TAG = "LGCOM";
	
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

		// equal 15s = 15000, 100ms * 50 = 5s
		for (int i = 0; i < 50; i++) {
			nret = TSSurveyProvider.DoMeasure(buffer, GetCoordCommandString());
			if (nret < 0) return nret;
			
			nret = TSSurveyProvider.ReadMeasureString2(buffer);
			if (nret < 0) return nret;
			CRTBLog.d(TAG, "i->" + i);
			if (testObject.setCoordinate(String.valueOf(buffer).trim(), true)) break;
		}
		nret = testObject.ErrCode;
		return nret;
	}

	public byte[] DoMeasureCommandString() {
		return "\n%R1Q,2127:1\r\n".getBytes();
	}

	public byte[] GetCoordCommandString() {
		return "\n%R1Q,2082:100,1\r\n".getBytes();
	}

	@Override
	public byte[] GetCoordRETString() {
		//return "%R1P,0,0:0,101.12345,101,101,3,101.12345,101,101,3";
		
		double e = 101.12345f + Math.random();
		double n = 101.0f + Math.random();
		double h = 102.0f + Math.random();
		String enh = String.format("%1$.4f,%2$.4f,%3$.4f",e,n,h);
		return ("%R1P,0,0:0," + enh + ",3," + enh + ",3").getBytes();
	}

	public String ParseRETString(String text) {
		return text;
	}

	public byte[] ClearMeasureCommandString() {
		return "\n%R1Q,2008:3,1\r\n".getBytes();
	}

	public byte[] TestTSConnectString ()
	{
		return "\n%R1Q,0:\r\n".getBytes();
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

		String[] rets = sLine.split(",");
		// compare GRC_OK
		if (!rets[2].endsWith(":0")) {
			String[] retCodes = rets[2].split(":");
			nret = Integer.parseInt(retCodes[1]);
			if (nret == 1) nret = 0;
		}

		return nret;
	}

}
