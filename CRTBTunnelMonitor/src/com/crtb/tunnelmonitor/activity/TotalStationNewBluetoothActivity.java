package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.common.Constant.TotalStationType;
import com.crtb.tunnelmonitor.dao.impl.v2.RecordSubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TotalStationInfoDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.TotalStationIndex;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;

/**
 * 新建全站仪蓝牙连接
 * 
 */
public class TotalStationNewBluetoothActivity extends Activity implements OnClickListener {
    private TextView ts_new_tv_header;

    private Spinner pps, infos;
    private EditText pp, name, info;
    private ArrayAdapter adapter;
    private List<String> pplist = null;
//    private List<BluetoothDevice> btDevices = null;
    private List<String> infolist = null;
    private List<String> btDeviceNamelist = null;
    private TotalStationIndex editInfo = null;
    private boolean isCreating = true;
    /** 确定按钮 */
    private Button section_btn_queding;
    /** 取消按钮 */
    private Button section_btn_quxiao;

    private AppCRTBApplication CurApp = null;

    private boolean bEdit=false;

    private int selectedBtDevicePosition = -1;

    private HashSet<String> mExistingStationNames = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totalstationnew_bluetooth);
        ts_new_tv_header = (TextView)findViewById(R.id.tv_topbar_title);
        ts_new_tv_header.setText(R.string.new_control_station_title);

        CurApp = ((AppCRTBApplication)getApplicationContext());

        pplist = new ArrayList<String>();
        infolist = new ArrayList<String>();
        btDeviceNamelist = new ArrayList<String>();
//        btDevices = new ArrayList<BluetoothDevice>();
        Bundle bundle=getIntent().getExtras();
        editInfo = (TotalStationIndex)bundle.getSerializable(Constant.Select_TotalStationRowClickItemsName_Data);
        isCreating = (editInfo == null);

        List<TotalStationIndex> stations = TotalStationInfoDao.defaultDao().queryAllTotalStations();
        if (stations != null && stations.size() > 0) {
            for (TotalStationIndex station : stations) {
                mExistingStationNames.add(station.getName());
            }
            if (editInfo != null) {
                mExistingStationNames.remove(editInfo.getName());
            }
        }

        bEdit=bundle.getBoolean("bEdit");
        initUI();

        name.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                String n = s.toString();
                boolean contains = mExistingStationNames.contains(n);
                if (contains) {
                    Toast.makeText(TotalStationNewBluetoothActivity.this, "已有该名称的全站仪，请更改名称！", Toast.LENGTH_LONG).show();
                }
                if (section_btn_queding != null) {
                    section_btn_queding.setEnabled(!contains);
                }
            }
        });

        name.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText _v=(EditText)v;
                if (!hasFocus) {// 失去焦点
                    _v.setHint(_v.getTag().toString());
                } else {
                    String hint=_v.getHint().toString();
                    _v.setTag(hint);
                    _v.setHint("");
                }
            }
        });

        infos.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (infolist == null || infolist.size() == 0) {
                    Toast.makeText(TotalStationNewBluetoothActivity.this, "请先配对蓝牙连接！", Toast.LENGTH_SHORT).show();
                } else {
                    infos.performClick();
                }
                return true;
            }
        });

