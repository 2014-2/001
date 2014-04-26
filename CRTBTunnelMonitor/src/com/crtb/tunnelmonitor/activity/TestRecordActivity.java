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
		CommonObject.remove(TestSectionExecuteActivity.KEY_TEST_OBJECT);
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
				
				RawSheetIndex bean = mTestTunnelSectionList.getSelectedSection() ;
				
				if(bean == null){
					showText("请选择测量单");
				} else {
					
					CommonObject.putObject(TestSectionExecuteActivity.KEY_TEST_OBJECT, bean) ;
					CommonObject.putInteger(TestSectionExecuteActivity.KEY_TEST_SECTION_TYPE, RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL);
					
					Intent intent = new Intent() ;
					intent.setClass(TestRecordActivity.this, TestSectionExecuteActivity.class);
					startActivity(intent);
				}
			} else {
				
				RawSheetIndex bean = mTestSubsidenceList.getSelectedSection() ;
				
				if(bean == null){
					showText("请选择测量单");
				} else {
					
					CommonObject.putObject(TestSectionExecuteActivity.KEY_TEST_OBJECT, bean) ;
					CommonObject.putInteger(TestSectionExecuteActivity.KEY_TEST_SECTION_TYPE, RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES);
					
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

	public void Layout1() {
		/** 隧道内断面界面的控件 */
		/** List集合中存儲的是View,获取界面上的控件,就List.get(0),0就是集合中第一个界面,1就是集合中第二个界面 */
//		listView = (ListView) list.get(0).findViewById(R.id.record_lv_dibiao);

//		adapter = new TestRecordAdapter(TestRecordActivity.this, infos);
//		listView.setAdapter(adapter);
		// listview的行点击
//		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				iListPos1 = position;
//				// 对话框的选项
//				//CharSequence items[] = { "打开", "编辑", "导出", "删除" };
//				// 实例化对话
//				new AlertDialog.Builder(TestRecordActivity.this)
//						.setItems(/*items*/Constant.RecordRowClickItems, new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
//								RecordInfo item = (RecordInfo)listView.getItemAtPosition(iListPos1);
//								switch (which) {
//								case 0:// 编辑
//									Intent intent = new Intent(TestRecordActivity.this,
//											RecordNewActivity.class);
//									Bundle mBundle = new Bundle();  
//									mBundle.putInt(Constant.Select_RecordRowClickItemsName_Name, 2);
//							        mBundle.putParcelable(Constant.Select_RecordRowClickItemsName_Data, item);
//							        intent.putExtras(mBundle);
//							        TestRecordActivity.this.startActivityForResult(intent,0);
//									//startActivity(intent);
//									break;
//								case 1:// 删除
////									WorkInfos Curw = CurApp.GetCurWork();
////									TunnelCrossSectionDaoImpl impl = new TunnelCrossSectionDaoImpl(RecordActivity.this,Curw.getProjectName());
////									int iRet = impl.DeleteSection(item.getId());
////									switch (iRet) {
////									case 0:
////										Toast.makeText(RecordActivity.this, "删除失败", 3000).show();
////										break;
////									case 1:
////										Curw.DelTunnelCrossSectionInfo(item);
////										CurApp.UpdateWork(Curw);
////										adapter.notifyDataSetChanged();
////										Toast.makeText(RecordActivity.this, "删除成功", 3000).show();
////										break;
////									case -1:
////										Toast.makeText(RecordActivity.this, "删除的断面中存在数据,不可删除", 3000).show();
////										break;
////									default:
////										break;
////									}
//									break;
//								default:
//									break;
//								}
//
//							}
//						})
//						.setCancelable(false)
//						.show().setCanceledOnTouchOutside(true);// 显示对话框
//				return true;
//			}
//		});

	}

	public void Layout2() {
		/** 隧道内断面界面的控件 */
//		/** List集合中存儲的是View,获取界面上的控件,就List.get(0),0就是集合中第一个界面,1就是集合中第二个界面 */
//		listView1 = (ListView) list.get(1).findViewById(R.id.record_lv_dibiao);
//
//		adapter1 = new TestRecordAdapter(TestRecordActivity.this, infos1);
//		listView1.setAdapter(adapter1);
//		// listview的行点击
//		listView1.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				iListPos2 = position;
//				new AlertDialog.Builder(TestRecordActivity.this)
//						.setItems(/*items*/Constant.RecordRowClickItems, new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
//								RecordInfo item = (RecordInfo)listView1.getItemAtPosition(iListPos2);
//								switch (which) {
//								case 0:// 编辑
//									Intent intent = new Intent(TestRecordActivity.this,
//											RecordNewActivity.class);
//									Bundle mBundle = new Bundle();  
//									mBundle.putInt(Constant.Select_RecordRowClickItemsName_Name, 4);
//							        mBundle.putParcelable(Constant.Select_RecordRowClickItemsName_Data, item);
//							        intent.putExtras(mBundle);
//									TestRecordActivity.this.startActivityForResult(intent,0);
//									//startActivity(intent);
//									break;
//								case 1:// 删除
////									WorkInfos Curw = CurApp.GetCurWork();
////									SubsidenceCrossSectionDaoImpl impl = new SubsidenceCrossSectionDaoImpl(TestRecordActivity.this,Curw.getProjectName());
////									int iRet = impl.DeleteSubsidenceCrossSection(item.getId());
////									switch (iRet) {
////									case 0:
////										Toast.makeText(TestRecordActivity.this, "删除失败", 3000).show();
////										break;
////									case 1:
////										Curw.DelSubsidenceCrossSectionInfo(item);
////										CurApp.UpdateWork(Curw);
////										adapter1.notifyDataSetChanged();
////										Toast.makeText(TestRecordActivity.this, "删除成功", 3000).show();
////										break;
////									case -1:
////										Toast.makeText(TestRecordActivity.this, "删除的断面中存在数据,不可删除", 3000).show();
////										break;
////									default:
////										break;
////									}
//									break;
//								default:
//									break;
//								}
//
//							}
//						})
//						.setCancelable(false)
//						.show().setCanceledOnTouchOutside(true);// 显示对话框
//				return true;
//			}
//		});
	}
}
