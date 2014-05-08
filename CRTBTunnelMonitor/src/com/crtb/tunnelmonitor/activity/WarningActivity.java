package com.crtb.tunnelmonitor.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.zw.android.framework.util.DateUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.crtb.tunnelmonitor.adapter.AlertListAdapter;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertHandlingInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.utils.AlertManager;
import com.crtb.tunnelmonitor.utils.AlertUtils;

public class WarningActivity extends Activity {

    protected static final String TAG = "WarningActivity";

    private ListView listview;

    private RelativeLayout warningBottom;
    private RelativeLayout mHandleCompleteView;
    private RadioGroup mDealWayRadios;
    private RadioButton mDealWayBtnDiscard;
    private RadioButton mDealWayBtnAsFirst;
    private RadioButton mDealWayBtnCorrection;
    private EditText mCorrectionView;
    private EditText mWarningRemarkView;

    private TextView baojing, yixiao;
    private TextView warningSignalTV, warningPointNumTV, warningStateTV,
            warningValueTV, warningDateTV, warningMessageTV, warningDealWayTV,
            oldDateMileageTV, oldDateListNumTV, oldDatePointTV;
    private RadioButton radioButtonVoid,radioButtonFirst,radioButtonAdd;
    private EditText addEdit,warningRemarkET;
    private Button dealWithBtn, completeBtn,completeOkBtn,completeCancelBtn;
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

        radioButtonVoid = (RadioButton)findViewById(R.id.radio_button_void);
        radioButtonFirst = (RadioButton)findViewById(R.id.radio_button_first);
        radioButtonAdd = (RadioButton)findViewById(R.id.radio_button_add);

        addEdit = (EditText)findViewById(R.id.add_edit);
        warningRemarkET = (EditText)findViewById(R.id.warning_remark);

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
        dealWithBtn = (Button) findViewById(R.id.deal_with_btn);
        setBtnClickListener(dealWithBtn);
        completeBtn = (Button) findViewById(R.id.complete_btn);
        setBtnClickListener(completeBtn);
        initB();
        initView();

        alerts = AlertUtils.getAlertInfoList();
        adapter = new AlertListAdapter(this, alerts);

        mHandleCompleteView= (RelativeLayout) findViewById(R.id.complete_warning_rl);
        mDealWayRadios = (RadioGroup) mHandleCompleteView.findViewById(R.id.radio_group);
        mDealWayBtnDiscard = (RadioButton) mDealWayRadios.findViewById(R.id.radio_button_void);
        mDealWayBtnAsFirst = (RadioButton) mDealWayRadios.findViewById(R.id.radio_button_first);
        mDealWayBtnCorrection = (RadioButton) mDealWayRadios.findViewById(R.id.radio_button_add);
        mDealWayRadios.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mCheckedRaidoId = checkedId;
            }
        });

        mCorrectionView = (EditText) mHandleCompleteView.findViewById(R.id.add_edit);
        mWarningRemarkView = (EditText) mHandleCompleteView.findViewById(R.id.warning_remark);
        listviewInit();

        baojing = (TextView) findViewById(R.id.rizhi);
        yixiao = (TextView) findViewById(R.id.yixiaojing);
        refreshNum();
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
                case R.id.deal_with_btn:
                    switch (handlingStep) {
                        case 0:
                            AlertHandlingInfoDao.defaultDao().updateAlertStatus(alerts.get(clickedItem).getAlertHandlingId(), 2);
//                            alerts.get(clickedItem).setAlertStatusMsg(sss[1]);
                            handlingStep = 1;
                            adapter.notifyDataSetChanged();
                            alerts = AlertUtils.getAlertInfoList();
                            adapter.refreshData(alerts);
                            refreshNum();
                            break;
                        case 1:
                            Toast.makeText(WarningActivity.this, " 已开始处理", 1000).show();
                            break;
                    }
                    break;
                case R.id.complete_btn:
                    switch (handlingStep) {
                        case 0:
                        case 1:
                            if (alerts != null && alerts.size() > 0 && clickedItem >= 0 && clickedItem < alerts.size()) {
                                AlertInfo alert = alerts.get(clickedItem);
                                if (alert != null) {
                                    RawSheetIndex sheet = RawSheetIndexDao.defaultDao().queryOneById(alert.getSheetId());
                                    String date = DateUtils.toDateString(sheet.getCreateTime(),DateUtils.DATE_TIME_FORMAT);

                                    mHandleCompleteView.setVisibility(View.VISIBLE);
                                    warningSignalTV.setText(alert.getXinghao());
                                    warningPointNumTV.setText("点号："+alert.getPntType());
                                    warningStateTV.setText("状态："+alert.getAlertStatusMsg());
                                    warningValueTV.setText("超限值: " + String.format("%1$.4f", alert.getUValue())
                                            + AlertUtils.getAlertValueUnit(alert.getUType()));
                                    warningDateTV.setText(alert.getDate());
                                    warningMessageTV.setText(alert.getUTypeMsg());
                                    warningDealWayTV.setText(alert.getChuliFangshi());
                                    oldDateMileageTV.setText(Html.fromHtml("<font color=\"#0080ee\">里程: </font>" + alert.getXinghao()));
                                    oldDateListNumTV.setText(Html.fromHtml("<font color=\"#0080ee\">记录单号: </font>" + date));
                                    oldDatePointTV.setText(Html.fromHtml("<font color=\"#0080ee\">测点: </font>" + alert.getPntType()));
                                }
                            }
                            break;
                        case 2:
                            Toast.makeText(WarningActivity.this, " 已消警", 1000).show();
                            break;
                    }
                    break;
                case R.id.complete_ok:
                    //alerts.get(clickedItem).setAlertStatusMsg(sss[2]);
                    if (mCheckedRaidoId == mDealWayBtnDiscard.getId()
                            || mCheckedRaidoId == mDealWayBtnAsFirst.getId()
                            || mCheckedRaidoId == mDealWayBtnCorrection.getId()) {
                        handleAlert();
                        handlingStep = 2;
                        mHandleCompleteView.setVisibility(View.GONE);
                        alerts = AlertUtils.getAlertInfoList();
                        adapter.refreshData(alerts);
                        refreshNum();
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
            warningBottom.setVisibility(View.GONE);
        }
    };

    public void setBtnClickListener(final Button btn) {
        btn.setOnClickListener(mBtnOnClickListener);
    }

    public void listviewInit() {

        warningBottom = (RelativeLayout) findViewById(R.id.warning_bottom);
        listview = (ListView) findViewById(R.id.listView12);
        listview.setDividerHeight(1);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (oldChooseView != null) oldChooseView.setBackgroundResource(R.color.warning_bg);
                view.setBackgroundResource(R.color.lightyellow);
                clickedItem = i;
                if (alerts.get(i).getAlertStatus() == 1) {//"开"
                    handlingStep = 0;
                    warningBottom.setVisibility(View.VISIBLE);
                }
                if (alerts.get(i).getAlertStatus() == 2) {//"正在处理"
                    handlingStep = 1;
                    warningBottom.setVisibility(View.VISIBLE);
                }
                if (alerts.get(i).getAlertStatus() == 0) {//"已消警"
                    warningBottom.setVisibility(View.GONE);
                    Toast.makeText(WarningActivity.this, "已消警", 1000).show();
                }
                oldChooseView = view;
            }
        });

        warningBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warningBottom.setVisibility(View.GONE);
                if (oldChooseView != null) oldChooseView.setBackgroundResource(R.color.warning_bg);
            }
        });
