package com.crtb.tunnelmonitor.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.zw.android.framework.util.DateUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.adapter.AlertListAdapter;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertHandlingInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.CrtbUser;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.utils.AlertManager;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public class WarningActivity extends Activity {

    protected static final String TAG = "WarningActivity";

//    private static final int MSG_REFRESH_LIST = 0;

    private ListView listview;

    private RelativeLayout warningMenu;
    private RelativeLayout mHandleCompleteView;
    private RadioGroup mDealWayRadios;
//    private RadioButton mDealWayBtnDiscard;
//    private RadioButton mDealWayBtnAsFirst;
    private RadioButton mDealWayBtnCorrection;
    private RadioButton mDealWayBtnRebury;
//    private RadioButton mDealWayBtnNormal;

    private RadioButton[] mRadioBtns;

    private EditText mCorrectionView;
    private TextView mCorrectionUnitView;
    private EditText mWarningRemarkView;

    private TextView baojing, yixiao;
    private TextView warningSignalTV, warningPointNumTV, warningStateTV,
            warningValueTV, warningDateTV, warningMessageTV, warningDealWayTV,
            oldDateMileageTV, oldDateListNumTV, oldDatePointTV;
    private Button /*normalCalBtn, discardBtn, asFirstLineBtn,*/ correctionBtn, reburyBtn, handlingDetailBtn, completeOkBtn,completeCancelBtn;
    private View oldChooseView;
    private int clickedItem;
    private int handlingStep;
    private ArrayList<AlertInfo> alerts;
//    private LinearLayout rela;
    private AlertListAdapter adapter;
    private Random ran = new Random();
    private String s[] = new String[20];
//    private String ss[] = {"拱顶", "测线S1", "测线S2"};
//    private String sss[] = {"开","正在处理","已消警"};
//    private String ssss[] = {"", "", "", ""};
//    private String s2[] = {"拱顶的累计沉降值超限", "拱顶的单次下沉速率超限", "累计收敛值超限",
//            "地表的累计沉降值超限","地表的单次下沉速率超限" ,"单次收敛速率超限"};

    private int mAlertNum;
    private int mHandledAlertNum;

    protected int mCheckedRaidoId;

    private int mUserType = CrtbUser.LICENSE_TYPE_DEFAULT;

    public void initView(){
        warningSignalTV = (TextView)findViewById(R.id.warning_signal);
        warningPointNumTV = (TextView)findViewById(R.id.warning_point_num);
        warningStateTV = (TextView)findViewById(R.id.warning_state);
        warningValueTV = (TextView)findViewById(R.id.warning_value);
        warningDateTV = (TextView)findViewById(R.id.warning_date);
        warningMessageTV = (TextView)findViewById(R.id.warning_message);
        warningDealWayTV = (TextView)findViewById(R.id.warning_deal_way);
        oldDateMileageTV = (TextView)findViewById(R.id.old_date_mileage);
        oldDateListNumTV = (TextView)findViewById(R.id.old_date_list_num);
        oldDatePointTV = (TextView)findViewById(R.id.old_date_point);

        completeOkBtn = (Button)findViewById(R.id.complete_ok);
        setBtnClickListener(completeOkBtn);
        completeCancelBtn =  (Button)findViewById(R.id.complete_cancel);
        setBtnClickListener(completeCancelBtn);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);
        //rela = (LinearLayout) findViewById(R.id.rela);
//        normalCalBtn = (Button) findViewById(R.id.normal);
//        setBtnClickListener(normalCalBtn);
//        discardBtn = (Button) findViewById(R.id.discard_btn);
//        setBtnClickListener(discardBtn);
//        asFirstLineBtn = (Button) findViewById(R.id.as_first_line);
//        setBtnClickListener(asFirstLineBtn);
        correctionBtn = (Button) findViewById(R.id.correction);
        setBtnClickListener(correctionBtn);
        reburyBtn = (Button) findViewById(R.id.rebury);
        setBtnClickListener(reburyBtn);
        handlingDetailBtn = (Button) findViewById(R.id.handling_detail);
        setBtnClickListener(handlingDetailBtn);

        initB();
        initView();

        adapter = new AlertListAdapter(this, alerts);
        refreshData();

        mHandleCompleteView= (RelativeLayout) findViewById(R.id.complete_warning_rl);
        mDealWayRadios = (RadioGroup) mHandleCompleteView.findViewById(R.id.radio_group);
//        mDealWayBtnDiscard = (RadioButton) mDealWayRadios.findViewById(R.id.radio_button_void);
//        mDealWayBtnAsFirst = (RadioButton) mDealWayRadios.findViewById(R.id.radio_button_first);
        mDealWayBtnCorrection = (RadioButton) mDealWayRadios.findViewById(R.id.radio_button_add);
        mDealWayBtnRebury = (RadioButton) mDealWayRadios.findViewById(R.id.radio_button_rebury);
//        mDealWayBtnNormal = (RadioButton) mDealWayRadios.findViewById(R.id.radio_button_normal);
        mDealWayRadios.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mCheckedRaidoId = checkedId;
                if (mCorrectionView != null) {
                    mCorrectionView.setEnabled(checkedId == mDealWayBtnCorrection.getId());
                }
            }
        });

        mRadioBtns = new RadioButton[]{/*mDealWayBtnNormal, mDealWayBtnDiscard, mDealWayBtnAsFirst, */mDealWayBtnCorrection, mDealWayBtnRebury};
        mWarningRemarkView = (EditText) mHandleCompleteView.findViewById(R.id.warning_remark);

        mCorrectionUnitView = (TextView) mHandleCompleteView.findViewById(R.id.correction_unit);
        mCorrectionView = (EditText) mHandleCompleteView.findViewById(R.id.add_edit);
        mCorrectionView.addTextChangedListener(new TextWatcher() {
            
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
                if (alerts == null) {
                    return;
                }
                AlertInfo alert = alerts.get(clickedItem);
                if (alert != null) {
                    Editable e = mCorrectionView.getText();
                    float correction = 0;
                    if (e != null && e.length() > 0) {
                        String cstr = e.toString();
                        if (cstr != null && !cstr.trim().endsWith("-") && !cstr.equals(".")) {
                            correction = Float.valueOf(cstr);
                        }
                    }
                    if (warningValueTV != null) {
                        warningValueTV.setText("超限值: "
                                + String.format("%1$.1f",
                                        CrtbUtils.formatDouble(alert.getUValue() + correction, 1))
                                + AlertUtils.getAlertValueUnit(alert.getUType()));
                    }
                    if (mWarningRemarkView != null) {
                        mWarningRemarkView.setText(getString(R.string.remark_correction, correction));
                    }
                }
            }
        });

        listviewInit();

        baojing = (TextView) findViewById(R.id.rizhi);
        yixiao = (TextView) findViewById(R.id.yixiaojing);
        refreshNum();

        mUserType  = AppCRTBApplication.getInstance().getCurUserType();
    }

    @Override
    public void onBackPressed() {
        if (mHandleCompleteView != null && mHandleCompleteView.getVisibility() == View.VISIBLE) {
            cancelHandling();
            mHandleCompleteView.setVisibility(View.GONE);
            if (oldChooseView != null) {
                oldChooseView.setBackgroundResource(R.color.warning_bg);
            }
        } else if (warningMenu != null && warningMenu.getVisibility() == View.VISIBLE) {
            warningMenu.setVisibility(View.GONE);
            if (oldChooseView != null) {
                oldChooseView.setBackgroundResource(R.color.warning_bg);
            }
        } else {
            super.onBackPressed();
        }
    }

    private void refreshNum() {
        mAlertNum = adapter == null ? 0 :adapter.getCount();
        mHandledAlertNum = adapter == null ? 0 :adapter.getHandledCount();
        baojing.setText("报警日志：(" + mAlertNum + ")");
        yixiao.setText("已消警：(" + mHandledAlertNum + ")");
    }

    private View.OnClickListener mBtnOnClickListener  =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
