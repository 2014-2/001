package com.byd.player.bluetooth;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.byd.player.bluetooth.BtDevice;
import com.byd.player.bluetooth.BtActionManager.BtCmd;
import com.byd.player.bluetooth.BtActionManager.BtCmdEnum;

import android.util.Log;

public class BtStatus {
	
	/*
	 * 由蓝牙模块传递到系统的状态
	 */
	public static final int BT_STATUS_INVALID = 0;
	
	public static final int BT_STATUS_DISCONNECT = 1;
	public static final int BT_STATUS_PAIRING = 2;
	public static final int BT_STATUS_LINKING = 3;
	public static final int BT_STATUS_CONNECT = 4;
	
	public static final int BT_STATUS_RING = 5;
	public static final int BT_STATUS_DIALING = 6;
	public static final int BT_STATUS_TALKING = 7;
	public static final int BT_STATUS_CALL_FAILED = 8;
	public static final int BT_STATUS_CALL_RELEASE = 9;
	public static final int BT_STATUS_MP3_PLAY = 10;
	public static final int BT_STATUS_A2DP_CONNECTED = 11;
	public static final int BT_STATUS_A2DP_DISCONNECTED = 12;
	
	public static final int BT_STATUS_HFP_A2DP = 13;
	public static final int BT_STATUS_PB = 14;
	public static final int BT_STATUS_PC = 15;
	public static final int BT_STATUS_PAIRED_SUCCESSFUL = 16;
	public static final int BT_STATUS_SEARCH_DEVICE_NAME = 17;
	public static final int BT_STATUS_SEARCH_FINISHED = 18;
	public static final int BT_STATUS_SEARCH_NO_SEARCH_DEVICE = 19;
	public static final int BT_STATUS_PAIRED_LIST = 20;
	public static final int BT_STATUS_PAIRED_LIST_UPDATE_OK = 21;
	public static final int BT_STATUS_NO_PAIRED_DEVICE = 22;
	//end
	
	public static final int HFP_STATUS_DISCONNECT = 0x30;
	public static final int HFP_STATUS_CONNECTING = 0x31;
	public static final int HFP_STATUS_CONNECTED = 0x32;
	public static final int HFP_STATUS_RING = 0x33;
	public static final int HFP_STATUS_DIALING = 0x34;
	public static final int HFP_STATUS_TALKING = 0x35;

    public static final int A2DP_STATUS_DISCONNECT = 0x30;
    public static final int A2DP_STATUS_CONNECTING = 0x31;
    public static final int A2DP_STATUS_CONNECTED = 0x32;
    public static final int A2DP_STATUS_MP3_PLAY = 0x33;

	/*
	 * 本地维护的蓝牙状态
	 */
	private ArrayList<BtDevice> searchDevice = new ArrayList<BtDevice>();
	private ArrayList<BtDevice> pairedDevice = new ArrayList<BtDevice>();
	private String incomingCallNumber;
	private int hfpStatus;
	private int a2dpStatus;
	private boolean isAutoConnect = false;
	private boolean isAutoAnswer = false;
	private boolean isMICmute = false;
	//end
	BtService btService;
	
	public BtStatus(BtService btService){
		this.btService = btService;
	}
	
	public boolean setStatus(BtCmd cmd){
		switch (cmd.type){
		case BT_CMD_GET_CURRENT_BT_SETTING_FUNCTION_STATUS:
			praseSettingFunctionStatus(cmd.ret, cmd.retLen);
			break;
		default:
			return false;
		}
		return true;
	}
	
	private void praseSettingFunctionStatus(byte[] buffer, int size){
		if (size > 0){
			byte ret = buffer[0];
			isAutoConnect = (1 == (ret & 0x80));
			isAutoAnswer = (1 == (ret & 0x40));
			isMICmute = (1 == (ret & 0x20));
		}
	}

	public int setStatus(byte[] buffer, int size){
		int status = BT_STATUS_INVALID;
		switch (buffer[0]) {
		case 'X':
			status = BtStatus.BT_STATUS_DISCONNECT;
			break;
		case 'P':
			break;
		case 'S':
			if (size == 1){
				status = BtStatus.BT_STATUS_LINKING;
			} else if (buffer[1] == 'A'){
				
			} else if (buffer[1] == 'B'){
				
			}
			break;
        case 'O':
        	status = BtStatus.BT_STATUS_CONNECT;
        	break;
        case 'R':
        	status = BtStatus.BT_STATUS_RING;
        	break;
        case 'D':
        	status = BtStatus.BT_STATUS_DIALING;
        	break;
        case 'I':
        	status = BtStatus.BT_STATUS_TALKING;
        	break;
        case 'L':
        	status = BtStatus.BT_STATUS_CALL_FAILED;
        	break;
        case 'E':
        	status = BtStatus.BT_STATUS_CALL_RELEASE;
        	break;
        case 'A':
        	status = BtStatus.BT_STATUS_MP3_PLAY;
        	break;
        case 'H':
        	status = BtStatus.BT_STATUS_A2DP_CONNECTED;
        	break;
        case 'V':
        	status = BtStatus.BT_STATUS_A2DP_DISCONNECTED;
        	break;
        case 'C':
        	status = BtStatus.BT_STATUS_HFP_A2DP;
        	praseHFPA2DPstatus(buffer, size);
        	break;
        case 'W':
        	if (buffer[1] == 0x01){
        		status = BtStatus.BT_STATUS_SEARCH_NO_SEARCH_DEVICE;
            	Log.d("btStatus", "没有搜索到设备");
        	} else if(buffer[1] == 'C'){
            	Log.d("btStatus", "取消配对");
            	btService.doAction(BtCmdEnum.BT_CMD_READ_PAIRED_DEVICE_LIST_INFO);
        	} else {
        		status = BtStatus.BT_STATUS_SEARCH_DEVICE_NAME;
            	praseDeviceInfo(buffer, size);
        	}
        	break;
        case (byte) 0xb8:
        	if (buffer[1] == 0x00){
        		status = BtStatus.BT_STATUS_SEARCH_FINISHED;
            	Log.d("btStatus", "搜索完毕");
        	}
        	break;
        case '#':
        	if (buffer[1] == 0x01 && buffer[2] == 0x89){
        		status = BtStatus.BT_STATUS_NO_PAIRED_DEVICE;
        	} else {
        		status = BtStatus.BT_STATUS_PAIRED_LIST;
            	prasePairingList(buffer, size);
        	}
            break;
        case (byte) 0x83:
        	status = BtStatus.BT_STATUS_PAIRED_SUCCESSFUL;
		    Log.d("btStatus", "Paired Successful return");
        	break;
        case (byte) 0xb9:
        	if (buffer[1] == 0x00){
        		status = BtStatus.BT_STATUS_PAIRED_LIST_UPDATE_OK;
        		Log.d("btStatus", "Reading paired list update ok");
        	}
        	break;   
        case '(':
        	praseIncomingCallNumber(buffer, size);
            break;
        case '$':
        	status = BtStatus.BT_STATUS_PAIRED_SUCCESSFUL;
        	prasePairedSuccess(buffer, size);
        	break;
        case 'F':
        	Log.d("btStatus", "不能执行主机命令");
        	break;
		default:
			break;
		}
		return status;
	}
	
