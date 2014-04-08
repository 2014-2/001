package com.crtb.tunnelmonitor.activity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.crtb.tunnelmonitor.adapter.RecordSectionAdapter;
import com.crtb.tunnelmonitor.adapter.RecordSubsidenceCrossSectionInfoAdapter;
import com.crtb.tunnelmonitor.adapter.RecordTunnelCrossSectionInfoAdapter;
import com.crtb.tunnelmonitor.adapter.TunnelCrossSectionInfoAdapter;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.RecordDaoImpl;
import com.crtb.tunnelmonitor.dao.impl.SubsidenceCrossSectionDaoImpl;
import com.crtb.tunnelmonitor.dao.impl.TunnelCrossSectionDaoImpl;
import com.crtb.tunnelmonitor.entity.RecordInfo;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionInfo;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;
import com.crtb.tunnelmonitor.entity.WorkInfos;
import com.crtb.tunnelmonitor.utils.Time;
import com.crtb.tunnelmonitor.activity.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 新建记录单
 * @author   代世明
 * 创建时间:    2014-3-16   下午5:57:37
 * @version   1.0
 * @since       JDK  1.6
 *
 */
public class RecordNewActivity extends Activity implements OnPageChangeListener, OnClickListener {
    private ListView listView;

    private ViewPager mPager;// 页卡内容
    private List<View> listViews; // Tab页面列表
    private ImageView cursor;// 动画图片
    private TextView t1, t2;// 页卡头标
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    double disPlayWidth, offSet;
    Bitmap b;
    ArrayList<View> list = null;
    LinearLayout xin;
    private int num,itype;
    private TextView record_new_tv_header;
    
    private EditText record_Chainage;
    private EditText record_Person;
    private EditText record_Card;
    private EditText record_C;
    private EditText record_dotype;
    private List<TunnelCrossSectionInfo> infos = null;
    private List<SubsidenceCrossSectionInfo> infos1 = null;

    private RecordTunnelCrossSectionInfoAdapter adapter = null;
    private RecordSubsidenceCrossSectionInfoAdapter adapter1 = null;
    
	/** 确定按钮 */
	private Button section_btn_queding;
	/** 取消按钮 */
	private Button section_btn_quxiao;
	
