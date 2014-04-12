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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.entity.MenuSystemItem;
import com.crtb.tunnelmonitor.widget.CrtbSystemMenu;
import com.crtb.tunnelmonitor.widget.CrtbSystemMenu.ISystemMenuOnclick;
import com.crtb.tunnelmonitor.widget.SectionSubsidenceListView;
import com.crtb.tunnelmonitor.widget.SectionTunnelListView;

/**
 * 
 * @author zhouwei
 *
 */
@InjectLayout(layout=R.layout.activity_section)
public class SectionActivity extends WorkFlowActivity implements OnPageChangeListener {
	
	public static final int TAB_SECTION			= 0 ;
	public static final int TAB_SUBSIDENCE		= 1 ;
	
	@InjectView(id=R.id.vPager)
	private ViewPager mPager;
	
	@InjectView(id=R.id.cursor)
	private ImageView cursor;
	
	private ArrayList<View> list = new ArrayList<View>();
	
	private TextView tabLeft, tabRight;// 页卡头标
	
	@InjectView(layout=R.layout.layout_3)
	private LinearLayout leftLayout ;
	
	@InjectView(id=R.id.listView4,parent="leftLayout")
	private SectionTunnelListView mSectionTunnelList ;
	
	@InjectView(layout=R.layout.layout_5)
	private LinearLayout rightLayout ;
	
	@InjectView(id=R.id.listView4,parent="rightLayout")
	private SectionSubsidenceListView mSectionSubsidenceList ;
	
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	int disPlayWidth, offSet;
	Bitmap b;
	
	// system menu
	private CrtbSystemMenu	systemMenu ;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);

		// title
		setTopbarTitle(getString(R.string.section_title));
		
		// view pager
		loadViewPager();
		
		// system menu
		loadSystemMenu();
	}
	
	private void loadViewPager(){
		
		tabLeft 	= (TextView) findViewById(R.id.text1);
		tabRight 	= (TextView) findViewById(R.id.text2);
		
		disPlayWidth = mDisplayMetrics.widthPixels ;
		b = BitmapFactory.decodeResource(this.getResources(), R.drawable.heng);
		offSet = ((disPlayWidth / 4) - b.getWidth() / 2);
		
		ViewGroup.LayoutParams lp = cursor.getLayoutParams() ;
		lp.width = disPlayWidth >> 1 ;
		lp.height = 4 ;
		cursor.setLayoutParams(lp);
		
		//
		list.add(leftLayout);
		list.add(rightLayout);
		
		tabLeft.setOnClickListener(new MyOnClickListener(TAB_SECTION));
		tabRight.setOnClickListener(new MyOnClickListener(TAB_SUBSIDENCE));
		
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
			public Object instantiateItem(View views, int index) {
				((ViewPager) views).addView((View) list.get(index));
				return (View) list.get(index);
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
		mPager.setCurrentItem(TAB_SECTION);
		mPager.setOnPageChangeListener(this);
	}
	
	public void loadSystemMenu(){
		
		List<MenuSystemItem> systems = new ArrayList<MenuSystemItem>();
		
		MenuSystemItem item = new MenuSystemItem() ;
		item.setIcon(R.drawable.ic_menu_create);
		item.setName(getString(R.string.common_create_new));
		systems.add(item);
		
		LinearLayout root = (LinearLayout) getLayoutInflater().inflate(R.layout.menu_system_container, null);
		
		systemMenu	= new CrtbSystemMenu(this,root, mDisplayMetrics.widthPixels, 
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, systems);
		
		systemMenu.setMenuOnclick(new ISystemMenuOnclick() {
			
			@Override
			public void onclick(MenuSystemItem menu) {
				
				String name = menu.getName() ;
				
				if(name.equals(getString(R.string.common_create_new))){
					
					Intent intent = new Intent() ;
					
					if(mPager.getCurrentItem() == TAB_SECTION){
						intent.setClass(SectionActivity.this, SectionNewActivity.class);
					} else {
						intent.setClass(SectionActivity.this, SectionNewSubsidenceActivity.class);
					}
					
					startActivity(intent);
				}
			}
		}) ;
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
	
	public class MyOnClickListener implements View.OnClickListener {
		
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

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
			}
		}
	};

