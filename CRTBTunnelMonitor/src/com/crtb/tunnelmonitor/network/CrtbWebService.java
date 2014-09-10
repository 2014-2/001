package com.crtb.tunnelmonitor.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import ICT.utils.RSACoder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.task.WorkZone;
import com.crtb.tunnelmonitor.utils.CrtbAppConfig;

public final class CrtbWebService {
	private static final String TAG = "CrtbWebService";
	
	private static final String NAMESPACE = "webservice.riskcontrol.com";
	private static final String USRE_AUTH_URL = Constant.getUserAuthUrl();
	private static final String DATA_UPLOAD_URL = Constant.getUploadUrl();
	
	private static final int RETRY_COUNT = 11;
	private static final int CONNECITON_TIME_OUT = 10000;
	
	private static CrtbWebService sInstance;
	private Handler mHandler;
	private static long sTransactionId = 1L;
	private final Executor mRpcExecutor;
	
	private long mRandomCode;
	private String mZoneCode;
	private String mSiteCode;
	
	private CrtbWebService() {
		System.setProperty("http.keepAlive", "false");
		mHandler = new Handler(Looper.getMainLooper());
		mRpcExecutor = Executors.newSingleThreadExecutor();
	}
	
	public static synchronized CrtbWebService getInstance() {
		if (sInstance == null) {
			sInstance = new CrtbWebService();
		}
		return sInstance;
	}
	
	public boolean isLogined() {
		return (mRandomCode != 0) ? true : false;
	}
	
	public void login(final String account, final String password, final RpcCallback callback) {
		GetPublicKeyRpc rpc = new GetPublicKeyRpc(account, Constant.getDeviceMac(), new RpcCallbackWrapper(new RpcCallback() {
			@Override
			public void onSuccess(Object[] data) {
				String publicKey = (String)data[0];
				if ("0".equals(publicKey)) {
					if (callback != null) {
						callback.onFailed("用户名未通过验证");
					}
					return ;
				}
				String encnryptPublicKey = RSACoder.encnryptRSA(Constant.testDeskey, publicKey);
				String encnryptPassword = RSACoder.encnryptDes(password, Constant.testDeskey);
				verifyAppUser(account, encnryptPassword, encnryptPublicKey, new RpcCallbackWrapper(new RpcCallback() {

					@Override
					public void onSuccess(Object[] data) {
						String randomCode = (String) data[0];
						final long retCode = Long.parseLong(randomCode);
						setRandomCode(retCode);
						if (retCode != 0) {
							if (callback != null) {
								callback.onSuccess(null);
							}
						} else {
							if (callback != null) {
								callback.onFailed("密码未通过验证");
							}
						}
					}
					
					@Override
					public void onFailed(String reason) {
						if (callback != null) {
							callback.onFailed(reason);
						}
					}
				}));
				
			}
			
			@Override
			public void onFailed(String reason) {
				if (callback != null) {
					callback.onFailed(reason);
				}
			}
		}));
		sendRequestAsync(rpc, USRE_AUTH_URL);
		//RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		//task.execute();
	}
	/**
	 * 
	 * @param account
	 * @param macAddress
	 * @param callback
	 */
	public void getPublicKey(String account, String macAddress, RpcCallback callback) {
		GetPublicKeyRpc rpc = new GetPublicKeyRpc(account, macAddress, new RpcCallbackWrapper(callback));
		sendRequestAsync(rpc, USRE_AUTH_URL);
		//RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		//task.execute();
	}
	
