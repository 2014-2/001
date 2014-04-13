package com.crtb.tunnelmonitor;

import java.util.List;

import org.zw.android.framework.IFrameworkFacade;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.MenuSystemItem;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogList;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogList.OnMenuItemClick;
import com.crtb.tunnelmonitor.widget.CrtbSystemMenu;
import com.crtb.tunnelmonitor.widget.CrtbSystemMenu.ISystemMenuOnclick;


/**
 * Base Activity
 * 
 * @author zhouwei
 *
 */
public abstract class BaseActivity extends Activity {
	
	protected static final int TAB_ONE			= 0 ;
	protected static final int TAB_TWO			= 1 ;
	protected static final int TAB_THREE		= 2 ;
	
	public static final String KEY_CURRENT_WORKPLAN 	= "_key_current_workplan" ;

	// base handler
	protected AppHandler 			mHanlder ;
	
	// Framework
	protected IFrameworkFacade 		mFramework ;
	
	// topbar title
	private TextView				mTopbarTitle ;
	
	// window display
	protected DisplayMetrics 		mDisplayMetrics ;
	
	// system menu
	private CrtbSystemMenu			mSystemMenu ;
	
	// list item dialog
	@SuppressWarnings("rawtypes")
	private CrtbDialogList			mListActionMenu ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mFramework	= ((AppCRTBApplication)getApplication()).getFrameworkFacade();
		mHanlder 	= getHandler() ;
		
		mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
	}
	
	protected AppHandler getHandler(){
		return new AppHandler(this) ;
	}
	
	protected void showText(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show() ;
	}
	
	/**
	 * set topbar title
	 * 
	 * @param title
	 */
	protected void setTopbarTitle(String title){
		
		if(mTopbarTitle == null){
			mTopbarTitle = (TextView)findViewById(R.id.tv_topbar_title) ;
		}
		
		mTopbarTitle.setText(title);
	}
	
	/**
	 * create system menu
	 */
	protected final void createSystemMenu(List<MenuSystemItem> menus){
		
		if(menus == null || menus.isEmpty()){
			return ;
		}
		
		if(mSystemMenu != null){
			return ;
		}
		
		LinearLayout root = (LinearLayout) getLayoutInflater().inflate(R.layout.menu_system_container, null);
		
		mSystemMenu	= new CrtbSystemMenu(this,root, mDisplayMetrics.widthPixels, 
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, menus);
		
		mSystemMenu.setMenuOnclick(new ISystemMenuOnclick() {
			
			@Override
			public void onclick(MenuSystemItem menu) {
				onSystemMenuClick(menu);
			}
		}) ;
	}
	
	/**
	 * please Override the method
	 * 
	 * @param menu
	 */
	protected void onSystemMenuClick(MenuSystemItem menu){
		
	}
	
	/**
	 * clear list action menu
	 */
	protected final void clearListActionMenu(){
		mListActionMenu	= null ;
	}
	
	/**
	 * Show List Action menu
	 * 
	 * @param title
	 * @param menus
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	protected final void showListActionMenu(String title,String[] menus,Object bean){
		
		if(mListActionMenu == null){
			
			mListActionMenu = new CrtbDialogList<Object>(this, menus, title);
			mListActionMenu.setMenuItemClick(new OnMenuItemClick<Object>() {
				
				@Override
				public void onItemClick(Object bean, int position, String menu) {
					onListItemSelected(bean,position,menu);
				}
			}) ;
		}
		
		//
		mListActionMenu.showDialog(bean);
	}
	
	protected void onListItemSelected(Object bean, int position, String menu){
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(mSystemMenu != null){
			mSystemMenu.onTouchEvent(event);
		}
		
		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(mSystemMenu != null){
			mSystemMenu.dismiss() ;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(mSystemMenu != null){
			mSystemMenu.dismiss() ;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			
			if (keyCode == KeyEvent.KEYCODE_MENU) {
				if(mSystemMenu != null){
					mSystemMenu.show();
				}
				return true ;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
