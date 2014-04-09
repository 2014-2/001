package com.crtb.tunnelmonitor.activity;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import ICT.utils.RSACoder;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
//import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.service.CrtbWebService;
import com.crtb.tunnelmonitor.service.RpcCallback;

/**
 * 服务器登录界面 创建时间：2014-3-18下午4:11:55
 */
public class LoginActivity extends Activity implements OnClickListener {

	/**登录按钮 */
	private Button login_btn;

	/**登录用户信息列表*/
	//private RelativeLayout login_rl_listview;
	//用户名输入框
	private EditText mUserName;
	//密码输入框
	private EditText mPassword;
	
	private EditText mServerIp;
	
	private TextView mDownload;
	
	//APP实例
	private AppCRTBApplication CurApp;
	private String veri;
	private String rsal;
	private String[] sZoneAndSiteCode = null;
	private int Result = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
	}

	/** 初始化控件 */
	private void initView() {
		login_btn = (Button) findViewById(R.id.login_btn);
		//login_rl_listview = (RelativeLayout) findViewById(R.id.login_rl_listview);
		mServerIp=(EditText)findViewById(R.id.server_ip);
		
		mUserName = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);
		CurApp = ((AppCRTBApplication)getApplicationContext());
	    mDownload=(TextView) findViewById(R.id.load_teser);
	    mDownload.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	    mDownload.setOnClickListener(this);
		// 点击事件
		login_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn:
			//获取用户名和密码
			String name = mUserName.getText().toString().trim();
			String pwd = mPassword.getText().toString().trim();

			//用户验证
			if(TextUtils.isEmpty(name)||TextUtils.isEmpty(pwd)){
				login(Constant.testUsername,Constant.testPassword);
			}else{
				login(Constant.testUsername,Constant.testPassword);
			}
			//验证失败时提示并返回
//			if("0".equals(verify)){
//				Toast.makeText(LoginActivity.this, "用户验证失败！", Toast.LENGTH_LONG).show();
//				break;
//			}else if("-1".equals(verify)){
//				break;
//			}
			
			break;
		case R.id.load_teser:
			Intent intent=new Intent(this,TesterLoadActivity.class);
			startActivity(intent);
		default:
			break;
		}
	}

