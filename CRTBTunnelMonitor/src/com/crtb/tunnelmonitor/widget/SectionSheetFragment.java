package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
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
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
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
        mAdapter = new SheetAdapter(loadData());
        mSheetList.setAdapter(mAdapter);
        mSheetList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.revertCheck(position);
            }
        });
    }

    private List<RawSheetData> loadData() {
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
            for (RawSheetIndex sheet : rawSheets) {
                RawSheetData sheetData = new RawSheetData();
                sheetData.setRowId(sheet.getID());
                sheetData.setSectionIds(sheet.getCrossSectionIDs());
                sheetData.setCreatedTime(CrtbUtils.formatDate(sheet.getCreateTime()));
                switch (mSheetType) {
                    case TUNNEL_CROSS:
                        List<TunnelCrossSectionIndex> unUploadTunnelSections = getUnUploadTunnelSections(sheet
                                .getCrossSectionIDs());
                        if (unUploadTunnelSections.size() > 0) {
                            sheetData.setUploaded(false);
                            sheetData.setChecked(true);
                        } else {
                            sheetData.setUploaded(true);
                            sheetData.setChecked(false);
                        }
                        break;
                    case SUBSIDENCE_CROSS:
                        List<SubsidenceCrossSectionIndex> unUploadSubsidenceSections = getUnUploadSubsidenceSections(sheet
                                .getCrossSectionIDs());
                        if (unUploadSubsidenceSections.size() > 0) {
                            sheetData.setUploaded(false);
                            sheetData.setChecked(true);
                        } else {
                            sheetData.setUploaded(true);
                            sheetData.setChecked(false);
                        }
                        break;
                }
                sheetDataList.add(sheetData);
            }
        }
        return sheetDataList;
    }

    public void refreshUI() {
        mAdapter.setSheetList(loadData());
    }

    /**
     * 获取未上传隧道内断面列表
     * 
     * @param sectionRowIds 断面数据库ids
     * @return
     */
    public List<TunnelCrossSectionIndex> getUnUploadTunnelSections(String sectionRowIds) {
        TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
        List<TunnelCrossSectionIndex> unUploadSectionList = new ArrayList<TunnelCrossSectionIndex>();
        List<TunnelCrossSectionIndex> sectionList = dao.querySectionByIds(sectionRowIds);
        if (sectionList != null && sectionList.size() > 0) {
            for (TunnelCrossSectionIndex section : sectionList) {
                // 1表示未上传, 2表示已上传
                if ("1".equals(section.getInfo())) {
                    unUploadSectionList.add(section);
                }
            }
        }
        return unUploadSectionList;
    }

    /**
     * 获取未上传地表下层断面列表
     * 
     * @param sectionRowIds 断面数据库ids
     * @return
     */
    public List<SubsidenceCrossSectionIndex> getUnUploadSubsidenceSections(String sectionRowIds) {
        SubsidenceCrossSectionIndexDao dao = SubsidenceCrossSectionIndexDao.defaultDao();
        List<SubsidenceCrossSectionIndex> unUploadSectionList = new ArrayList<SubsidenceCrossSectionIndex>();
        // List<SubsidenceCrossSectionIndex> sectionList =
        // dao.querySectionByIds(sectionRowIds);
        // if (sectionList != null && sectionList.size() > 0) {
        // for (SubsidenceCrossSectionIndex section : sectionList) {
        // // 1表示未上传, 2表示已上传
        // if ("1".equals(section.getInfo())) {
        // unUploadSectionList.add(section);
        // }
        // }
        // }
        return unUploadSectionList;
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

        public void setSheetList(List<RawSheetData> sheetDataList) {
            mSheetDataList = sheetDataList;
            notifyDataSetChanged();
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
    	private int mRowId;
    	private String mSectionIds;
        private String mCreatedTime;
        private boolean mIsUploaded;
        private boolean mIsChecked;

        public void setRowId(int rowId) {
        	mRowId = rowId;
        }
        
        public int getRowId() {
        	return mRowId;
        }
        
        public void setSectionIds(String sectionIds) {
        	mSectionIds = sectionIds;
        }
        
        public String getSectionIds() {
        	return mSectionIds;
        }
        
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
