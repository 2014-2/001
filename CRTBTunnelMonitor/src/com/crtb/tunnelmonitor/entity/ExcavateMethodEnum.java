package com.crtb.tunnelmonitor.entity;

/**
 * 开挖方式
 * @author zhouwei
 *
 */
public enum ExcavateMethodEnum {
	
	DT("台阶法",1),ST("三台阶法",2),QD("全断面法",3),SC("双侧壁法",7),UNKOWN("未知",-10) ;

	// DT台阶法=1；ST三台阶法=2；QD全断面法=3；SC双侧壁法=7   ；
	
	private String name ;
	private int code ;
	
	ExcavateMethodEnum(String name,int code){
		this.name	= name ;
		this.code	= code ;
	}
	
	public int getCode(){
		return code ;
	}
	
	public String getName(){
		return name ;
	}
	
	public static ExcavateMethodEnum parser(String name){
		
		if(name == null){
			return UNKOWN ;
		}
		
		if(name.equals("台阶法")){
			return DT ;
		} else if(name.equals("三台阶法")){
			return ST ;
		} else if(name.equals("全断面法")){
			return QD ;
		} else if(name.equals("双侧壁法")){
			return SC ;
		} 
		
		return UNKOWN ;
	}
	
	public static ExcavateMethodEnum parser(int code){
		
		if(code == 1){
			return DT ;
		} else if(code == 2){
			return ST ;
		} else if(code == 3){
			return QD ;
		} else if(code == 7){
			return SC ;
		}
		
		return UNKOWN ;
	}
}
