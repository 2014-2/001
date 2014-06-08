package com.crtb.tssurveyprovider;

import java.io.IOException;

import android.text.TextUtils;

class TSLeicaGSI16 extends TSLeicaGSIBase implements ITSCoordinate {
	
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
		return nret;
	}

	public TSLeicaGSI16()
	{
		mPat = "(\\d{2}....)(\\+|\\-)(\\S{16})\\s";
	}
	
	public byte[] DoMeasureCommandString() {
		return "GET/M/WI81/WI82/WI83\r\n".getBytes();
	}

	public byte[] GetCoordCommandString() {
		return null;
	}
	
	public byte[] SETGSI16CommandString() {
		return "SET/137/1\r\n".getBytes();
	}

	@Override
	public byte[] GetCoordRETString() {
		return "*81..06-0000000875627575 82..06+0000000495297877 83..06+0000000005037249 ".getBytes();
	}

	public String ParseRETString(String text) {
		if (text.startsWith("@E158") || 
				text.startsWith("@W158") || 
				text.startsWith("@E151")) {
			return "%R1P,0,0:1283,101.12345,101,101,3,101.12345,101,101,3";
		}
		else if (text.startsWith("@E139") ||
				text.startsWith("@E155") ||
				text.startsWith("@E156")) {
			return "%R1P,0,0:1285,101.12345,101,101,3,101.12345,101,101,3";
		}
		else if (text.startsWith("@W127")) {
			return "%R1P,0,0:770,101.12345,101,101,3,101.12345,101,101,3";
		}
		else if (text.startsWith("@W100")) {
			return "%R1P,0,0:786,101.12345,101,101,3,101.12345,101,101,3";
		}
		
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
	public int TestTSConnect() throws IOException, InterruptedException {
		int nret = -1;
		final char[] buffer = new char[1024];
		
		nret = TSSurveyProvider.DoMeasure(buffer, TestTSConnectString());
		if (nret < 0) return nret;
		
		nret = TSSurveyProvider.ReadMeasureString2(buffer);
		if (nret < 0) return nret;

		String sLine = String.valueOf(buffer).trim();
		if (TextUtils.isEmpty(sLine)) return -2;
		if (!sLine.contains("?")) return -3;
		
		// to gsi16
		nret = TSSurveyProvider.DoMeasure(buffer, SETGSI16CommandString());
		if (nret < 0) return nret;
		
		nret = TSSurveyProvider.ReadMeasureString2(buffer);
		if (nret < 0) return nret;

		sLine = String.valueOf(buffer).trim();
		if (TextUtils.isEmpty(sLine)) return -2;
		if (!sLine.contains("?")) return -3;
		
		return nret;
	}

}
