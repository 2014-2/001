/**
 * 
 */
package com.crtb.tunnelmonitor.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.ControlPointsDaoImpl;
import com.crtb.tunnelmonitor.entity.ControlPointsInfo;
import com.crtb.tunnelmonitor.entity.WorkInfos;

public class ControlNewActivityTwo extends Activity implements OnClickListener {

	private TextView cp_new_tv_header;

	private EditText mName, mPointX, mPointY, mPointZ, mNote;
	private ControlPointsInfo info = null;
	/** 确定按钮 */
	private Button mOk;
	/** 取消按钮 */
	private Button mCancel;

	private AppCRTBApplication CurApp = null;

	private Dialog dlg;

	private boolean bEdit = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controlnewtwo);

		CurApp = ((AppCRTBApplication) getApplicationContext());
		Bundle bundle = getIntent().getExtras();
		info = (ControlPointsInfo) bundle
				.getParcelable(Constant.Select_ControlPointsRowClickItemsName_Data);
		bEdit = bundle.getBoolean("bEdit");
		initUI();
		initData();
	}

	private void initUI() {
		cp_new_tv_header = (TextView) findViewById(R.id.cp_new_tv_header);
		mOk = (Button) findViewById(R.id.work_btn_queding);
		mCancel = (Button) findViewById(R.id.work_btn_quxiao);
		mName = (EditText) findViewById(R.id.point_name);
			mName.addTextChangedListener(new TextWatcher() {
private CharSequence temp;
				@Override
				public void onTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
					temp=arg0;
									}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
				}

				@Override
				public void afterTextChanged(Editable arg0) {
					if (temp.length() > 0) {
						mOk.setEnabled(true);
					} else {
						mOk.setEnabled(false);
					}

				}
			});
		
		mPointX = (EditText) findViewById(R.id.point_x);
		mPointY = (EditText) findViewById(R.id.point_y);
		mPointZ = (EditText) findViewById(R.id.point_z);
		mNote = (EditText) findViewById(R.id.point_info);
		if (bEdit) {
			mName.setText(info.getName());
			mPointX.setText(String.valueOf(info.getX()));
			mPointY.setText(String.valueOf(info.getY()));
			mPointZ.setText(String.valueOf(info.getZ()));
			mNote.setText(info.getInfo());
		}
		mOk.setOnClickListener(this);
		mCancel.setOnClickListener(this);
	}

	// 点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.work_btn_quxiao:
			Intent IntentCancel = new Intent();
			IntentCancel.putExtra(
					Constant.Select_ControlPointsRowClickItemsName_Data, "");
			setResult(RESULT_CANCELED, IntentCancel);
			this.finish();// 关闭当前界面
			break;
		case R.id.work_btn_queding: // 数据库
			if (mName.getText().toString().trim().length() <= 0) {
				Toast.makeText(this, "请输入名称", 3000).show();
				return;
			}
			WorkInfos Curw = CurApp.GetCurWork();
			ControlPointsInfo ts = new ControlPointsInfo();
			if (info != null) {
				ts.setId(info.getId());
			}
			String name = mName.getText().toString().trim();
			ts.setName(name);
			ts.setX(Double.valueOf(mPointX.getText().toString().trim()));
			ts.setY(Double.valueOf(mPointY.getText().toString().trim()));
			ts.setZ(Double.valueOf(mPointZ.getText().toString().trim()));
			ts.setInfo(mNote.getText().toString().trim());
			ControlPointsDaoImpl impl = new ControlPointsDaoImpl(this,
					Curw.getProjectName());
			List<ControlPointsInfo> cpinfos = null;
			cpinfos = Curw.getCpList();
			if (cpinfos == null) {
				Toast.makeText(this, "添加失败", 3000).show();
				return;
			} else {
				if (!bEdit) {
					for (int i = 0; i < cpinfos.size(); i++) {
						if (cpinfos.get(i).getName().equals(name)) {
							showDialog("该控制点名已存在");
							return;
						}
					}
				}
				if (!bEdit) {
					if (impl.InsertStationInfo(ts)) {
						cpinfos.add(ts);
						CurApp.UpdateWork(Curw);
						Toast.makeText(this, "添加成功", 3000).show();
					} else {
						Toast.makeText(this, "添加失败", 3000).show();
					}
				} else {
					impl.UpdateStationInfo(ts);
					Curw.UpdateContrlPointsInfo(ts);
					CurApp.UpdateWork(Curw);
					Toast.makeText(this, "编辑成功", 3000).show();
				}
			}
			Intent IntentOk = new Intent();
			IntentOk.putExtra(
					Constant.Select_ControlPointsRowClickItemsName_Data, "");
			setResult(RESULT_OK, IntentOk);
			this.finish();
			break;
		default:
			break;
		}

	}

	private void initData() {
		if (info != null) {
			cp_new_tv_header.setText("编辑控制点");

			//mName.setFocusableInTouchMode(false);
			//mPointX.setFocusableInTouchMode(false);
			//mPointY.setFocusableInTouchMode(false);
			//mPointZ.setFocusableInTouchMode(false);

			//mName.setText(info.getName());
			//mPointX.setText(Double.toString(info.getX()));
			//mPointY.setText(Double.toString(info.getY()));
			//mPointZ.setText(Double.toString(info.getZ()));
			//mNote.setText(info.getInfo());
		} else {
			cp_new_tv_header.setText("新建控制点");
			mPointX.setText("0");
			mPointY.setText("0");
			mPointZ.setText("0");
		}
	}

	private void showDialog(String text) {
		AlertDialog.Builder builder = new Builder(this);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog, null);
		view.findViewById(R.id.cancel).setVisibility(View.GONE);
		view.findViewById(R.id.delete2).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (dlg != null) {
							dlg.dismiss();
						}
					}
				});
		TextView message = (TextView) view.findViewById(R.id.message);
		message.setText(text);
		dlg = builder.create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(view);
		Button ok = (Button) view.findViewById(R.id.ok);
		ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dlg != null) {
					dlg.dismiss();
				}

			}
		});
	}

}
