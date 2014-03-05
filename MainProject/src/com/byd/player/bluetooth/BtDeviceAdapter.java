package com.byd.player.bluetooth;

import java.util.List;

import com.byd.player.bluetooth.BTBaseActivity;
import com.byd.player.R;
import com.byd.player.bluetooth.BtDevice;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
/**
 * 蓝牙连接列表的适配器
 */
public class BtDeviceAdapter extends BaseContentAdapter<BtDevice> {

	public BtDeviceAdapter(BTBaseActivity activity,
			List<? extends BtDevice> list) {
		super(activity, list);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		BtDevice btDevice = getItem(position);
	    if (convertView == null){
			convertView = activity.getLayoutInflater().inflate(R.layout.item_bt_device, null);
	    }
	    
		TextView name = (TextView) convertView.findViewById(R.id.tv_bt_device_name);
		TextView isConnect = (TextView) convertView.findViewById(R.id.tv_bt_device_isconnected);
		CheckBox cbConnect = (CheckBox) convertView.findViewById(R.id.cb_bt_connect);
		cbConnect.setTag(btDevice);
		cbConnect.setOnCheckedChangeListener(null);
		cbConnect.setChecked(btDevice.isAutoConnect());
		cbConnect.setOnCheckedChangeListener(this);
		name.setText(btDevice.getName());
		if (btDevice.isConnected()){
			isConnect.setText("已连接");
		} else if (btDevice.isPaired()){
			isConnect.setText("已配对");
		} else{
			isConnect.setText("未连接");
		}
		return convertView;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
		super.onCheckedChanged(cb, isChecked);
		BtDevice btDevice = (BtDevice) cb.getTag();
		btDevice.setAutoConnect(isChecked);
	}

	
}