//                case R.id.deal_with_btn:
//                    switch (handlingStep) {
//                        case 0:
//                            AlertHandlingInfoDao.defaultDao().updateAlertStatus(alerts.get(clickedItem).getAlertHandlingId(), 2);
//                            handlingStep = 1;
//                            refreshData();
//                            break;
//                        case 1:
//                            Toast.makeText(WarningActivity.this, "已开始处理", Toast.LENGTH_LONG).show();
//                            break;
//                    }
//                    break;
//                case R.id.normal:
//                case R.id.discard_btn:
//                case R.id.as_first_line:
                case R.id.correction:
                case R.id.rebury:
                    switch (handlingStep) {
                        case 0:
                        case 1:
                            if (alerts != null && alerts.size() > 0 && clickedItem >= 0 && clickedItem < alerts.size()) {
                                AlertInfo alert = alerts.get(clickedItem);
                                if (alert != null) {

                                    if (alert.getAlertStatus() == 0) {//"已消警"
                                        Toast.makeText(WarningActivity.this, "已消警", Toast.LENGTH_LONG).show();
                                        warningMenu.setVisibility(View.GONE);
                                        if (oldChooseView != null) {
                                            oldChooseView.setBackgroundResource(R.color.warning_bg);
                                        }
                                        return;
                                    }

                                    //FIXME: TIM 
                                   // RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneById(Integer.valueOf(alert.getSheetId()));
                                    RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneByGuid(alert.getSheetId());
                                    String date = DateUtils.toDateString(sheet.getCreateTime(),DateUtils.DATE_TIME_FORMAT);

                                    mHandleCompleteView.setVisibility(View.VISIBLE);
                                    warningSignalTV.setText(alert.getXinghao());
                                    warningPointNumTV.setText("点号："+alert.getPntType());
                                    warningStateTV.setText("状态："+alert.getAlertStatusMsg());
                                    warningValueTV.setText("超限值: " + String.format("%1$.1f", CrtbUtils.formatDouble(alert.getUValue(), 1))
                                            + AlertUtils.getAlertValueUnit(alert.getUType()));
                                    warningDateTV.setText(alert.getDate());
                                    warningMessageTV.setText(alert.getUTypeMsg());
                                    warningDealWayTV.setText(alert.getChuliFangshi());
                                    oldDateMileageTV.setText(Html.fromHtml("<font color=\"#0080ee\">里程: </font>" + alert.getXinghao()));
                                    oldDateListNumTV.setText(Html.fromHtml("<font color=\"#0080ee\">记录单号: </font>" + date));
                                    oldDatePointTV.setText(Html.fromHtml("<font color=\"#0080ee\">测点: </font>" + alert.getPntType()));
                                    mCorrectionView.setText(null);
//                                    mWarningRemarkView.setText(alert.getHandling());

                                    int raidoId = 0;
                                    String remark = "";
                                    switch (view.getId()) {
//                                        case R.id.normal:
//                                            raidoId = mDealWayBtnNormal.getId();
//                                            remark = getString(R.string.deal_way_normal);
//                                            break;
//                                        case R.id.discard_btn:
//                                            raidoId = mDealWayBtnDiscard.getId();
//                                            remark = getString(R.string.deal_way_void);
//                                            break;
//                                        case R.id.as_first_line:
//                                            raidoId = mDealWayBtnAsFirst.getId();
//                                            remark = getString(R.string.deal_way_first);
//                                            break;
                                        case R.id.correction:
                                            raidoId = mDealWayBtnCorrection.getId();
                                            remark = getString(R.string.remark_correction, 0f);
                                            break;
                                        case R.id.rebury:
                                            raidoId = mDealWayBtnRebury.getId();
                                            remark = getString(R.string.deal_way_rebury);
                                            break;
                                    }

                                    if (raidoId != 0) {
                                        for (RadioButton b : mRadioBtns) {
                                            boolean selected = b.getId() == raidoId;
                                            b.setVisibility(selected ? View.VISIBLE : View.GONE);
                                            b.setChecked(selected);
                                        }
                                        boolean isCorrection = raidoId == mDealWayBtnCorrection.getId();
                                        mCorrectionUnitView.setVisibility(isCorrection ? View.VISIBLE : View.GONE);
                                        mCorrectionView.setVisibility(isCorrection ? View.VISIBLE : View.GONE);
                                        mCheckedRaidoId = raidoId;
                                    }

                                    if (mWarningRemarkView != null) {
                                        mWarningRemarkView.setText(remark);
                                    }

                                }
                            }
                            break;
                        case 2:
                            Toast.makeText(WarningActivity.this, " 已消警", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
                case R.id.handling_detail:
                    if (alerts != null && alerts.size() > 0 && clickedItem >= 0 && clickedItem < alerts.size()) {
                        AlertInfo alert = alerts.get(clickedItem);
                        Intent i = new Intent(WarningActivity.this, HandlingDetailsActivity.class);
                        i.putExtra(HandlingDetailsActivity.EXTRA_ALERT_ID, alert.getAlertId());
                        startActivity(i);
                    }
                    break;
                case R.id.complete_ok:
                    //alerts.get(clickedItem).setAlertStatusMsg(sss[2]);
                    if (
//                            mCheckedRaidoId == mDealWayBtnDiscard.getId()
//                            || mCheckedRaidoId == mDealWayBtnAsFirst.getId() ||
                             mCheckedRaidoId == mDealWayBtnCorrection.getId()
//                            || mCheckedRaidoId == mDealWayBtnNormal.getId()
                            || mCheckedRaidoId == mDealWayBtnRebury.getId()) {
                        handleAlert();
                        handlingStep = 2;
                        mHandleCompleteView.setVisibility(View.GONE);
                        refreshData();
                    } else {
                        Toast.makeText(WarningActivity.this, "请选择处理方式！", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.complete_cancel:
                    cancelHandling();
                    mHandleCompleteView.setVisibility(View.GONE);
                    if (oldChooseView != null) {
                        oldChooseView.setBackgroundResource(R.color.warning_bg);
                    }
                    break;

            }
            warningMenu.setVisibility(View.GONE);
        }
    };

    public void setBtnClickListener(final Button btn) {
        btn.setOnClickListener(mBtnOnClickListener);
    }

    public void listviewInit() {

        warningMenu = (RelativeLayout) findViewById(R.id.warning_menu);
        listview = (ListView) findViewById(R.id.listView12);
        listview.setDividerHeight(1);
        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mUserType != CrtbUser.LICENSE_TYPE_REGISTERED) {
                    Toast.makeText(getApplicationContext(), "预警处理对非注册用户不可用!", Toast.LENGTH_LONG).show();
                    return true;
                }
                
                if (oldChooseView != null) {
                    oldChooseView.setBackgroundResource(R.color.warning_bg);
                }
                
                view.setBackgroundResource(R.color.lightyellow);
                clickedItem = position;
                if (alerts.get(position).getAlertStatus() == 1) {//"开"
                    handlingStep = 0;
                    warningMenu.setVisibility(View.VISIBLE);
                }
                if (alerts.get(position).getAlertStatus() == 2) {//"正在处理"
                    handlingStep = 1;
                    warningMenu.setVisibility(View.VISIBLE);
                }
                if (alerts.get(position).getAlertStatus() == 0) {//"已消警"
                    warningMenu.setVisibility(View.VISIBLE);
//                    Toast.makeText(WarningActivity.this, "已消警", Toast.LENGTH_LONG).show();
                }
                oldChooseView = view;
                return true;
            }
        });

