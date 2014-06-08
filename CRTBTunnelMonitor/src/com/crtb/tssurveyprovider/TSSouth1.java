package com.crtb.tssurveyprovider;

import java.io.IOException;

import android.text.TextUtils;

/**
 * debug app using South NTS-352RL
 * ASCII-Request: Z64 088 ETX CRLF
 * ASCII-Response: U+1234566148-00023344-00029912m+2515656d101
 * @author CMo
 *
 */
class TSSouth1 implements ITSCoordinate {
	private final String ETX = "\003";
	private final String CRLF = "\r\n";
	private final String ACK = "\006";

	// 设置为精测坐标模式
	public byte[] DoMeasureCommandString() {
		return ("Z64088" + ETX + CRLF).getBytes();
	}

	// 测量得到值
	public byte[] GetCoordCommandString() {
		return ("C067" + ETX + CRLF).getBytes();
	}

	// 确认指令
	public byte[] GetACKCommandString() {
		return (ACK + "006" + ETX + CRLF).getBytes();
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
		int pricis = 1000;
		if(text.substring(0, 1).equals("/")) pricis = 10000;
		
		String coordX = "", coordY = "", coordZ = "";
		String[] surveyInfo = text.split(" ");
		String coordString = surveyInfo[0].substring(1, surveyInfo[0].length() - 1);
		char dataValueSign = coordString.charAt(0);
		String datavalue = "";
		char ch;
		final char chPlus = '+';
		for (int i = 1; i < coordString.length(); i++)
		{
			ch = coordString.charAt(i);
			if (ch < '0' || ch > '9')
			{
				if (TextUtils.isEmpty(coordX))
				{
					if (dataValueSign == chPlus) coordX = datavalue;
					else coordX = dataValueSign + datavalue;
				}
				else if (TextUtils.isEmpty(coordY))
				{
					if (dataValueSign == chPlus) coordY = datavalue;
					else coordY = dataValueSign + datavalue;
				}
				else if (TextUtils.isEmpty(coordZ))
				{
					if (dataValueSign == chPlus) coordZ = datavalue;
					else coordZ = dataValueSign + datavalue;

					dataValueSign = ch;
					break;
				}
				dataValueSign = ch;
				datavalue = "";
			}
			else
				datavalue += ch;
		}
		
		Coordinate3D testObject = new Coordinate3D(null);
		testObject.N = Double.parseDouble(coordX) / pricis;
		testObject.E = Double.parseDouble(coordY) / pricis;
		testObject.H = Double.parseDouble(coordZ) / pricis;
		
		if (dataValueSign == 'i')
		{
			testObject.N = ((int)testObject.N + (testObject.N - (int)testObject.N) / 1.16) * 0.3048;
			testObject.E = ((int)testObject.E + (testObject.E - (int)testObject.E) / 1.16) * 0.3048;
			testObject.H = ((int)testObject.H + (testObject.H - (int)testObject.H) / 1.16) * 0.3048;
		}
		else if (dataValueSign == 'f')
		{
			testObject.N *= 0.3048;
			testObject.E *= 0.3048;
			testObject.H *= 0.3048;
		}

		// "%R1P,0,0:0,101.12345,101,101,3,101.12345,101,101,3";
		return "%R1P,0,0:0," + testObject.E +"," +testObject.N+","+testObject.H+",3,101.12345,101,101,3";
	}

	public byte[] ClearMeasureCommandString() {
		return null;
	}

	public byte[] TestTSConnectString ()
	{
		return null;
	}

	@Override
	public int measure(Coordinate3D testObject) throws IOException, InterruptedException {
		int nret = -1;
		final char[] buffer = new char[1024];
		String sLine = "";
		
		// <1>. 改到精测坐标模式, 0.05s内仪器返回ACK
		nret = TSSurveyProvider.DoMeasure(buffer, DoMeasureCommandString());
		if (nret < 0) return nret;
		
		int i = 0, retryCount = 15;
		do {
			nret = TSSurveyProvider.ReadMeasureString(buffer);
			if (nret < 0 || i > retryCount) return nret;
			sLine = String.valueOf(buffer).trim();
			i++;
		}
		while (!sLine.contains("006"));
		
		// <2>. 执行测量
		nret = TSSurveyProvider.DoMeasure(buffer, GetCoordCommandString());
		if (nret < 0) return nret;
	  
		// <3>. 发送ACK, 0.3s内发送ACK
		//nret = TSSurveyProvider.DoMeasure(buffer, GetACKCommandString());

		i = 0;
		do {
			nret = TSSurveyProvider.ReadMeasureString(buffer);
			if (nret < 0 || i > retryCount) return nret;
			sLine = String.valueOf(buffer).trim();
			i++;
		}
		while (sLine.contains("006"));
	  
		// <3>. 多发一次ACK也没影响的
		TSSurveyProvider.DoMeasure(buffer, GetACKCommandString());
		
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
