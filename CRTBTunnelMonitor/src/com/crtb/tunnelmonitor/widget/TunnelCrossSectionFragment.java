package com.crtb.tunnelmonitor.widget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;

public class TunnelCrossSectionFragment extends Fragment {

    private ListView mlvTunnelSurface;

    private List<TunnelCrossSectionIndex> mList;

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_section, container, false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mlvTunnelSurface = (ListView)getView().findViewById(R.id.section_list);
        mList = new ArrayList<TunnelCrossSectionIndex>();

        //TODO: Fake data
//        TunnelCrossSectionIndex t1 = new TunnelCrossSectionIndex();
//        t1.setInBuiltTime(new Date(114, 1, 10, 12, 42));
//        mList.add(t1);
//
//        TunnelCrossSectionIndex t2 = new TunnelCrossSectionIndex();
//        t2.setInBuiltTime(new Date(114, 2, 5, 11, 37));
//        mList.add(t2);

        mlvTunnelSurface.setAdapter(new TunnelSurfaceAdapter());
    }

    class TunnelSurfaceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return mList.get(position).getID();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_section_item, null);
            }
            TunnelCrossSectionIndex section = mList.get(position);
            TextView time = (TextView)convertView.findViewById(R.id.section_time);
            time.setText(DATE_FORMAT.format(section.getInBuiltTime()));
            TextView is_uploaded = (TextView)convertView.findViewById(R.id.section_is_uploaded);
            is_uploaded.setText("已上传");
            return convertView;
        }

    }
}