	public ArrayList<BtDevice> getSearchDevice(){
		return searchDevice;
	}
	
	public ArrayList<BtDevice> getPairedDevice(){
		return pairedDevice;
	}
	
	private void praseHFPA2DPstatus(byte[] buffer, int size) {
		if (hfpStatus != buffer[1]){
			if (hfpStatus == HFP_STATUS_CONNECTED || buffer[1] == HFP_STATUS_CONNECTED){
				//TODO get current connected device info
			}
			hfpStatus = buffer[1];
			Log.d("btStatus", "HFP changed:" + new String(buffer, 1, 1));
			//TODO notice status changed
		} 
		if (a2dpStatus != buffer[2]){
			a2dpStatus = buffer[2];
			Log.d("btStatus", "a2dp changed:" + new String(buffer, 2, 1));
			//TODO notice status changed 
		}
	}
	
	private void prasePairingList(byte[] buffer, int size){
		if (buffer[1] <= 4){
			return;
		}
		int SerialNumber = buffer[3];
		String BtAddress = new String(buffer, 4, 6);
		String DeviceName = null;
		try {
			DeviceName = new String(buffer, 11, buffer[10], "UnicodeBigUnmarked");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			DeviceName = "未知设备";
		}

	    Log.d("prasePairingList", SerialNumber + "," + BtAddress + "," + DeviceName);
	    BtDevice device = new BtDevice(SerialNumber, BtAddress, DeviceName);
	    device.setPaired(true);
	    addPairedDevice(device);
	}	
	
	private void praseDeviceInfo(byte[] buffer, int size){
		
		int SerialNumber = buffer[2];
		String DeviceName = null;
		try {
			DeviceName = new String(buffer, 4, buffer[3], "UnicodeBigUnmarked");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			DeviceName = "未知设备";
		}

		if (buffer[1] == '0'){
			Log.d("praseDeviceInfo", "clear device info");
			searchDevice.clear();
		} else {
			Log.d("praseDeviceInfo", SerialNumber + "," + DeviceName);
			BtDevice device = new BtDevice(SerialNumber, DeviceName);
			addSearchedDevice(device);
		}
	}	
		
	private void praseIncomingCallNumber(byte[] buffer, int size){
		incomingCallNumber = new String(buffer, 1, size - 1);
		int index = incomingCallNumber.indexOf(')');
		if (index > 0 && index < incomingCallNumber.length()){
			incomingCallNumber = incomingCallNumber.substring(0, index);
		} else {
			incomingCallNumber = "未知来电";
		}
		Log.d("praseIncomingCallNumber", incomingCallNumber);
	}
	
	private void prasePairedSuccess(byte[] buffer, int size){
		int SerialNumber = buffer[1] - '0';
		String DeviceName = "未知设备";
		try {
			DeviceName = new String(buffer, 2, size - 3, "UnicodeBigUnmarked");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("prasePairedSuccess", SerialNumber + "," + DeviceName);
	    BtDevice device = new BtDevice(SerialNumber, DeviceName);
	    device.setPaired(true);
	    addPairedDevice(device);
	}
	
	private void addPairedDevice(BtDevice device){
		for (int i = 0; i < pairedDevice.size(); i++){
			if (device.getSerialNumber() == pairedDevice.get(i).getSerialNumber()){
				return;
			}
		}
	    pairedDevice.add(device);
	}
	
	private void addSearchedDevice(BtDevice device){
		for (int i = 0; i < searchDevice.size(); i++){
			if (device.getSerialNumber() == searchDevice.get(i).getSerialNumber()){
				return;
			}
		}
		searchDevice.add(device);
	}
		
	public String getIncomingCallNumber() {
		return incomingCallNumber;
	}
	
	public int getHfpStatus() {
		return hfpStatus;
	}

	public int getA2dpStatus() {
		return a2dpStatus;
	}

	public boolean isAutoConnect() {
		return isAutoConnect;
	}

	public boolean isAutoAnswer() {
		return isAutoAnswer;
	}

	public boolean isMICmute() {
		return isMICmute;
	}

}
