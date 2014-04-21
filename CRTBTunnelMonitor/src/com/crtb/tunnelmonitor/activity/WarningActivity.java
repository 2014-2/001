package com.crtb.tunnelmonitor.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.crtb.tunnelmonitor.adapter.Myadapter;
import com.crtb.tunnelmonitor.entity.AlertInfo;

public class WarningActivity extends Activity {

    private ListView listview;
    private RelativeLayout warningBottom;
    private RelativeLayout completeView;
    private TextView baojing, yixiao;
    private TextView warningSignalTV, warningPointNumTV,warningStateTV,warningDateTV,
            warningMessageTV,warningDealWayTV,oldDateMileageTV,oldDateListNumTV,
            oldDatePointTV;
    private RadioButton radioButtonVoid,radioButtonFirst,radioButtonAdd;
    private EditText addEdit,warningRemarkET;
    private Button dealWithBtn, completeBtn,completeOkBtn,completeCancelBtn;
    private View oldChooseView;
    private int CheckID;
    private int stpeNum;
    private ArrayList<AlertInfo> listt;
    private LinearLayout rela;
    private Myadapter adapter;
    private Random ran = new Random();
    private String s[] = new String[20];
    private String ss[] = {"拱顶", "测线S1", "测线S2"};
    private String sss[] = {"开","正在处理","已消警"};
    private String ssss[] = {"", "", "", ""};
    private String s2[] = {"拱顶的累计沉降值超限", "拱顶的单次下沉速率超限", "累计收敛值超限",
            "地表的累计沉降值超限","地表的单次下沉速率超限" ,"单次收敛速率超限"};

    public void initView(){
        warningSignalTV = (TextView)findViewById(R.id.warning_signal);
        warningPointNumTV = (TextView)findViewById(R.id.warning_point_num);
        warningStateTV = (TextView)findViewById(R.id.warning_state);
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
        clickListen(completeOkBtn);
        completeCancelBtn =  (Button)findViewById(R.id.complete_cancel);
        clickListen(completeCancelBtn);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);
        //rela = (LinearLayout) findViewById(R.id.rela);
        dealWithBtn = (Button) findViewById(R.id.deal_with_btn);
        clickListen(dealWithBtn);
        completeBtn = (Button) findViewById(R.id.complete_btn);
        clickListen(completeBtn);
        initB();
        initView();
        listviewInit();
        baojing = (TextView) findViewById(R.id.rizhi);
        baojing.setText("报警日志：(" + AlertInfo.count + ")");
        yixiao = (TextView) findViewById(R.id.yixiaojing);
        yixiao.setText("已消警：(" + AlertInfo.yixiao + ")");
    }


    public void clickListen(final Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.deal_with_btn:
                        switch (stpeNum) {
                            case 0:
                                listt.get(CheckID).setState(sss[1]);
                                stpeNum = 1;
                                adapter.notifyDataSetChanged();
                                break;
                            case 1:
                                Toast.makeText(WarningActivity.this, " 已开始处理", 1000).show();
                                break;
                        }
                        break;
                    case R.id.complete_btn:
                        switch (stpeNum) {
                            case 0:
                            case 1:
                                completeView.setVisibility(View.VISIBLE);
                                warningSignalTV.setText(listt.get(CheckID).getXinghao());
                                warningPointNumTV.setText("点号："+listt.get(CheckID).getDianhao());
                                warningStateTV.setText("状态："+listt.get(CheckID).getState());
                                warningDateTV.setText(listt.get(CheckID).getDate());
                                warningMessageTV.setText(listt.get(CheckID).getMessage());
                                warningDealWayTV.setText(listt.get(CheckID).getChuliFangshi());
                                oldDateMileageTV.setText(Html.fromHtml("<font color=\"#0080ee\">里程：</font>"+listt.get(CheckID).getXinghao()));
                                oldDateListNumTV.setText(Html.fromHtml("<font color=\"#0080ee\">记录单号：</font>"+listt.get(CheckID).getDate()));
                                oldDatePointTV.setText(Html.fromHtml("<font color=\"#0080ee\">测点：</font>"+listt.get(CheckID).getDianhao()));
                                break;
                            case 2:
                                Toast.makeText(WarningActivity.this, " 已消警", 1000).show();
                                break;
                        }
                        break;
                    case R.id.complete_ok:
                        listt.get(CheckID).setState(sss[2]);
                        stpeNum = 2;
                        completeView.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.complete_cancel:
                        completeView.setVisibility(View.GONE);
                        if (oldChooseView != null) oldChooseView.setBackgroundResource(R.color.warning_bg);
                        break;

                }
                warningBottom.setVisibility(View.GONE);
            }
        });
    }

    public void listviewInit() {

        completeView= (RelativeLayout) findViewById(R.id.complete_warning_rl);
        warningBottom = (RelativeLayout) findViewById(R.id.warning_bottom);
        listview = (ListView) findViewById(R.id.listView12);
        listview.setDividerHeight(1);
        adapter = new Myadapter(WarningActivity.this, getdata());
        listt = (ArrayList<AlertInfo>) adapter.getList();
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (oldChooseView != null) oldChooseView.setBackgroundResource(R.color.warning_bg);
                view.setBackgroundResource(R.color.lightyellow);
                CheckID = i;
                if (listt.get(i).getState().equals("开")) {
                    stpeNum = 0;
                    warningBottom.setVisibility(View.VISIBLE);
                }
                if (listt.get(i).getState().equals("正在处理")) {
                    stpeNum = 1;
                    warningBottom.setVisibility(View.VISIBLE);
                }
                if (listt.get(i).getState().equals("已消警")) {
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
        AlertInfo.count = adapter.getCount();

    }

    public void initB() {
        for (int i = 0; i < s.length; i++) {
            s[i] = "DK+"
                    + (10 + ran.nextInt(10))
                    + ((double) (Math.round(ran.nextDouble() + 100) / 100.0) + (ran
                    .nextInt(100) + 100));
        }
    }

    public ArrayList<AlertInfo> getdata() {
        AlertInfo.yixiao = 0;
        ArrayList<AlertInfo> listt = new ArrayList<AlertInfo>();
        AlertInfo infor;
        for (int i = 0; i < s.length; i++) {
            infor = new AlertInfo();
            infor.setDate(getdate());
            infor.setXinghao(s[i]);
            infor.setDianhao(ss[ran.nextInt(3)]);
            infor.setChuliFangshi("自由处理");
            infor.setState(sss[ran.nextInt(3)]);
            infor.setMessage(s2[ran.nextInt(4)]);
            infor.setEdtState(ssss[ran.nextInt(4)]);
            infor.setState1(true);
            if (infor.getState().equals("已消警")) {
                AlertInfo.yixiao = AlertInfo.yixiao + 1;
                System.out.println(AlertInfo.yixiao);

            }
            listt.add(infor);
        }
        return listt;
    }

    public String getdate() {
        SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return simp.format(new Date());
    }
}
