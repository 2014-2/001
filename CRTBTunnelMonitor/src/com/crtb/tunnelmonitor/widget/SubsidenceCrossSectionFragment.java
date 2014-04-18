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
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;

public class SubsidenceCrossSectionFragment extends Fragment {
    private ListView mlvSubsidenceSurface;

    private List<SubsidenceCrossSectionIndex> mList;

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
        mlvSubsidenceSurface = (ListView)getView().findViewById(R.id.section_list);
        mList = new ArrayList<SubsidenceCrossSectionIndex>();

        //TODO: Fake data
        SubsidenceCrossSectionIndex t1 = new SubsidenceCrossSectionIndex();
        t1.setInbuiltTime(new Date(114, 1, 9, 7, 13));
        mList.add(t1);

        SubsidenceCrossSectionIndex t2 = new SubsidenceCrossSectionIndex();
        t2.setInbuiltTime(new Date(114, 2, 14, 15, 23));
        mList.add(t2);

        SubsidenceCrossSectionIndex t3 = new SubsidenceCrossSectionIndex();
        t3.setInbuiltTime(new Date(114, 3, 7, 18, 20));
        mList.add(t3);

        mlvSubsidenceSurface.setAdapter(new SubsidenceSurfaceAdapter());
    }

    class SubsidenceSurfaceAdapter extends BaseAdapter {

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
            SubsidenceCrossSectionIndex section = mList.get(position);
            TextView time = (TextView)convertView.findViewById(R.id.section_time);
            time.setText(DATE_FORMAT.format(section.getInbuiltTime()));
            TextView is_uploaded = (TextView)convertView.findViewById(R.id.section_is_uploaded);
            is_uploaded.setText("已上传");
            return convertView;
        }

    }
}
