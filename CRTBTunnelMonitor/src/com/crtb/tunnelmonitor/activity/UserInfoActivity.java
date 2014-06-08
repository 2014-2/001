package com.crtb.tunnelmonitor.activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.crtb.tunnelmonitor.AppCRTBApplication;

/**
 * 
 * @author edison.xiao
 * @since 2014.4.5
 */
public class UserInfoActivity extends Activity {

    private EditText mName,mIdCard,mNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_layout);
        mName=(EditText) findViewById(R.id.name);
        mName.setText(AppCRTBApplication.mUserName);
        mIdCard=(EditText) findViewById(R.id.idcard);
        mIdCard.setText(AppCRTBApplication.mCard);
        mNote=(EditText) findViewById(R.id.note);
        mNote.setText(AppCRTBApplication.mNote);
    }

}
