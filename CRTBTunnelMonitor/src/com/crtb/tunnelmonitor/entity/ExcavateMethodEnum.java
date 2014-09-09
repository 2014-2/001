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
	
	DT("台阶法",1, 5),
	ST("三台阶法",2, 7),
	QD("全断面法",3, 3),
	HX("环行开发法",4, 0),
	CD("中隔壁法",5, 0),
	CRD("交叉中隔壁法",6, 0),
	SC("双侧壁法",7, 7),
	UNKOWN("未知",-10, -1) ;

	private String name ;
	private int code ;
	private int points;
	
	ExcavateMethodEnum(String name,int code, int points){
		this.name	= name ;
		this.code	= code ;
		this.points = points;
	}
	
	public int getCode(){
		return code ;
	}
	
	public int getPoints() {
		return points;
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
		
		// 1表示台阶法
		if(code == 1){
			return DT ;
		} 
		// 2表示三台阶法；
		else if(code == 2){
			return ST ;
		} 
		// 3是全断面法；
		else if(code == 3){
			return QD ;
		} 
		// 4是环形开挖方法；
		else if(code == 4){
			return HX ;
		} 
		// 5中隔壁法（软件里面默认的CD法）；
		else if(code == 5){
			return CD ;
		} 
		// 6是交叉中隔壁法（软件里面默认的交叉中隔壁法）
		else if(code == 6){
			return CRD ;
		} 
		// 7是双侧壁导坑法(软件里面默认的双侧壁导坑法)
		else if(code == 7){
			return SC ;
		}
		
		return UNKOWN ;
	}
}
