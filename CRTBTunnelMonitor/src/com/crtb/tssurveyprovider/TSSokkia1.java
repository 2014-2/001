package com.crtb.tssurveyprovider;

import java.io.IOException;

/**
 * debug app using Sokkia SET210k
 * ASCII-Request: Ed
 * ASCII-Response: 
 * @author CMo
 *
 */
class TSSokkia1 implements ITSCoordinate {

	public byte[] DoMeasureCommandString() {
		return "\r\n".getBytes();
	}

	public byte[] GetCoordCommandString() {
		return "Ed\r\n".getBytes();
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
		String surveyInfos = text.trim().replace("\r", "");
		String[] surveyDatas = surveyInfos.split(",");
		Coordinate3D testObject = new Coordinate3D(null);
		if (surveyDatas.length == 7) {
			// surveyData.TargetHeight = Convert.ToDouble(surveyDatas[2]);
			// surveyData.PPM = Convert.ToDouble(surveyDatas[3]);
			if (surveyDatas[4] != "E200") {
				testObject.N = Double.parseDouble(surveyDatas[4]);
				testObject.E = Double.parseDouble(surveyDatas[5]);
				testObject.H = Double.parseDouble(surveyDatas[6]);
			}
		}

		return "%R1P,0,0:0," + testObject.E + "," + testObject.N + "," + testObject.H + ",3,101.12345,101,101,3";
	}

	public byte[] ClearMeasureCommandString() {
		return "\r\n".getBytes();
	}

	public byte[] TestTSConnectString ()
	{
		return "\r\n".getBytes();
	}

	@Override
	public int measure(Coordinate3D testObject) throws IOException, InterruptedException {
		int nret = -1;
		final char[] buffer = new char[1024];
		String sLine = "";
		
		// <1>. 
		// <2>. 执行测量
		nret = TSSurveyProvider.DoMeasure(buffer, GetCoordCommandString());
		if (nret < 0) return nret;
		
		nret = TSSurveyProvider.ReadMeasureString2(buffer);
		if (nret < 0) return nret;
		
		sLine = String.valueOf(buffer).trim();
		String coordRet = ParseRETString(sLine);
		testObject.setCoordinate(coordRet.trim(), true);
		nret = testObject.ErrCode;
		return nret;
	}

	@Override
	public int TestTSConnect() throws IOException, InterruptedException {
		return 1;
	}
}
