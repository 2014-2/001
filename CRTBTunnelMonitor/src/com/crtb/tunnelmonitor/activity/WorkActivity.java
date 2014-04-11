package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectResource;
import org.zw.android.framework.ioc.InjectView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.dao.impl.v2.WorkPlanDao;
import com.crtb.tunnelmonitor.entity.MenuSystemItem;
import com.crtb.tunnelmonitor.entity.WorkPlan;
import com.crtb.tunnelmonitor.widget.CrtbItemPopMenu;
import com.crtb.tunnelmonitor.widget.CrtbItemPopMenu.IMenuOnclick;
import com.crtb.tunnelmonitor.widget.CrtbSystemMenu;
import com.crtb.tunnelmonitor.widget.CrtbSystemMenu.ISystemMenuOnclick;
import com.crtb.tunnelmonitor.widget.CrtbWorkPlanListView;

/**
 * work plan
 * 
 * @author zhouwei
 *
 */
@InjectLayout(layout=R.layout.activity_work)
public final class WorkActivity extends WorkFlowActivity {
	
	@InjectView(id=R.id.list_view_workplans)
	private CrtbWorkPlanListView mListView ;
	
	@InjectResource(stringArray=R.array.work_plan_item_menus)
	private String[] workpalnItemMenu ;
	
	// workplan item menu
	private CrtbItemPopMenu<WorkPlan>  workplanItemMenu ;
	
	// system menu
	private CrtbSystemMenu	systemMenu ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);

		// title
		setTopbarTitle(getString(R.string.work_plan_title));
		
		// load workplan item menu
		loadWorkPlanMenu();

		// load system menu
		loadSystemMenu();
		
		// load workplan list
		loadWorkPlanList();
		
	}
	
	private void loadWorkPlanMenu(){
		
		workplanItemMenu = new CrtbItemPopMenu<WorkPlan>(WorkActivity.this,
				mDisplayMetrics.widthPixels >> 1, 
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, workpalnItemMenu);
		
		workplanItemMenu.setMenuOnclick(new IMenuOnclick<WorkPlan>() {

			@Override
			public void onclick(String menu, WorkPlan bean) {

				if(menu.equals(getString(R.string.common_open))){
					
				} else if(menu.equals(getString(R.string.common_edit))){
					
				} else if(menu.equals(getString(R.string.common_delete))){
					
					if(WorkPlanDao.defaultWorkPlanDao().delete(bean)){
						mListView.onReload() ;
					}
				}
			}
		}) ;
	}
	
	private void loadSystemMenu(){
		
		List<MenuSystemItem> systems = new ArrayList<MenuSystemItem>();
		
		MenuSystemItem item = new MenuSystemItem() ;
		item.setIcon(R.drawable.ic_menu_create);
		item.setName(getString(R.string.common_create_new));
		systems.add(item);
		
		// 
		if(WorkPlanDao.defaultWorkPlanDao().hasWorkPlan()){
			item = new MenuSystemItem() ;
			item.setIcon(R.drawable.ic_menu_export);
			item.setName(getString(R.string.common_export));
			systems.add(item);
		}
		
		item = new MenuSystemItem() ;
		item.setIcon(R.drawable.ic_menu_inport);
		item.setName(getString(R.string.common_inport));
		systems.add(item);
		
		systemMenu	= new CrtbSystemMenu(this, mDisplayMetrics.widthPixels, 
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, systems);
		
		systemMenu.setMenuOnclick(new ISystemMenuOnclick() {
			
			@Override
			public void onclick(MenuSystemItem menu) {
				
				String name = menu.getName() ;
				
				if(name.equals(getString(R.string.common_create_new))){
					
					Intent intent = new Intent() ;
					intent.setClass(WorkActivity.this, WorkNewActivity.class);
					startActivity(intent);
					
				} else if(name.equals(getString(R.string.common_export))){
					
				} else if(name.equals(getString(R.string.common_inport))){
					
				}
			}
		}) ;
	}
	
	private void loadWorkPlanList(){
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
				workplanItemMenu.show(view,mDisplayMetrics.widthPixels >> 1, mListView.getItem(position));
				return false;
			}
		}) ;
		
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				workplanItemMenu.dismiss() ;
			}
		}) ;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		workplanItemMenu.onTouchEvent(event);
		systemMenu.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
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
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mListView.onResume() ;
		workplanItemMenu.dismiss() ;
		systemMenu.dismiss() ;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		workplanItemMenu.dismiss() ;
		systemMenu.dismiss() ;
	}
}
