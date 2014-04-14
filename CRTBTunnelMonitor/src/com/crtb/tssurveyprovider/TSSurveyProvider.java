package com.crtb.tssurveyprovider;

public class TSSurveyProvider implements ISurveyProvider {
	private static ISurveyProvider gTSProvider;

	public static ISurveyProvider getDefaultAdapter() {
		if (gTSProvider == null)
			gTSProvider = new TSSurveyProvider();
		return gTSProvider;
	}

	public int BeginConnection(TSConnectType tsType, String[] params) {
		return 1;
	}

	public int GetCoord(double prismAddConst, double prismHeight,
			Coordinate3D xyh) throws InterruptedException {
		Thread.sleep(1000L);
		xyh.x = 100.0D;
		xyh.y = 100.0D;
		xyh.z = 20.0D;
		return 1;
	}

	public int EndConnection() {
		return 1;
	}

	public int TestConnection() {
		return 1;
	}
}