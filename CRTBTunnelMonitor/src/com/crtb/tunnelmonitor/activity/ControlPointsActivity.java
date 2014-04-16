package com.crtb.tunnelmonitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.crtb.tunnelmonitor.adapter.ControlPonitsListAdapter2;
import com.crtb.tunnelmonitor.dao.impl.v2.ControlPointsInfoDao;
import com.crtb.tunnelmonitor.entity.ControlPointsInfo;
import com.crtb.tunnelmonitor.utils.ConPopuWindow;

public class ControlPointsActivity extends Activity {
    /**
     * 显示用户名和选中状态
     */
    private ListView mContrlPointList;
    private int iItemPos = -1;
    public List<ControlPointsInfo> mControlPoints = null;
    public static ControlPonitsListAdapter2 mAdapter;
    private View vie;
    private ConPopuWindow menuWindow;

    private DialogInterface.OnClickListener itemsOnClick = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d("ControlPointsActivity", "which = " + which);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlpoints);
        TextView title = (TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.control_point_manage);
        mContrlPointList = (ListView) findViewById(R.id.control_sonlist);
        loadData();
        /** 长按 */
        mContrlPointList
        .setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent,
                    View view, int position, long id) {
                iItemPos = position;
                UsePointPopupWindow usePointWindow = new UsePointPopupWindow(
                        ControlPointsActivity.this,
                        position);
                usePointWindow.showAsDropDown(view, 120, -30);
                return true;
            }
        });
        mContrlPointList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                ControlPointsInfo item = mControlPoints.get(arg2);
                boolean bCheck = !item.isbCheck();
                item.setbCheck(!item.isbCheck());
                mControlPoints.set(arg2, item);
                if (bCheck) {
                    for (int i = 0; i < mControlPoints.size(); i++) {
                        if (i != arg2) {
                            mControlPoints.get(i).setbCheck("false");
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void loadData() {
        mControlPoints = ControlPointsInfoDao.defaultDao()
                .queryAllControlPoints();
        if (mControlPoints == null) {
            mControlPoints = new ArrayList<ControlPointsInfo>();
        }
        mAdapter = new ControlPonitsListAdapter2(ControlPointsActivity.this,
                mControlPoints);
        mContrlPointList.setAdapter(mAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                vie = new View(this);
                int num = 1;
                menuWindow = new ConPopuWindow(ControlPointsActivity.this,
                        itemsOnClick, 1, 0);
                menuWindow.showAtLocation(vie, Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                this.finish();
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                loadData();
                mAdapter.notifyDataSetChanged();
                break;

            default:
                break;
        }
    }

    class UsePointPopupWindow extends PopupWindow implements OnClickListener {
        private TextView bluetooth;

        private Context mContext;

        private View mUsePointView;

        private int mPosition;

        public UsePointPopupWindow(Context context, int position) {
            mContext = context;
            mPosition = position;
            LayoutInflater inflater = (LayoutInflater)mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mUsePointView = inflater.inflate(R.layout.layout_use_point_popup, null);
            bluetooth = (TextView)mUsePointView.findViewById(R.id.use_point);
            bluetooth.setOnClickListener(this);
            setContentView(mUsePointView);
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
                case R.id.use_point:
                    ControlPointsInfo item = mControlPoints.get(iItemPos);
                    item.setbUse("true");
                    item.setbCheck("true");
                    mControlPoints.set(iItemPos, item);
                    for (int i = 0; i < mControlPoints.size(); i++) {
                        if (i != iItemPos) {
                            mControlPoints.get(i).setbUse("false");
                            mControlPoints.get(i).setbCheck("false");
                        }
                    }
                    ControlPointsInfoDao.defaultDao().update(item);
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            if (this.isShowing()) {
                this.dismiss();
            }
        }
    }
}
