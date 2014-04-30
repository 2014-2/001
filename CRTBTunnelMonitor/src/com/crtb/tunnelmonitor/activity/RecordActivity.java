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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.MenuSystemItem;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogDelete;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogDelete.IButtonOnClick;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogHint;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogResult;
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
	@InjectView(layout = R.layout.record_listview_suidao)
	private LinearLayout mSectionRecordLayout;

	@InjectView(id=R.id.record_lv_tunnnel_section_list,parent="mSectionRecordLayout")
	private CrtbRecordTunnelSectionListView	mTunnelSectionList ;
	
	// 地表下沉断面距离单
	@InjectView(layout = R.layout.record_listview_dibiao)
	private LinearLayout mSectionSubsidenceRecordLayout;
	
	@InjectView(id = R.id.record_lv_subsidence_list, parent = "mSectionSubsidenceRecordLayout")
	private CrtbRecordSubsidenceListView mSubsidenceSectionList;
	
	private ImageView cursor;// 动画图片
	private TextView t1, t2;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
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
		
		mTunnelSectionList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				RawSheetIndex bean = mTunnelSectionList.getItem(position) ;
				
				showListActionMenu(getString(R.string.work_plan_title), new String[]{"打开","编辑","删除"}, bean);
			}
		}) ;

		mSubsidenceSectionList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				RawSheetIndex bean = mSubsidenceSectionList.getItem(position) ;
				
				showListActionMenu(getString(R.string.work_plan_title), new String[]{"打开","编辑","删除"}, bean);
			}
		}) ;
		
		// clear object
		CommonObject.remove(RecordNewActivity.KEY_RECORD_TUNNEL_OBJECT);
		CommonObject.remove(RecordNewSubsidenceActivity.KEY_RECORD_SUBSIDENCE_OBJECT);
		CommonObject.remove(TestSectionExecuteActivity.KEY_TEST_RAWSHEET_LIST);
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
			
			CommonObject.remove(RecordNewActivity.KEY_RECORD_TUNNEL_OBJECT);
			CommonObject.remove(RecordNewSubsidenceActivity.KEY_RECORD_SUBSIDENCE_OBJECT);
			
			if(mPager.getCurrentItem() == TAB_ONE){
				intent.setClass(RecordActivity.this, RecordNewActivity.class);
			} else {
				intent.setClass(RecordActivity.this, RecordNewSubsidenceActivity.class);
			}
			
			startActivity(intent);
		}
	}

	@Override
	protected void onListItemSelected(Object bean, int position, String menu) {
		
		if(bean instanceof RawSheetIndex){
			
			final RawSheetIndex info = (RawSheetIndex) bean ;
			
			// 打开---> 测量
			if(position == 0){
				
				List<RawSheetIndex> list = new ArrayList<RawSheetIndex>();
				list.add(info);
				
				CommonObject.putObject(TestSectionExecuteActivity.KEY_TEST_RAWSHEET_LIST, list) ;
				
				Intent intent = new Intent() ;
				intent.setClass(this, TestSectionExecuteActivity.class);
				startActivity(intent);
			} 
			// 编辑
			else if(position == 1){
				
				Intent intent = new Intent() ;
				
				// 隧道内断面
				if(info.getCrossSectionType() == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
					
					CommonObject.putObject(RecordNewActivity.KEY_RECORD_TUNNEL_OBJECT, info);
					intent.setClass(RecordActivity.this, RecordNewActivity.class);
					startActivity(intent);
					
				} 
				// 地表下沉
				else if(info.getCrossSectionType() == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
					
					CommonObject.putObject(RecordNewSubsidenceActivity.KEY_RECORD_SUBSIDENCE_OBJECT, info);
					
					intent.setClass(RecordActivity.this, RecordNewSubsidenceActivity.class);
					startActivity(intent);
				}
				
			} 
			// 删除
			else if(position == 2){
				
				// 隧道内
				if(info.getCrossSectionType() == RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL){
					
					// 是否最后一条数据
					if(mTunnelSectionList.isLastRawSheetIndex(info)){
						
						CrtbDialogDelete delete = new CrtbDialogDelete(RecordActivity.this,R.drawable.ic_warnning,"执行该操作将删除操作面的全部数据,不可恢复!");
						
						delete.setButtonClick(new IButtonOnClick() {
							
							@Override
							public void onClick(int id) {
								
								if(id == CrtbDialogDelete.BUTTON_ID_CONFIRM){
									
									int code = RawSheetIndexDao.defaultDao().delete(info) ;
									
									if(code == RawSheetIndexDao.DB_EXECUTE_SUCCESS){
										CrtbDialogResult.createDeleteSuccessDialog(RecordActivity.this).show();
										mTunnelSectionList.onReload() ;
									}
								}
							}
						}) ;
						
						delete.show(); 
						
					} else {
						
						CrtbDialogHint hint = new CrtbDialogHint(RecordActivity.this,R.drawable.ic_warnning, "你不能删除以往的数据");
						hint.show() ;
					}
				}
				// 地表下沉
				else if(info.getCrossSectionType() == RawSheetIndex.CROSS_SECTION_TYPE_SUBSIDENCES){
					
					// 是否最后一条数据
					if(mSubsidenceSectionList.isLastRawSheetIndex(info)){
						
						CrtbDialogDelete delete = new CrtbDialogDelete(RecordActivity.this,R.drawable.ic_warnning,"执行该操作将删除操作面的全部数据,不可恢复!");
						
						delete.setButtonClick(new IButtonOnClick() {
							
							@Override
							public void onClick(int id) {
								
								if(id == CrtbDialogDelete.BUTTON_ID_CONFIRM){
									
									int code = RawSheetIndexDao.defaultDao().delete(info) ;
									
									if(code == RawSheetIndexDao.DB_EXECUTE_SUCCESS){
										CrtbDialogResult.createDeleteSuccessDialog(RecordActivity.this).show();
										mSubsidenceSectionList.onReload() ;
									}
								}
							}
						}) ;
						
						delete.show(); 
						
					} else {
						CrtbDialogHint hint = new CrtbDialogHint(RecordActivity.this,R.drawable.ic_warnning, "你不能删除以往的数据");
						hint.show() ;
					}
				}
			}
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
}
