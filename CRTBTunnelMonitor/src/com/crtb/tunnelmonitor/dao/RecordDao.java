package com.crtb.tunnelmonitor.dao;


import java.util.List;

import com.crtb.tunnelmonitor.entity.RecordInfo;
import com.crtb.tunnelmonitor.entity.WorkInfos;
/**
 * 记录单的接口
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