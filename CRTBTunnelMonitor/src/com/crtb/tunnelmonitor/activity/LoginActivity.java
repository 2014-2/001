package com.crtb.tunnelmonitor.activity;


import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import ICT.utils.RSACoder;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
//import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.SurveyerInformationDao;
import com.crtb.tunnelmonitor.dao.impl.SurveyerInformationDaoImpl;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.RpcCallback;

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

	private EditText mCard;
	
	private EditText mNote;
	
	private TextView mDownload;
	
	//APP实例
	private AppCRTBApplication CurApp;
	
	private String veri;
	
	private String rsal;
	
	private String[] sZoneAndSiteCode = null;
	
	private int Result = 0;

	private ImageView mArrow,mNotesArrow;
	
	private PopupWindow mPop;
	
	private List<String> mNames;
	
	private List<String> mNotes;
	
	private Dialog dlg;
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
		mNote=(EditText)findViewById(R.id.note);
		mArrow=(ImageView) findViewById(R.id.img);
		mNotesArrow=(ImageView) findViewById(R.id.note_arrow);
		mUserName = (EditText) findViewById(R.id.username);
		mCard = (EditText) findViewById(R.id.idcard);
		CurApp = ((AppCRTBApplication)getApplicationContext());
	    mDownload=(TextView) findViewById(R.id.load_teser);
	    mDownload.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	    mDownload.setOnClickListener(this);
		// 点击事件
		login_btn.setOnClickListener(this);
	    
	    mPop=new PopupWindow();
	    SurveyerInformationDao dao=SurveyerInformationDaoImpl.getInstance();
        mNames=dao.getFieldsByName(SurveyerInformationDaoImpl.NAME);
        mNotes=dao.getFieldsByName(SurveyerInformationDaoImpl.NOTE);
	    //ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,names);
	   
	    mArrow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mPop!=null && mPop.isShowing()){
					return;
				}
				View view=LayoutInflater.from(LoginActivity.this).inflate(R.layout.username_popup_layout, null);
			    ListView list=(ListView) view.findViewById(R.id.list);
			    final NameAdapter adapter=new NameAdapter(mNames);
			    list.setAdapter(adapter);
		        list.setOnItemClickListener(new OnItemClickListener() {
 
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int pos, long arg3) {
					 mUserName.setText((String)adapter.getItem(pos));
					 if(mPop!=null){
						 mPop.dismiss();
					 }
					}
		        	
				});
			    mPop.setContentView(view);
			    mPop.setFocusable(true);
		        mPop.setWidth(mUserName.getWidth());
		        mPop.setHeight(LayoutParams.WRAP_CONTENT);
			    mPop.setOutsideTouchable(true);
			    mPop.setBackgroundDrawable(new ColorDrawable());
				mPop.showAsDropDown(mUserName,0,0);
				//mPop.showAtLocation(mUserName,Gravity.BOTTOM, 0, 0);
			    //mPop.showAtLocation(mUserName, Gravity.TOP, 0, 0);
			}
		});
	    
	    mNotesArrow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(mPop!=null && mPop.isShowing()){
					return;
				}
				View view=LayoutInflater.from(LoginActivity.this).inflate(R.layout.username_popup_layout, null);
			    ListView list=(ListView) view.findViewById(R.id.list);
			    final NameAdapter adapter=new NameAdapter(mNotes);
			    list.setAdapter(adapter);
		        list.setOnItemClickListener(new OnItemClickListener() {
 
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int pos, long arg3) {
					 mNote.setText((String)adapter.getItem(pos));
					 if(mPop!=null){
						 mPop.dismiss();
					 }
					}
		        	
				});
			    mPop.setContentView(view);
			    mPop.setFocusable(true);
		        mPop.setWidth(mNote.getWidth());
		        mPop.setHeight(LayoutParams.WRAP_CONTENT);
			    mPop.setOutsideTouchable(true);
			    mPop.setBackgroundDrawable(new ColorDrawable());
				mPop.showAsDropDown(mNote,0,0);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn:
			//获取用户名和密码
			String name = mUserName.getText().toString().trim();
			String card = mCard.getText().toString().trim();
            
			//用户验证
			if(TextUtils.isEmpty(name)||TextUtils.isEmpty(card)){
				Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
			}else{
				//login(Constant.testUsername,Constant.testPassword);
				SurveyerInformationDaoImpl dao=SurveyerInformationDaoImpl.getInstance();
	            if(name.equals(dao.getIdCardByName(card))){
	            	OnClickListener listener=new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			    			intent.putExtra(Constant.LOGIN_TYPE
			    					, Constant.SERVER_USER);
			    			startActivity(intent);
						}
					};
					showDialog(true,listener);
	            }else{
	            	showDialog(false,null);
	            }
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
//    private void login(String username,String password){
//    	CrtbWebService.getInstance().login(username, password, new RpcCallback() {
//
//    		@Override
//    		public void onSuccess(Object[] data) {
//    			 
//    		}
//
//    		
//
//			@Override
//			public void onFailed(String reason) {
//				Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
//			}
//    		});
//    }
	
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
	
	class NameAdapter extends BaseAdapter{
		private List<String> strs;
		
        public NameAdapter(List<String> list){
           strs=list;	
        }
		
		@Override
		public int getCount() {
			return strs.size();
		}

		@Override
		public Object getItem(int arg0) {
			return strs.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if(view==null){
				view=LayoutInflater.from(LoginActivity.this).inflate(R.layout.name_item_layout, null);
			}
			TextView text=(TextView) view.findViewById(R.id.text);
			text.setText(strs.get(pos));
			
			return view;
		}
	}
	
	private void showDialog(boolean bSuccess,final OnClickListener listener){
		if(dlg!=null && dlg.isShowing()){
			return ;
		}
		dlg=new Dialog(this,R.style.custom_dlg);
		View view=LayoutInflater.from(this).inflate(R.layout.success_dialog_layout, null);		
		dlg.setContentView(view);
		TextView text=(TextView) dlg.findViewById(R.id.text);
		if(!bSuccess){
			text.setText("用户名和身份证不匹配");
			text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fail,0, 0, 0);
		}
		Button bt=(Button) dlg.findViewById(R.id.bt);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			   if(listener!=null){
				   listener.onClick(v);
			   }
			   if(dlg!=null){
				   dlg.dismiss();
			   }
			}
		});
		dlg.show();
		WindowManager.LayoutParams param=dlg.getWindow().getAttributes();
		param.width=getWindowManager().getDefaultDisplay().getWidth()*3/4;
		dlg.getWindow().setAttributes(param);
	}
	
	
}
