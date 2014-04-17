package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
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
import com.crtb.tunnelmonitor.dao.impl.v2.TotalStationInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.entity.TotalStationIndex;
import com.crtb.tunnelmonitor.entity.ProjectIndex;

/**
 * 新建全站仪串口
 * 
 */
public class ControlNewActivity extends Activity implements OnClickListener {
    private TextView ts_new_tv_header;

    private Spinner pps, xyws, btls, cks;
    private EditText pp, name, xyw, btl, ck, sjw, tzw;
    private ArrayAdapter adapter;
    private List<String> pplist = null;
    private List<String> xylist = null;
    private List<String> btllist = null;
    private List<String> cklist = null;
    private TotalStationIndex editInfo = null;
    /** 确定按钮 */
    private Button section_btn_queding;
    /** 取消按钮 */
    private Button section_btn_quxiao;

    private AppCRTBApplication CurApp = null;

    private boolean bEdit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlnew);
        ts_new_tv_header = (TextView)findViewById(R.id.tv_topbar_title);
        ts_new_tv_header.setText(R.string.new_control_station_title);

        CurApp = ((AppCRTBApplication)getApplicationContext());

        pplist = new ArrayList<String>();
        xylist = new ArrayList<String>();
        btllist = new ArrayList<String>();
        cklist = new ArrayList<String>();
        Bundle bundle=getIntent().getExtras();
        editInfo = (TotalStationIndex)bundle.getSerializable(Constant.Select_TotalStationRowClickItemsName_Data);
        bEdit=bundle.getBoolean("bEdit");
        initUI();
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
        adap(pps,pplist);
        adap(xyws,xylist);
        adap(btls,btllist);
        adap(cks,cklist);
        onCli();

        initData();
    }

    private void initUI() {
        for (TotalStationType type : TotalStationType.values()){
            pplist.add(type.getDesc());
        }

        xylist.add("0");
        xylist.add("1");
        xylist.add("2");

        btllist.add("19200");
        btllist.add("9600");
        btllist.add("4800");
        btllist.add("2400");
        btllist.add("1200");

        cklist.add("COM1");
        cklist.add("COM2");
        cklist.add("COM3");

        pps = (Spinner) findViewById(R.id.pps);
        xyws = (Spinner) findViewById(R.id.xyws);
        btls = (Spinner) findViewById(R.id.btls);
        cks = (Spinner) findViewById(R.id.cks);
        pp = (EditText) findViewById(R.id.pp);
        name = (EditText) findViewById(R.id.name);
        xyw = (EditText) findViewById(R.id.xyw);
        btl = (EditText) findViewById(R.id.btl);
        ck = (EditText) findViewById(R.id.ck);
        sjw = (EditText) findViewById(R.id.sjw);
        tzw = (EditText) findViewById(R.id.tzw);
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
                        ts.setTotalstationType(type.name());
                        break;
                    }
                }
                ts.setName(name.getText().toString().trim());
                //                ts.setParity(Integer.valueOf(xyw.getText().toString().trim()));
                ts.setBaudRate(Integer.valueOf(btl.getText().toString().trim()));
                ts.setPort(cklist.indexOf(ck.getText().toString().trim()));
                ts.setDatabits(Integer.valueOf(sjw.getText().toString().trim()));
                ts.setStopbits(Integer.valueOf(tzw.getText().toString().trim()));

                if(!CurApp.IsValidTotalStationInfo(ts))
                {
                    Toast.makeText(this, "请输入完整信息", 3000).show();
                    return;
                }
                if (editInfo == null) {
                    TotalStationInfoDao dao = TotalStationInfoDao.defaultDao();
                    if (dao.insert(ts)) {
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
                if(type.name().equals(editInfo.getTotalstationType()))
                {
                    pps.setSelection(pplist.indexOf(type.getDesc()));
                    break;
                }
            }
            name.setText(editInfo.getName());
            //            xyws.setSelection(xylist.indexOf(Integer.toString(editInfo.getParity())));
            btls.setSelection(btllist.indexOf(Integer.toString(editInfo.getBaudRate())));
            cks.setSelection(editInfo.getPort());
            sjw.setText(Integer.toString(editInfo.getDatabits()));
            tzw.setText(Integer.toString(editInfo.getStopbits()));
        }
        else {
            ts_new_tv_header.setText(R.string.new_control_station_title);
            xyws.setSelection(xylist.indexOf("1"));
            btls.setSelection(btllist.indexOf("19200"));
            cks.setSelection(0);
            sjw.setText("8");
            tzw.setText("1");
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
        xyws.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                xyw.setText(xylist.get(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 添加事件Spinner事件监听
        btls.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                btl.setText(btllist.get(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btls.setVisibility(View.VISIBLE);

        // 添加事件Spinner事件监听
        cks.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                ck.setText(cklist.get(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        cks.setVisibility(View.VISIBLE);
    }
}
