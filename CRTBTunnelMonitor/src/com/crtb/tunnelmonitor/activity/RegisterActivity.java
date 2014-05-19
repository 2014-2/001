package com.crtb.tunnelmonitor.activity;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.AbstractDao;
import com.crtb.tunnelmonitor.dao.impl.v2.CrtbLicenseDao;

import ICT.utils.RSACoder;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity implements OnClickListener {

    private EditText mSerialNumberView, mRegisterCodeView;

    private Button mOk, mCancel;

    private String mDeviceId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        mSerialNumberView = (EditText) findViewById(R.id.serial_number);
        mRegisterCodeView = (EditText) findViewById(R.id.regist_code);
        mOk = (Button) findViewById(R.id.ok);
        mCancel = (Button) findViewById(R.id.cancel);
        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mDeviceId = getDeviceId();
        mSerialNumberView.setText(mDeviceId);
    }

    private String getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ok:
            boolean b = checkAndRegistration();
            showDialog(b, null);
            break;
        case R.id.cancel:
            RegisterActivity.this.finish();
            break;
        }
    }

    private boolean checkAndRegistration() {
        if (mRegisterCodeView != null && mDeviceId != null) {
            String registerCode = mRegisterCodeView.getText().toString();
            if (!TextUtils.isEmpty(registerCode)) {
                registerCode = registerCode.trim();
                String decodedSerial = RSACoder.decnryptDes(registerCode,
                        Constant.testDeskey);
                if (decodedSerial != null
                        && decodedSerial.startsWith(mDeviceId)
                        && decodedSerial.length() == mDeviceId.length() + 10) {
                    int err = CrtbLicenseDao.defaultDao().registLicense(
                            getApplicationContext(), decodedSerial,
                            registerCode);
                    return err == AbstractDao.DB_EXECUTE_SUCCESS;
                }
            }
        }
        return false;
    }

    private void showDialog(final boolean bSuccess, final OnClickListener listener) {
        final Dialog dlg = new Dialog(this, R.style.custom_dlg);
        View view = LayoutInflater.from(this).inflate(
                R.layout.success_dialog_layout, null);
        dlg.setContentView(view);
        TextView text = (TextView) dlg.findViewById(R.id.text);
        if (!bSuccess) {
            text.setText("注册失败！ 请确定输入的信息正确！");
            text.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_warnning, 0, 0, 0);
        }
        Button bt = (Button) dlg.findViewById(R.id.bt);
        bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v);
                }
                if (dlg != null) {
                    dlg.dismiss();
                }
                if (bSuccess) {
                    RegisterActivity.this.finish();
                }
            }
        });
        dlg.show();
        WindowManager.LayoutParams param = dlg.getWindow().getAttributes();
        param.width = getWindowManager().getDefaultDisplay().getWidth() * 3 / 4;
        dlg.getWindow().setAttributes(param);
    }
}