//	public void Layout1() {
//		/** 隧道内断面界面的控件 */
//		/** List集合中存儲的是View,获取界面上的控件,就List.get(0),0就是集合中第一个界面,1就是集合中第二个界面 */
//		listView = (ListView) list.get(0).findViewById(R.id.listView4);
//
//		setdata1();
//		adapter = new TunnelCrossSectionInfoAdapter(SectionActivity.this, infos);
//		listView.setAdapter(adapter);
//		// listview的行点击
//		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				iListPos1 = position;
//				// 对话框的选项
//				//CharSequence items[] = { "打开", "编辑", "导出", "删除" };
//				// 实例化对话
//				new AlertDialog.Builder(SectionActivity.this)
//						.setItems(/*items*/Constant.SectionRowClickItems, new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
//								TunnelCrossSectionInfo item = (TunnelCrossSectionInfo)listView.getItemAtPosition(iListPos1);
//								switch (which) {
//								case 0:// 编辑 
//									intent = new Intent(SectionActivity.this,
//											SectionNewActivity.class);
//									intent.putExtra(Constant.Select_SectionRowClickItemsName_Name,
//											Double.toString(item.getChainage()));
//									SectionActivity.this.startActivityForResult(intent,0);
//									//startActivity(intent);
//									break;
//								case 1:// 删除
//									WorkInfos Curw = CurApp.GetCurWork();
//									TunnelCrossSectionDaoImpl impl = new TunnelCrossSectionDaoImpl(SectionActivity.this,Curw.getProjectName());
//									int iRet = impl.DeleteSection(item.getId());
//									switch (iRet) {
//									case 0:
//										Toast.makeText(SectionActivity.this, "删除失败", 3000).show();
//										break;
//									case 1:
//										Curw.DelTunnelCrossSectionInfo(item);
//										CurApp.UpdateWork(Curw);
//										adapter.notifyDataSetChanged();
//										Toast.makeText(SectionActivity.this, "删除成功", 3000).show();
//										break;
//									case -1:
//										Toast.makeText(SectionActivity.this, "删除的断面中存在数据,不可删除", 3000).show();
//										break;
//									default:
//										break;
//									}
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
//	}
//
//	public void Layout2() {
//		/** 隧道内断面界面的控件 */
//		/** List集合中存儲的是View,获取界面上的控件,就List.get(1),0就是集合中第一个界面,1就是集合中第二个界面 */
//		listView1 = (ListView) list.get(1).findViewById(R.id.listView4);
//
//		setdata2();
//		adapter1 = new SubsidenceCrossSectionInfoAdapter(SectionActivity.this, infos1);
//		listView1.setAdapter(adapter1);
//		// listview的行点击
//		listView1.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				iListPos2 = position;
//				new AlertDialog.Builder(SectionActivity.this)
//						.setItems(/*items*/Constant.SectionRowClickItems, new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
//								SubsidenceCrossSectionInfo item = (SubsidenceCrossSectionInfo)listView1.getItemAtPosition(iListPos2);
//								switch (which) {
//								case 0:// 编辑
//									intent = new Intent(SectionActivity.this,
//											SectionEditActivity.class);
//									intent.putExtra(Constant.Select_SectionRowClickItemsName_Name,
//											Double.toString(item.getChainage()));
//									SectionActivity.this.startActivityForResult(intent,0);
//									//startActivity(intent);
//									break;
//								case 1:// 删除
//									WorkInfos Curw = CurApp.GetCurWork();
//									SubsidenceCrossSectionDaoImpl impl = new SubsidenceCrossSectionDaoImpl(SectionActivity.this,Curw.getProjectName());
//									int iRet = impl.DeleteSubsidenceCrossSection(item.getId());
//									switch (iRet) {
//									case 0:
//										Toast.makeText(SectionActivity.this, "删除失败", 3000).show();
//										break;
//									case 1:
//										Curw.DelSubsidenceCrossSectionInfo(item);
//										CurApp.UpdateWork(Curw);
//										adapter1.notifyDataSetChanged();
//										Toast.makeText(SectionActivity.this, "删除成功", 3000).show();
//										break;
//									case -1:
//										Toast.makeText(SectionActivity.this, "删除的断面中存在数据,不可删除", 3000).show();
//										break;
//									default:
//										break;
//									}
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
//	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			
			if (keyCode == KeyEvent.KEYCODE_MENU) {
				systemMenu.show();
				return true ;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}

}

