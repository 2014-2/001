package com.crtb.tunnelmonitor.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crtb.tssurveyprovider.TSCommandType;
import com.crtb.tssurveyprovider.TSConnectType;
import com.crtb.tssurveyprovider.TSSurveyProvider;
import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.BaseActivity;
import com.crtb.tunnelmonitor.adapter.ControlPonitsListAdapter;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.common.Constant.TotalStationType;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TotalStationInfoDao;
import com.crtb.tunnelmonitor.entity.TotalStationIndex;

public class StationActivity extends BaseActivity {
	public static final String TAG = "StationActivity";
	/**
	 * 显示用户名和选中状态
	 */
	private ListView listview;
	private Intent intent;
	public ControlPonitsListAdapter adapter;
	int iListPos = -1;
	private OnClickListener itemsOnClick;
	private View vie;

	private SonPopupWindow menuWindow;
	public List<TotalStationIndex> mStations = null;
	private AppCRTBApplication CurApp = null;
	private int iConnectType = 0;
	private boolean bConnect = false;

	private AlertDialog dlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sta);
		CurApp = ((AppCRTBApplication) getApplicationContext());
		TextView title = (TextView) findViewById(R.id.tv_topbar_title);
		title.setText(R.string.total_station_manage);
		init();
		loadData();
		/** 长按 */
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				iListPos = position;
				showListActionMenu(getString(R.string.total_station_manage),
						getResources().getStringArray(R.array.total_station_item_menus), mStations.get(position));
				// ConnectPopupWindow connectWindow = new
				// ConnectPopupWindow(StationActivity.this,
				// position);
				// connectWindow.showAsDropDown(view, 120, -30);
				return true;
			}
		});
		// listview.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// TotalStationIndex item = mStations.get(arg2);
		// boolean bCheck = item.isChecked() ;
		// item.setChecked(!bCheck);
		// mStations.set(arg2, item);
		//
		// // 更新数据库
		// TotalStationInfoDao.defaultDao().update(item);
		//
		// // if (bCheck) {
		// // for (int i = 0; i < mStations.size(); i++) {
		// // if (i != arg2) {
		// // mStations.get(i).setChecked(false);
		// // }
		// // }
		// // }
		//
		// adapter.notifyDataSetChanged();
		// }
		// });
	}

	@Override
	protected void onListItemSelected(Object bean, int position, String menu) {
		Log.d(TAG, "position: " + position + ", menu: " + menu);
		TotalStationIndex tsInfo = (TotalStationIndex) bean;
		// 全站仪品牌
		TSCommandType tsCmdType = TSCommandType.NoneTS;
		TotalStationType t = null;
		try {
			t = Enum.valueOf(TotalStationType.class, tsInfo.getTotalstationType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (t != null) {
			switch (t) {
			case Leica:
				tsCmdType = TSCommandType.LeicaGEOCOM;
				break;
			case LeicaTPS:
				tsCmdType = TSCommandType.LeicaGSI16;
				break;

			default:
				break;
			}
		}
		// 全站仪参数数组
		String[] tsParams;
		switch (position) {
		case 0: // 编辑
			Intent intent = new Intent(StationActivity.this, TotalStationNewBluetoothActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putSerializable(Constant.Select_TotalStationRowClickItemsName_Data, tsInfo);
			mBundle.putBoolean("edit", true);
			intent.putExtras(mBundle);
			startActivityForResult(intent, 0);
			break;
		case 1: // 删除
			showExitGameAlert(tsInfo);
			break;
		case 2: // 蓝牙连接
			Log.d(TAG, "蓝牙连接  clicked");
			tsParams = new String[] { tsInfo.getName(), tsInfo.getInfo() };
			int ret = connect(TSConnectType.Bluetooth, tsCmdType, tsParams);
			if (ret == 1) {
				// mStations.get(position)
				// .setbUse(true);
				AppCRTBApplication.getInstance().setCurUsedStationId(String.valueOf(tsInfo.getID()));
			}
			break;
		case 3: // 串口连接
			Log.d(TAG, "串口连接  clicked");
			tsParams = new String[] { tsInfo.getName(), String.valueOf(tsInfo.getBaudRate()) };
			ret = connect(TSConnectType.RS232, tsCmdType, tsParams);
			if (ret == 1) {
				AppCRTBApplication.getInstance().setCurUsedStationId(String.valueOf(tsInfo.getID()));
			}
			break;
		case 4: // 断开连接
			ret = disconnect();
			if (ret == 1) {
				// mStations.get(position)
				// .setbUse(false);
			}
			break;
		default:
			break;
		}
	}

	public boolean Connect() {
		// TSConnectType tstype = TSConnectType.Bluetooth;
		// String [] tsParams = new String[] {"9600"};
		//
		// int nret =
		// TSSurveyProvider.getDefaultAdapter().BeginConnection(tstype,tsParams
		// );
		//
		// Coordinate3D point = new Coordinate3D();
		// try {
		// nret = TSSurveyProvider.getDefaultAdapter().GetCoord(0, 0, point);
		// String text = String.format("%1$s,%2$s,%3$s",
		// point.x,point.y,point.z);
		// tv.setText(text);
		// }
		// catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// nret = TSSurveyProvider.getDefaultAdapter().EndConnection();
		// TSSurveyProvider.getDefaultAdapter().TestConnection();
		// }
		return true;
	}

	private void init() {
		listview = (ListView) findViewById(R.id.lv_conlist);
	}

	public void loadData() {
		if (ProjectIndexDao.defaultWorkPlanDao().queryEditWorkPlan() == null) {
			return;
		}
		mStations = TotalStationInfoDao.defaultDao().queryAllTotalStations();
		if (mStations != null && mStations.size() > 0) {
			adapter = new ControlPonitsListAdapter(StationActivity.this, mStations);
			listview.setAdapter(adapter);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_MENU) {
				vie = new View(this);
				int num = 1;
				menuWindow = new SonPopupWindow(this, itemsOnClick, 1, 0);
				menuWindow.showAtLocation(vie, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				this.finish();
			}
		}
		return true;
	}

	/** 连接全站仪失败 */
	private void diagolno() {
		AlertDialog dlg = new AlertDialog.Builder(StationActivity.this).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.connectdialog);
		TextView text = (TextView) window.findViewById(R.id.connertexr);
		text.setText("连接" + mStations.get(iListPos).getName() + "全站仪成功");
	}

	/** 连接全站仪成功 */
	private int connect(TSConnectType type, TSCommandType tsCmdType, String[] tsParams) {
		String result = null;
		int ret = 0;
		try {
			ret = TSSurveyProvider.getDefaultAdapter().BeginConnection(type, tsCmdType, tsParams);
			if (ret == 1) {
				result = "成功";
			} else {
				result = "失败";
			}
			// nret = TSSurveyProvider.getDefaultAdapter().TestConnection();

		} catch (Exception e) {

		}
        AlertDialog dlg = new AlertDialog.Builder(StationActivity.this)
//                .setIcon(R.drawable.successgreen)
                .setMessage("连接全站仪" + mStations.get(iListPos).getName() + result).create();
		dlg.show();
//		Window window = dlg.getWindow();
//		window.setContentView(R.layout.connectyesdialog);
//		TextView text = (TextView) window.findViewById(R.id.connertexryes);
//		text.setText("连接全站仪" + mStations.get(iListPos).getName() + result);
		return ret;
	}

	/** 断开全站仪 */
	private int disconnect() {
		int ret = -1;
		String result;
		try {
			ret = TSSurveyProvider.getDefaultAdapter().EndConnection();
		} catch (Exception e) {

		}

		AlertDialog dlg = new AlertDialog.Builder(StationActivity.this).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.connectyesdialog);
		ImageView icon = (ImageView) window.findViewById(R.id.icon);
		if (ret == 1) {
			result = "成功";
		} else {
			result = "失败";
			icon.setImageResource(R.drawable.failred);
		}
		TextView text = (TextView) window.findViewById(R.id.connertexryes);
		text.setText("断开连接" + mStations.get(iListPos).getName() + result);
		return ret;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			loadData();
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}

	// class ConnectPopupWindow extends PopupWindow implements OnClickListener {
	// private TextView edit;
	// private TextView delete;
	// private TextView bluetooth;
	// private TextView com;
	// private TextView disconnect;
	//
	// private Context mContext;
	//
	// private View mConnectView;
	//
	// private int mPosition;
	//
	// private AlertDialog dlg;
	//
	// public ConnectPopupWindow(Context context, int position) {
	// mContext = context;
	// dlg = new AlertDialog.Builder(mContext).create();
	// mPosition = position;
	// LayoutInflater inflater = (LayoutInflater)mContext
	// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// mConnectView = inflater.inflate(R.layout.layout_total_station_popup,
	// null);
	// edit = (TextView)mConnectView.findViewById(R.id.edit);
	// edit.setOnClickListener(this);
	// delete = (TextView)mConnectView.findViewById(R.id.delete);
	// delete.setOnClickListener(this);
	// bluetooth = (TextView)mConnectView.findViewById(R.id.bluetooth_connect);
	// bluetooth.setOnClickListener(this);
	// com = (TextView)mConnectView.findViewById(R.id.com_connect);
	// com.setOnClickListener(this);
	// disconnect = (TextView)mConnectView.findViewById(R.id.disconnect);
	// disconnect.setOnClickListener(this);
	// setContentView(mConnectView);
	// setWidth(LayoutParams.WRAP_CONTENT);
	// setHeight(LayoutParams.WRAP_CONTENT);
	// setFocusable(true);
	// setBackgroundDrawable(new BitmapDrawable());
	// }
	//
	// @Override
	// public void onClick(View v) {
	// int id = v.getId();
	// int ret;
	//
	// TotalStationIndex tsInfo = mStations.get(mPosition);
	//
	// // 全站仪品牌
	// TSCommandType tsCmdType = TSCommandType.NoneTS;
	// TotalStationType t = null;
	// try {
	// t = Enum.valueOf(TotalStationType.class, tsInfo.getTotalstationType());
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// if (t != null) {
	// switch (t) {
	// case Leica:
	// tsCmdType = TSCommandType.LeicaGEOCOM;
	// break;
	// case LeicaTPS:
	// tsCmdType = TSCommandType.LeicaGSI16;
	// break;
	//
	// default:
	// break;
	// }
	// }
	//
	// // 全站仪参数数组
	// String [] tsParams;
	//
	// switch (id) {
	// case R.id.edit://编辑
	// Intent intent = new Intent(StationActivity.this,
	// TotalStationNewBluetoothActivity.class);
	// Bundle mBundle = new Bundle();
	// mBundle.putSerializable(Constant.Select_TotalStationRowClickItemsName_Data,
	// tsInfo);
	// mBundle.putBoolean("edit", true);
	// intent.putExtras(mBundle);
	// startActivityForResult(intent, 0);
	// break;
	// case R.id.delete://删除
	// showExitGameAlert();
	// break;
	// case R.id.bluetooth_connect: // 蓝牙连接
	// Log.d(TAG, "蓝牙连接  clicked");
	// tsParams = new String[] {tsInfo.getName(), tsInfo.getInfo()};
	// ret = connect(TSConnectType.Bluetooth, tsCmdType, tsParams);
	// if (ret == 1) {
	// // mStations.get(position)
	// // .setbUse(true);
	// AppCRTBApplication.getInstance().setCurUsedStationId(String.valueOf(tsInfo.getID()));
	// }
	// break;
	// case R.id.com_connect:// 串口连接
	// Log.d(TAG, "串口连接  clicked");
	// tsParams = new String[] {tsInfo.getName(), String.valueOf(
	// tsInfo.getBaudRate())};
	// ret = connect(TSConnectType.RS232, tsCmdType, tsParams);
	// if (ret == 1) {
	// AppCRTBApplication.getInstance().setCurUsedStationId(String.valueOf(tsInfo.getID()));
	// }
	// break;
	// case R.id.disconnect:// 断开连接
	// ret = disconnect();
	// if (ret == 1) {
	// // mStations.get(position)
	// // .setbUse(false);
	// }
	// default:
	// break;
	// }
	// if (this.isShowing()) {
	// this.dismiss();
	// }
	// }
	//
	// private void showExitGameAlert() {
	// dlg.show();
	// Window window = dlg.getWindow();
	// // *** 主要就是在这里实现这种效果的.
	// window.setContentView(R.layout.dialog_delete);
	//
	// Button ok = (Button) window.findViewById(R.id.ok);
	// Button cancel = (Button) window.findViewById(R.id.cancel);
	// ok.setOnClickListener(listener);
	// cancel.setOnClickListener(listener);
	// }
	//
	// private OnClickListener listener = new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.ok:
	//
	// TotalStationIndex tsInfo = (TotalStationIndex)
	// adapter.getItem(mPosition);
	// if (tsInfo != null) {
	// if (!tsInfo.isUsed()) {
	// TotalStationInfoDao.defaultDao().delete(tsInfo);
	// adapter.remove(tsInfo);
	// showDialog("操作成功");
	// } else {
	// showDialog("当前全站仪正在使用中，无法删除");
	// }
	// }
	//
	// dlg.dismiss();
	//
	// // AppCRTBApplication app = (AppCRTBApplication) SonPopupWindow.this.c
	// // .getApplicationContext();
	// // WorkInfos curWork = app.getCurrentWorkingFace();
	// // if (curWork != null && curWork.getStaionList() != null) {
	// // List<TotalStationInfo> list = curWork.getStaionList();
	// // boolean hasSelected = false;
	// // for (int i = 0; i < list.size(); i++) {
	// // if (list.get(i).isbCheck()) {
	// // hasSelected = true;
	// // if (!list.get(i).isbUse()) {
	// // TotalStationDaoImpl impl = new TotalStationDaoImpl(
	// // SonPopupWindow.this.c,
	// // curWork.getProjectName());
	// // impl.DeleteTotalStation(list.get(i).getId());
	// // list.remove(i);
	// // StationActivity.adapter
	// // .notifyDataSetChanged();
	// // } else {
	// // showDialog("当前全站仪正在使用中，无法删除");
	// // }
	// // break;
	// // }
	// // }
	// // if (!hasSelected) {
	// // showDialog("请先选择要删除的全站仪");
	// // }
	// // }
	// /*
	// * for(int i=0;i<.size();i++) if
	// * (MainActivity.list.get(i).getInfo().equals("选中")) {
	// * MainActivity.list.remove(i);
	// *
	// * }
	// */
	// break;
	// case R.id.cancel:
	// dlg.cancel();
	// break;
	// }
	//
	// }
	// };
	// }

	class SonPopupWindow extends PopupWindow {
		private RelativeLayout xinjian;
		// public RelativeLayout bianji;
		// public RelativeLayout delete;
		private View mMenuView;
		private Intent intent;
		public Context c;
		AlertDialog dlg = null;

		public SonPopupWindow(Activity context, OnClickListener itemsOnClick, final int num, final int currIndex) {
			super(context);
			this.c = context;
			dlg = new AlertDialog.Builder(c).create();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mMenuView = inflater.inflate(R.layout.son_dialog, null);
			xinjian = (RelativeLayout) mMenuView.findViewById(R.id.cr1);
			// bianji = (RelativeLayout) mMenuView.findViewById(R.id.cr2);
			// delete = (RelativeLayout) mMenuView.findViewById(R.id.cr3);

			xinjian.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(c, TotalStationNewBluetoothActivity.class);
					Bundle mBundle = new Bundle();
					mBundle.putParcelable(Constant.Select_TotalStationRowClickItemsName_Data, null);

					intent.putExtras(mBundle);
					((Activity) c).startActivityForResult(intent, 0);
				}
			});
			// bianji.setOnClickListener(new OnClickListener() {
			//
			// // @SuppressLint("ShowToast")
			// @Override
			// public void onClick(View v) {
			// List<TotalStationIndex> tmpList = ((StationActivity)
			// c).mStations;
			// TotalStationIndex tmp = null;
			// if (tmpList == null) {
			// Toast.makeText(c, "请选择需要编辑的全站仪", 3000)
			// .show();
			// return;
			// } else {
			// for (int i = 0; i < tmpList.size(); i++) {
			// if (tmpList.get(i).isChecked()) {
			// tmp = tmpList.get(i);
			// break;
			// }
			// }
			// }
			// if (tmp == null) {
			// Toast.makeText(c, "请选择需要编辑的全站仪", 3000)
			// .show();
			// return;
			// }
			// Intent intent = new Intent(c,
			// TotalStationNewBluetoothActivity.class);
			// Bundle mBundle = new Bundle();
			// mBundle.putSerializable(Constant.Select_TotalStationRowClickItemsName_Data,
			// tmp);
			// mBundle.putBoolean("edit", true);
			// intent.putExtras(mBundle);
			// ((Activity) c).startActivityForResult(intent, 0);
			// }
			// });
			// delete.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			//
			// dismiss();
			//
			// boolean finded = false;
			//
			// // 删除当前选中的全站仪
			// List<TotalStationIndex> list = TotalStationInfoDao
			// .defaultDao().queryAllTotalStations();
			//
			// if (list == null) {
			// return;
			// }
			//
			// for (TotalStationIndex info : list) {
			// if (info.isChecked()) {
			// finded = true;
			// break;
			// }
			// }
			//
			// if (!finded) {
			// showDialog("请先选择要删除的全站仪");
			// return;
			// }
			//
			// showExitGameAlert();
			// }
			// });
			setContentView(mMenuView);
			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.WRAP_CONTENT);
			// 设置SelectPicPopupWindow弹出窗体可点击
			setFocusable(true);
			// 实例化一个ColorDrawable颜色为半透明
			ColorDrawable dw = new ColorDrawable(0xFF000000);
			// 设置SelectPicPopupWindow弹出窗体的背景
			setBackgroundDrawable(dw);
			setOutsideTouchable(true);
		};

	}

	private void showDialog(String text) {
		Builder builder = new Builder(this);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog, null);
		view.findViewById(R.id.cancel).setVisibility(View.GONE);
		view.findViewById(R.id.delete2).setOnClickListener(new View.OnClickListener() {

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

		if (menuWindow != null) {
			menuWindow.dismiss();
		}
	}

	private void showExitGameAlert(TotalStationIndex tsInfo) {
		final Dialog dialog = new AlertDialog.Builder(this).create();
		dialog.show();
		Window window = dialog.getWindow();
		// *** 主要就是在这里实现这种效果的.
		window.setContentView(R.layout.dialog_delete);
		Button ok = (Button) window.findViewById(R.id.ok);
		Button cancel = (Button) window.findViewById(R.id.cancel);
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TotalStationIndex tsInfo = (TotalStationIndex) adapter.getItem(iListPos);
				if (tsInfo != null) {
					if (!tsInfo.isUsed()) {
						TotalStationInfoDao.defaultDao().delete(tsInfo);
						adapter.remove(tsInfo);
						showDialog("操作成功");
					} else {
						showDialog("当前全站仪正在使用中，无法删除");
					}
				}
				dialog.dismiss();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
	}
}
