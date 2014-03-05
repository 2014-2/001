package com.byd.player.services;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import com.byd.player.bluetooth.BtActionManager;
import com.byd.player.bluetooth.BtActionManager.BtCmd;
import com.byd.player.bluetooth.BtActionManager.BtCmdEnum;
import com.byd.player.bluetooth.BtStatus;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android_serialport_api.BT_Controller;
import android_serialport_api.BT_Controller.onReadCmdListener;

public class BtService extends Service implements onReadCmdListener{

	private BT_Controller btController;
	private BtStatus btStatus;
	private BtCmd currentCmd;
	private boolean inited = false;
	private Queue<BtCmd> cmdQueue = new LinkedList<BtCmd>();

	private final LocalBinder mBinder = new LocalBinder();
	public class LocalBinder extends Binder {
		public BtService getService() {
	        return BtService.this;
	    }
	}
	
	@Override
	public void onCreate() {
    	super.onCreate();
    	init();
    }
    
    @Override      
    public void onDestroy() {          
    	super.onDestroy();  
    	btController.BT_stop();
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	public BtStatus getBtStatus() {
		return btStatus;
	}
	
	public void init(){
		btStatus = new BtStatus(this);
		btController = new BT_Controller();
		btController.setOnReadCmdListener(this);
		try {
			btController.BT_start();
			inited = true;
			
			//TODO init bt status
			Log.d("BtService", "send bt commands start!!!");
			doAction(BtCmdEnum.BT_CMD_GET_CURRENT_BT_SETTING_FUNCTION_STATUS);
			doAction(BtCmdEnum.BT_CMD_READ_PAIRED_DEVICE_LIST_INFO);
			Log.d("BtService", "send bt commands start!!!");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean doAction(BtCmdEnum type){
		return doAction(type, null);
	}
	
	public boolean doAction(BtCmdEnum type, String param){
		if (inited){
			BtCmd cmd = BtActionManager.instance().createBtCmd(type, param);
			cmdQueue.offer(cmd);
			return doNextAction();
		} else {
			return false;
		}
	}
	
	public boolean doNextAction(){
		if (currentCmd != null && BtCmd.BTCMD_STATUS_START == currentCmd.status){
			//TODO end currentCmd or wait
			return false;
		} else {
			currentCmd = cmdQueue.poll();
            if (currentCmd != null){
            	currentCmd.status = BtCmd.BTCMD_STATUS_START;
    			boolean ret = btController.writeCmd(currentCmd.cmd);
    			if (ret){
    				if (currentCmd.type == BtCmdEnum.BT_CMD_READ_PAIRED_DEVICE_LIST_INFO){
        				btStatus.getPairedDevice().clear();
        			} else if (currentCmd.type == BtCmdEnum.BT_CMD_SEARCH_BT_MOBILE) {
        				btStatus.getSearchDevice().clear();
        			}
    			}
            }
			return true;
		}
	}

	public void onReadCmd(byte[] buffer, int size) {

		boolean needUpdateStatus = true;
		if (currentCmd != null && currentCmd.status == BtCmd.BTCMD_STATUS_START){
			if (!currentCmd.config.hasRet){
				currentCmd.status = BtCmd.BTCMD_STATUS_SUCCESS;
			} else if (currentCmd.checkRetValue(buffer, size)) {
				btStatus.setStatus(currentCmd);
				
				Intent intent = new Intent();
				intent.setAction("com.byd.player.receiver.action.BT_STATUS_CHANGED");
				intent.putExtra("type", currentCmd.type);
				intent.putExtra("success", true);
				if (currentCmd.ret != null){
					intent.putExtra("ret", currentCmd.ret);
				}
				currentCmd.status = BtCmd.BTCMD_STATUS_SUCCESS;
				sendBroadcast(intent);				
				doNextAction();
				needUpdateStatus = false;
			} else {
				Intent intent = new Intent();
				intent.setAction("com.byd.player.receiver.action.BT_STATUS_CHANGED");
				intent.putExtra("type", currentCmd.type);
				intent.putExtra("success", false);
				//TODO add fail reason;
				currentCmd.status = BtCmd.BTCMD_STATUS_FAIL;
				sendBroadcast(intent);
			}
		}
		
		if (needUpdateStatus){
			int status = btStatus.setStatus(buffer, size);
			//if (BtStatus.BT_STATUS_INVALID != status){
				Intent intent = new Intent();
				intent.setAction("com.byd.player.receiver.action.BT_STATUS_CHANGED");
				intent.putExtra("status", status);
				sendBroadcast(intent);
			//}
		}
	}
	
}