//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            }
//        });

        warningMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warningMenu.setVisibility(View.GONE);
                if (oldChooseView != null) oldChooseView.setBackgroundResource(R.color.warning_bg);
            }
        });
//        AlertInfo.count = adapter.getCount();
    }

    private void handleAlert() {

//        int alertHandlingId = alerts.get(clickedItem).getAlertHandlingId();
        AlertInfo ai = (AlertInfo) adapter.getItem(clickedItem);
        if (ai == null) {
            return;
        }

        boolean isRebury = false;

        int curStatus = ai.getAlertStatus();
        float correction = 0f;

        if (mCorrectionView != null && mCorrectionView.getVisibility() == View.VISIBLE) {
            Editable e = mCorrectionView.getText();
            if (e != null && e.length() > 0) {
                correction = Float.valueOf(e.toString());
            }
        }

        int alertId = ai.getAlertId();
        int dataStatus = AlertUtils.POINT_DATASTATUS_NONE;
       /* if (mCheckedRaidoId == mDealWayBtnDiscard.getId()) {
            Log.d(TAG, "Handling way: discard data");
            dataStatus = AlertUtils.POINT_DATASTATUS_DISCARD;
        } else if (mCheckedRaidoId == mDealWayBtnAsFirst.getId()) {
            Log.d(TAG, "Handling way: As First line");
            dataStatus = AlertUtils.POINT_DATASTATUS_AS_FIRSTLINE;
        } else */if (mCheckedRaidoId == mDealWayBtnCorrection.getId()) {
            Log.d(TAG, "Handling way: Correction");
            dataStatus = AlertUtils.POINT_DATASTATUS_CORRECTION;
        }/* else if (mCheckedRaidoId == mDealWayBtnNormal.getId()) {
            Log.d(TAG, "Handling way: Normal");
            dataStatus = AlertUtils.POINT_DATASTATUS_NORMAL;
        } */else if (mCheckedRaidoId == mDealWayBtnRebury.getId()) {
            Log.d(TAG, "Handling way: Rebury");
            isRebury = true;
            dataStatus = AlertUtils.POINT_DATASTATUS_CORRECTION;
        } else if (correction != 0) {
            Log.d(TAG, "Handling way: Correction");
            //If no radio button is selected and correction is input, treat as correction
            dataStatus = AlertUtils.POINT_DATASTATUS_CORRECTION;
        }

        int alertStatus = 0;// TODO : may also be 2?, if so,
                            // 需要将mBtnOnClickListener中deal_with_btn也要和complete_btn一样的逻辑
        String handling = mWarningRemarkView.getText().toString();
        Log.d(TAG, "handleAlert 处理内容：" + handling);
        new AlertManager().handleAlert(alertId, dataStatus, isRebury, correction, curStatus, alertStatus, handling,
                new Date(System.currentTimeMillis()), new AlertManager.HandleFinishCallback() {

                    @Override
                    public void onFinish() {
//                        mHandler.sendEmptyMessageDelayed(MSG_REFRESH_LIST, 200);
                        refreshData();
                    }
                });
    }

    private void cancelHandling() {
        mCheckedRaidoId = 0;
    }

    private void refreshData() {
        new RefreshTask().execute();
    }

    public void initB() {
        for (int i = 0; i < s.length; i++) {
            s[i] = "DK+"
                    + (10 + ran.nextInt(10))
                    + ((double) (Math.round(ran.nextDouble() + 100) / 100.0) + (ran
                    .nextInt(100) + 100));
        }
    }

//    private Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch(msg.what) {
//                case MSG_REFRESH_LIST:
//                    refreshData();
//                    break;
//                default:
//                    super.handleMessage(msg);
//            }
//        }
//        
//    };

    class RefreshTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            alerts = AlertUtils.getAlertInfoList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (adapter != null) {
                adapter.refreshData(alerts);
                refreshNum();
            }
        }
        
    }
}
