package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionParameter;

/**
 * 开挖方法对应的DAO
 * 
 * @author zhouwei
 * 
 */
public final class ExcavateMethodDao extends AbstractDao<TunnelCrossSectionParameter> {

	private static ExcavateMethodDao _instance;

	private ExcavateMethodDao() {

	}

	public static ExcavateMethodDao defaultDao() {

		if (_instance == null) {
			_instance = new ExcavateMethodDao();
		}

		return _instance;
	}

	public List<TunnelCrossSectionParameter> queryExcavateMethod() {

		final IAccessDatabase mDatabase = getCurrentDb();

		if (mDatabase == null) {
			return null;
		}
		
		// 开挖方法降序
		return mDatabase.queryObjects("select * from TunnelCrossSectionParameter order by ExcavateMethod DESC", TunnelCrossSectionParameter.class);
	}
	
	public int getExcavateMethodValue(){
		
		List<TunnelCrossSectionParameter> list = queryExcavateMethod();
		
		if(list == null || list.isEmpty()) return Constant.CUSTOM_METHOD_START_INDEX ;
		
		return list.get(0).getExcavateMethod() + 1 ;
	}

}