	/**
	 * 
	 * @param account
	 * @param password
	 * @param macAddress
	 * @param callback
	 */
	public void verifyAppUser(String account, String encnryptPassword, String encnryptPublicKey, RpcCallback callback) {

	    VerifyAppUserRpc rpc = new VerifyAppUserRpc(account, encnryptPassword, Constant.getDeviceMac(), encnryptPublicKey, new RpcCallbackWrapper(callback));
		sendRequestAsync(rpc, USRE_AUTH_URL);
		//RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		//task.execute();
	}
	/**
	 * 
	 * @param userName
	 * @param password
	 * @param callback
	 */
	public void getZoneAndSiteCode(final RpcCallback callback) {
		long randomCode = getRandomCode();
		if (randomCode == 0) {
			Log.e(TAG, "getZoneAndSiteCode() get called before logined.");
		}
		GetZoneAndSiteCodeRpc rpc = new GetZoneAndSiteCodeRpc(randomCode, new RpcCallbackWrapper(new RpcCallback() {
			
			@Override
			public void onSuccess(Object[] data) {
				if (callback != null) {
					callback.onSuccess(data);
				}
			}

			@Override
			public void onFailed(String reason) {
				if (callback != null) {
					callback.onFailed(reason);
				}
			}
		}));
		sendRequestAsync(rpc, USRE_AUTH_URL);
		//RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		//task.execute();
	}
	
	/**
	 * 下载测量人员信息
	 * 
	 * @param callback
	 */
	public void getSurveyors(final RpcCallback callback) {
		getZoneAndSiteCode(new RpcCallback() {
			@Override
			public void onSuccess(Object[] data) {
				WorkZone[] workZoneList = (WorkZone[]) data;
				if (workZoneList != null && workZoneList.length > 0) {
					WorkZone workZone = workZoneList[0];
					String zoneCode = workZone.getZoneCode();
					GetSurveyorsRpc rpc = new GetSurveyorsRpc(zoneCode, getRandomCode(), new RpcCallbackWrapper(callback));
					sendRequestAsync(rpc, USRE_AUTH_URL);
					//RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
					//task.execute();
				} else {
					if (callback != null) {
						callback.onFailed("get zone code failed!");
					}
				}
			}
			
			@Override
			public void onFailed(String reason) {
				if (callback != null) {
					callback.onFailed(reason);
				}
			}
		});
	}
	
	/**
	 * 获取断面的编号列表
	 * 
	 * @param status
	 * @param callback
	 */
	public void getSectionCodeList(final String siteCode, SectionStatus status, final RpcCallback callback) {
		GetSectInfosRpc rpc = new GetSectInfosRpc(siteCode, status.value(), getRandomCode(), new RpcCallbackWrapper(new RpcCallback() {
			
			@Override
			public void onSuccess(Object[] data) {
				List<String> sectionCodeList = Arrays.asList((String[])data);
				List<Integer> sequenceList = new ArrayList<Integer>();
				for(String sectionCode : sectionCodeList) {
					sequenceList.add(Integer.parseInt(sectionCode.replace(siteCode, "")));
				}
				CrtbAppConfig config = CrtbAppConfig.getInstance();
				config.setSectionSequence(Collections.max(sequenceList));
				if (callback != null) {
					callback.onSuccess(data);
				}
			}
			
			@Override
			public void onFailed(String reason) {
				if (callback != null) {
					callback.onFailed(reason);
				}
			}
		}));
		sendRequestAsync(rpc, USRE_AUTH_URL);
		//RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		//task.execute();
	}
	
	/**
	 * 获取断面的信息
	 * 
	 * @param sectionCode
	 * @param callback
	 */
	public void getSectionInfo(String sectionCode, RpcCallback callback) {
		GetSectInfoByCodeRpc rpc = new GetSectInfoByCodeRpc(sectionCode, getRandomCode(), new RpcCallbackWrapper(callback));
		sendRequestAsync(rpc, USRE_AUTH_URL);
		//RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		//task.execute();
	}
	
	/**
	 * 获取的测点编号列表
	 * 
	 * @param status
	 * @param callback
	 */
	public void getPointCodeList(PointStatus status, RpcCallback callback) {
		GetTestCodesRpc rpc = new GetTestCodesRpc(getSiteCode(), status.value(), getRandomCode(), new RpcCallbackWrapper(callback));
		sendRequestAsync(rpc, USRE_AUTH_URL);
		//RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		//task.execute();
	}
	/**
	 * 获取测量信息
	 * 
	 * @param pointCode
	 * @param callback
	 */
	public void getPointInfo(String pointCode, RpcCallback callback) {
		GetMonitorValueInfoRpc rpc = new GetMonitorValueInfoRpc(pointCode, getRandomCode(), new RpcCallbackWrapper(callback));
		sendRequestAsync(rpc, USRE_AUTH_URL);
		//RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		//task.execute();
	}
	
