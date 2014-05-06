package com.crtb.tunnelmonitor.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.TotalStationIndex;

public class ControlPonitsListAdapter extends BaseAdapter {
    private List<TotalStationIndex>listinfos;
    private Context context;
    public ControlPonitsListAdapter(Context ct,List<TotalStationIndex> lis){
        context=ct;
        listinfos=lis;
    }
    @Override
    public int getCount() {
        return listinfos == null ? 0 : listinfos.size();
    }

    @Override
    public Object getItem(int arg0) {
        return listinfos == null ? null : listinfos.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(TotalStationIndex bean) {

        for (TotalStationIndex info : listinfos) {

            if (info.getID() == bean.getID()) {
                listinfos.remove(info);
                break;
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater lay;
        if(convertView==null){
            lay=LayoutInflater.from(context);
            convertView=lay.inflate(R.layout.listinfos_pic, null);
        }
        TextView name=(TextView) convertView.findViewById(R.id.t1);
//        ImageView start=(ImageView) convertView.findViewById(R.id.t2);
        name.setTextColor(Color.BLACK);
        //start.setTextColor(Color.BLACK);
        name.setText(listinfos.get(position).getName());
//        if (listinfos.get(position).isChecked()) {
//            start.setImageResource(R.drawable.yes);
//        } else {
//            start.setImageResource(R.drawable.no);
//        }
        return convertView;
    }
}
