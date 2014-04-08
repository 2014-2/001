package com.crtb.tunnelmonitor.dao;

import java.util.List;

import com.crtb.tunnelmonitor.entity.WorkInfos;
import com.crtb.tunnelmonitor.entity.list_infos;
/**
 * 工作面的接口
 *创建时间：2014-3-21下午9:23:23
 *@author 张涛
 *@since JDK1.6
 *@version 1.0
 */
public interface WorkDao {

	/**查询全部工作面*/
	public List<list_infos> SelectWork();
	public void GetWorkList(List<WorkInfos> lt);
	/**查询全部工作面信息*/
	public WorkInfos SelectWorkMsg();
	/**新建工作*/
	public Boolean InsertWork(WorkInfos W);
	/**删除工作*/
	public void DeleteWork(String name);
	/**删除工作*/
	public void UpdateWork(WorkInfos w);
}
