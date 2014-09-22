package com.crtb.tunnelmonitor.utils;

import java.util.ArrayList;
import java.util.List;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.dao.impl.v2.ExcavateMethodDao;
import com.crtb.tunnelmonitor.entity.ExcavateMethodEnum;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionParameter;

import android.content.Context;

/**
 * 开挖方法转换工具
 * @author zhouwei
 *
 */
public final class ExcavateMethodUtil {
	
	private static Context mContext ;

	public static void initContext(Context context){
		mContext	= context ;
	}
	
	/**
	 * 得到所有开挖方法
	 * 
	 * @return
	 */
	public static List<String> getAllExcavateMethod(){
		
		List<String> excaMethods = new ArrayList<String>();
		
		// 固定开挖方式
		String[] local = mContext.getResources().getStringArray(R.array.section_excavation);
		for(String str : local){
			excaMethods.add(str);
		}
		
		// 自定义开挖方式
		List<TunnelCrossSectionParameter> customExcaMethod = ExcavateMethodDao.defaultDao().queryAllExcavateMethod();
		if(customExcaMethod != null && !customExcaMethod.isEmpty()){
			for(TunnelCrossSectionParameter item : customExcaMethod){
				excaMethods.add(item.getMethodName());
			}
		}
		
		return excaMethods ;
	}
	
	/**
	 * 解析开挖方法对应的code
	 * 
	 * @param method
	 * @return
	 */
	public static int parserExcavateMethodCode(String method){
		
		if(method == null) return ExcavateMethodEnum.UNKOWN.getCode() ;
		
		// 固定开挖方式
		ExcavateMethodEnum exca = ExcavateMethodEnum.parser(method);
		
		if(exca == ExcavateMethodEnum.UNKOWN){
			// 自定义开挖方式
			List<TunnelCrossSectionParameter> customExcaMethod = ExcavateMethodDao.defaultDao().queryAllExcavateMethod();
			if(customExcaMethod != null && !customExcaMethod.isEmpty()){
				for(TunnelCrossSectionParameter item : customExcaMethod){
					if(item.getMethodName().equals(method)){
						return item.getExcavateMethod() ;
					}
				}
			}
			
			return ExcavateMethodEnum.UNKOWN.getCode() ;
		} else {
			return exca.getCode() ;
		}
	}
	
	/**
	 * 解析开挖方法名称
	 * @param code
	 * @return
	 */
	public static String parserExcavateMethodName(int code){
		
		if(code < 0 ) return ExcavateMethodEnum.UNKOWN.getName() ;
		
		// 固定开挖方法
		ExcavateMethodEnum exca = ExcavateMethodEnum.parser(code);
		
		if(exca == ExcavateMethodEnum.UNKOWN){
			
			// 自定义开挖方式
			List<TunnelCrossSectionParameter> customExcaMethod = ExcavateMethodDao.defaultDao().queryAllExcavateMethod();
			
			if (customExcaMethod != null && !customExcaMethod.isEmpty()) {
				for (TunnelCrossSectionParameter item : customExcaMethod) {
					if (item.getExcavateMethod() == code) {
						return item.getMethodName() ;
					}
				}
			}

			return ExcavateMethodEnum.UNKOWN.getName();
		} else {
			return exca.getName() ;
		}
	}
	
	/**
	 * 查找自定义开挖方法
	 * @param method
	 * @return
	 */
	public static TunnelCrossSectionParameter findCustomExcavateMethod(String method){
		
		List<TunnelCrossSectionParameter> customExcaMethod = ExcavateMethodDao.defaultDao().queryAllExcavateMethod();
		
		if(method == null || customExcaMethod == null) return null;
		
		for(TunnelCrossSectionParameter item : customExcaMethod){
			if(item.getMethodName().equals(method)){
				return item ;
			}
		}
		
		return null ;
	}
	
	/**
	 * 检测开挖方法名称是否存在
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkExcavateMethodName(String name){
		
		List<String> list = getAllExcavateMethod();
		
		for(String method : list){
			if(method.equals(name)){
				return false ;
			}
		}
		
		return true ;
	}
}
