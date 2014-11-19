package com.crtb.tunnelmonitor.infors;

/**
 * 位移管理等级
 * @author xu
 *
 */
public class OffsetLevel{
	public String Content;
	public int TextColor;
	public boolean IsLargerThanMaxValue;
	/**
	 * 类型值：用于构造内容的前缀
	 */
	public int PreType;
	
	/**
	 * 测量值
	 */
	public double Value;
	
	/**
	 * 是否超限
	 */
	public boolean IsTransfinite;
	
	/**
	 * 超限等级
	 */
	public int TransfiniteLevel;
}
