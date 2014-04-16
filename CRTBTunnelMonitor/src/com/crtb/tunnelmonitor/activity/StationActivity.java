package com.crtb.tunnelmonitor.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crtb.tssurveyprovider.TSConnectType;
import com.crtb.tssurveyprovider.TSSurveyProvider;
import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.adapter.ControlPonitsListAdapter;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.TotalStationInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.WorkPlanDao;
import com.crtb.tunnelmonitor.entity.TotalStationInfo;

public class StationActivity extends Activity {
    /**
     * 显示用户名和选中状态
     */
    private ListView listview;
    private Intent intent;
    public static ControlPonitsListAdapter adapter;
    int iListPos = -1;
    private OnClickListener itemsOnClick;
    private View vie;

    private SonPopupWindow menuWindow;
    public List<TotalStationInfo> mStations = null;
    private AppCRTBApplication CurApp = null;
    private int iConnectType = 0;
    private boolean bConnect = false;

    private AlertDialog dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sta);
        CurApp = ((AppCRTBApplication) getApplicationContext());
        TextView title=(TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.total_station_manage);
        init();
        loadData();
        /** 长按 */
        listview.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    final int position, long id) {
                iListPos = position;
                ConnectPopupWindow connectWindow = new ConnectPopupWindow(StationActivity.this,
                        position);
                connectWindow.showAsDropDown(view, 120, -30);
                return true;
            }
        });
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                TotalStationInfo item = mStations.get(arg2);
                boolean bCheck = item.getChecked().equals("true");
                item.setChecked(bCheck ? "false" : "true");
                mStations.set(arg2, item);
                
                // 更新数据库
                TotalStationInfoDao.defaultDao().update(item);
                
                if (bCheck) {
                    for (int i = 0; i < mStations.size(); i++) {
                        if (i != arg2) {
                            mStations.get(i).setChecked("false");
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public boolean Connect() {
//         TSConnectType tstype = TSConnectType.Bluetooth;
//         String [] tsParams = new String[] {"9600"};
//        
//         int nret =
//         TSSurveyProvider.getDefaultAdapter().BeginConnection(tstype,tsParams
//         );
//        
//         Coordinate3D point = new Coordinate3D();
//         try {
//         nret = TSSurveyProvider.getDefaultAdapter().GetCoord(0, 0, point);
//         String text = String.format("%1$s,%2$s,%3$s",
//         point.x,point.y,point.z);
//         tv.setText(text);
//         }
//         catch (InterruptedException e) {
//         e.printStackTrace();
//         }
//         nret = TSSurveyProvider.getDefaultAdapter().EndConnection();
//         TSSurveyProvider.getDefaultAdapter().TestConnection();
//         }
        return true;
    }

    private void init() {
        listview = (ListView) findViewById(R.id.lv_conlist);
    }

    public void loadData() {
        if (WorkPlanDao.defaultWorkPlanDao().queryEditWorkPlan() == null) {
            return ;
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
        AlertDialog dlg = new AlertDialog.Builder(StationActivity.this)
        .create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.connectdialog);
        TextView text = (TextView) window.findViewById(R.id.connertexr);
        text.setText("连接" + mStations.get(iListPos).getName() + "全站仪成功");
    }

    /** 连接全站仪成功 */
    private int connect(TSConnectType type, int baudRate) {
        String result = null;
        int ret = 0;
        try {
            String[] tsParams = new String[] { String.valueOf(baudRate) };
            ret = TSSurveyProvider.getDefaultAdapter().BeginConnection(type,
                    tsParams);
            if (ret == 1) {
                result = "成功";
            } else {
                result = "失败";
            }
            // nret = TSSurveyProvider.getDefaultAdapter().TestConnection();

        } catch (Exception e) {

        }
        AlertDialog dlg = new AlertDialog.Builder(StationActivity.this)
        .create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.connectyesdialog);
        TextView text = (TextView) window.findViewById(R.id.connertexryes);
        text.setText("连接全站仪" + mStations.get(iListPos).getName() + result);
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

        AlertDialog dlg = new AlertDialog.Builder(StationActivity.this)
        .create();
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

    class ConnectPopupWindow extends PopupWindow implements OnClickListener {
        private TextView bluetooth;

        private TextView com;

        private TextView disconnect;

        private Context mContext;

        private View mConnectView;

        private int mPosition;

        public ConnectPopupWindow(Context context, int position) {
            mContext = context;
            mPosition = position;
            LayoutInflater inflater = (LayoutInflater)mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mConnectView = inflater.inflate(R.layout.layout_total_station_popup, null);
            bluetooth = (TextView)mConnectView.findViewById(R.id.bluetooth_connect);
            bluetooth.setOnClickListener(this);
            com = (TextView)mConnectView.findViewById(R.id.com_connect);
            com.setOnClickListener(this);
            disconnect = (TextView)mConnectView.findViewById(R.id.disconnect);
            disconnect.setOnClickListener(this);
            setContentView(mConnectView);
            setWidth(LayoutParams.WRAP_CONTENT);
            setHeight(LayoutParams.WRAP_CONTENT);
            setFocusable(true);
            setBackgroundDrawable(new BitmapDrawable());
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            int ret;
            switch (id) {
                case R.id.bluetooth_connect: // 蓝牙连接
                    ret = connect(TSConnectType.Bluetooth, mStations.get(mPosition).getBaudRate());
                    if (ret == 1) {
                        // mStations.get(position)
                        // .setbUse(true);
                    }
                    break;
                case R.id.com_connect:// 串口连接
                    ret = connect(TSConnectType.RS232, mStations.get(mPosition).getBaudRate());
                    if (ret == 1) {
                        // mStations.get(position)
                        // .setbUse(true);
                    }
                    break;
                case R.id.disconnect:// 断开连接
                    ret = disconnect();
                    if (ret == 1) {
                        // mStations.get(position)
                        // .setbUse(false);
                    }
                default:
                    break;
            }
            if (this.isShowing()) {
                this.dismiss();
            }
        }
    }

    class SonPopupWindow extends PopupWindow {
        private RelativeLayout xinjian;
        public RelativeLayout bianji;
        public RelativeLayout delete;
        private View mMenuView;
        private Intent intent;
        public Context c;
        AlertDialog dlg = null;

        public SonPopupWindow(Activity context, OnClickListener itemsOnClick,
                final int num, final int currIndex) {
            super(context);
            this.c = context;
            dlg = new AlertDialog.Builder(c).create();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mMenuView = inflater.inflate(R.layout.son_dialog, null);
            xinjian = (RelativeLayout) mMenuView.findViewById(R.id.cr1);
            bianji = (RelativeLayout) mMenuView.findViewById(R.id.cr2);
            delete = (RelativeLayout) mMenuView.findViewById(R.id.cr3);

            xinjian.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c, ControlNewActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putParcelable(
                            Constant.Select_TotalStationRowClickItemsName_Data,
                            null);

                    intent.putExtras(mBundle);
                    ((Activity) c).startActivityForResult(intent, 0);
                }
            });
            bianji.setOnClickListener(new OnClickListener() {

                // @SuppressLint("ShowToast")
                @Override
                public void onClick(View v) {
                    List<TotalStationInfo> tmpList = ((StationActivity) c).mStations;
                    TotalStationInfo tmp = null;
                    if (tmpList == null) {
                        Toast.makeText(c, "请选择需要编辑的全站仪", 3000)
                        .show();
                        return;
                    } else {
                        for (int i = 0; i < tmpList.size(); i++) {
                            if (tmpList.get(i).getChecked().equals("true")) {
                                tmp = tmpList.get(i);
                                break;
                            }
                        }
                    }
                    if (tmp == null) {
                        Toast.makeText(c, "请选择需要编辑的全站仪", 3000)
                        .show();
                        return;
                    }
                    Intent intent = new Intent(c, ControlNewActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(Constant.Select_TotalStationRowClickItemsName_Data, tmp);
                    mBundle.putBoolean("edit", true);
                    intent.putExtras(mBundle);
                    ((Activity) c).startActivityForResult(intent, 0);
                }
            });
            delete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showExitGameAlert();
                }
            });
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

        private void showExitGameAlert() {
            dismiss();
            dlg.show();
            Window window = dlg.getWindow();
            // *** 主要就是在这里实现这种效果的.
            window.setContentView(R.layout.dialog_delete);

            Button ok = (Button) window.findViewById(R.id.ok);
            Button cancel = (Button) window.findViewById(R.id.cancel);
            ok.setOnClickListener(listener);
            cancel.setOnClickListener(listener);

        }

        private OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ok:
					// 测试

					boolean finded = false;
					// 删除当前选中的全站仪
					List<TotalStationInfo> list = TotalStationInfoDao
							.defaultDao().queryAllTotalStations();

					if (list == null) {
						return;
					}

					for (TotalStationInfo info : list) {
						if (info.getChecked().equals("true")) {
							finded = true;
							break;
						}
					}

					if (!finded) {
						showDialog("请先选择要删除的全站仪");
						return;
					}

					for (TotalStationInfo info : list) {

						if (info.getChecked().equals("true")) {

							if (info.getUsed().equals("false")) {
								TotalStationInfoDao.defaultDao().delete(info);
								StationActivity.adapter.remove(info);
							} else {
								showDialog("当前全站仪正在使用中，无法删除");
							}

							break;
						}
					}
                    	

                        //                        AppCRTBApplication app = (AppCRTBApplication) SonPopupWindow.this.c
                        //                        .getApplicationContext();
                        //                        WorkInfos curWork = app.getCurrentWorkingFace();
                        //                        if (curWork != null && curWork.getStaionList() != null) {
                        //                            List<TotalStationInfo> list = curWork.getStaionList();
                        //                            boolean hasSelected = false;
                        //                            for (int i = 0; i < list.size(); i++) {
                        //                                if (list.get(i).isbCheck()) {
                        //                                    hasSelected = true;
                        //                                    if (!list.get(i).isbUse()) {
                        //                                        TotalStationDaoImpl impl = new TotalStationDaoImpl(
                        //                                                SonPopupWindow.this.c,
                        //                                                curWork.getProjectName());
                        //                                        impl.DeleteTotalStation(list.get(i).getId());
                        //                                        list.remove(i);
                        //                                        StationActivity.adapter
                        //                                        .notifyDataSetChanged();
                        //                                    } else {
                        //                                        showDialog("当前全站仪正在使用中，无法删除");
                        //                                    }
                        //                                    break;
                        //                                }
                        //                            }
                        //                            if (!hasSelected) {
                        //                                showDialog("请先选择要删除的全站仪");
                        //                            }
                        //                        }
                        /*
                         * for(int i=0;i<.size();i++) if
                         * (MainActivity.list.get(i).getInfo().equals("选中")) {
                         * MainActivity.list.remove(i);
                         * 
                         * }
                         */
                        dlg.cancel();
                        break;
                    case R.id.cancel:
                        dlg.cancel();
                        break;
                }

            }
        };
    }

    private void showDialog(String text) {
        Builder builder = new Builder(this);
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
