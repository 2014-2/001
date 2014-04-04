/**
 * 
 */
package com.sxlc.activity;

import java.util.ArrayList;
import java.util.List;

import com.sxlc.common.Constant;
import com.sxlc.common.Constant.TotalStationType;
import com.sxlc.dao.impl.ControlPointsDaoImpl;
import com.sxlc.dao.impl.TotalStationDaoImpl;
import com.sxlc.entity.ControlPointsInfo;
import com.sxlc.entity.TotalStationInfo;
import com.sxlc.entity.WorkInfos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 *
 */
public class ControlNewActivityTwo extends Activity implements OnClickListener {

	private TextView cp_new_tv_header;
	
	private EditText point_name, point_x, point_y, point_z, point_info;
	private ControlPointsInfo editInfo = null;
	/** 确定按钮 */
	private Button section_btn_queding;
	/** 取消按钮 */
	private Button section_btn_quxiao;

	private CRTBTunnelMonitor CurApp = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controlnewtwo);
		
		CurApp = ((CRTBTunnelMonitor)getApplicationContext());
		
		editInfo = (ControlPointsInfo)getIntent().getExtras().getParcelable(Constant.Select_ControlPointsRowClickItemsName_Data);
		
		initUI();
		initData();
	}

	private void initUI() {
		cp_new_tv_header = (TextView) findViewById(R.id.cp_new_tv_header);
		
		point_name = (EditText) findViewById(R.id.point_name);
		point_x = (EditText) findViewById(R.id.point_x);
		point_y = (EditText) findViewById(R.id.point_y);
		point_z = (EditText) findViewById(R.id.point_z);
		point_info = (EditText) findViewById(R.id.point_info);
		section_btn_queding = (Button) findViewById(R.id.work_btn_queding);
		section_btn_quxiao = (Button) findViewById(R.id.work_btn_quxiao);

		section_btn_queding.setOnClickListener(this);
		section_btn_quxiao.setOnClickListener(this);
    }
	// 点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.work_btn_quxiao:
			Intent IntentCancel = new Intent();
			IntentCancel.putExtra(Constant.Select_ControlPointsRowClickItemsName_Data,"");
			setResult(RESULT_CANCELED, IntentCancel);
			this.finish();// 关闭当前界面
			break;
		case R.id.work_btn_queding: // 数据库
			if(point_name.getText().toString().trim().length() <= 0)
			{
				Toast.makeText(this, "请输入完整信息", 3000).show();
				return;
			}
			WorkInfos Curw = CurApp.GetCurWork();
			ControlPointsInfo ts = new ControlPointsInfo();
			if (editInfo != null) {
				ts.setId(editInfo.getId());
			}
			ts.setName(point_name.getText().toString().trim());
			ts.setX(Double.valueOf(point_x.getText().toString().trim()));
			ts.setY(Double.valueOf(point_y.getText().toString().trim()));
			ts.setZ(Double.valueOf(point_z.getText().toString().trim()));
			ts.setInfo(point_info.getText().toString().trim());

			if(!CurApp.IsValidControlPointInfo(ts))
			{
				Toast.makeText(this, "请输入完整信息", 3000).show();
				return;
			}
			List<ControlPointsInfo> cpinfos = null;
			cpinfos = Curw.getCpList();
			if(cpinfos == null)
			{
				Toast.makeText(this, "添加失败", 3000).show();
			}
			else
			{
				if(editInfo == null)
				{
					ControlPointsDaoImpl impl = new ControlPointsDaoImpl(this,Curw.getProjectName());
					if(impl.InsertStationInfo(ts))
					{
						cpinfos.add(ts);
						CurApp.UpdateWork(Curw);
						Toast.makeText(this, "添加成功", 3000).show();
					}
					else
					{
						Toast.makeText(this, "添加失败", 3000).show();
					}
				}
				else
				{
					ControlPointsDaoImpl impl = new ControlPointsDaoImpl(this,Curw.getProjectName());
					impl.UpdateStationInfo(ts);
					Curw.UpdateContrlPointsInfo(ts);
					CurApp.UpdateWork(Curw);
					Toast.makeText(this, "编辑成功", 3000).show();
				}
			}
			Intent IntentOk = new Intent();
			IntentOk.putExtra(Constant.Select_ControlPointsRowClickItemsName_Data,"");
			setResult(RESULT_OK, IntentOk);
			this.finish();
			break;
		default:
			break;
		}

	}

	private void initData() {
		if (editInfo != null) {
			cp_new_tv_header.setText("编辑控制点");
			
			point_name.setFocusableInTouchMode(false);
			point_x.setFocusableInTouchMode(false);
			point_y.setFocusableInTouchMode(false);
			point_z.setFocusableInTouchMode(false);
			
			point_name.setText(editInfo.getName());
			point_x.setText(Double.toString(editInfo.getX()));
			point_y.setText(Double.toString(editInfo.getY()));
			point_z.setText(Double.toString(editInfo.getZ()));
			point_info.setText(editInfo.getInfo());
		}
		else {
			cp_new_tv_header.setText("新建控制点");
			point_x.setText("0");
			point_y.setText("0");
			point_z.setText("0");
		}
	}
}
