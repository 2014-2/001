package com.crtb.tunnelmonitor.event;

public interface DatabaseListener {
	/**
	 * 数据库表内容发生变化时被调用
	 */
	public void onChanged();
}
