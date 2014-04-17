package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.AppPreferences;
import com.crtb.tunnelmonitor.entity.ProjectIndex;

/**
 * 工程信息DAO(工作面)
 * 
 * @author zhouwei
 *
 */
public final class ProjectIndexDao extends AbstractDao<ProjectIndex> {
	
	private static ProjectIndexDao _instance ;

	private ProjectIndexDao(){
		
	}
	
	public static ProjectIndexDao defaultWorkPlanDao(){
		
		if(_instance == null){
			_instance	= new ProjectIndexDao() ;
		}
		
		return _instance ;
	}
	
	public List<ProjectIndex> queryAllWorkPlan(){
		
		String sql = "select * from ProjectIndex" ;
		
		return mDatabase.queryObjects(sql, ProjectIndex.class);
	}
	
	public ProjectIndex queryEditWorkPlan(){
		
		String projectName = AppPreferences.getPreferences().getCurrentProject() ; 
		
		String sql 		= "select * from ProjectIndex where ProjectName = ?" ;
		String[] param 	= new String[]{projectName} ;
		
		return mDatabase.queryObject(sql,param , ProjectIndex.class);
	}
	
	public void updateCurrentWorkPlan(ProjectIndex bean){
		
		AppPreferences.getPreferences().putCurrentProject(bean.getProjectName());
		
		/*String sql 	= "update ProjectIndex set workPalnStatus = ?" ;
		String[] param 	= new String[]{String.valueOf(ProjectIndex.STATUS_IDLE)} ;
		
		executeSql(sql, param);
		
		update(bean);*/
	}
	
	public void resetAllWorkPlan(){
		
		List<ProjectIndex> list = queryAllWorkPlan();
		
		if(list == null) return ;
		
		for(ProjectIndex work : list){
			
			update(work) ;
		}
	}
	
	public boolean hasWorkPlan(){
		
		List<ProjectIndex> list = queryAllWorkPlan() ;
		
		return list != null && list.size() > 0 ;
	}
}