	private RecordInfo editInfo = null;
	private CRTBTunnelMonitor CurApp = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_new);
        itype = 1;
        num = getIntent().getExtras().getInt(Constant.Select_RecordRowClickItemsName_Name);
        if (num == 2) {
        	itype = 1;
        	editInfo = (RecordInfo)getIntent().getExtras().getParcelable(Constant.Select_RecordRowClickItemsName_Data);
		}
        if (num == 3) {
        	itype = 2;
		}
        if (num == 4) {
        	itype = 2;
        	editInfo = (RecordInfo)getIntent().getExtras().getParcelable(Constant.Select_RecordRowClickItemsName_Data);
		}
        
        initUI();
        InitImageView();
        initPager();
        CurApp = ((CRTBTunnelMonitor)getApplicationContext());
        if (num == 2) {
        	record_new_tv_header.setText("编辑隧道内断面记录单");
		}
        if (num == 3) {
        	record_new_tv_header.setText("新建地表下沉断面记录单");
		}
        if (num == 4) {
        	record_new_tv_header.setText("编辑地表下沉断面记录单");
		}

    }

    //初始化
    public void initUI() {
        t1 = (TextView) findViewById(R.id.text1);
        t2 = (TextView) findViewById(R.id.text2);
        record_new_tv_header = (TextView) findViewById(R.id.record_new_tv_header);
        cursor = (ImageView) findViewById(R.id.cursor);
        mPager = (ViewPager) findViewById(R.id.vPager);
        t1.setOnClickListener(tv_Listener);
        t2.setOnClickListener(tv_Listener);
        
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
			IntentCancel.putExtra(Constant.Select_SectionRowClickItemsName_Name,itype);
			setResult(RESULT_CANCELED, IntentCancel);
			this.finish();// 关闭当前界面
			break;
		case R.id.work_btn_queding: // 数据库
			if(record_Chainage.getText().toString().trim().length() <= 0)
			{
				Toast.makeText(this, "请输入完整信息", 3000).show();
				return;
			}
			WorkInfos Curw = CurApp.GetCurWork();
			RecordInfo ts = new RecordInfo();
			if (editInfo != null) {
				ts.setId(editInfo.getId());
			}
			ts.setFacedk(Double.valueOf(record_Chainage.getText().toString().trim()));
			ts.setCrossSectionType(itype);
			// 获取手机的当前时间
			final String time = Time.getDateEN();
			ts.setCreateTime(Timestamp.valueOf(time));
			ts.setFacedescription(record_dotype.getText().toString().trim());
			ts.setTemperature(Double.valueOf(record_C.getText().toString().trim()));
			ts.setInfo("");
			if (itype == 1) {
				ts.setCrossSectionIDs(CRTBTunnelMonitor.GetSectionIDArrayForTunnelCrossArray(infos));
			}
			else {
				ts.setCrossSectionIDs(CRTBTunnelMonitor.GetSectionIDArrayForSubCrossArray(infos1));
			}
			ts.setChainageName(CurApp.GetSectionName(ts.getFacedk().doubleValue()));
			if(!CurApp.IsValidRecordInfo(ts))
			{
				Toast.makeText(this, "请输入完整信息", 3000).show();
				return;
			}
			if ((ts.getFacedk().doubleValue() < Curw.getStartChainage().doubleValue()) ||
					(ts.getFacedk().doubleValue() > Curw.getEndChainage().doubleValue())){
				String sStart = CurApp.GetSectionName(Curw.getStartChainage().doubleValue());
				String sEnd = CurApp.GetSectionName(Curw.getEndChainage().doubleValue());
				String sMsg = "请输入里程为"+sStart+"到"+sEnd+"之间的里程";
				Toast.makeText(this, sMsg, 3000).show();
				return;
			}
			List<RecordInfo> rinfos = null;
			if (itype == 1) {
				rinfos = Curw.getTcsirecordList();
			}
			else {
				rinfos = Curw.getScsirecordList();
			}
			if(rinfos == null)
			{
				Toast.makeText(this, "添加失败", 3000).show();
			}
			else
			{
				if(editInfo == null)
				{
					RecordDaoImpl impl = new RecordDaoImpl(this,Curw.getProjectName());
					if(impl.AddRecord(ts))
					{
						rinfos.add(ts);
						CurApp.UpdateWork(Curw);
						Toast.makeText(this, "添加成功", 3000).show();
					}
					else
					{
						Toast.makeText(this, "添加失败", 3000).show();
					}
				}
				else
				{
					RecordDaoImpl impl = new RecordDaoImpl(this,Curw.getProjectName());
					impl.UpdateRecord(ts);
					Curw.UpdateRecordInfo(itype,ts);
					CurApp.UpdateWork(Curw);
					Toast.makeText(this, "编辑成功", 3000).show();
				}
			}
			Intent IntentOk = new Intent();
			IntentOk.putExtra(Constant.Select_SectionRowClickItemsName_Name,itype);
			setResult(RESULT_OK, IntentOk);
			this.finish();
			break;
		default:
			break;
		}

	}

  
    public void InitImageView() {
        Display dis = this.getWindowManager().getDefaultDisplay();
        disPlayWidth = dis.getWidth();
        b = BitmapFactory.decodeResource(this.getResources(), R.drawable.heng);
        offSet = ((disPlayWidth / 4) - b.getWidth() / 2);
        list = new ArrayList<View>();
        LayoutInflater li = LayoutInflater.from(RecordNewActivity.this);
        list.add(li.inflate(R.layout.lrecord_new_xuanze, null));
        list.add(li.inflate(R.layout.record_new_xinxi, null));
    }
    public void setdata(int type) {
    	CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
		WorkInfos CurW = CurApp.GetCurWork();
		if(CurW == null)
		{
			return;
		}
		if (type == 1) {
			if(infos == null)
			{
				infos = new ArrayList<TunnelCrossSectionInfo>();
			}
			TunnelCrossSectionDaoImpl impl = new TunnelCrossSectionDaoImpl(this, CurW.getProjectName());
			impl.GetTunnelCrossSectionList(infos);
			if (editInfo != null) {
				String sSels = editInfo.getCrossSectionIDs();
				List<Integer> iSels = CRTBTunnelMonitor.GetSectionIDArray(sSels);
				for (int i = 0; i < infos.size(); i++) {
					for (int j = 0; j < iSels.size(); j++) {
						if (infos.get(i).getId() == iSels.get(j)) {
							infos.get(i).setbUse(true);
							break;
						}
					}
				}
			}
		}
		else {
			if(infos1 == null)
			{
				infos1 = new ArrayList<SubsidenceCrossSectionInfo>();
			}
			SubsidenceCrossSectionDaoImpl impl = new SubsidenceCrossSectionDaoImpl(this, CurW.getProjectName());
			impl.GetSubsidenceCrossSectionList(infos1);
			if (editInfo != null) {
				String sSels = editInfo.getCrossSectionIDs();
				List<Integer> iSels = CRTBTunnelMonitor.GetSectionIDArray(sSels);
				for (int i = 0; i < infos1.size(); i++) {
					for (int j = 0; j < iSels.size(); j++) {
						if (infos1.get(i).getId() == iSels.get(j)) {
							infos1.get(i).setbUse(true);
							break;
						}
					}
				}
			}
		}
	}    
    public void initPager() {
        PagerAdapter pa = new PagerAdapter() {
            
            @Override
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView((View) list.get(arg1));

            }

            @Override
            public void finishUpdate(View arg0) {

            }

            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object instantiateItem(View arg0, int arg1) {
                ((ViewPager) arg0).addView((View) list.get(arg1), 0);
                
               record_Chainage = (EditText) findViewById(R.id.record_Chainage);
               record_Person = (EditText) findViewById(R.id.record_Person);
    		   record_Card = (EditText) findViewById(R.id.record_Card);
    		   record_C = (EditText) findViewById(R.id.record_C);
    		   record_dotype = (EditText) findViewById(R.id.record_dotype);
    		   if (arg1 == 0) {
                   record_Person.setText(CurApp.getCurPerson().getSurveyerName());
        		   record_Card.setText(CurApp.getCurPerson().getCertificateID());
        		   if (editInfo != null) {
        			   record_Chainage.setText(Double.toString(editInfo.getFacedk().doubleValue()));
        			   record_Chainage.setFocusableInTouchMode(false);
            		   record_C.setText(Double.toString(editInfo.getTemperature()));
            		   record_dotype.setText(editInfo.getFacedescription());
    			}
			}
    		   else
    		   if(arg1 == 1)
    		   {
    				listView = (ListView) ((View) list.get(arg1)).findViewById(R.id.section_use_list);
    				listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							if (itype == 1) {
								TunnelCrossSectionInfo item = infos.get(arg2);	
								item.setbUse(!item.isbUse());
								infos.set(arg2, item);
								adapter.notifyDataSetChanged();
							}
							else {
								SubsidenceCrossSectionInfo item = infos1.get(arg2);
								item.setbUse(!item.isbUse());
								infos1.set(arg2, item);
								adapter1.notifyDataSetChanged();
							}
						}
					});
