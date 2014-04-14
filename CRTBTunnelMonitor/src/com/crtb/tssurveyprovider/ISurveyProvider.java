package com.crtb.tssurveyprovider;

public abstract interface ISurveyProvider {
	public abstract int BeginConnection(TSConnectType paramTSConnectType,
			String[] paramArrayOfString);

	public abstract int TestConnection();

	public abstract int GetCoord(double paramDouble1, double paramDouble2,
			Coordinate3D paramCoordinate3D) throws InterruptedException;

	public abstract int EndConnection();
}