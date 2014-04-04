package com.sxlc.activity;

import java.util.ArrayList;
import java.util.List;

//import com.sxlc.adapter.listAdapter;
import com.sxlc.adapter.WorkListAdapter;
import com.sxlc.common.Constant;
import com.sxlc.dao.impl.WorkDaoImpl;
//import com.sxlc.entity.list_infos;
import com.sxlc.entity.*;
import com.sxlc.utils.SelectPicPopupWindow;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 工作面 创建时间：2014-3-19上午9:45:02
 * 
 * @author 邓孟
 * @since JDK1.6
 * @version 1.0
 */
public class WorkActivity extends Activity {
	private ListView list;
	//private listAdapter adapter;
	private WorkListAdapter adapter;
	//private List<list_infos> infos = null;
	private List<WorkInfos> infos = null;
	private int iListPos = -1;
	private SelectPicPopupWindow menuWindow;
	private OnClickListener itemsOnClick;
	private RelativeLayout layout;
	private PopupWindow mPopupWindow;
	private View vie;
	/***/
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work);
		// 初始化空间
		layout = (RelativeLayout) findViewById(R.layout.alert_dialog);
		list = (ListView) findViewById(R.id.listView1);
		// 分布线高度
		list.setDividerHeight(0);
		setdata();
		// 实例化适配器
		adapter = new WorkListAdapter(WorkActivity.this, infos);
		// list加载适配器
		list.setAdapter(adapter);
		// listview的行点击
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				iListPos = position;
				// 对话框的选项
				//CharSequence items[] = { "打开", "编辑", "导出", "删除" };
				// 实例化对话
				new AlertDialog.Builder(WorkActivity.this)
						.setItems(/*items*/Constant.WorkRowClickItems, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
								WorkInfos item = (WorkInfos)list.getItemAtPosition(iListPos);
								switch (which) {
								case 0: // 打开
									CurApp.SetCurWork(WorkActivity.this,item);
									intent = new Intent(WorkActivity.this,
											MainActivity.class);
									intent.putExtra(Constant.Select_WorkRowClickItemsName_Name,
											Constant.Select_WorkRowClickItemsValue_Open);
									startActivity(intent);
									break;
								case 1:// 编辑
									intent = new Intent(WorkActivity.this,
											WorkNewActivity.class);
									intent.putExtra(Constant.Select_WorkRowClickItemsName_Name,
											item.getProjectName());
									WorkActivity.this.startActivityForResult(intent,0);
									//startActivity(intent);
									break;
								case 2:
									break;
								case 3:// 删除
									boolean bDel = true;
									WorkInfos CurW = CurApp.GetCurWork();
									if(CurW != null)
									{
										if(CurW.getProjectName().equals(item.getProjectName()))
										{
											bDel = false;
										}
									}
									if(!bDel)
									{
										Toast.makeText(WorkActivity.this, "当前工作面正在使用，不能删除", 3000).show();
									}
									else
									{
										CurApp.GetDB().DeleteWork(item.getProjectName());
										CurApp.DelWork(item);
										adapter.notifyDataSetChanged();
										Toast.makeText(WorkActivity.this, "删除成功", 3000).show();
									}
								default:
									break;
								}

							}
						})
						.setCancelable(false)
						.show().setCanceledOnTouchOutside(true);// 显示对话框
				return true;
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (resultCode) {
		case RESULT_OK:
		{
			adapter.notifyDataSetChanged();
		}
			break;

		default:
			break;
		}
	}

	// listview数据
	/**
	 * 
	 */
	public void setdata() {
		//WorkDaoImpl d = new WorkDaoImpl(WorkActivity.this, "yy");
		CRTBTunnelMonitor CurApp = ((CRTBTunnelMonitor)getApplicationContext());
		infos = CurApp.GetWorkList();
		boolean bLoadDB = true;
		if(infos!=null)
		{
			if(infos.size()>0)
			{
				bLoadDB = false;
			}
		}
		if(bLoadDB)
		{
			if(infos == null)
			{
				infos = new ArrayList<WorkInfos>();
				
			}
			CurApp.GetDB().GetWorkList(infos);
			CurApp.SetWorkList(infos);
		}
	}
		
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			// TODO Auto-generated method stub
			if (keyCode == 82) {
				vie = new View(this);
				int num = 1;
				menuWindow = new SelectPicPopupWindow(this, itemsOnClick,1,0);
				menuWindow.showAtLocation(vie, Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				this.finish();
			}
		}
		return true;
	}
}
