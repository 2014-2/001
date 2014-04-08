package com.crtb.tunnelmonitor.service;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.crtb.tunnelmonitor.common.Constant;

import ICT.utils.RSACoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

public final class CrtbWebService {
	private static final String TAG = "CrtbWebService";
	
	private static final String NAMESPACE = "webservice.riskcontrol.com";
	private static final String USRE_AUTH_URL = "http://61.237.239.144/fxkz/basedown";
	private static final String DATA_UPLOAD_URL = "http://61.237.239.144/fxkz/testdata";
	
	private static final int CONNECITON_TIME_OUT = 10000;
	
	private static CrtbWebService sInstance;
	private Handler mHandler;
	
	private String mPublicKey;
	private String mRandomCode;
	
	private CrtbWebService() {
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	public static synchronized CrtbWebService getInstance() {
		if (sInstance == null) {
			sInstance = new CrtbWebService();
		}
		return sInstance;
	}
	
	public synchronized String getPublicKey() {
		return mPublicKey;
	}

	public synchronized void setPublicKey(String publicKey) {
		mPublicKey = publicKey;
	}

	public synchronized String getRandomCode() {
		return mRandomCode;
	}

	public synchronized void setRandomCode(String randomCode) {
		mRandomCode = randomCode;
	}

	public void login(final String account, final String password, final RpcCallback callback) {
		GetPublicKeyRpc rpc = new GetPublicKeyRpc(Constant.testUsername, Constant.testPhysical, new RpcCallbackWrapper(new RpcCallback() {
			@Override
			public void onSuccess(Object[] data) {
				String publicKey = (String)data[0];
				String encnryptPublicKey = RSACoder.encnryptRSA(Constant.testDeskey, publicKey);
				String encnryptPassword = RSACoder.encnryptDes(Constant.testPassword, Constant.testDeskey);
				verifyAppUser(Constant.testUsername, encnryptPassword, encnryptPublicKey, new RpcCallbackWrapper(new RpcCallback() {

					@Override
					public void onSuccess(Object[] data) {
						String randomCode = (String) data[0];
						setRandomCode(randomCode);
						if (callback != null) {
							callback.onSuccess(null);
						}
					}
					
					@Override
					public void onFailed() {
						if (callback != null) {
							callback.onFailed();
						}
					}
				}));
				
			}
			
			@Override
			public void onFailed() {
				if (callback != null) {
					callback.onFailed();
				}
			}
		}));
		RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		task.execute();
	}
	/**
	 * 
	 * @param account
	 * @param macAddress
	 * @param callback
	 */
	public void getPublicKey(String account, String macAddress, RpcCallback callback) {
		GetPublicKeyRpc rpc = new GetPublicKeyRpc(account, macAddress, new RpcCallbackWrapper(callback));
		RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		task.execute();
	}
	
	/**
	 * 
	 * @param account
	 * @param password
	 * @param macAddress
	 * @param callback
	 */
	public void verifyAppUser(String account, String encnryptPassword, String encnryptPublicKey, RpcCallback callback) {
		VerifyAppUserRpc rpc = new VerifyAppUserRpc(account, encnryptPassword, Constant.testPhysical, encnryptPublicKey, new RpcCallbackWrapper(callback));
		RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		task.execute();
	}
	/**
	 * 
	 * @param userName
	 * @param password
	 * @param callback
	 */
	public void getZoneAndSiteCode(RpcCallback callback) {
		String randomCode = getRandomCode();
		if (TextUtils.isEmpty(randomCode)) {
			throw new IllegalStateException("getZoneAndSiteCode: random code is invalid.");
		}
		GetZoneAndSiteCodeRpc rpc = new GetZoneAndSiteCodeRpc(randomCode, new RpcCallbackWrapper(callback));
		RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		task.execute();
	}
	
	private class RpcSendTask extends AsyncTask<Void, Void, Void> {
		private AbstractRpc mRpc;
		private String mUrl;
		
		RpcSendTask(AbstractRpc rpc, String url) {
			mRpc = rpc;
			mUrl = url;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			SoapObject rpcMessage = mRpc.getRpcMessage(NAMESPACE);
			Log.d(TAG, "sending request: " + rpcMessage.toString());
	        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	        soapEnvelope.bodyOut = rpcMessage;
	        //soapEnvelope.dotNet  = true;
	        soapEnvelope.encodingStyle = "UTF-8";
	        HttpTransportSE localHttpTransportSE = new HttpTransportSE(mUrl, CONNECITON_TIME_OUT);
	        try {
				localHttpTransportSE.call(NAMESPACE + rpcMessage.getName(), soapEnvelope);
			} catch (Exception e) {
				e.printStackTrace();
			} 
	        Log.d(TAG, "received response: " + soapEnvelope.bodyIn);
	        mRpc.onResponse(soapEnvelope.bodyIn);
			return null;
		}
	}

	private class RpcCallbackWrapper implements RpcCallback {
		private RpcCallback mCallback;

		RpcCallbackWrapper(RpcCallback callback) {
			mCallback = callback;
		}

		@Override
		public void onSuccess(final Object[] data) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mCallback != null) {
						mCallback.onSuccess(data);
					}
				}
			});
		}

		@Override
		public void onFailed() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mCallback != null) {
						mCallback.onFailed();
					}
				}
			});
		}
	}
	
}
