
package com.crtb.tunnelmonitor.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.AlertInfo;

public class AlertListAdapter extends BaseAdapter {
    private List<AlertInfo> list;
    private LayoutInflater inflater;
    private Context context;
    private int gaoliang = -1;
    private static int select = -1;

    public int getselect() {
        return select;
    }

    public void setselect(int select) {
        this.select = select;
    }

    public AlertListAdapter(Context cont, List<AlertInfo> list) {
        context = cont;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    public List<AlertInfo> getList() {
        return list;
    }

    public void refreshData(List<AlertInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return (list != null && list.size() > position) ? list.get(position) : null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void setbackgroud(int a) {
        gaoliang = a;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int pos, View view, ViewGroup arg2) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list, null);
        // if(list.get(pos).isState1()){
        ((TextView) view.findViewById(R.id.list_num)).setText("" + pos);
        TextView data = (TextView) view.findViewById(R.id.date);
        data.setText(list.get(pos).getDate());
        TextView xinghao = (TextView) view.findViewById(R.id.xinghao);
        xinghao.setText(list.get(pos).getXinghao());
        TextView dianhao = (TextView) view.findViewById(R.id.dianhao);
        dianhao.setText("点号：" + list.get(pos).getPntType());
        TextView chushi = (TextView) view.findViewById(R.id.chulifangshi);
        String handleWay = list.get(pos).getChuliFangshi();
        if (handleWay == null || handleWay.equalsIgnoreCase("null")) {
            handleWay = "未作任何处理" ;//TODO: 默认
        }
        chushi.setText("处理方式：" + handleWay);
        TextView state = (TextView) view.findViewById(R.id.state);
        state.setText("状态：" + list.get(pos).getAlertStatusMsg());
        TextView message = (TextView) view.findViewById(R.id.message);
        message.setText(list.get(pos).getUTypeMsg());
        TextView edtstate = (TextView) view.findViewById(R.id.edtstate);
        edtstate.setText(list.get(pos).getHandling());

        return view;
        // }else{
        // view.setVisibility(view.GONE);
        // }

        // return new TextView(context);
    }

    public int getHandledCount() {
        int c = 0;
        if (list != null) {
            for (AlertInfo ai : list) {
                if (ai.getAlertStatus() == 0) {
                    c++;
                }
            }
        }
        return c;
    }
}
