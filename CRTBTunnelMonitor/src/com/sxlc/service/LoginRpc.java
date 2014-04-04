package com.sxlc.service;

import org.ksoap2.serialization.SoapObject;

import com.sxlc.common.Constant;

public class LoginRpc extends AbstractRpc {
	private static final String LOG_TAG = "LoginRpc";
	private static final String KEY_ACCOUNT = "登陆帐号";
	private static final String KEY_PASSWORD = "登陆密码";
	private static final String KEY_MAC_ADDRESS = "设备物理地址";
	private static final String KEY_PUBLIC_KEY = "加密后密钥";
	private static final String KEY_ACTION = "getPublicKey";
	
	public LoginRpc() {
		SoapObject soapObject = new SoapObject(Constant.NameSpace,"/verifyAppUser");
		soapObject.addProperty("登陆账号", username);
		soapObject.addProperty("登陆密码", pwd);
		soapObject.addProperty("设备物理地址", Constant.testPhysical);
		soapObject.addProperty("加密后密钥", CurApp.getPublickey());
	}
	@Override
	public SoapObject getRpcMessage(String namesapce) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onResponse(Object response) {
		// TODO Auto-generated method stub
		
	}


}
