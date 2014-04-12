package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectResource;
import org.zw.android.framework.ioc.InjectView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.WorkPlanDao;
import com.crtb.tunnelmonitor.entity.MenuSystemItem;
import com.crtb.tunnelmonitor.entity.WorkPlan;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogDelete;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogDelete.IButtonOnClick;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogHint;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogList;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogList.OnMenuItemClick;
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
	
	// list item menu
	private CrtbDialogList<WorkPlan>  itemDialog ;
	
	// delete dialog
	private CrtbDialogHint 	mWarringDialog ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);
		
		// title
		setTopbarTitle(getString(R.string.work_plan_title));
		
		// load workplan item menu
		loadWorkPlanMenu();
		
		// load workplan list
		loadWorkPlanList();

		// load system menu
		loadSystemMenu();
		
		mWarringDialog	= new CrtbDialogHint(WorkActivity.this, R.drawable.ic_warnning, "该工作面正在使用无法删除");
	}
	
	private void loadWorkPlanMenu(){
		
		itemDialog = new CrtbDialogList<WorkPlan>(this, workpalnItemMenu, getString(R.string.work_plan_title));
		itemDialog.setMenuItemClick(new OnMenuItemClick<WorkPlan>() {
			
			@Override
			public void onItemClick(final WorkPlan bean, int position, String menu) {
				
				if(position == 0){
					
					WorkPlanDao.defaultWorkPlanDao().updateCurrentWorkPlan(bean) ;
					
					// start new MainActivity
					Intent intent = new Intent() ;
					intent.setClass(WorkActivity.this, MainActivity.class);
					intent.putExtra(Constant.LOGIN_TYPE, Constant.LOCAL_USER);
					startActivity(intent);
					
				} else if(position == 1){
					
					CommonObject.putObject(WorkNewActivity.KEY_WORKPLAN_OBJECT,bean);
					Intent intent = new Intent() ;
					intent.setClass(WorkActivity.this, WorkNewActivity.class);
					startActivity(intent);
					
				} else if(position == 2){
					
					if(bean.getWorkPalnStatus() == WorkPlan.STATUS_EDIT){
						 mWarringDialog.show() ;
					} else {
						
						CrtbDialogDelete delete = new CrtbDialogDelete(WorkActivity.this,R.drawable.ic_warnning,"执行该操作将删除操作面的全部数据,无法恢复!");
						
						delete.setButtonClick(new IButtonOnClick() {
							
							@Override
							public void onClick(int id) {
								
								if(id == CrtbDialogDelete.BUTTON_ID_CONFIRM){
									
									if(WorkPlanDao.defaultWorkPlanDao().delete(bean)){
										loadSystemMenu();
									}
								}
							}
						}) ;
						
						delete.show(); 
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
		
		createSystemMenu(systems);
		
		// load data
		mListView.onReload() ;
	}
	
	@Override
	protected void onSystemMenuClick(MenuSystemItem menu) {
		
		String name = menu.getName() ;
		
		if(name.equals(getString(R.string.common_create_new))){
			
			Intent intent = new Intent() ;
			intent.setClass(this, WorkNewActivity.class);
			startActivity(intent);
		}
	}

	private void loadWorkPlanList(){
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				itemDialog.showDialog(mListView.getItem(position)) ;
			}
		}) ;
		
		mListView.setCacheColorHint(Color.TRANSPARENT);
	}
}
