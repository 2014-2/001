package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

@SuppressLint("ValidFragment")
public class SectionSheetFragment extends Fragment {
    public static final int TUNNEL_CROSS = 0;

    public static final int SUBSIDENCE_CROSS = 1;

    private int mSheetType;

    private ListView mSheetList;
    private SheetAdapter mAdapter;

    public SectionSheetFragment() {
    }

    public SectionSheetFragment(int mSheetType) {
        this.mSheetType = mSheetType;
    }

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
        mSheetList = (ListView)getView().findViewById(R.id.section_list);
        List<RawSheetData> sheetDataList = new ArrayList<RawSheetData>();
        List<RawSheetIndex> rawSheets = null;
        switch (mSheetType) {
            case TUNNEL_CROSS:
                rawSheets = RawSheetIndexDao.defaultDao().queryTunnelSectionRawSheetIndex();
                break;
            case SUBSIDENCE_CROSS:
                rawSheets = RawSheetIndexDao.defaultDao().queryAllSubsidenceSectionRawSheetIndex();
                break;
        }
        if (rawSheets != null && rawSheets.size() > 0) {
            for(RawSheetIndex sheet: rawSheets) {
                RawSheetData sheetData = new RawSheetData();
                sheetData.setCreatedTime(CrtbUtils.formatDate(sheet.getCreateTime()));
                sheetData.setUploaded(false);
                sheetData.setChecked(true);
                sheetDataList.add(sheetData);
            }
        }
        mAdapter = new SheetAdapter(sheetDataList);
        mSheetList.setAdapter(mAdapter);
        mSheetList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.revertCheck(position);
            }
        });
    }

    public List<RawSheetData> getSheets() {
        return mAdapter.getSheetList();
    }

    class SheetAdapter extends BaseAdapter {
        private List<RawSheetData> mSheetDataList;

        SheetAdapter(List<RawSheetData> sheetDataList) {
            mSheetDataList = new ArrayList<RawSheetData>();
            if (sheetDataList != null) {
                mSheetDataList = sheetDataList;
            }
        }

        public List<RawSheetData> getSheetList() {
            return mSheetDataList;
        }

        @Override
        public int getCount() {
            return mSheetDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mSheetDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void revertCheck(int position) {
            RawSheetData sheetData = mSheetDataList.get(position);
            sheetData.setChecked(!sheetData.isChecked());
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RawSheetData sheetData = mSheetDataList.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_section_item, null);
                RawSheetDataHolder holder = new RawSheetDataHolder();
                holder.tvCreatedTime = (TextView)convertView.findViewById(R.id.section_time);
                holder.tvIsUploaded = (TextView)convertView.findViewById(R.id.section_is_uploaded);
                holder.ivIsChecked = (ImageView)convertView.findViewById(R.id.checked_switch);
                convertView.setTag(holder);
            }
            bindView(sheetData, convertView);
            return convertView;
        }

        private void bindView(RawSheetData data, View convertView) {
            RawSheetDataHolder holder = (RawSheetDataHolder)convertView.getTag();
            holder.tvCreatedTime.setText(data.getCreatedTime());
            if (data.isUploaded()) {
                holder.tvIsUploaded.setText("已上传");
            } else {
                holder.tvIsUploaded.setText("未上传");
            }
            if (data.isChecked()) {
                holder.ivIsChecked.setImageResource(R.drawable.yes);
            } else {
                holder.ivIsChecked.setImageResource(R.drawable.no);
            }
        }
    }

    private class RawSheetDataHolder {
        TextView tvCreatedTime;

        TextView tvIsUploaded;

        ImageView ivIsChecked;
    }

    public class RawSheetData {
        private String mCreatedTime;
        private boolean mIsUploaded;
        private boolean mIsChecked;

        public void setCreatedTime(String time) {
            mCreatedTime = time;
        }

        public String getCreatedTime() {
            return mCreatedTime;
        }

        public void setUploaded(boolean flag) {
            mIsUploaded = flag;
        }

        public boolean isUploaded() {
            return mIsUploaded;
        }

        public void setChecked(boolean checked) {
            mIsChecked = checked;
        }

        public boolean isChecked() {
            return mIsChecked;
        }
    }
}
