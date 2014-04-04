package com.sxlc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public final class CrtbWebService {
	private static final String TAG = "CrtbWebService";
	private static final String NAMESPACE = "http://tempuri.org/";
	private static final String TRAFFIC_SERVICE_URI_GET  = "https://lccs.cr-tb.com/DTMS/ictrcp/basedown.asmx";
	private static final String TRAFFIC_SERVICE_URI_POST = "https://lccs.cr-tb.com/DTMS/ictrcp/testdata.asmx";
	private static final int CONNECITON_TIME_OUT = 10000;
	
	private static CrtbWebService sInstance;
	
	public static synchronized CrtbWebService getInstance() {
		if (sInstance == null) {
			sInstance = new CrtbWebService();
		}
		return sInstance;
	}
	
	/**
	 * 
	 * @param userName
	 * @param password
	 * @param callback
	 */
	public void getZoneAndSiteCode(String userName, String password, RpcCallback callback) {
		GetZoneAndSiteCodeRpc rpc = new GetZoneAndSiteCodeRpc(userName, password, callback);
		RpcSendTask task = new RpcSendTask(rpc, TRAFFIC_SERVICE_URI_GET);
		task.execute();
	}

	private static SoapObject createMessage(String action, Map<String, String> parameters) {
		if (TextUtils.isEmpty(action)) {
			throw new IllegalArgumentException("createMessage: action is NULL." );
		}
		SoapObject message = new SoapObject(NAMESPACE, action);
		for(String key : parameters.keySet()) {
			message.addProperty(key, parameters.get(key));
		}
		return message;
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
	        soapEnvelope.dotNet  = true;
	        soapEnvelope.setOutputSoapObject(rpcMessage);
	        HttpTransportSE localHttpTransportSE = new HttpTransportSE(mUrl, CONNECITON_TIME_OUT);
	        List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
	        headerList.add(new HeaderProperty("Content-Type", "text/xml; charset=utf-8"));
	       // headerList.add(new HeaderProperty("SoapAction", "url/name"));
	        try {
				localHttpTransportSE.call(NAMESPACE + rpcMessage.getName(), soapEnvelope, headerList);
			} catch (Exception e) {
				e.printStackTrace();
			} 
	        mRpc.onResponse(soapEnvelope.bodyIn);
			return null;
		}
	}
	
	private CrtbWebService() {}
}
