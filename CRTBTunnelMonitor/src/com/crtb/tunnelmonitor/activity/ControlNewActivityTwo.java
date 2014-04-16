package com.crtb.tunnelmonitor.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.ControlPointsInfoDao;
import com.crtb.tunnelmonitor.entity.ControlPointsInfo;

public class ControlNewActivityTwo extends Activity implements OnClickListener {

    private TextView cp_new_tv_header;

    private EditText mName, mPointX, mPointY, mPointZ, mNote;
    private ControlPointsInfo info = null;
    /** 确定按钮 */
    private Button mOk;
    /** 取消按钮 */
    private Button mCancel;

    private Dialog dlg;

    private boolean bEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlnewtwo);
        cp_new_tv_header = (TextView)findViewById(R.id.tv_topbar_title);
        cp_new_tv_header.setText(R.string.new_control_point_title);

        Bundle bundle = getIntent().getExtras();
        info = (ControlPointsInfo) bundle
                .getSerializable(Constant.Select_ControlPointsRowClickItemsName_Data);
        bEdit = bundle.getBoolean("bEdit");
        initUI();
        initData();
    }

    private void initUI() {
        mOk = (Button) findViewById(R.id.work_btn_queding);
        mCancel = (Button) findViewById(R.id.work_btn_quxiao);
        mName = (EditText) findViewById(R.id.point_name);
        mName.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void onTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3) {
                temp=arg0;
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (temp.length() > 0) {
                    mOk.setEnabled(true);
                } else {
                    mOk.setEnabled(false);
                }

            }
        });

        mPointX = (EditText) findViewById(R.id.point_x);
        mPointY = (EditText) findViewById(R.id.point_y);
        mPointZ = (EditText) findViewById(R.id.point_z);
        mNote = (EditText) findViewById(R.id.point_info);
        if (bEdit) {
            mName.setText(info.getName());
            mPointX.setText(String.valueOf(info.getX()));
            mPointY.setText(String.valueOf(info.getY()));
            mPointZ.setText(String.valueOf(info.getZ()));
            mNote.setText(info.getInfo());
        }
        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    // 点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.work_btn_quxiao:
                Intent IntentCancel = new Intent();
                IntentCancel.putExtra(
                        Constant.Select_ControlPointsRowClickItemsName_Data, "");
                setResult(RESULT_CANCELED, IntentCancel);
                this.finish();// 关闭当前界面
                break;
            case R.id.work_btn_queding: // 数据库
                if (mName.getText().toString().trim().length() <= 0) {
                    Toast.makeText(this, "请输入名称", 3000).show();
                    return;
                }
                ControlPointsInfo controlPoint = new ControlPointsInfo();
                if (info != null) {
                    controlPoint.setId(info.getId());
                }
                String name = mName.getText().toString().trim();
                controlPoint.setName(name);
                controlPoint.setX(Float.valueOf(mPointX.getText().toString().trim()));
                controlPoint.setY(Float.valueOf(mPointY.getText().toString().trim()));
                controlPoint.setZ(Float.valueOf(mPointZ.getText().toString().trim()));
                controlPoint.setInfo(mNote.getText().toString().trim());
                ControlPointsInfoDao dao = ControlPointsInfoDao.defaultDao();
                List<ControlPointsInfo> controlPoints = dao.queryAllControlPoints();
                if (!bEdit) {
                    if (controlPoints != null) {
                        for (int i = 0; i < controlPoints.size(); i++) {
                            if (controlPoints.get(i).getName().equals(name)) {
                                showDialog("该点名已存在,请重新输入！");
                                return;
                            }
                        }
                    }
                    if (dao.insert(controlPoint)) {
                        Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "添加失败", Toast.LENGTH_LONG).show();
                    }
                } else {
                    dao.update(controlPoint);
                    Toast.makeText(this, "编辑成功", Toast.LENGTH_LONG).show();
                }
                Intent IntentOk = new Intent();
                IntentOk.putExtra(Constant.Select_ControlPointsRowClickItemsName_Data, "");
                setResult(RESULT_OK, IntentOk);
                this.finish();
                break;
            default:
                break;
        }

    }

    private void initData() {
        if (info != null) {
            cp_new_tv_header.setText(R.string.edit_control_point_title);

            //mName.setFocusableInTouchMode(false);
            //mPointX.setFocusableInTouchMode(false);
            //mPointY.setFocusableInTouchMode(false);
            //mPointZ.setFocusableInTouchMode(false);

            //mName.setText(info.getName());
            //mPointX.setText(Double.toString(info.getX()));
            //mPointY.setText(Double.toString(info.getY()));
            //mPointZ.setText(Double.toString(info.getZ()));
            //mNote.setText(info.getInfo());
        } else {
            cp_new_tv_header.setText(R.string.new_control_point_title);
            mPointX.setText("0");
            mPointY.setText("0");
            mPointZ.setText("0");
        }
    }

    private void showDialog(String msgText) {
        AlertDialog.Builder builder = new Builder(this);
        dlg = builder.create();

        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_custom_message);
        TextView message = (TextView)window.findViewById(R.id.message);
        message.setText(msgText);
        Button ok = (Button)window.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dlg != null) {
                    dlg.dismiss();
                }
            }
        });
    }

}