//
    				setdata(itype);
    				if (itype == 1) {
        				adapter = new RecordTunnelCrossSectionInfoAdapter(RecordNewActivity.this, infos);
        				listView.setAdapter(adapter);
					}
    				else {
        				adapter1 = new RecordSubsidenceCrossSectionInfoAdapter(RecordNewActivity.this, infos1);
        				listView.setAdapter(adapter1);
					}
    		   }

                
                return (View) list.get(arg1);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public void restoreState(Parcelable arg0, ClassLoader arg1) {

            }

            @Override
            public Parcelable saveState() {
                return null;
            }

            @Override
            public void startUpdate(View arg0) {
                
            }
        };
        mPager.setAdapter(pa);
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(this);
        /** 隧道内断面 */
        Layout1();
        /** 地表下沉断面 */
        Layout2();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        int single = (int) (b.getWidth() + offSet * 2);
        TranslateAnimation ta = new TranslateAnimation(currIndex * single,
                single * arg0, 0, 0);
        ta.setFillAfter(true);
        ta.setDuration(200);
        cursor.startAnimation(ta);
        currIndex = arg0;
    }

    public OnClickListener tv_Listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int single = (int) (b.getWidth() + offSet * 2);
            switch (v.getId()) {
            case R.id.t1:
                mPager.setCurrentItem(0);
                if (currIndex != 0) {
                    TranslateAnimation ta = new TranslateAnimation(
                            (currIndex * single), 0, 0, 0);
                    ta.setFillAfter(true);
                    ta.setDuration(200);
                    cursor.startAnimation(ta);
                }
                currIndex = 0;
                break;
            case R.id.t2:
                mPager.setCurrentItem(1);
                if (currIndex != 1) {
                    TranslateAnimation ta = new TranslateAnimation(currIndex
                            * single, single, 0, 0);
                    ta.setFillAfter(true);
                    ta.setDuration(200);
                    cursor.startAnimation(ta);
                }
                currIndex = 1;
                break;
            default:
                break;
            }
        }
    };

    public void Layout1() {
        /** 隧道内断面界面的控件 */
        /**List集合中存儲的是View,获取界面上的控件,就List.get(0),0就是集合中第一个界面,1就是集合中第二个界面*/
    

    }

    public void Layout2() {
                    
    }
}
