package com.crtb.tunnelmonitor.activity;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppConfig;
import com.crtb.tunnelmonitor.dao.impl.v2.SurveyerInformationDao;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;
import com.crtb.tunnelmonitor.event.EventDispatcher;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.utils.CrtbAppConfig;

public class TesterLoadActivity extends Activity implements OnClickListener {

    private Button mLoad;

    private EditText mServerAddress;

    private EditText mUserName;

    private EditText mPassword;

    private RelativeLayout mTvLayout;

    private List<SurveyerInformation> mTestors ;

    private ListView mListView;

    private TestorAdapter mAdapter;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_tester_layout);
        mServerAddress = (EditText) findViewById(R.id.server_ip);
        mServerAddress.setText("http://61.237.239.144/fxkz/basedown");
        mServerAddress.setSingleLine(true);
        mLoad = (Button) findViewById(R.id.load);
        mUserName = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mTvLayout = (RelativeLayout) findViewById(R.id.tv_layout);

        mLoad.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.listview);
        init();
    }

    private void init() {

        mTestors = SurveyerInformationDao.defaultDao().queryAllSurveyerInformation() ;

        if (mTestors!=null && mTestors.size() > 0) {
            mTvLayout.setVisibility(View.VISIBLE);
            mAdapter = new TestorAdapter();
            mListView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load:

                String name = mUserName.getText().toString().trim();
                String pwd = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
                    Toast.makeText(this, "请填写用户名和密码", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(TesterLoadActivity.this, null,
                            getString(R.string.tester_loading), true, false);
                }
                mProgressDialog.show();
                login(name, pwd);

                break;
        }
    }

    private void login(final String username, final String password) {

        CrtbWebService.getInstance().login(username, password,
                new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                // Toast.makeText(TesterLoadActivity.this,
                // "获取成功",Toast.LENGTH_LONG).show();
                CrtbAppConfig config = CrtbAppConfig.getInstance();
                config.setUserName(username);
                config.setPassword(password);
                CrtbWebService.getInstance().getSurveyors(
                        new RpcCallback() {

                            @Override
                            public void onSuccess(Object[] data) {

                                if (data != null && data.length > 0) {

                                    mTestors = Arrays
                                            .asList((SurveyerInformation[]) data);

                                    SurveyerInformationDao.defaultDao().deleteAll() ;

                                    mTvLayout.setVisibility(View.VISIBLE);
                                    mAdapter=new TestorAdapter();
                                    mListView.setAdapter(mAdapter);

                                    for (int i = 0; i < mTestors.size(); i++){
                                        SurveyerInformationDao.defaultDao().insert(mTestors.get(i)) ;
                                    }

                                    // 通知
                                    sendBroadcast(new Intent(AppConfig.ACTION_RELOAD_ALL_SURVEYER));

                                    EventDispatcher.getInstance().notifyDatabaseChanged();
                                    mProgressDialog.dismiss();
                            Toast.makeText(TesterLoadActivity.this, R.string.tester_load_success,
                                            Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailed(String reason) {
                                mProgressDialog.dismiss();
                        Toast.makeText(TesterLoadActivity.this, R.string.tester_load_failed,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }

            @Override
            public void onFailed(String reason) {
                mProgressDialog.dismiss();
                Toast.makeText(TesterLoadActivity.this, R.string.tester_load_failed,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    class TestorAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mTestors != null) {
                return mTestors.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int pos) {
            if (mTestors != null) {
                return mTestors.get(pos);
            }
            return null;
        }

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public View getView(int pos, View view, ViewGroup arg2) {
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(TesterLoadActivity.this).inflate(
                        R.layout.testor_item_layout, null);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.idCard = (TextView) view.findViewById(R.id.idcard);
                holder.note = (TextView) view.findViewById(R.id.note);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            SurveyerInformation info = mTestors.get(pos);
            holder.name.setText(info.getSurveyerName());
            holder.idCard.setText(info.getCertificateID());
            holder.note.setText(info.getInfo());
            return view;
        }

    }

    class ViewHolder {
        TextView name;
        TextView idCard;
        TextView note;
    }
}
