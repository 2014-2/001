/**
 * 
 */
package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.entity.MenuSystemItem;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogSearchTest;
import com.crtb.tunnelmonitor.widget.CrtbTestRecordSubsidenceListView;
import com.crtb.tunnelmonitor.widget.CrtbTestRecordTunnelSectionListView;

/**
 * 测量模块
 * 
 * @author zhouwei
 *
 */
@InjectLayout(layout=R.layout.activity_testrecord)
public class TestRecordActivity extends WorkFlowActivity implements OnPageChangeListener{
	
	@InjectView(id=R.id.vPager)
	private ViewPager mPager;
	
	ArrayList<View> list = new ArrayList<View>();
	
	@InjectView(id=R.id.cursor)
	private ImageView cursor;
	
	private TextView t1, t2;
	private int currIndex = 0;
	int disPlayWidth, offSet;
	Bitmap b;
	
	// 隧道内断面测量单
	@InjectView(layout=R.layout.testrecord_listdibiao)
	private LinearLayout mSectionRecordLayout ;
	
	@InjectView(id=R.id.test_record_tunnel_section_list,parent="mSectionRecordLayout")
	private CrtbTestRecordTunnelSectionListView mTestTunnelSectionList ;
	
	// 地表下沉测量单
	@InjectView(layout=R.layout.testrecord_subsidence_layout)
	private LinearLayout mSubsidenceRecordLayout ;
	
	@InjectView(id=R.id.test_record_subsidence_list,parent="mSubsidenceRecordLayout")
	private CrtbTestRecordSubsidenceListView mTestSubsidenceList ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);

		// title
		setTopbarTitle(getString(R.string.test_record_title));
		
		// init ViewPager
		initPager() ;
		
		// load system menu
		loadSystemMenu();
		
		// clear cache
		CommonObject.remove(TestSectionExecuteActivity.KEY_TEST_RAWSHEET_LIST);
	}
	
	private void loadSystemMenu(){
		
		List<MenuSystemItem> systems = new ArrayList<MenuSystemItem>();
		
		MenuSystemItem item = new MenuSystemItem() ;
		item.setIcon(R.drawable.ic_menu_open);
		item.setName(getString(R.string.common_open));
		systems.add(item);
		
		item = new MenuSystemItem() ;
		item.setIcon(R.drawable.ic_menu_search);
		item.setName(getString(R.string.common_search));
		systems.add(item);
		
		createSystemMenu(systems);
	}

	@Override
	protected void onSystemMenuClick(MenuSystemItem menu) {
		
		final String name = menu.getName() ;
		
		if(name.equals(getString(R.string.common_open))){
			
			if(currIndex == 0){
				
				List<RawSheetIndex> list = mTestTunnelSectionList.getSelectedSection() ;
				
				if(list.isEmpty()){
					showText("请选择测量单");
				} else {
					
					CommonObject.putObject(TestSectionExecuteActivity.KEY_TEST_RAWSHEET_LIST, list) ;
					
					Intent intent = new Intent() ;
					intent.setClass(TestRecordActivity.this, TestSectionExecuteActivity.class);
					startActivity(intent);
				}
			} else {
				
				List<RawSheetIndex> list = mTestSubsidenceList.getSelectedSection() ;
				
				if(list.isEmpty()){
					showText("请选择测量单");
				} else {
					
					CommonObject.putObject(TestSectionExecuteActivity.KEY_TEST_RAWSHEET_LIST, list) ;
					
					Intent intent = new Intent() ;
					intent.setClass(TestRecordActivity.this, TestSectionExecuteActivity.class);
					startActivity(intent);
				}
			}
		} else if(name.equals(getString(R.string.common_search))){
			
			CrtbDialogSearchTest dialog = null ;
			
			// 隧道内测量
			if(currIndex == 0){
				dialog = new CrtbDialogSearchTest(this,mDisplayMetrics.heightPixels >> 1,0);
			} 
			// 地表下沉测量
			else {
				dialog = new CrtbDialogSearchTest(this,mDisplayMetrics.heightPixels >> 1,1);
			}
			
			dialog.show() ;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mTestTunnelSectionList.onReload() ;
		mTestTunnelSectionList.onReload() ;
	}
	
	public void initPager() {
		
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		
		t1.setOnClickListener(tv_Listener);
		t2.setOnClickListener(tv_Listener);
		
		list.add(mSectionRecordLayout);
		list.add(mSubsidenceRecordLayout);
		
		disPlayWidth = mDisplayMetrics.widthPixels ;
		b = BitmapFactory.decodeResource(this.getResources(), R.drawable.heng);
		offSet = ((disPlayWidth / 4) - b.getWidth() / 2);
		
		ViewGroup.LayoutParams lp = cursor.getLayoutParams() ;
		lp.width = disPlayWidth >> 1 ;
		lp.height = 4 ;
		cursor.setLayoutParams(lp);
		
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
		mPager.setCurrentItem(TAB_ONE);
		mPager.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int index) {
		int single = (int) (b.getWidth() + offSet * 2);
		TranslateAnimation ta = new TranslateAnimation(currIndex * single,
				single * index, 0, 0);
		ta.setFillAfter(true);
		ta.setDuration(200);
		cursor.startAnimation(ta);
		currIndex = index;
		
		switchTestList(index);
	}
	
	private void switchTestList(int index){
		
		if(index == 0){
			mTestTunnelSectionList.onResume() ;
		} else if(index == 1){
			mTestSubsidenceList.onResume() ;
		}
	}

	public OnClickListener tv_Listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int single = (int) (b.getWidth() + offSet * 2);
			switch (v.getId()) {
			case R.id.text1:
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
			case R.id.text2:
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
}
