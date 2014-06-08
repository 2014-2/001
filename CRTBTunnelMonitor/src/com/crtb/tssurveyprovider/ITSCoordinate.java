package com.crtb.tssurveyprovider;

import java.io.IOException;

public interface ITSCoordinate {
	
	byte[] GetCoordRETString ();
	
	String ParseRETString (String text);
	
	int measure(final Coordinate3D testObject) throws IOException, InterruptedException;
	
	int TestTSConnect() throws IOException, InterruptedException;
}
