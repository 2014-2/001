package com.crtb.tunnelmonitor.infors;


/*
 *隧道内断面基础信息类
 */
public class TunnelCrossSection {
	  public int Id ;
	public String ExcavateMethod;// 施工方法
	   public String Chainage ;
	    //埋设时间
	    public String InBuiltTime ;
	    public String SurveyPntNames ;
	    public double Width ;
	    public int ConstructionType;//断面类型编号1为SubsidenceCrossSectionIndex表  2，为TunnelCrossSectionIndex表
	    public String Info;
	    // 新加的属性
	    public String Lithologic ;
	    public float LAYVALUE ;
	    public String ROCKGRADE ;
	    
	    public String ChainagePrefix;
	    public float GDU0 ;
	    public float GDVelocity ;
	    public String GDU0Time ;
	    public String GDU0Description ;
	    public float SLU0 ;
	    public float SLLimitVelocity ;
	    public String SLU0Time;
	    public String SLU0Description ;
	    
	    
	    
}
