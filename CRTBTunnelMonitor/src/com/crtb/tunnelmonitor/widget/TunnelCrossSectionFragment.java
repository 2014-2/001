package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

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
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public class TunnelCrossSectionFragment extends Fragment {

    private ListView mTunnelSheetList;
    private TunnelSheetAdapter mAdapter;

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
    	mTunnelSheetList = (ListView)getView().findViewById(R.id.section_list);
        mAdapter = new TunnelSheetAdapter(loadData());
        mTunnelSheetList.setAdapter(mAdapter);
        mTunnelSheetList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mAdapter.revertCheck(position);
			}
		});
    }

	private List<RawSheetData> loadData() {
		List<RawSheetData> sheetDataList = new ArrayList<RawSheetData>();
		List<RawSheetIndex> rawSheets = RawSheetIndexDao.defaultDao().queryTunnelSectionRawSheetIndex();
		if (rawSheets != null && rawSheets.size() > 0) {
			for (RawSheetIndex sheet : rawSheets) {
				RawSheetData sheetData = new RawSheetData();
				sheetData.setCreatedTime(CrtbUtils.formatDate(sheet.getCreateTime()));
				List<TunnelCrossSectionIndex> unUploadSections = getUnUploadSections(sheet.getCrossSectionIDs());
				if (unUploadSections.size() > 0) {
					sheetData.setUploaded(false);
					sheetData.setChecked(true);
				} else {
					sheetData.setUploaded(true);
					sheetData.setChecked(false);
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
     * 获取未上传断面列表
     * 
     * @param sectionRowIds  断面数据库ids
     * @return
     */
    public List<TunnelCrossSectionIndex> getUnUploadSections(String sectionRowIds) {
    	TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
    	List<TunnelCrossSectionIndex> unUploadSectionList = new ArrayList<TunnelCrossSectionIndex>();
		List<TunnelCrossSectionIndex> sectionList = dao.querySectionByIds(sectionRowIds);
		if (sectionList != null && sectionList.size() > 0) {
			for(TunnelCrossSectionIndex section: sectionList) {
				//1表示未上传, 2表示已上传
				if ("1".equals(section.getInfo())) {
					unUploadSectionList.add(section);
				}
			}
		}
		return unUploadSectionList;
    }
    
    public List<RawSheetData> getTunnelSheets() {
    	return mAdapter.getSheetList();
    }
    
    class TunnelSheetAdapter extends BaseAdapter {
    	private List<RawSheetData> mSheetDataList;
    	
    	TunnelSheetAdapter(List<RawSheetData> sheetDataList) {
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
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_section_item, null);
            }
            RawSheetData sheetData = mSheetDataList.get(position);
            TextView time = (TextView)convertView.findViewById(R.id.section_time);
            time.setText(sheetData.getCreatedTime());
            TextView is_uploaded = (TextView)convertView.findViewById(R.id.section_is_uploaded);
            if (sheetData.isUploaded()) {
            	is_uploaded.setText("已上传");
            } else {
            	is_uploaded.setText("未上传");
            }
            ImageView checkedStatus = (ImageView) convertView.findViewById(R.id.checked_switch);
            if (sheetData.isChecked()) {
            	checkedStatus.setImageResource(R.drawable.yes);
            } else {
            	checkedStatus.setImageResource(R.drawable.no);
            }
            return convertView;
        }
    }
    
   public class RawSheetData {
	    private String mSectionIds;
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
    	
    	public void setSectionIds(String sectionIds) {
    		mSectionIds = sectionIds;
    	}
    	
    	public String getSectionIds() {
    		return mSectionIds;
    	}
    }
}
