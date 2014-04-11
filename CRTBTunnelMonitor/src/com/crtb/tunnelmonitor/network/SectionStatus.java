package com.crtb.tunnelmonitor.network;

public enum SectionStatus {
	VALID(1), SEALED(2);
	private int value = 0;

	private SectionStatus(int value) {
		this.value = value;
	}

	public static SectionStatus valueOf(int value) {
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
};
