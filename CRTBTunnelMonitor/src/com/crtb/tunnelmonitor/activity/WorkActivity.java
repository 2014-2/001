package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.ioc.InjectCore;
import org.zw.android.framework.ioc.InjectLayout;
import org.zw.android.framework.ioc.InjectView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.AppPreferences;
import com.crtb.tunnelmonitor.CommonObject;
import com.crtb.tunnelmonitor.WorkFlowActivity;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.entity.CrtbUser;
import com.crtb.tunnelmonitor.entity.MenuSystemItem;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogConnecting;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogDelete;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogDelete.IButtonOnClick;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogFileBrowse;
import com.crtb.tunnelmonitor.mydefine.CrtbDialogHint;
import com.crtb.tunnelmonitor.utils.CrtbDbFileUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
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
	
	// delete dialog
	private CrtbDialogHint 	mWarringDialog ;
	private CrtbDialogConnecting	mProgressDialog ;
	private String mProgressText ;
	
	BroadcastReceiver mReceiver ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// add by wei.zhou
		InjectCore.injectUIProperty(this);
		
		// title
		setTopbarTitle(getString(R.string.work_plan_title));
		
		// load workplan list
		loadWorkPlanList();

		// load system menu
		loadSystemMenu();
		
		mWarringDialog	= new CrtbDialogHint(WorkActivity.this, R.drawable.ic_warnning, "该工作面正在使用无法删除");
	}
	
	@Override
	protected void onListItemSelected(Object obj, int position, String menu) {
		
		if(obj == null || menu == null){
			return ;
		}
		
		final ProjectIndex bean = (ProjectIndex) obj ;
		
		if(menu.equals(getString(R.string.common_open))){
			
			// current edit workplan
			String error = ProjectIndexDao.defaultWorkPlanDao().openProjectIndex(bean.getProjectName());
			
			if(error != null){
				showText(error);
				return ;
			}
			
			CrtbUtils.updateNewSectionCodeNumber();
			CrtbUtils.updateWorkSiteInfo(bean);			
			finish() ;
			
		} else if(menu.equals(getString(R.string.common_edit))){
			
			CommonObject.putObject(WorkNewActivity.KEY_WORKPLAN_OBJECT,bean);
			Intent intent = new Intent() ;
			intent.setClass(WorkActivity.this, WorkNewActivity.class);
			startActivity(intent);
			
		} else if(menu.equals(getString(R.string.common_export))){
			
			mProgressText	= "正在导出文件,请稍等..." ;
			String dbname 	= bean.getProjectName();
			String path 	= CrtbDbFileUtils.getLocalDbPath(dbname);
			
			CrtbDbFileUtils.exportDb(path, dbname, mHanlder);
			
		}else if(menu.equals(getString(R.string.common_delete))){
			
			String name = AppPreferences.getPreferences().getCurrentProject() ;
			
			if(name != null && name.equals(bean.getProjectName())){
				 mWarringDialog.show() ;
			} else {
				
				CrtbDialogDelete delete = new CrtbDialogDelete(WorkActivity.this,R.drawable.ic_warnning,"执行该操作将删除操作面的全部数据,无法恢复!");
				
				delete.setButtonClick(new IButtonOnClick() {
					
					@Override
					public void onClick(int id) {
						
						if(id == CrtbDialogDelete.BUTTON_ID_CONFIRM){
							
							int code = ProjectIndexDao.defaultWorkPlanDao().delete(bean) ;
							
							ProjectIndexDao.defaultWorkPlanDao().removeProjectIndex(bean);
							
							if(code == ProjectIndexDao.DB_EXECUTE_SUCCESS){
								loadSystemMenu();
							}
						}
					}
				}) ;
				
				delete.show(); 
			}
		}
	}

	@Override
	protected AppHandler getHandler() {
		return new AppHandler(this){

			@Override
			protected void dispose(Message msg) {
				
				switch(msg.what){
				case MSG_TASK_START :
					
					if(mProgressDialog == null){
						mProgressDialog	= new CrtbDialogConnecting(WorkActivity.this) ;
					}
					
					if(!mProgressDialog.isShowing()){
						mProgressDialog.showDialog(mProgressText);
					}
					
					break ;
				case MSG_TASK_END :
					
					if(mProgressDialog != null){
						mProgressDialog.dismiss() ;
					}
					
					break ;
					
				case MSG_EXPORT_DB_SUCCESS :
					
					String str = msg.obj != null ? msg.obj.toString() : "数据库导出完成" ;
					
					CrtbDialogHint dialog = new CrtbDialogHint(WorkActivity.this, R.drawable.ic_reslut_sucess, str);
					dialog.show() ;
					break ;
				case MSG_EXPORT_DB_FAILED :
					showText("数据库导出错误!");
					break ;
				case MSG_INPORT_DB_SUCCESS :
					mListView.onReload() ;
					break ;
				}
			}
			
		};
	}
	
	private void loadSystemMenu(){
		
		List<MenuSystemItem> systems = new ArrayList<MenuSystemItem>();
		
		MenuSystemItem item = new MenuSystemItem() ;
		item.setIcon(R.drawable.ic_menu_create);
		item.setName(getString(R.string.common_create_new));
		systems.add(item);
		
		item = new MenuSystemItem() ;
		item.setIcon(R.drawable.ic_menu_inport);
		item.setName(getString(R.string.common_inport));
		systems.add(item);
		
		createSystemMenu(systems);
		
		// load data
		mListView.onReload() ;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// load data
		mListView.onReload() ;
	}
	
	@Override
	protected void onSystemMenuClick(MenuSystemItem menu) {
		
		String name = menu.getName() ;
		
		if(name.equals(getString(R.string.common_create_new))){
			
			CommonObject.remove(WorkNewActivity.KEY_WORKPLAN_OBJECT);
			
			Intent intent = new Intent() ;
			intent.setClass(this, WorkNewActivity.class);
			startActivity(intent);
		} else if(name.equals(getString(R.string.common_inport))){
			
			CrtbDialogFileBrowse browse = new CrtbDialogFileBrowse(WorkActivity.this, mDisplayMetrics.heightPixels >> 1,mHanlder);
			
			if(browse.hasIuputFiles()){
				browse.show() ;
			} else {
				showText("没有可导入的数据库文件");
			}
			
		}
	}

	private void loadWorkPlanList(){
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				ProjectIndex bean = mListView.getItem(position) ;
				
				String[] menus = null ;
				
				int type = AppCRTBApplication.getInstance().getCurUserType() ;
				
				if(type == CrtbUser.LICENSE_TYPE_DEFAULT 
						|| type == CrtbUser.LICENSE_TYPE_TRIAL){
					
					menus	= new String[]{getString(R.string.common_open),
											getString(R.string.common_edit),
											getString(R.string.common_delete)} ;
					
				} else if(type == CrtbUser.LICENSE_TYPE_REGISTERED){
					
					menus	= new String[]{getString(R.string.common_open),
							getString(R.string.common_edit),
							getString(R.string.common_export),
							getString(R.string.common_delete)} ;
				}
				
				if(menus == null){
					return ;
				}
				
				showListActionMenu(getString(R.string.work_plan_title), menus, bean);
			}
		}) ;
		
		mListView.setCacheColorHint(Color.TRANSPARENT);
	}
}
