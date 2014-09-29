
package com.crtb.tunnelmonitor.activity;
import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

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
import android.widget.TextView;

import com.crtb.tunnelmonitor.BaseActivity;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionParameter;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogDelete;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogHint;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogDelete.IButtonOnClick;
import com.crtb.tunnelmonitor.widget.CrtbExcavationLayout;
import com.crtb.tunnelmonitor.widget.CrtbExcavationManagerLayout;
import com.crtb.tunnelmonitor.widget.CrtbExcavationManagerLayout.ExcavationClick;

/**
 * 自定义开挖方式
 * 
 * @author zhouwei
 *
 */
@InjectLayout(layout=R.layout.activity_custom_excavation_layout)
public class CustomExcavationActivity extends BaseActivity implements OnPageChangeListener{
	
	public static final int TAB_SECTION			= 0 ;
	public static final int TAB_SUBSIDENCE		= 1 ;
	
	@InjectView(id=R.id.vPager)
	private ViewPager mPager;
	
	@InjectView(id=R.id.cursor)
	private ImageView cursor;
	
	private ArrayList<View> list = new ArrayList<View>();
	
	private TextView tabLeft, tabRight;// 页卡头标
	private int currIndex = 0;// 当前页卡编号
	int disPlayWidth, offSet;
	Bitmap b;
	
	private CrtbExcavationLayout leftLayout ;
	private CrtbExcavationManagerLayout rightLayout ;
	private TunnelCrossSectionIndexDao sectionDao ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);
		
		// title
		setTopbarTitle("设置");
		
		leftLayout	= new CrtbExcavationLayout(this);
		rightLayout	= new CrtbExcavationManagerLayout(this);
		sectionDao	= TunnelCrossSectionIndexDao.defaultDao() ;
		
		loadViewPager();
		
		// 点击事件
		rightLayout.setExcavationClick(new ExcavationClick() {
			
			@Override
			public void onClick(TunnelCrossSectionParameter bean) {
				
				String[] menus = {getString(R.string.common_delete)} ;
				showListActionMenu("开挖方法管理", menus , bean);
			}
		}) ;
	}
	
	@Override
	protected void onListItemSelected(final Object bean, int position, String menu) {
		
		if(menu.equals(getString(R.string.common_delete))){
			
			final TunnelCrossSectionParameter item = (TunnelCrossSectionParameter)bean ;
			List<TunnelCrossSectionIndex> list = sectionDao.querySectionByExcavationMethod(item.getExcavateMethod());
			
			if(list != null && !list.isEmpty()){
				CrtbDialogHint hint = new CrtbDialogHint(this, R.drawable.ic_warnning, "该开挖方法正在使用,不可删除!");
				hint.show() ;
			} else {
				
				CrtbDialogDelete dialog = new CrtbDialogDelete(this, R.drawable.ic_warnning,"你确定要删除改自定义开挖方法?");
				dialog.setButtonClick(new IButtonOnClick() {
					
					@Override
					public void onClick(int id) {
						
						if(id == CrtbDialogDelete.BUTTON_ID_CONFIRM){
							rightLayout.removeExcavation(item);
						}
					}
				}) ;
				
				dialog.show() ;
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
				
				rightLayout.onResume() ;
				
				break;
			}
		}
	};
}