	/**
	 * 上传断面信息
	 * 
	 * @param section
	 * @param callback
	 */
	public void uploadSection(SectionUploadParamter parameter, RpcCallback callback) {
		long randomCode = getRandomCode();
		UploadSectionPointInfoRpc rpc = new UploadSectionPointInfoRpc(getZoneCode(), getSiteCode(), randomCode, parameter, new RpcCallbackWrapper(callback));
		sendRequestAsync(rpc, DATA_UPLOAD_URL);
		//RpcSendTask task = new RpcSendTask(rpc, DATA_UPLOAD_URL);
		//task.execute();
	}
	
	/**
	 * 设置已上传断面信息
	 * 
	 * @param section
	 * @param callback
	 */
	public void updateSection(Object section, RpcCallback callback) {
		long randomCode = getRandomCode();
		UploadRemoveSectionTestDataRpc rpc = new UploadRemoveSectionTestDataRpc(getZoneCode(), getSiteCode(), randomCode, new RpcCallbackWrapper(callback));
		sendRequestAsync(rpc, DATA_UPLOAD_URL);
		//RpcSendTask task = new RpcSendTask(rpc, DATA_UPLOAD_URL);
		//task.execute();
		
	}
	/**
	 * 上传测量数据
	 * 
	 * @param testData
	 * @param callback
	 */
	public void uploadTestResult(PointUploadParameter parameter, final RpcCallback callback) {
		final long randomCode = getRandomCode();
		UploadTestResultDataRpc rpc = new UploadTestResultDataRpc(randomCode, parameter, new RpcCallback() {
			
			@Override
			public void onSuccess(Object[] data) {
				confirmSubmitData(callback);
			}
			
			@Override
			public void onFailed(final String reason) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						if (callback != null) {
							callback.onFailed(reason);
						}
					}
				});
			}
		});
		sendRequestAsync(rpc, DATA_UPLOAD_URL);
		//RpcSendTask task = new RpcSendTask(rpc, DATA_UPLOAD_URL);
		//task.execute();
	}
	
	/**
	 * 上传预警信息
	 * 
	 * @param callback
	 */
	public void uploadWarningData(WarningUploadParameter parameter, RpcCallback callback) {
		long randomCode = getRandomCode();
		UploadWarningDataRpc rpc = new UploadWarningDataRpc(randomCode, parameter, new RpcCallbackWrapper(callback));
		sendRequestAsync(rpc, DATA_UPLOAD_URL);
		//RpcSendTask task = new RpcSendTask(rpc, DATA_UPLOAD_URL);
		///task.execute();
	}
	
	/**
	 * 上传测量数据后需要调此接口确认
	 * 
	 * @param callback
	 */
	public void confirmSubmitData(RpcCallback callback) {
		ConfirmSubmitDataRpc rpc = new ConfirmSubmitDataRpc(getRandomCode(), new RpcCallbackWrapper(callback));
		sendRequest(rpc, DATA_UPLOAD_URL);
		//RpcSendTask task = new RpcSendTask(rpc, DATA_UPLOAD_URL);
		//task.execute();
	}
	
	private void sendRequest(AbstractRpc rpc, String url) {
		final long transactionId = sTransactionId++;
		SoapObject rpcMessage = rpc.getRpcMessage(NAMESPACE);
		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		soapEnvelope.bodyOut = rpcMessage;
		// soapEnvelope.dotNet = true;
		soapEnvelope.encodingStyle = "UTF-8";
		// Register Marshaler
		// For date marshaling
		Marshal dateMarshal = new MarshalDate();
		dateMarshal.register(soapEnvelope);
		new MarshalFloat().register(soapEnvelope);
		HttpTransportSE localHttpTransportSE = new HttpTransportSE(url, CONNECITON_TIME_OUT);
		Object response = null;
		Log.d(TAG, "sending request(" + transactionId +"): " + rpcMessage.toString());
		for (int i = 0; i < RETRY_COUNT; i++) {
			try {
				localHttpTransportSE.call(NAMESPACE + rpcMessage.getName(), soapEnvelope);
			} catch (Exception e) {
				e.printStackTrace();
			}
			response = soapEnvelope.bodyIn;
			if (i == 0) {
				Log.d(TAG, "received response("+ transactionId +"): " + response);
			} else {
				Log.d(TAG, "received response("+ transactionId +") - retry:" + i + ": "+ response);
			}
			if (response != null) {
				break;
			}
		}
		rpc.onResponse(response);
	}
	
	private void sendRequestAsync(final AbstractRpc rpc, final String url) {
		mRpcExecutor.execute(new Runnable() {
			@Override
			public void run() {
				sendRequest(rpc, url);
			}
		});
	}
	
