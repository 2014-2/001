package com.crtb.tunnelmonitor.entity;

/**
 * 开挖方式
 * @author zhouwei
 *
 */
public enum ExcavateMethodEnum {
	
	/* 台阶法=1；
	 * 三台阶法=2；
	 * 全断面法=3；
		环形开挖法 = 4,
		中隔壁法，CD工法 = 5,   
		交叉中隔壁法，CRD工法 = 6,   
		双侧壁法 = 7,
	*/
	
	//DT-台阶法
	//ST-三台阶法
	//QD-全断面法
	//HX-环行开挖
	//ZG-中壁
	//JC-交叉
	//SC-双侧壁法
	//DB-地表下沉
	
	DT("台阶法",1),
	ST("三台阶法",2),
	QD("全断面法",3),
	HX("环行开发法",4),
	CD("中壁",5),
	CRD("中壁",6),
	SC("双侧壁法",7),
	UNKOWN("未知",-10) ;

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
		
		if(name.equals(DT.getName())){
			return DT ;
		} else if(name.equals(ST.getName())){
			return ST ;
		} else if(name.equals(QD.getName())){
			return QD ;
		} else if(name.equals(HX.getName())){
			return HX ;
		} else if(name.equals(CD.getName())){
			return CD ;
		} else if(name.equals(CRD.getName())){
			return CRD ;
		} else if(name.equals(SC.getName())){
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
		} else if(code == 4){
			return HX ;
		} else if(code == 5){
			return CD ;
		} else if(code == 6){
			return CRD ;
		} else if(code == 7){
			return SC ;
		}
		
		return UNKOWN ;
	}
}
