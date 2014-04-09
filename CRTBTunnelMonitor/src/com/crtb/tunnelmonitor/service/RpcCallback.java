package com.crtb.tunnelmonitor.service;

public interface RpcCallback {
	/**
	 * Called when the remote process call is success.
	 * 
	 * @param data The response data retrieved from server
	 */
	public void onSuccess(Object[] data);
	/**
	 * Called when the remote process call is failed.
	 */
	public void onFailed(String reason);
}
