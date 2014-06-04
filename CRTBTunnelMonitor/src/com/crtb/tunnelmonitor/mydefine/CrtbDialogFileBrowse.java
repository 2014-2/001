package com.crtb.tunnelmonitor.mydefine;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppHandler;
import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.utils.CrtbDbFileUtils;
import com.crtb.tunnelmonitor.widget.CrtbBaseAdapter;

/**
 * 数据库文件导入对话框
 * 
 * @author zhouwei
 *
 */
public final class CrtbDialogFileBrowse extends CrtbDialog {
	
	private int			 	mHeight = 300 ;
	private List<File> 		mInportFileList ;
	private List<ProjectIndex> 	mProjectList ;
	private AppHandler		mHanlder ;
	private CrtbDialogConnecting	mProgressDialog ;
	private TextView		mTitle ;
	private AppHandler		mHomeHandler ;

	public CrtbDialogFileBrowse(final Context context,int height,AppHandler handler) {
		super(context);
		
		mHomeHandler = handler ;
		mHeight	= height ;
		mInportFileList	= CrtbDbFileUtils.getImportFiles() ;
		mProjectList	= ProjectIndexDao.defaultWorkPlanDao().queryAllWorkPlan() ;
		
		mHanlder= new AppHandler(context){

			@Override
			protected void dispose(Message msg) {
				
				switch(msg.what){
				case MSG_TASK_START :
					
					if(mProgressDialog == null){
						mProgressDialog	= new CrtbDialogConnecting(context) ;
					}
					
					if(!mProgressDialog.isShowing()){
						mProgressDialog.showDialog("正在导入数据,请稍等...");
					}
					
					break ;
				case MSG_TASK_END :
					
					if(mProgressDialog != null){
						mProgressDialog.dismiss() ;
					}
					
					break ;
					
				case MSG_INPORT_DB_SUCCESS :
					
					CrtbDialogHint dialog = new CrtbDialogHint(context, R.drawable.ic_reslut_sucess, "数据库导入完成");
					dialog.show() ;
					
					// 通知UI
					mHomeHandler.sendMessage(MSG_INPORT_DB_SUCCESS);
					
					break ;
				case MSG_INPORT_DB_FAILED :
					
					String error = msg.obj != null ? msg.obj.toString() : "数据库导入失败!" ;
					
					Toast.makeText(context, error, Toast.LENGTH_LONG).show() ;
					break ;
				}
			}
			
		};
		
	}
	
	public boolean hasIuputFiles(){
		return mInportFileList != null && mInportFileList.size() > 0 ;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hint_dialog_input_db_layout);
		
		LinearLayout container = (LinearLayout)findViewById(R.id.input_container);
		ViewGroup.LayoutParams lp = container.getLayoutParams() ;
		lp.width 	= ViewGroup.LayoutParams.MATCH_PARENT ;
		lp.height 	= mHeight ;
		
		mTitle		= (TextView) findViewById(R.id.hint_text);

		ListView lv = (ListView)findViewById(R.id.inport_listview);
		final FileAdapter	adapter = new FileAdapter(getContext());
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				dismiss() ;
				
				// 选择的导入文件
				File file = adapter.getItem(position);
				
				// 导入
				CrtbDbFileUtils.importDb(getContext(), mProjectList,file.getAbsolutePath(), mHanlder);
			}
		}) ;
		
		if(mInportFileList == null || mInportFileList.isEmpty()){
			mTitle.setText("没有可导入的文件");
			Toast.makeText(getContext(), "没有可导入的文件", Toast.LENGTH_LONG).show() ;
		}
	}
	
	class FileAdapter extends CrtbBaseAdapter {

		protected FileAdapter(Context context) {
			super(context);
		}

		@Override
		public int getCount() {
			return mInportFileList != null ? mInportFileList.size() : 0 ;
		}

		@Override
		public File getItem(int position) {
			return mInportFileList != null ? mInportFileList.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position ;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.item_file_layout, null);
			}
			
			TextView tv = (TextView)convertView.findViewById(R.id.t1);
			tv.setText(getItem(position).getName());
			
			return convertView;
		}
		
	}
}
