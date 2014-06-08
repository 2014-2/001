package com.crtb.tunnelmonitor.network;

import org.ksoap2.serialization.SoapObject;

public abstract class AbstractRpc {
	/**
	 * 
	 * @param namesapce
	 * @return
	 */
	public abstract SoapObject getRpcMessage(String namesapce);
	/**
	 * 
	 * @param response
	 */
	public abstract void onResponse(Object response);

}
