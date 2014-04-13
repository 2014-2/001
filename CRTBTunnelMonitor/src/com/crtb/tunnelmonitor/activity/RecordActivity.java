package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.entity.MenuSystemItem;
import com.crtb.tunnelmonitor.widget.CrtbRecordSubsidenceListView;
import com.crtb.tunnelmonitor.widget.CrtbRecordTunnelSectionListView;

/**
 * 记录单管理
 * @author zhouwei
 *
 */
@InjectLayout(layout=R.layout.activity_record)
public class RecordActivity extends WorkFlowActivity implements OnPageChangeListener {
	
	@InjectView(id=R.id.vPager)
	private ViewPager mPager;
	
	ArrayList<View> list = new ArrayList<View>();
	
	// 隧道内断面距离单
	@InjectView(layout=R.layout.record_listview_dibiao)
	private LinearLayout mSectionRecordLayout ;
	
	@InjectView(layout=R.id.record_lv_dibiao,parent="mSectionRecordLayout")
	private CrtbRecordTunnelSectionListView	mTunnelSectionList ;
	
	// 地表下沉断面距离单
	@InjectView(layout=R.layout.record_listview_suidao)
	private LinearLayout mSectionSubsidenceRecordLayout ;
	
	@InjectView(layout=R.id.record_lv_dibiao,parent="mSectionSubsidenceRecordLayout")
	private CrtbRecordSubsidenceListView	mSubsidenceSectionList ;
	
	private ImageView cursor;// 动画图片
	private TextView t1, t2;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	int disPlayWidth, offSet;
	Bitmap b;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);

		// title
		setTopbarTitle(getString(R.string.record_title));
		
		// init ViewPager
		initPager();
		
		// menu
		loadSystemMenu();
	}
	
	public void loadSystemMenu(){
		
		List<MenuSystemItem> systems = new ArrayList<MenuSystemItem>();
		
		MenuSystemItem item = new MenuSystemItem() ;
		item.setIcon(R.drawable.ic_menu_create);
		item.setName(getString(R.string.common_create_new));
		systems.add(item);
		
		createSystemMenu(systems);
	}

	@Override
	protected void onSystemMenuClick(MenuSystemItem menu) {
		
		String name = menu.getName() ;
		
		if(name.equals(getString(R.string.common_create_new))){
			
			Intent intent = new Intent() ;
			
			if(mPager.getCurrentItem() == TAB_ONE){
				intent.setClass(RecordActivity.this, RecordNewActivity.class);
			} else {
				intent.setClass(RecordActivity.this, RecordNewSubsidenceActivity.class);
			}
			
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mTunnelSectionList.onReload() ;
		mSubsidenceSectionList.onReload();
	}

	public void initPager() {
		
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		cursor = (ImageView) findViewById(R.id.cursor);
		
		t1.setOnClickListener(tv_Listener);
		t2.setOnClickListener(tv_Listener);
		
		disPlayWidth = mDisplayMetrics.widthPixels ;
		b = BitmapFactory.decodeResource(this.getResources(), R.drawable.heng);
		offSet = ((disPlayWidth / 4) - b.getWidth() / 2);
		
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		ViewGroup.LayoutParams lp = cursor.getLayoutParams() ;
		lp.width = disPlayWidth / 2;
		lp.height = 4 ;
		cursor.setLayoutParams(lp);
		cursor.setImageMatrix(matrix);
		
		list.add(mSectionRecordLayout);
		list.add(mSectionSubsidenceRecordLayout);
		
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
		
		if(index == 0){
			mTunnelSectionList.onResume() ;
		} else {
			mSubsidenceSectionList.onResume();
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
			}
		}
	};

//	public void Layout1() {
//		/** 隧道内断面界面的控件 */
//		/** List集合中存儲的是View,获取界面上的控件,就List.get(0),0就是集合中第一个界面,1就是集合中第二个界面 */
//		listView = (ListView) list.get(0).findViewById(R.id.record_lv_dibiao);
//
//		adapter = new RecordAdapter(RecordActivity.this, infos);
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
//				new AlertDialog.Builder(RecordActivity.this)
//						.setItems(/*items*/Constant.RecordRowClickItems, new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
//								RecordInfo item = (RecordInfo)listView.getItemAtPosition(iListPos1);
//								switch (which) {
//								case 0:// 编辑
//									Intent intent = new Intent(RecordActivity.this,
//											RecordNewActivity.class);
//									Bundle mBundle = new Bundle();  
//									mBundle.putInt(Constant.Select_RecordRowClickItemsName_Name, 2);
//							        mBundle.putParcelable(Constant.Select_RecordRowClickItemsName_Data, item);
//							        intent.putExtras(mBundle);
//									RecordActivity.this.startActivityForResult(intent,0);
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
//
//	}

//	public void Layout2() {
//		/** 隧道内断面界面的控件 */
//		/** List集合中存儲的是View,获取界面上的控件,就List.get(0),0就是集合中第一个界面,1就是集合中第二个界面 */
//		listView1 = (ListView) list.get(1).findViewById(R.id.record_lv_suidao);
//
//		adapter1 = new RecordAdapter(RecordActivity.this, infos1);
//		listView1.setAdapter(adapter1);
//		// listview的行点击
//		listView1.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				iListPos2 = position;
//				new AlertDialog.Builder(RecordActivity.this)
//						.setItems(/*items*/Constant.RecordRowClickItems, new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								AppCRTBApplication CurApp = ((AppCRTBApplication)getApplicationContext());
//								RecordInfo item = (RecordInfo)listView1.getItemAtPosition(iListPos2);
//								switch (which) {
//								case 0:// 编辑
//									Intent intent = new Intent(RecordActivity.this,
//											RecordNewActivity.class);
//									Bundle mBundle = new Bundle();  
//									mBundle.putInt(Constant.Select_RecordRowClickItemsName_Name, 4);
//							        mBundle.putParcelable(Constant.Select_RecordRowClickItemsName_Data, item);
//							        intent.putExtras(mBundle);
//									RecordActivity.this.startActivityForResult(intent,0);
//									//startActivity(intent);
//									break;
//								case 1:// 删除
////									WorkInfos Curw = CurApp.GetCurWork();
////									SubsidenceCrossSectionDaoImpl impl = new SubsidenceCrossSectionDaoImpl(RecordActivity.this,Curw.getProjectName());
////									int iRet = impl.DeleteSubsidenceCrossSection(item.getId());
////									switch (iRet) {
////									case 0:
////										Toast.makeText(RecordActivity.this, "删除失败", 3000).show();
////										break;
////									case 1:
////										Curw.DelSubsidenceCrossSectionInfo(item);
////										CurApp.UpdateWork(Curw);
////										adapter1.notifyDataSetChanged();
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
//	}
}