//        infos.setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                if (infolist == null || infolist.size() == 0) {
//                    Toast.makeText(TotalStationNewBluetoothActivity.this, "请先配对蓝牙连接！", Toast.LENGTH_SHORT).show();
//                } else {
//                    infos.performClick();
//                }
//            }
//        });
        adap(pps, pplist);
        adap(infos, btDeviceNamelist);
        onCli();

        initData();
    }

    private void initUI() {
        for (TotalStationType type : TotalStationType.values()) {
            pplist.add(type.getDesc());
        }

        BluetoothAdapter btAdapt = BluetoothAdapter.getDefaultAdapter();
        if (btAdapt == null) {
            Toast.makeText(this, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
        } else {
            // 获得已配对的远程蓝牙设备的集合
            Set<BluetoothDevice> devices = btAdapt.getBondedDevices();
            // btDevices.addAll(devices);
            if (devices.size() > 0) {
                for (Iterator<BluetoothDevice> it = devices.iterator(); it.hasNext();) {
                    BluetoothDevice device = (BluetoothDevice) it.next();
                    // 打印出远程蓝牙设备的物理地址
                    infolist.add(device.getAddress());
                    btDeviceNamelist.add(device.getName());
                }
            } else {
                Toast.makeText(this, "请先配对蓝牙连接！", Toast.LENGTH_SHORT).show();
            }
        }

        pps = (Spinner) findViewById(R.id.pps);
        infos = (Spinner) findViewById(R.id.infos);
        pp = (EditText) findViewById(R.id.pp);
        name = (EditText) findViewById(R.id.name);
        info = (EditText) findViewById(R.id.info);
        section_btn_queding = (Button) findViewById(R.id.work_btn_queding);
        section_btn_quxiao = (Button) findViewById(R.id.work_btn_quxiao);

        section_btn_queding.setOnClickListener(this);
        section_btn_quxiao.setOnClickListener(this);
    }

    // 点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.work_btn_quxiao:
                Intent IntentCancel = new Intent();
                IntentCancel.putExtra(Constant.Select_TotalStationRowClickItemsName_Name,"");
                setResult(RESULT_CANCELED, IntentCancel);
                this.finish();// 关闭当前界面
                break;
            case R.id.work_btn_queding: // 数据库
                if(pp.getText().toString().trim().length() <= 0)
                {
                    Toast.makeText(this, "请输入完整信息", 3000).show();
                    return;
                }
                if(name.getText().toString().trim().length() <= 0)
                {
                    Toast.makeText(this, "请输入全站仪名称", 3000).show();
                    return;
                }
                ProjectIndex workPlan = ProjectIndexDao.defaultWorkPlanDao().queryEditWorkPlan();
                if (workPlan == null) {
                    Toast.makeText(this, "未找到当前工作面", 3000).show();
                    return;
                }
                TotalStationIndex ts = new TotalStationIndex();
                if (editInfo != null) {
                    ts.setID(editInfo.getID());
                }
                for (TotalStationType type : TotalStationType.values()){
                    if(type.getDesc().equals(pp.getText().toString()))
                    {
                        ts.setTotalstationTypeString(type.name());
                        break;
                    }
                }
                ts.setName(name.getText().toString().trim());
                if (selectedBtDevicePosition != -1) {
                    ts.setInfo(infolist.get(selectedBtDevicePosition).trim());
                }

                if(!CurApp.IsValidTotalStationInfo(ts))
                {
                    Toast.makeText(this, "请输入完整信息", 3000).show();
                    return;
                }
                if (editInfo == null) {
                    TotalStationInfoDao dao = TotalStationInfoDao.defaultDao();
                    if (dao.insert(ts) == RecordSubsidenceTotalDataDao.DB_EXECUTE_SUCCESS) {
                        Toast.makeText(this, "添加成功", 3000).show();
                    } else {
                        Toast.makeText(this, "添加失败", 3000).show();
                    }
                } else {
                    TotalStationInfoDao.defaultDao().update(ts);
                    Toast.makeText(this, "编辑成功", 3000).show();
                }
                Intent IntentOk = new Intent();
                IntentOk.putExtra(Constant.Select_TotalStationRowClickItemsName_Name,"");
                setResult(RESULT_OK, IntentOk);
                this.finish();
                break;
            default:
                break;
        }

    }

    private void initData() {
        if (editInfo != null) {
            ts_new_tv_header.setText(R.string.edit_control_station_title);
            for (TotalStationType type : TotalStationType.values()){
                if(type.name().equals(editInfo.getTotalstationTypeString()))
                {
                    pps.setSelection(pplist.indexOf(type.getDesc()));
                    break;
                }
            }
            name.setText(editInfo.getName());
            info.setText(editInfo.getInfo());
        }
        else {
            ts_new_tv_header.setText(R.string.new_control_station_title);
            if (btDeviceNamelist.size() > 0) infos.setSelection(0);
        }
    }

    private void adap(Spinner spinner, List<String> list) {
        // 将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        // 设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void onCli() {
        // 添加事件Spinner事件监听
        pps.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                pp.setText(pplist.get(position).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        pps.setVisibility(View.VISIBLE);

        // 添加事件Spinner事件监听
        infos.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                selectedBtDevicePosition  = position;
//                info.setText(infolist.get(position).toString());
                String btDeviceName = btDeviceNamelist.get(position);
                info.setText(btDeviceName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        infos.setVisibility(View.VISIBLE);
        
    }
}
