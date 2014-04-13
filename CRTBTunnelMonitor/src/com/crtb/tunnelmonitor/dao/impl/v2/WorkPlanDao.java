package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.WorkPlan;

/**
 * WorkPlan Dao
 * 
 * @author zhouwei
 *
 */
public final class WorkPlanDao extends AbstractDao<WorkPlan> {
	
	private static WorkPlanDao _instance ;

	private WorkPlanDao(){
		
	}
	
	public static WorkPlanDao defaultWorkPlanDao(){
		
		if(_instance == null){
			_instance	= new WorkPlanDao() ;
		}
		
		return _instance ;
	}
	
	public List<WorkPlan> queryAllWorkPlan(){
		
		String sql = "select * from WorkPlan" ;
		
		return mDatabase.queryObjects(sql, WorkPlan.class);
	}
	
	public WorkPlan queryEditWorkPlan(){
		
		String sql 		= "select * from WorkPlan where workPalnStatus = ?" ;
		String[] param 	= new String[]{String.valueOf(WorkPlan.STATUS_EDIT)} ;
		
		return mDatabase.queryObject(sql,param , WorkPlan.class);
	}
	
	public void updateCurrentWorkPlan(WorkPlan bean){
		
		String sql 	= "update WorkPlan set workPalnStatus = ?" ;
		String[] param 	= new String[]{String.valueOf(WorkPlan.STATUS_IDLE)} ;
		
		executeSql(sql, param);
		
		bean.setWorkPalnStatus(WorkPlan.STATUS_EDIT);
		update(bean);
	}
	
	public void resetAllWorkPlan(){
		
		List<WorkPlan> list = queryAllWorkPlan();
		
		if(list == null) return ;
		
		for(WorkPlan work : list){
			
			work.setWorkPalnStatus(WorkPlan.STATUS_IDLE);
			
			update(work) ;
		}
	}
	
	public boolean hasWorkPlan(){
		
		List<WorkPlan> list = queryAllWorkPlan() ;
		
		return list != null && list.size() > 0 ;
	}
}
