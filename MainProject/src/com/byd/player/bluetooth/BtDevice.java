package com.byd.player.bluetooth;

public class BtDevice{

	/**
	 * 蓝牙设备实体类
	 */
	private int serialNumber;
	private String name;
	private String address;	
	private boolean isPaired = false;
	private boolean isAutoConnect = false;
	private boolean isConnected = false;
	
	public BtDevice(int serialNumber, String name) {
		this.serialNumber = serialNumber;
		this.name = name;
	}
	public BtDevice(int serialNumber, String address, String name) {
		this.serialNumber = serialNumber;
		this.address = address;
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSerialNumber() {
		return serialNumber;
	}
	public String getSerialNumberAsString(){
		String sn = "" + serialNumber;
		return sn;
	}
	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public boolean isPaired() {
		return isPaired;
	}
	public void setPaired(boolean isPaired) {
		this.isPaired = isPaired;
	}
	public boolean isAutoConnect() {
		return isAutoConnect;
	}
	public void setAutoConnect(boolean isAutoConnect) {
		this.isAutoConnect = isAutoConnect;
	}
	public boolean isConnected() {
		return isConnected;
	}
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
}
