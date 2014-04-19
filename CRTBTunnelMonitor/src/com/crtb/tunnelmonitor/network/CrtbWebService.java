package com.crtb.tunnelmonitor.network;

import java.io.IOException;

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
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;

public final class CrtbWebService {
	private static final String TAG = "CrtbWebService";
	
	private static final String NAMESPACE = "webservice.riskcontrol.com";
	private static final String USRE_AUTH_URL = "http://61.237.239.144/fxkz/basedown";
	private static final String DATA_UPLOAD_URL = "http://61.237.239.144/fxkz/testdata";
	
	private static final int RETRY_COUNT = 3;
	private static final int CONNECITON_TIME_OUT = 10000;
	
	private static CrtbWebService sInstance;
	private Handler mHandler;
	
	private long mRandomCode;
	private String mZoneCode;
	private String mZoneName;
	private String mSiteCode;
	private String mSiteName;
	
	private CrtbWebService() {
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	public static synchronized CrtbWebService getInstance() {
		if (sInstance == null) {
			sInstance = new CrtbWebService();
		}
		return sInstance;
	}
	
	/**
	 * 获取工区的名字
	 * 
	 * @return 工作名字
	 */
	public String getZoneName() {
		return mZoneName;
	}
	
	/**
	 * 获取站点的名字
	 * 
	 * @return 站点名字
	 */
	public String getSiteName() {
		return mSiteName;
	}
	
	public void login(final String account, final String password, final RpcCallback callback) {
		GetPublicKeyRpc rpc = new GetPublicKeyRpc(account, Constant.testPhysical, new RpcCallbackWrapper(new RpcCallback() {
			@Override
			public void onSuccess(Object[] data) {
				String publicKey = (String)data[0];
				String encnryptPublicKey = RSACoder.encnryptRSA(Constant.testDeskey, publicKey);
				String encnryptPassword = RSACoder.encnryptDes(password, Constant.testDeskey);
				verifyAppUser(account, encnryptPassword, encnryptPublicKey, new RpcCallbackWrapper(new RpcCallback() {

					@Override
					public void onSuccess(Object[] data) {
						String randomCode = (String) data[0];
						setRandomCode(Long.parseLong(randomCode));
						if (callback != null) {
							callback.onSuccess(null);
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
	public void getZoneAndSiteCode(final RpcCallback callback) {
		long randomCode = getRandomCode();
		if (randomCode == 0) {
			throw new IllegalStateException("invalid random code.");
		}
		GetZoneAndSiteCodeRpc rpc = new GetZoneAndSiteCodeRpc(randomCode, new RpcCallbackWrapper(new RpcCallback() {
			
			@Override
			public void onSuccess(Object[] data) {
				String zoneCode = (String) data[0];
				String zoneName = (String) data[1];
				String siteCode = (String) data[2];
				String siteName = (String) data[3];
				setZoneCode(zoneCode);
				setZoneName(zoneName);
				setSiteCode(siteCode);
				setSiteName(siteName);
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
		RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		task.execute();
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
				String zoneCode = (String) data[0];
				GetSurveyorsRpc rpc = new GetSurveyorsRpc(zoneCode, getRandomCode(), new RpcCallbackWrapper(callback));
				RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
				task.execute();
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
	public void getSectionCodeList(SectionStatus status, RpcCallback callback) {
		GetSectInfosRpc rpc = new GetSectInfosRpc(getSiteCode(), status.value(), getRandomCode(), new RpcCallbackWrapper(callback));
		RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		task.execute();
	}
	
	/**
	 * 获取断面的信息
	 * 
	 * @param sectionCode
	 * @param callback
	 */
	public void getSectionInfo(String sectionCode, RpcCallback callback) {
		GetSectInfoByCodeRpc rpc = new GetSectInfoByCodeRpc(sectionCode, getRandomCode(), new RpcCallbackWrapper(callback));
		RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		task.execute();
	}
	
	/**
	 * 获取的测点编号列表
	 * 
	 * @param status
	 * @param callback
	 */
	public void getPointCodeList(String sectionCode, PointStatus status, RpcCallback callback) {
		GetTestCodesRpc rpc = new GetTestCodesRpc(sectionCode, status.value(), getRandomCode(), new RpcCallbackWrapper(callback));
		RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		task.execute();
	}
	/**
	 * 获取测量信息
	 * 
	 * @param pointCode
	 * @param callback
	 */
	public void getPointInfo(String pointCode, RpcCallback callback) {
		GetMonitorValueInfoRpc rpc = new GetMonitorValueInfoRpc(pointCode, getRandomCode(), new RpcCallbackWrapper(callback));
		RpcSendTask task = new RpcSendTask(rpc, USRE_AUTH_URL);
		task.execute();
	}
	
	/**
	 * 上传断面信息
	 * 
	 * @param section
	 * @param callback
	 */
	public void uploadSection(Object section, RpcCallback callback) {
		String zoneCode = getZoneCode();
		String siteCode = getSiteCode();
		long randomCode = getRandomCode();
		UploadSectionPointInfoRpc rpc = new UploadSectionPointInfoRpc(zoneCode, siteCode, randomCode, section, new RpcCallbackWrapper(callback));
		RpcSendTask task = new RpcSendTask(rpc, DATA_UPLOAD_URL);
		task.execute();
	}
	
	/**
	 * 设置已上传断面信息
	 * 
	 * @param section
	 * @param callback
	 */
	public void updateSection(Object section, RpcCallback callback) {
		String zoneCode = getZoneCode();
		String siteCode = getSiteCode();
		long randomCode = getRandomCode();
		UploadRemoveSectionTestDataRpc rpc = new UploadRemoveSectionTestDataRpc(zoneCode, siteCode, randomCode, new RpcCallbackWrapper(callback));
		RpcSendTask task = new RpcSendTask(rpc, DATA_UPLOAD_URL);
		task.execute();
		
	}
	/**
	 * 上传测量数据
	 * 
	 * @param testData
	 * @param callback
	 */
	public void uploadTestResult(Object testData, RpcCallback callback) {
		final long randomCode = getRandomCode();
		UploadTestResultDataRpc rpc = new UploadTestResultDataRpc(randomCode, testData, new RpcCallbackWrapper(callback));
		RpcSendTask task = new RpcSendTask(rpc, DATA_UPLOAD_URL);
		task.execute();
	}
	
	/**
	 * 上传预警信息
	 * 
	 * @param callback
	 */
	public void uploadWarningData(Object warningData, RpcCallback callback) {
		long randomCode = getRandomCode();
		UploadWarningDataRpc rpc = new UploadWarningDataRpc(randomCode, new RpcCallbackWrapper(callback));
		RpcSendTask task = new RpcSendTask(rpc, DATA_UPLOAD_URL);
		task.execute();
	}
	
	/**
	 * 上传测量数据后需要调此接口确认
	 * 
	 * @param callback
	 */
	public void confirmSubmitData(RpcCallback callback) {
		ConfirmSubmitDataRpc rpc = new ConfirmSubmitDataRpc(getRandomCode(), new RpcCallbackWrapper(callback));
		RpcSendTask task = new RpcSendTask(rpc, DATA_UPLOAD_URL);
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
			HttpTransportSE localHttpTransportSE = new HttpTransportSE(mUrl, CONNECITON_TIME_OUT);
			Object response = null;
			for (int i = 0; i < RETRY_COUNT; i++) {
				try {
					localHttpTransportSE.call(NAMESPACE + rpcMessage.getName(), soapEnvelope);
				} catch (Exception e) {
					e.printStackTrace();
				}
				response = soapEnvelope.bodyIn;
				Log.d(TAG, "received response: " + response);
				if (response != null) {
					break;
				}
			}
			mRpc.onResponse(response);
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
	
	private String getZoneCode() {
		return mZoneCode;
	}
	
	private void setZoneCode(String zoneCode) {
		mZoneCode = zoneCode;
	}
	
	private void setZoneName(String zoneName) {
		mZoneName = zoneName;
	}
	
	private String getSiteCode() {
		return mSiteCode;
	}
	
	private void setSiteCode(String siteCode) {
		mSiteCode = siteCode;
	}
	
	private void setSiteName(String siteName) {
		mSiteName = siteName;
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
	
}
