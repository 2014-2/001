package com.crtb.tunnelmonitor.activity;


import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.AppConfig;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.SurveyerInformationDao;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;

/**
 * 服务器登录界面 创建时间：2014-3-18下午4:11:55
 */
public class LoginActivity extends Activity implements OnClickListener {

    /**登录按钮 */
    private Button login_btn;

    /**登录用户信息列表*/
    //private RelativeLayout login_rl_listview;
    //用户名输入框
	//private EditText mUserName;

    private EditText mCard;

    private EditText mNote;

    private TextView mDownload;

	//private FrameLayout mArrow;
	private Spinner mNameSpinner;
    //private ImageView mNotesArrow;

    private PopupWindow mPop;

    private Dialog dlg;

    // add by wei.zhou
    private List<SurveyerInformation> mAllSurveyer ;
    private BroadcastReceiver mReceiver ;

    private AppCRTBApplication mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mApp = AppCRTBApplication.getInstance();
        mAllSurveyer = SurveyerInformationDao.defaultDao().queryAllSurveyerInformation();
        mApp.setPersonList(mAllSurveyer);

        initView();

        mReceiver	= new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().equals(AppConfig.ACTION_RELOAD_ALL_SURVEYER)){
                    mAllSurveyer	= SurveyerInformationDao.defaultDao().queryAllSurveyerInformation() ;
                    mNameSpinner.setAdapter(new NameAdapter());
                }
            }
        };

        IntentFilter filter = new IntentFilter() ;
        filter.addAction(AppConfig.ACTION_RELOAD_ALL_SURVEYER) ;
        registerReceiver(mReceiver, filter);
    }

    /** 初始化控件 */
    private void initView() {

        login_btn = (Button) findViewById(R.id.login_btn);

        //login_rl_listview = (RelativeLayout) findViewById(R.id.login_rl_listview);
        mNote=(EditText)findViewById(R.id.note);
		//mArrow=(FrameLayout) findViewById(R.id.img);
		mNameSpinner = (Spinner) findViewById(R.id.name_spinner);
		mNameSpinner.setAdapter(new NameAdapter());
        //mNotesArrow=(ImageView) findViewById(R.id.note_arrow);
		//mUserName = (EditText) findViewById(R.id.username);
        mCard = (EditText) findViewById(R.id.idcard);
        mDownload=(TextView) findViewById(R.id.load_teser);
        mDownload.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mDownload.setOnClickListener(this);

        // 点击事件
        login_btn.setOnClickListener(this);

//		mArrow.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//				if(mPop!=null && mPop.isShowing()){
//					return;
//				}
//				
//				View view=LayoutInflater.from(LoginActivity.this).inflate(R.layout.username_popup_layout, null);
//			    ListView list=(ListView) view.findViewById(R.id.list);
//			    final NameAdapter adapter=new NameAdapter();
//			    list.setAdapter(adapter);
//		        list.setOnItemClickListener(new OnItemClickListener() {
// 
//					@Override
//					public void onItemClick(AdapterView<?> arg0, View arg1,
//							int pos, long arg3) {
//						
//						mUserName.setText(adapter.getItem(pos).getSurveyerName());
//					 
//					 if(mPop!=null){
//						 mPop.dismiss();
//						 mPop = null ;
//					 }
//					}
//				});
//		        
//		        if(mPop == null){
//		        	mPop= new PopupWindow(view,mUserName.getWidth(),LayoutParams.WRAP_CONTENT);
//		        	mPop.setFocusable(true);
//		        	mPop.setOutsideTouchable(true);
//		        	mPop.setBackgroundDrawable(new ColorDrawable());
//		        }
//		        
//				mPop.showAsDropDown(mUserName,0,0);
//				//mPop.showAtLocation(mUserName,Gravity.BOTTOM, 0, 0);
//			    //mPop.showAtLocation(mUserName, Gravity.TOP, 0, 0);
//			}
//		});

        //	    mNotesArrow.setOnClickListener(new OnClickListener() {
        //
        //			@Override
        //			public void onClick(View arg0) {
        //
        //				if(mPop!=null && mPop.isShowing()){
        //					return;
        //				}
        //
        //				View view=LayoutInflater.from(LoginActivity.this).inflate(R.layout.username_popup_layout, null);
        //			    ListView list=(ListView) view.findViewById(R.id.list);
        //			    final NameAdapter adapter=new NameAdapter();
        //			    list.setAdapter(adapter);
        //		        list.setOnItemClickListener(new OnItemClickListener() {
        //
        //					@Override
        //					public void onItemClick(AdapterView<?> arg0, View arg1,
        //							int pos, long arg3) {
        //
        //					 mNote.setText(adapter.getItem(pos).getInfo());
        //
        //					 if(mPop!=null){
        //						 mPop.dismiss();
        //						 mPop = null ;
        //					 }
        //					}
        //
        //				});
        //
        //		        if(mPop == null){
        //		        	mPop = new PopupWindow(view, mNote.getWidth(), LayoutParams.WRAP_CONTENT);
        //				    mPop.setFocusable(true);
        //				    mPop.setOutsideTouchable(true);
        //				    mPop.setBackgroundDrawable(new ColorDrawable());
        //		        }
        //
        //				mPop.showAsDropDown(mNote,0,0);
        //			}
        //		});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                //获取用户名和密码
			//String name = mUserName.getText().toString().trim();
			String name = "";
			Object surveyor = mNameSpinner.getSelectedItem();
			if (surveyor instanceof SurveyerInformation) {
				name = ((SurveyerInformation) surveyor).getSurveyerName();
			}
                String card = mCard.getText().toString().trim();
				String note = mNote.getText().toString().trim();
                //用户验证
                if(TextUtils.isEmpty(name)||TextUtils.isEmpty(card)){
                    Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                }else{

                    //login(Constant.testUsername,Constant.testPassword);

                    final SurveyerInformation info = SurveyerInformationDao.defaultDao().querySurveyerByName(name);

                    if(info != null && info.getCertificateID().equals(card)){
                        OnClickListener listener=new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                finish();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra(Constant.LOGIN_TYPE
                                        , Constant.SERVER_USER);
                                startActivity(intent);
                                mApp.setCurPerson(info);
                            }
                        };
                        AppCRTBApplication.mUserName = name;
                        AppCRTBApplication.mCard = card;
                        AppCRTBApplication.mNote = note;
                        showDialog(true,listener);
                    } else {
                        showDialog(false,null);
                    }

                    //				SurveyerInformationDaoImpl dao=SurveyerInformationDaoImpl.getInstance();
                    //	            if(name.equals(dao.getIdCardByName(card))){
                    //	            	OnClickListener listener=new OnClickListener() {
                    //
                    //						@Override
                    //						public void onClick(View v) {
                    //							Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    //			    			intent.putExtra(Constant.LOGIN_TYPE
                    //			    					, Constant.SERVER_USER);
                    //			    			startActivity(intent);
                    //						}
                    //					};
                    //					showDialog(true,listener);
                    //	            }else{
                    //	            	showDialog(false,null);
                    //	            }
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

        public NameAdapter(){

        }

        public void setNameList(List<String> nameList) {
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mAllSurveyer != null ? mAllSurveyer.size() :0 ;
        }

        @Override
        public SurveyerInformation getItem(int index) {
            return mAllSurveyer.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int pos, View view, ViewGroup arg2) {

            if(view==null){
                view=LayoutInflater.from(LoginActivity.this).inflate(R.layout.name_item_layout, null);
            }

            TextView text=(TextView) view.findViewById(R.id.text);
            text.setText(getItem(pos).getSurveyerName());

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
