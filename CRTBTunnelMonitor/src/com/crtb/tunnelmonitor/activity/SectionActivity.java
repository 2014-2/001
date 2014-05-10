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
import android.util.Log;
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
import com.crtb.tunnelmonitor.dao.impl.v2.AbstractDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.MenuSystemItem;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogHint;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogResult;
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
	
	private int currIndex = 0;// 当前页卡编号
	int disPlayWidth, offSet;
	Bitmap b;
	
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
		
		mSectionTunnelList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				TunnelCrossSectionIndex bean = mSectionTunnelList.getItem(position);
				
				showListActionMenu("断面管理", new String[]{"编辑","删除"}, bean);
			}
		}) ;
		
		mSectionSubsidenceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				SubsidenceCrossSectionIndex bean = mSectionSubsidenceList.getItem(position);
				
				showListActionMenu("断面管理", new String[]{"编辑","删除"}, bean);
			}
		}) ;
		
		// clear
		CommonObject.remove(SectionNewActivity.KEY_NEW_TUNNEL_SECTION_OBJECT);
		CommonObject.remove(SectionNewSubsidenceActivity.KEY_NEW_SUBSIDENCE_SECTION_OBJECT);
	}
	
	@Override
	protected void onListItemSelected(Object bean, int position, String menu) {
		
		if(bean instanceof TunnelCrossSectionIndex){
			
			TunnelCrossSectionIndex section = (TunnelCrossSectionIndex)bean ;
			
			if(position == 0){
				
				CommonObject.putObject(SectionNewActivity.KEY_NEW_TUNNEL_SECTION_OBJECT, section);
				
				Intent intent = new Intent() ;
				intent.setClass(SectionActivity.this, SectionNewActivity.class);
				startActivity(intent);
				
			} else if(position == 1){
				
				int code = TunnelCrossSectionIndexDao.defaultDao().delete(section);
				TunnelCrossSectionExIndex sectionExIndex = TunnelCrossSectionExIndexDao.defaultDao().querySectionById(section.getID());
				if (sectionExIndex != null) {
					int result =TunnelCrossSectionExIndexDao.defaultDao().delete(sectionExIndex);
					if (result != AbstractDao.DB_EXECUTE_SUCCESS) {
						Log.e("SectionActivity", "delete TunnelCrossSectionExIndex failed!");
					}
				}
				
				CrtbDialogResult dialog = null ;
				
				if(code == TunnelCrossSectionIndexDao.DB_EXECUTE_SUCCESS){
					dialog = CrtbDialogResult.createDeleteSuccessDialog(SectionActivity.this);
					mSectionTunnelList.onReload() ;
				} else {
					dialog = CrtbDialogResult.createDeleteFailedDialog(SectionActivity.this);
				}
				
				if(dialog != null){
					dialog.show() ;
				}
			}
		} else if(bean instanceof SubsidenceCrossSectionIndex){
			
			SubsidenceCrossSectionIndex section = (SubsidenceCrossSectionIndex)bean ;
			SubsidenceCrossSectionExIndex sectionExIndex = SubsidenceCrossSectionExIndexDao.defaultDao().querySectionById(section.getID());
			if (sectionExIndex != null) {
				int result = SubsidenceCrossSectionExIndexDao.defaultDao().delete(sectionExIndex);
				if (result != AbstractDao.DB_EXECUTE_SUCCESS) {
					Log.e("SectionActivity", "delete SubsidenceCrossSectionExIndex failed!");
				}
			}
			
			if(position == 0){
				
				CommonObject.putObject(SectionNewSubsidenceActivity.KEY_NEW_SUBSIDENCE_SECTION_OBJECT, section);
				
				Intent intent = new Intent() ;
				intent.setClass(SectionActivity.this, SectionNewSubsidenceActivity.class);
				startActivity(intent);
				
			} else if(position == 1){
				
				if(section.isHasTestData()){
					CrtbDialogHint hint = new CrtbDialogHint(SectionActivity.this, R.drawable.ic_warnning, "该断面存在测量数据,不可删除!");
					hint.show() ;
				} else{
					
					int code = SubsidenceCrossSectionIndexDao.defaultDao().delete(section);
					
					CrtbDialogResult dialog = null ;
					
					if(code == SubsidenceCrossSectionIndexDao.DB_EXECUTE_SUCCESS){
						dialog = new CrtbDialogResult(SectionActivity.this, R.drawable.ic_reslut_sucess, "删除成功");
						mSectionSubsidenceList.onReload() ;
					} else {
						dialog = new CrtbDialogResult(SectionActivity.this, R.drawable.ic_reslut_error, "删除失败");
					}
					
					if(dialog != null){
						dialog.show() ;
					}
				}
			}
		}
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
		
		createSystemMenu(systems);
	}
	
	@Override
	protected void onSystemMenuClick(MenuSystemItem menu) {
		
		String name = menu.getName();

		if (name.equals(getString(R.string.common_create_new))) {
			
			CommonObject.remove(SectionNewActivity.KEY_NEW_TUNNEL_SECTION_OBJECT);
			CommonObject.remove(SectionNewSubsidenceActivity.KEY_NEW_SUBSIDENCE_SECTION_OBJECT);

			Intent intent = new Intent();

			if (mPager.getCurrentItem() == TAB_SECTION) {
				intent.setClass(SectionActivity.this, SectionNewActivity.class);
			} else {
				intent.setClass(SectionActivity.this,SectionNewSubsidenceActivity.class);
			}

			startActivity(intent);
		}
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
				
				mSectionTunnelList.onResume() ;
				
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
				
				mSectionSubsidenceList.onResume() ;
				
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		
		mSectionTunnelList.onReload() ;
		mSectionSubsidenceList.onReload() ;
	}
}

