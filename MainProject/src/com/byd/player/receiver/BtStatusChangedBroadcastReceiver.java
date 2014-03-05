package com.byd.player.receiver;

import java.util.ArrayList;

import com.byd.player.bluetooth.BtStatus;
import com.byd.player.bluetooth.BtActionManager.BtCmdEnum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BtStatusChangedBroadcastReceiver extends BroadcastReceiver{

	private static ArrayList<onBtStatusListener> statusListeners = new ArrayList<onBtStatusListener>();
	private static ArrayList<onBtActionListener> actionListeners = new ArrayList<onBtActionListener>();

	public static void registerStatusListener(onBtStatusListener statusListener){
		statusListeners.add(statusListener);
	}
	
    public static void unRegisterStatusListener(onBtStatusListener statusListener){
    	statusListeners.remove(statusListener);
	}
    
    public static void registerActionListener(onBtActionListener actionListener){
    	actionListeners.add(actionListener);
	}
	
    public static void unRegisterActionListener(onBtActionListener actionListener){
    	actionListeners.remove(actionListener);
	}
    
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent.hasExtra("status")){
			int status = intent.getIntExtra("status", BtStatus.BT_STATUS_INVALID);
			/*
			 * BtCmdEnum status_Enum = (BtCmdEnum)intent.getSerializableExtra("status");
			int status = status_Enum.ordinal();
			*/
			for (int i = 0; i < statusListeners.size(); i++){
				statusListeners.get(i).onStatusChanged(status);
			}
			
			switch (status){
			case BtStatus.BT_STATUS_RING:
				/* no use for bt music
				Intent in = new Intent();
				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				in.setClass(context, IncomingCallActivity.class);
				context.startActivity(in);
				*/
				break;
			default:
				break;
			}
		} else if (intent.hasExtra("type")) {
			BtCmdEnum type = (BtCmdEnum) intent.getSerializableExtra("type");
			boolean success = intent.getBooleanExtra("success", false);
			byte[] ret = intent.getByteArrayExtra("ret");
			for (int i = 0; i < actionListeners.size(); i++){
				actionListeners.get(i).onBtActionDone(success, type, ret);
			}
		}
	}
	
	public interface onBtStatusListener {
		public void onStatusChanged(int status);
	}
	
	public interface onBtActionListener {
		public void onBtActionDone(boolean success, BtCmdEnum type, byte[] ret);
	}
}
