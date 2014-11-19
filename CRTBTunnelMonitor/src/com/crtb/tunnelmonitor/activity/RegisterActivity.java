package com.crtb.tunnelmonitor.activity;

import com.crtb.tunnelmonitor.AppConfig;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.AbstractDao;
import com.crtb.tunnelmonitor.dao.impl.v2.CrtbLicenseDao;
import com.crtb.tunnelmonitor.entity.CrtbUser;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

import ICT.utils.RSACoder;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;



public class RegisterActivity extends Activity implements OnClickListener {

    private static final String TAG = "RegisterActivity";

    private EditText mSerialNumberView, mRegisterCodeView;

    private Button mOk, mCancel;

    private String mDeviceId = null;

    private Button mBrowseRegistryButton;/*<adong: private variable for BrowseRegistryFile Button>*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        mSerialNumberView = (EditText) findViewById(R.id.serial_number);
        mRegisterCodeView = (EditText) findViewById(R.id.regist_code);
        mOk = (Button) findViewById(R.id.ok);
        mCancel = (Button) findViewById(R.id.cancel);

        mDeviceId = getDeviceId();
        mSerialNumberView.setText(mDeviceId);
        mBrowseRegistryButton=( Button) findViewById(R.id.btn_browseRegisterFile);/*<adong: defualt value forBrowseRegistryFile Button>*/


        boolean registered = false;
        if (!TextUtils.isEmpty(mDeviceId)) {
            CrtbUser user = CrtbLicenseDao.defaultDao().queryCrtbUserByUsername(mDeviceId);
            if (user != null) {
                String license = user.getLicense();
                if (!TextUtils.isEmpty(license)) {
                    mRegisterCodeView.setText(license);
                    registered = true;
                }
            }
        }

        if (registered) {
            mSerialNumberView.setEnabled(false);
            mRegisterCodeView.setEnabled(false);
            mOk.setEnabled(false);
            mCancel.setEnabled(false);
            mBrowseRegistryButton.setEnabled(false);/*<adong: disable BrowseRegistryFile Button when already registred>*/
        } else {
            mSerialNumberView.setEnabled(true);
            mRegisterCodeView.setEnabled(true);
            mOk.setEnabled(true);
            mCancel.setEnabled(true);
            mOk.setOnClickListener(this);
            mCancel.setOnClickListener(this);
            mBrowseRegistryButton.setEnabled(true);/*<adong: enable BrowseRegistryFile Button>*/
            mBrowseRegistryButton.setOnClickListener(this);/*<adong: enable BrowseRegistryFile Button>*/
        }
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
            case R.id.btn_browseRegisterFile:
                browseRegistryFileList_Click();
                break;
        }
    }

    //
    //author:adong
    //date:2014-11-4
    //modify:2014-11-6
    //note:browse registery file
    //
    public void browseRegistryFileList_Click() {
        String registryCode = "";
        String hint_RootDirectoryNotFound = "未找到注册文件夹crtb_db!";
        String hint_RegistryNotFound = "在SD卡的crtb_db下未找到注册文件!";
        String hint_RegistryFormatError = "注册文件格式不正确！";
        String hint = "";

        final File file = Environment.getExternalStorageDirectory();
        String path = file.getAbsolutePath();
        File registryFilesRootPath = new File(path + AppConfig.DB_ROOT);
        String mSerialNumber=mSerialNumberView.getText().toString();
        String testFilePath = path + AppConfig.DB_ROOT + mSerialNumber+".CRTBReg";


        File testFile = new File(testFilePath);
        if (!file.isDirectory()) {
            hint = hint_RootDirectoryNotFound;
        } else {
            try {
                InputStream fis = new FileInputStream(testFile);
                int length = fis.available();
                byte[] buffer = new byte[length];

                fis.read(buffer);
                String res = org.apache.http.util.EncodingUtils.getString(buffer, "UTF-8");
                fis.close();

                String[] resList = res.split("\n");
                String expectedStartString = "RegisterCode:";
                int startPos = expectedStartString.length();
                hint = hint_RegistryFormatError;

                if ((resList.length == 3) && resList[1].length() > startPos) {
                    registryCode = resList[1].substring(startPos);

                    if (!registryCode.isEmpty()) {
                        mRegisterCodeView.setText(registryCode);

                        hint = "okay";
                    }

                }

            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
                hint = hint_RegistryNotFound;
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
                hint = hint_RegistryFormatError;
            }


        }

        //
        //author:adong
        //date:2014-11-6
        //note:begin to invoke checkAndRegistration
        //
        if(hint=="okay") {
            boolean b = checkAndRegistration();

            showDialog(b, null);
        }
        else
        {
            Toast.makeText(this, hint, Toast.LENGTH_LONG).show();

        }

    }

    private boolean checkAndRegistration() {
        if (mRegisterCodeView != null && mDeviceId != null) {
            String registerCode = mRegisterCodeView.getText().toString();
            if (!TextUtils.isEmpty(registerCode)) {
                registerCode = registerCode.trim();
                String decodedSerial = RSACoder.decnryptDes(registerCode, Constant.testDeskey);
                if (decodedSerial != null && decodedSerial.startsWith(mDeviceId)
                        && decodedSerial.length() == mDeviceId.length() + 10) {

                    String versionRangeLow = decodedSerial.substring(decodedSerial.length() - 8,
                            decodedSerial.length() - 4);
                    String versionRangeHigh = decodedSerial.substring(decodedSerial.length() - 4,
                            decodedSerial.length());
                    int low = 1000, high = -1;
                    try {
                        low = Integer.valueOf(versionRangeLow);
                        high = Integer.valueOf(versionRangeHigh);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "checkAndRegistration", e);
                    }

                    String typeStr = decodedSerial.substring(decodedSerial.length() - 10,
                            decodedSerial.length() - 8);
                    int userType = CrtbUtils.getCrtbUserTypeByTypeStr(typeStr);

                    String username = decodedSerial.substring(0, decodedSerial.length() - 10);
                    int err = CrtbLicenseDao.defaultDao().registLicense(getApplicationContext(),
                            username, userType, low, high, registerCode);
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
