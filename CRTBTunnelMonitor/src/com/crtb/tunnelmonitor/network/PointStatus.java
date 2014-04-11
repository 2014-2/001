package com.crtb.tunnelmonitor.network;

public enum PointStatus {
	VALID(1), SEALED(2);
	private int value = 0;

	private PointStatus(int value) {
		this.value = value;
	}

	public static PointStatus valueOf(int value) {
		switch (value) {
		case 1:
			return VALID;
		case 2:
			return SEALED;
		default:
			return null;
		}
	}
	
	public int value() {
		return this.value;
	}
}
