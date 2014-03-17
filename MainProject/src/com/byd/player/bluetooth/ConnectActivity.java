package com.byd.player.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.byd.player.R;
import com.byd.player.receiver.BtStatusChangedBroadcastReceiver.onBtStatusListener;
import com.byd.player.bluetooth.BaseContentAdapter.OnMyAdapterCheckedChange;
import com.byd.player.bluetooth.BtDeviceAdapter;
import com.byd.player.bluetooth.BTBaseActivity;
import com.byd.player.bluetooth.BtDevice;
import com.byd.player.bluetooth.BtStatus;
import com.byd.player.bluetooth.BtActionManager.BtCmdEnum;
/**
 * 蓝牙连接功能
 */
public class ConnectActivity extends BTBaseActivity implements OnMyAdapterCheckedChange, onBtStatusListener, OnItemClickListener {
	private static final String BTCONNECT = "ConnectActivity";
	ListView lv_bt_device;
	//private PopupWindow mPopupWindow;
	private BtDeviceAdapter adapter;
	
	/*
	static public BtService btService;
	
	@Override
	protected void onResume(){
		super.onDestroy();
		super.initBtService();
		initData();
		
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		super.unbindService();
	}
	*/
	
	@Override
	protected void initView() {
		super.initView();
		lv_bt_device = (ListView) findViewById(R.id.lv_bt_connect);
		tvTitle.setText("蓝牙连接");
		btnRight.setImageResource(R.drawable.button_play);
		btnRight.setVisibility(View.VISIBLE);
		btnRight.setOnClickListener(this);	
		lv_bt_device.setOnItemClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_right:
			BtStatus status_bt = btService.getBtStatus();
			if(status_bt.getHfpStatus() == BtStatus.HFP_STATUS_CONNECTED || 
			   status_bt.getA2dpStatus() == BtStatus.A2DP_STATUS_CONNECTED)
			{
				Intent intent = new Intent();
				intent.setClass(ConnectActivity.this, BTPlayerActivity.class);
	            startActivity(intent);
			}
			else {
				Toast.makeText(this, "Please connect BT device!", Toast.LENGTH_SHORT).show();
			}
			//searchDevice();	
			break;
		default:
			break;
		}
	}
	
	private void searchDevice() {
		if (!btService.doAction(BtCmdEnum.BT_CMD_SEARCH_BT_MOBILE)){
			//TODO deal with SearchPhone failed
			Log.w(BTCONNECT, "no bt device found!");
		} else {
			//showProcessDialog(null, "正在搜索附近蓝牙设备。。。", 0);
		}
	}
	
	@Override
	protected void onProcessDialogCanceled(int type){
		if (type == 0){
			btService.doAction(BtCmdEnum.BT_CMD_STOP_SEARCH_BT_MOBILE);
		}
	}
	
	@Override
	protected void initData(){
		super.initData();
		List<BtDevice> btDevices = new ArrayList<BtDevice>(btService.getBtStatus().getPairedDevice());
		btDevices.addAll(btService.getBtStatus().getSearchDevice());
		adapter = new BtDeviceAdapter(this, btDevices);
		adapter.setOnMyAdapterCheckedChange(this);
		lv_bt_device.setAdapter(adapter);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_bt_connect);
		listenBtStatus = true;
	}
	
	@Override
	public void onCheckChanged(CompoundButton cb, boolean isChecked) {
//		if(isChecked){
//			//弹出密码输入框
//			initPopupWindow(cb);
//			mPopupWindow.showAtLocation(cb, Gravity.CENTER, 0, 0);
//		}	
	}
	/*
	private void initPopupWindow(final CompoundButton cb) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View popupWindow = layoutInflater.inflate(R.layout.popup_bt_connect, null);
		mPopupWindow = new PopupWindow(popupWindow, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));
		
		final EditText pairCode = (EditText) popupWindow.findViewById(R.id.et_popup_input);
		View commit = popupWindow.findViewById(R.id.btn_popup_commit);
		View close = popupWindow.findViewById(R.id.btn_popup_lable_close);
		close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPopupWindow.dismiss();
			}
		});
		commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO check pairCode validation
				BtDevice device = (BtDevice) cb.getTag();
				String pairCodeStr = pairCode.getText().toString();
				//btService.getController().Stop_Search_phones();
				//btService.getController().Modify_Pairing_code(pairCodeStr);
				//btService.getController().Connect_Searched_phone(device.getSerialNumber());
				mPopupWindow.dismiss();
			}
		});
	}
	*/
	@Override
	public void onStatusChanged(int status) {
		super.onStatusChanged(status);
		switch (status){
		case BtStatus.BT_STATUS_SEARCH_DEVICE_NAME:
		case BtStatus.BT_STATUS_PAIRED_SUCCESSFUL:
		case BtStatus.BT_STATUS_PAIRED_LIST_UPDATE_OK:
			initData();
			if (!btService.doAction(BtCmdEnum.BT_CMD_LINK_LAST_BT)){
	    		Log.d("BaseActivity", "connect last BT FAILED!");
	    		//start bt music play activity.
			} else {
				Log.i("BaseActivity", "connect the last BT SUCCESSFULLY!");
			}
			break;
		case BtStatus.BT_STATUS_SEARCH_FINISHED:
			//Toast.makeText(this, "搜索完成", Toast.LENGTH_SHORT).show();
			finishProcessDialog();
		    break;
		case BtStatus.BT_STATUS_SEARCH_NO_SEARCH_DEVICE:
			//Toast.makeText(this, "没有搜索到设备", Toast.LENGTH_SHORT).show();
			finishProcessDialog();
			break;
		default:
			break;
		}
	}
	/*
	private void doPair(BtDevice btDevice){
		btService.doAction(BtCmdEnum.BT_CMD_PAIR);
	}
	*/
    private void doConnect(BtDevice btDevice){
    	if (btDevice.isPaired()){
        	btService.doAction(BtCmdEnum.BT_CMD_CONNECT_PAIRED_MOBILE_SERIAL_NUMBER, btDevice.getSerialNumberAsString());
    	} else if (!btDevice.isConnected()) {
        	btService.doAction(BtCmdEnum.BT_CMD_CONNECT_SEARCHED_MOBILE_SERIAL_NUMBER, btDevice.getSerialNumberAsString());
    	} else {
    	    //has connected	
    	}
    	
    	//TODO
    	//Start BT music play activity.
	}
    
    private void doDisconnect(BtDevice btDevice){
    	btService.doAction(BtCmdEnum.BT_CMD_DISCONNECT);
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		BtDevice btDevice = adapter.getItem(position);
		if (btDevice != null){
			if (btDevice.isConnected()){
				doDisconnect(btDevice);
			} else if (btDevice.isPaired()){
				doConnect(btDevice);
			} else {
				doConnect(btDevice);
			}
		}
	}
}
