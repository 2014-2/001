package com.sxlc.dao;


import java.util.List;

import com.sxlc.entity.RecordInfo;
import com.sxlc.entity.WorkInfos;
/**
 * 记录单的接口
 *创建时间：2014-3-21下午1:56:59
 *@author 张涛
 *@since JDK1.6
 *@version 1.0
 */
public interface RecordDao{
	/**查询全部*/
	public List<RecordInfo> RecordAll(int type);
	/**新建记录单*/
	public Boolean AddRecord(String type ,RecordInfo r);
	/**查询记录单*/
	public void SelectRecord(RecordInfo r);
	/**删除记录单*/
	public void DeleteRecord(String name);
	/**编辑记录单*/
	public void CompileRecord(RecordInfo r);
	public void GetRecordList(int type,WorkInfos w,List<RecordInfo> list);
	RecordInfo SelectRecord(int id);
	Boolean AddRecord(RecordInfo r);
	Boolean DeleteRecord(int id);
	Boolean UpdateRecord(RecordInfo r);
}