//	private class RpcSendTask extends AsyncTask<Void, Void, Void> {
//		private AbstractRpc mRpc;
//		private String mUrl;
//		
//		RpcSendTask(AbstractRpc rpc, String url) {
//			mRpc = rpc;
//			mUrl = url;
//		}
//		
//		@Override
//		protected Void doInBackground(Void... params) {
//			final int transactionId = sTransactionId.incrementAndGet();
//			SoapObject rpcMessage = mRpc.getRpcMessage(NAMESPACE);
//			SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
//					SoapEnvelope.VER11);
//			soapEnvelope.bodyOut = rpcMessage;
//			// soapEnvelope.dotNet = true;
//			soapEnvelope.encodingStyle = "UTF-8";
//			// Register Marshaler
//			// For date marshaling
//			Marshal dateMarshal = new MarshalDate();
//			dateMarshal.register(soapEnvelope);
//			new MarshalFloat().register(soapEnvelope);
//			HttpTransportSE localHttpTransportSE = new HttpTransportSE(mUrl, CONNECITON_TIME_OUT);
//			Object response = null;
//			for (int i = 0; i < RETRY_COUNT; i++) {
//				Log.d(TAG, "sending request(" + transactionId + "): " + rpcMessage.toString());
//				try {
//					localHttpTransportSE.call(NAMESPACE + rpcMessage.getName(), soapEnvelope);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				response = soapEnvelope.bodyIn;
//				Log.d(TAG, "received response("+ transactionId +"): " + response);
//				if (response != null) {
//					break;
//				}
//			}
//			mRpc.onResponse(response);
//			return null;
//		}
//	}

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
		public void onFailed(final String reason) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mCallback != null) {
						mCallback.onFailed(reason);
					}
				}
			});
		}
	}
	
	private long getRandomCode() {
		return mRandomCode;
	}

	private void setRandomCode(long randomCode) {
		mRandomCode = randomCode;
	}
	
	private static final class MarshalFloat implements Marshal {

		@Override
		public Object readInstance(XmlPullParser parser, String arg1,
				String arg2, PropertyInfo arg3) throws IOException,
				XmlPullParserException {
			return Float.parseFloat(parser.nextText());
		}

		@Override
		public void register(SoapSerializationEnvelope sse) {
			sse.addMapping(sse.xsd, "float", Float.class, this);
		}

		@Override
		public void writeInstance(XmlSerializer writer, Object obj)
				throws IOException {
			// TODO Auto-generated method stub
			writer.text(obj.toString());
		}
	}

	public String getZoneCode() {
		return mZoneCode;
	}

	public void setZoneCode(String zoneCode) {
		mZoneCode = zoneCode;
	}

	public String getSiteCode() {
		return mSiteCode;
	}

	public void setSiteCode(String siteCode) {
		mSiteCode = siteCode;
	}
	
}
