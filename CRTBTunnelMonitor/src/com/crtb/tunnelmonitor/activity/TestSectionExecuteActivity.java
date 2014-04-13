package com.crtb.tunnelmonitor.activity;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;

import android.os.Bundle;

import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.entity.RecordInfo;
import com.crtb.tunnelmonitor.entity.RecordSubsidenceInfo;

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
		if(bean instanceof RecordInfo){
			setTopbarTitle(((RecordInfo)bean).getChainageName());
		} else if(bean instanceof RecordSubsidenceInfo){
			setTopbarTitle(((RecordSubsidenceInfo)bean).getChainageName());
		}
		
	}

}
