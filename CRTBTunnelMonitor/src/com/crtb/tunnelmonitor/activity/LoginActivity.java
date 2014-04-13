package com.crtb.tunnelmonitor.activity;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.SurveyerInformationDao;
import com.crtb.tunnelmonitor.dao.impl.SurveyerInformationDaoImpl;
import com.crtb.tunnelmonitor.event.DatabaseListener;
//import android.webkit.WebView.FindListener;
import com.crtb.tunnelmonitor.event.EventDispatcher;

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
	    mDownload=(TextView) findViewById(R.id.load_teser);
	    mDownload.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	    mDownload.setOnClickListener(this);
		// 点击事件
		login_btn.setOnClickListener(this);
	    
	    mPop=new PopupWindow();
	    final SurveyerInformationDao dao=SurveyerInformationDaoImpl.getInstance();
        mNames=dao.getFieldsByName(SurveyerInformationDaoImpl.NAME);
        mNotes=dao.getFieldsByName(SurveyerInformationDaoImpl.NOTE);
	    //ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,names);
        EventDispatcher.getInstance().registerDatabaseListener(new DatabaseListener() {
			@Override
			public void onChanged() {
				 mNames = dao.getFieldsByName(SurveyerInformationDaoImpl.NAME);
			     mNotes = dao.getFieldsByName(SurveyerInformationDaoImpl.NOTE);
			}
		});
	   
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
	
	class NameAdapter extends BaseAdapter{
		private List<String> mNameList = new ArrayList<String>();
		
        public NameAdapter(List<String> list){
        	if (list != null) {
        		mNameList = list;	
        	}
        }
		
        public void setNameList(List<String> nameList) {
        	if (nameList != null) {
        		mNameList = nameList;
        		notifyDataSetChanged();
        	}
        }
        
		@Override
		public int getCount() {
			return mNameList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mNameList.get(arg0);
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
			text.setText(mNameList.get(pos));
			
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