//	private String loginTest(String username, String password) {
//		String ver = String.valueOf(0);
//		//获取公钥
//		String publicKey = getPub(username,Constant.testPhysical);
//		if("0".equals(publicKey)){
//			//获取失败时提示并返回0
//			Toast.makeText(LoginActivity.this, "获取公钥失败！", Toast.LENGTH_LONG).show();
//			ver = String.valueOf(-1);
//			return ver;
//		}
//		//成功则通过私钥加密
//		CurApp.setPublickey(publicKey);
//		ver = loginSelect(username,password);
//		return ver;
//	}
    private void login(String username,String password){
    	CrtbWebService.getInstance().login(username, password, new RpcCallback() {

    		@Override
    		public void onSuccess(Object[] data) {
    			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
    			intent.putExtra(Constant.LOGIN_TYPE
    					, Constant.SERVER_USER);
    			startActivity(intent);
    		}

    		

			@Override
			public void onFailed(String reason) {
				Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
			}
    		});
    }
	
	private String loginSelect(final String username, String password) {
		veri = String.valueOf(0);
		//密码加密
		final String pwd = RSACoder.encnryptDes(password, Constant.testDeskey);
		new Thread(){
			public void run() {
				//创建HttpTransportSe对象
				HttpTransportSE ht=new HttpTransportSE(Constant.UserSelect);
				ht.debug=true;
				SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER12);
				//实例化SoapObject对象
				SoapObject soapObject = new SoapObject(Constant.NameSpace,"/verifyAppUser");
				soapObject.addProperty("登陆账号", username);
				soapObject.addProperty("登陆密码", pwd);
				soapObject.addProperty("设备物理地址", Constant.testPhysical);
				soapObject.addProperty("加密后密钥", CurApp.getPublickey());

				envelope.bodyOut = soapObject;
				try {
					//调用 web Service	
					ht.call(Constant.NameSpace+"verifyAppUser",envelope);
					if(envelope.getResponse()!=null){
						SoapObject result=(SoapObject)envelope.bodyIn;
						SoapObject detail=(SoapObject)result.getProperty("verifyAppUserResponse");
						veri = detail.getProperty(0).toString();
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
		return veri;
	}

	private String getPub(final String username,final String testShebei) {
		rsal = String.valueOf(0);
		//调用 web Service
		new Thread(){
			public void run() {
				//创建HttpTransportSe对象
				HttpTransportSE ht=new HttpTransportSE(Constant.UserSelect);
				ht.debug=true;
				SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
				//实例化SoapObject对象
				SoapObject soapObject = new SoapObject(Constant.NameSpace,"getPublicKey");
				soapObject.addProperty("登陆账号", username);
				soapObject.addProperty("物理地址", testShebei);
				envelope.bodyOut = soapObject;

				try {
					ht.call(Constant.NameSpace+"getPublicKey",envelope);
					if(envelope.getResponse()!=null){
						SoapObject result=(SoapObject)envelope.bodyIn;
						String pub= result.getProperty(0).toString();
						if(!"0".equals(pub)){
							rsal = RSACoder.encnryptRSA(Constant.testDeskey, pub);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
		return rsal;
	}
	
	//获取工区工点序列
	public String[] getZoneAndSiteCode(final String Randomcode){
		sZoneAndSiteCode = null;
		new Thread(){
			public void run() {
				//创建HttpTransportSe对象
				HttpTransportSE ht=new HttpTransportSE(Constant.UserSelect);
				ht.debug=true;
				SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER12);
				//实例化SoapObject对象
				SoapObject soapObject = new SoapObject(Constant.NameSpace,"/getZoneAndSiteCode");
				soapObject.addProperty("随机码", Randomcode);

				envelope.bodyOut = soapObject;
				try {
					//调用 web Service	
					ht.call(Constant.NameSpace+"getZoneAndSiteCode",envelope);
					if(envelope.getResponse()!=null){
						SoapObject result=(SoapObject)envelope.bodyIn;
//						SoapObject detail=(SoapObject)result.getProperty("verifyAppUserResponse");
						sZoneAndSiteCode = (String[])result.getProperty(0);
					}
				}catch(Exception e) {
					e.printStackTrace();
					System.out.println("getZoneAndSiteCode:" + e.getLocalizedMessage());
				}
			};
		}.start();
		
		return sZoneAndSiteCode;
	}
	
	//设置量测断面及测点接口
	public int getSectionPointInfo(final String zonecode,final String Sitecode,final String Sectname,final String Sectcode,final String Sectkilo,
			final String Sectmethod,final float Sectwidth,final float Movevalueuo,final String Updatetime,final String Uoremark,final int Rocklevel,
			final String Testcodes,final String Objlaytime,final String Remark,final String Randomcode){
		int iResult = 0;
		
		new Thread(){
			public void run() {
				//创建HttpTransportSe对象
				HttpTransportSE ht=new HttpTransportSE(Constant.UserSelect);
				ht.debug=true;
				SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER12);
				//实例化SoapObject对象
				SoapObject soapObject = new SoapObject(Constant.NameSpace,"/getZoneAndSiteCode");
				soapObject.addProperty("工区编码", zonecode);
				soapObject.addProperty("",Sitecode);
				soapObject.addProperty("",Sectname);
				soapObject.addProperty("",Sectcode);
				soapObject.addProperty("",Sectkilo);
				soapObject.addProperty("",Sectmethod);
				soapObject.addProperty("",Sectwidth);
				soapObject.addProperty("",Movevalueuo);
				soapObject.addProperty("",Updatetime);
				soapObject.addProperty("",Uoremark);
				soapObject.addProperty("",Rocklevel);
				soapObject.addProperty("",Testcodes);
				soapObject.addProperty("",Objlaytime);
				soapObject.addProperty("",Remark);
				soapObject.addProperty("",Randomcode);

				envelope.bodyOut = soapObject;
				try {
					//调用 web Service	
					ht.call(Constant.NameSpace+"getZoneAndSiteCode",envelope);
					if(envelope.getResponse()!=null){
						SoapObject result=(SoapObject)envelope.bodyIn;
//						SoapObject detail=(SoapObject)result.getProperty("verifyAppUserResponse");
						Result = Integer.valueOf(result.getProperty(0).toString());
					}
				}catch(Exception e) {
					e.printStackTrace();
					System.out.println("getZoneAndSiteCode:" + e.getLocalizedMessage());
				}
			};
		}.start();
		iResult = Result;
		return iResult;
	}
}
