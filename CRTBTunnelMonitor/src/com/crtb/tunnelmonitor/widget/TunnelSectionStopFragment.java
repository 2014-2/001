package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

import android.R.bool;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.crtb.tunnelmonitor.dao.impl.v2.CrossSectionStopSurveyingDao;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.task.SheetRecord;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

@SuppressLint("ValidFragment")
public class TunnelSectionStopFragment extends Fragment {
    private ListView mSectionList;
    private SectionAdapter mAdapter = new SectionAdapter();
    private Handler mHandler = new Handler();
    private TunnelCrossSectionExIndexDao tunnelSectionExDao = TunnelCrossSectionExIndexDao.defaultDao();
    private RawSheetIndexDao rawDao = RawSheetIndexDao.defaultDao();
    
    public TunnelSectionStopFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_section_stop, container, false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mSectionList = (ListView)getView().findViewById(R.id.section_list);
        mSectionList.setAdapter(mAdapter);
        mSectionList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setCheckStatus(position);
            }
        });
        loadData();
    }

	private void loadData() {
		new LoadTask().execute();
	}
	
    public void refreshUI() {
    	loadData();
    }

    public TunnelCrossSectionIndex getChooseData(){
    	return mAdapter.getChooseData();
    }
    
    public boolean getUploadState(){
    	return mAdapter.getUploadState();
    }
    
    public boolean getSectionStopState(){
    	return mAdapter.getSectionState();
    }
    
    public boolean canStop(){
    	return mAdapter.canStop();
    }
    
    private class SectionAdapter extends BaseAdapter {
        private List<TunnelCrossSectionIndex> mSections;
        private int mCheckedPosition = -1;

        SectionAdapter() {
        	mSections = new ArrayList<TunnelCrossSectionIndex>();
        }

		public void setData(List<TunnelCrossSectionIndex> sections) {
			/** 保持数据全部未选中状态 */
			if (sections != null && sections.size() > 0) {
				for (TunnelCrossSectionIndex tunnel : sections) {
					tunnel.setChecked(false);
				}
				mCheckedPosition = -1;
				mSections = sections;
				notifyDataSetChanged();
			}

		}

		public boolean getUploadState() {
			boolean uploadState = false;
			if (mCheckedPosition != -1) {
				TunnelCrossSectionIndex entity = mSections.get(mCheckedPosition);
				TunnelCrossSectionExIndex exEntity = tunnelSectionExDao.querySectionById(entity.getID());
				if(entity.getUploadStatus() == 2 && exEntity != null){
					uploadState = true;
				}
			} 
			return uploadState;
		}

		public TunnelCrossSectionIndex getChooseData() {
			if (mSections != null && mSections.size() > 0) {
				for (TunnelCrossSectionIndex section : mSections) {
					if (section.isChecked()) {
						return section;
					}
				}
			}
			return null;
		}
		
		public boolean getSectionState() {
			boolean sectionState = true;
			if (mCheckedPosition != -1) {
				TunnelCrossSectionIndex entity = mSections.get(mCheckedPosition);
				if (entity != null) {
					sectionState = entity.getSectionStop();
				}
			} 
			return sectionState;
		}
		
		public boolean canStop(){
			boolean canStop = false;
			TunnelCrossSectionIndex entity = mSections.get(mCheckedPosition);
			if (entity != null) {
				canStop = rawDao.isExistSectionGuidInLastRawSheet(entity.getGuid(), RawSheetIndex.CROSS_SECTION_TYPE_TUNNEL);
			}
			return canStop;
		}

        @Override
        public int getCount() {
            return mSections.size();
        }

        @Override
        public Object getItem(int position) {
            return mSections.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

		public void setCheckStatus(int position) {
			if (mCheckedPosition != -1) {
				TunnelCrossSectionIndex oldSheetData = mSections.get(mCheckedPosition);
				oldSheetData.setChecked(false);
				if (mCheckedPosition != position) {
					TunnelCrossSectionIndex newSheetData = mSections.get(position);
					newSheetData.setChecked(true);
					mCheckedPosition = position;
				} else {
					mCheckedPosition = -1;
				}
			} else {
				TunnelCrossSectionIndex sheetData = mSections.get(position);
				sheetData.setChecked(true);
				mCheckedPosition = position;
			}
			notifyDataSetChanged();
		}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	TunnelCrossSectionIndex record = mSections.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_section_item, null);
                SectionStopDataHolder holder = new SectionStopDataHolder();
                holder.tvSectionAge = (TextView)convertView.findViewById(R.id.section_time);
                holder.tvIsStoped = (TextView)convertView.findViewById(R.id.section_is_uploaded);
                holder.ivIsChecked = (ImageView)convertView.findViewById(R.id.checked_switch);
                convertView.setTag(holder);
            }
            bindView(record, convertView);
            return convertView;
        }

        private void bindView(TunnelCrossSectionIndex data, View convertView) {
            SectionStopDataHolder holder = (SectionStopDataHolder)convertView.getTag();
            holder.tvSectionAge.setText(""+data.getSectionName());
            if(data.getSectionStop()){
            	holder.tvIsStoped.setText("已封存");
            } else{
            	holder.tvIsStoped.setText("未封存");
            }
            if (data.isChecked()) {
                holder.ivIsChecked.setImageResource(R.drawable.yes);
            } else {
                holder.ivIsChecked.setImageResource(R.drawable.no);
            }
        }
    }

    private class SectionStopDataHolder {
        TextView tvSectionAge;
        TextView tvIsStoped;
        ImageView ivIsChecked;
    }

    private class LoadTask extends AsyncTask<Void, Void, List<TunnelCrossSectionIndex>> {

		@Override
		protected List<TunnelCrossSectionIndex> doInBackground(Void... params) {
			List<TunnelCrossSectionIndex> sections = TunnelCrossSectionIndexDao.defaultDao().queryAllSection();
			CrossSectionStopSurveyingDao daoInstance = CrossSectionStopSurveyingDao.defaultDao();
			if (sections != null) {
				int size = sections.size();
				for (int i = 0; i < size; i++) {
					TunnelCrossSectionIndex entity = sections.get(i);
					entity.setSectionStop(daoInstance.getSectionStopState(entity.getGuid()));
				}
			}
			return sections;
		}
		
		@Override
		protected void onPostExecute(List<TunnelCrossSectionIndex> result) {
			mAdapter.setData(result);
		}
    }
}