//        AlertInfo.count = adapter.getCount();
    }

    private void handleAlert() {
        // TODO: SHOULD WE DELETE THIS CURRENT AlertHandlingInfo form db?
        AlertHandlingInfoDao.defaultDao().deleteItemById(
                alerts.get(clickedItem).getAlertHandlingId());

        AlertInfo ai = (AlertInfo) adapter.getItem(clickedItem);
        if (ai == null) {
            return;
        }

        float correction = 0f;

        Editable e = mCorrectionView.getText();
        if (e != null && e.length() > 0) {
            correction = Float.valueOf(e.toString());
        }

        int alertId = ai.getAlertId();
        int dataStatus = 0;
        if (mCheckedRaidoId == mDealWayBtnDiscard.getId()) {
            Log.d(TAG, "Handling way: discard data");
            dataStatus = 1;
        } else if (mCheckedRaidoId == mDealWayBtnAsFirst.getId()) {
            Log.d(TAG, "Handling way: As First line");
            dataStatus = 2;
        } else if (mCheckedRaidoId == mDealWayBtnCorrection.getId()) {
            Log.d(TAG, "Handling way: Correction");
            dataStatus = 3;
        } else if (correction != 0) {
            Log.d(TAG, "Handling way: Correction");
            dataStatus = 3;
        }

        int alertStatus = 0;//TODO : may also be 2?, if so, 需要将mBtnOnClickListener中deal_with_btn也要和complete_btn一样的逻辑
        String handling = mWarningRemarkView.getText().toString();
        Log.d(TAG, "handleAlert 处理内容：" + handling);
        new AlertManager().handleAlert(alertId, dataStatus, correction, alertStatus, handling, new Date(
                System.currentTimeMillis()));
    }

    private void cancelHandling() {
        mCheckedRaidoId = 0;
    }

    public void initB() {
        for (int i = 0; i < s.length; i++) {
            s[i] = "DK+"
                    + (10 + ran.nextInt(10))
                    + ((double) (Math.round(ran.nextDouble() + 100) / 100.0) + (ran
                    .nextInt(100) + 100));
        }
    }

//    public ArrayList<AlertInfo> getdata() {
//        AlertInfo.yixiao = 0;
//        ArrayList<AlertInfo> listt = new ArrayList<AlertInfo>();
//        AlertInfo infor;
//        for (int i = 0; i < s.length; i++) {
//            infor = new AlertInfo();
//            infor.setDate(getdate());
//            infor.setXinghao(s[i]);
//            infor.setPntType(ss[ran.nextInt(3)]);
//            infor.setChuliFangshi("自由处理");
//            infor.setAlertStatusMsg(sss[ran.nextInt(3)]);
//            infor.setUTypeMsg(s2[ran.nextInt(4)]);
//            infor.setEdtState(ssss[ran.nextInt(4)]);
////            infor.setState1(true);
//            if (infor.getAlertStatusMsg().equals("已消警")) {
//                AlertInfo.yixiao = AlertInfo.yixiao + 1;
//                System.out.println(AlertInfo.yixiao);
//
//            }
//            listt.add(infor);
//        }
//        return listt;
//    }

    public String getdate() {
        SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return simp.format(new Date());
    }
}
