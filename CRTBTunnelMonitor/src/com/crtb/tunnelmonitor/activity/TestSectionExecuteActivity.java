package com.crtb.tunnelmonitor.activity;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tssurveyprovider.Coordinate3D;
import com.crtb.tssurveyprovider.ISurveyProvider;
import com.crtb.tssurveyprovider.TSSurveyProvider;
import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.utils.Time;

/**
 * 开始测量
 * 
 * @author zhouwei
 *
 */
@InjectLayout(layout=R.layout.activity_testrecord_execute)
public class TestSectionExecuteActivity extends WorkFlowActivity {
	
	public static final String KEY_TEST_OBJECT	= "_key_test_object" ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);
		
		Object bean = CommonObject.findObject(KEY_TEST_OBJECT);
		
		// title
//		if(bean instanceof RawSheetIndex){
//			setTopbarTitle(((RawSheetIndex)bean).getSectionName());
//		} else if(bean instanceof SubsidenceTotalData){
//			setTopbarTitle(((SubsidenceTotalData)bean).getSectionName());
//		}
		
		// 测量按钮事件
		findViewById(R.id.bnt_test_a).setOnClickListener(mMeasListener);
		findViewById(R.id.bnt_test_s1_1).setOnClickListener(mMeasListener);
		findViewById(R.id.bnt_test_s1_2).setOnClickListener(mMeasListener);
	}

	 private OnClickListener mMeasListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 从全站仪得到数据
				ISurveyProvider ts = TSSurveyProvider.getDefaultAdapter();
				if (ts == null)
				{
					Toast.makeText(TestSectionExecuteActivity.this, "请先连接全站仪", Toast.LENGTH_SHORT).show();
					return;
				}
  			
        Coordinate3D point = new Coordinate3D(null);
        try {
        	int nret = ts.GetCoord(0, 0, point);
        	if (nret != 1) 
        	{
        		Toast.makeText(TestSectionExecuteActivity.this, "测量失败", Toast.LENGTH_SHORT).show();
        		return;
        	}
        	//String text = String.format("%1$s,%2$s,%3$s", point.N,point.E,point.H);
        	//tv.setText(text);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
        
        String text = "";
        
				Button btn = (Button) v;
				switch (btn.getId()) {
				case R.id.bnt_test_a:
					text = String.format("%1$.4f", point.N); // x
					((TextView)findViewById(R.id.test_a_x)).setText(text);
					
					text = String.format("%1$.4f", point.E); // y
					((TextView)findViewById(R.id.test_a_y)).setText(text);
					
					text = String.format("%1$.4f", point.H); // z
					((TextView)findViewById(R.id.test_a_z)).setText(text);
					
					((TextView)findViewById(R.id.test_a_time)).setText(Time.getDateEN()); // time
					break;
				case R.id.bnt_test_s1_1:
					text = String.format("%1$.4f", point.N); // x
					((TextView)findViewById(R.id.test_s1_1_x)).setText(text);
					
					text = String.format("%1$.4f", point.E); // y
					((TextView)findViewById(R.id.test_s1_1_y)).setText(text);
					
					text = String.format("%1$.4f", point.H); // z
					((TextView)findViewById(R.id.test_s1_1_z)).setText(text);
					
					((TextView)findViewById(R.id.test_s1_1_time)).setText(Time.getDateEN()); // time
					break;
				case R.id.bnt_test_s1_2:
					text = String.format("%1$.4f", point.N); // x
					((TextView)findViewById(R.id.test_s1_2_x)).setText(text);
					
					text = String.format("%1$.4f", point.E); // y
					((TextView)findViewById(R.id.test_s1_2_y)).setText(text);
					
					text = String.format("%1$.4f", point.H); // z
					((TextView)findViewById(R.id.test_s1_2_z)).setText(text);
					
					((TextView)findViewById(R.id.test_s1_2_time)).setText(Time.getDateEN()); // time
					break;

				default:
					break;
				}
			}
		};
		
}
