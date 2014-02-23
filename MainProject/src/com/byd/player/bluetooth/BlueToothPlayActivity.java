package com.byd.player.bluetooth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.byd.player.R;

public class BlueToothPlayActivity extends Activity {
	ListView lvBTDevices;
	ArrayAdapter<String> mBTlist;  
	static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	Button btnSearch, btnDis, btnExit;
	ToggleButton tbtnSwitch;
	List<String> lstDevices = new ArrayList<String>();
	BluetoothAdapter btAdapt;
	public static BluetoothSocket btSocket;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_play);
        mBTlist = new ArrayAdapter<String>(this,R.layout.list_btdevice, R.id.list_btdevice);
        
    	ClickEvent mClickEvent = new ClickEvent();
        
		btnSearch = (Button) this.findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(mClickEvent);
		btnDis = (Button) this.findViewById(R.id.btnDis);
		btnDis.setOnClickListener(mClickEvent);

		tbtnSwitch = (ToggleButton) this.findViewById(R.id.tbtnSwitch);
		tbtnSwitch.setOnClickListener(mClickEvent);

		lvBTDevices = (ListView) this.findViewById(R.id.lvDevices);
		mBTlist = new ArrayAdapter<String>(BlueToothPlayActivity.this,
				android.R.layout.simple_list_item_1, lstDevices);
		lvBTDevices.setAdapter(mBTlist);
		lvBTDevices.setOnItemClickListener(new ItemClickEvent());

		btAdapt = BluetoothAdapter.getDefaultAdapter();
        if (btAdapt == null) {
        	Toast.makeText(BlueToothPlayActivity.this, "此设备不支持蓝牙", 1000).show();
        }   

		if (btAdapt.getState() == BluetoothAdapter.STATE_OFF)
			tbtnSwitch.setChecked(false);
		else if (btAdapt.getState() == BluetoothAdapter.STATE_ON)
			tbtnSwitch.setChecked(true);

		// handle the message from bt device.
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);
		intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(searchDevices, intent);
    }
    
	private BroadcastReceiver searchDevices = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Bundle b = intent.getExtras();
			Object[] lstName = b.keySet().toArray();
			
            // display all the messages for debug.  
            for (int i = 0; i < lstName.length; i++) {  
                String keyName = lstName[i].toString();  
                Log.e(keyName, String.valueOf(b.get(keyName)));  
            }  
            BluetoothDevice device = null;  
            // get the macs.
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {  
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {  
                    String str = "未配对|" + device.getName() + "|"   + device.getAddress();  
                    if (lstDevices.indexOf(str) == -1)// avoid adding device duplicatedly.
                        lstDevices.add(str); 
                    mBTlist.notifyDataSetChanged();  
                }  
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){  
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
                switch (device.getBondState()) {  
                case BluetoothDevice.BOND_BONDING:  
                    Log.d("BlueToothTestActivity", "正在配对......");  
                    break;  
                case BluetoothDevice.BOND_BONDED:  
                    Log.d("BlueToothTestActivity", "完成配对");  
                    connect(device);//连接设备  
                    break;  
                case BluetoothDevice.BOND_NONE:  
                    Log.d("BlueToothTestActivity", "取消配对");  
                default:  
                    break;  
                }  
            }
		}
	};

	@Override
	protected void onDestroy() {
	    this.unregisterReceiver(searchDevices);
		super.onDestroy();
	}

	class ItemClickEvent implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(btAdapt.isDiscovering())btAdapt.cancelDiscovery();  
            String str = lstDevices.get(arg2);  
            String[] values = str.split("\\|");  
            String address = values[2];  
            Log.e("address", values[2]);  
            BluetoothDevice btDev = btAdapt.getRemoteDevice(address);  
            try {  
                Boolean returnValue = false;  
                if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {  
                    //利用反射方法调用BluetoothDevice.createBond(BluetoothDevice remoteDevice);  
                    Method createBondMethod = BluetoothDevice.class.getMethod("createBond");  
                    Log.d("BlueToothTestActivity", "开始配对");  
                    returnValue = (Boolean) createBondMethod.invoke(btDev);  
                      
                }else if(btDev.getBondState() == BluetoothDevice.BOND_BONDED){  
                    connect(btDev);  
                }  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
			
			/*
			AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					mAudioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, true);
					mAudioManager.startBluetoothSco();
					mAudioManager.setBluetoothScoOn(true);
		   */
		}
		
	}
	
    private void connect(BluetoothDevice btDev) {  
        UUID uuid = UUID.fromString(SPP_UUID);  
        try {  
            btSocket = btDev.createRfcommSocketToServiceRecord(uuid);  
            Log.d("BlueToothTestActivity", "开始连接...");  
            btSocket.connect();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }  
	
	
	class ClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == btnSearch) //search bt devices
			{
				if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {
					Toast.makeText(BlueToothPlayActivity.this, "请先打开蓝牙", 1000).show();
					return;
				}
				setTitle("本机蓝牙地址：" + btAdapt.getAddress());
				lstDevices.clear();
				btAdapt.startDiscovery();
			} else if (v == tbtnSwitch) { //open/close the device.
				if (tbtnSwitch.isChecked() == false)
					btAdapt.enable();
				else if (tbtnSwitch.isChecked() == true)
					btAdapt.disable();
			} else if (v == btnDis) //device visible
			{
				Intent discoverableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(
						BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
				startActivity(discoverableIntent);
			} 
		}

	}
}