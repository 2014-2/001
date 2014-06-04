package com.crtb.tunnelmonitor.widget;

import java.util.ArrayList;
import java.util.List;

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
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.task.SheetRecord;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

@SuppressLint("ValidFragment")
public class SubsidenceSectionSheetFragment extends Fragment {
    private ListView mSheetList;
    private SheetAdapter mAdapter;
    private Handler mHandler = new Handler();

    public SubsidenceSectionSheetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_section, container, false);
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
        mSheetList = (ListView)getView().findViewById(R.id.section_list);
        mAdapter = new SheetAdapter();
        mSheetList.setAdapter(mAdapter);
        mSheetList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.revertCheck(position);
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

    public List<RawSheetIndex> getUploadData() {
        return mAdapter.getUploadData();
    }

    class SheetAdapter extends BaseAdapter {
        private List<RawSheetIndex> mSheetRecords;

        SheetAdapter() {
        	mSheetRecords = new ArrayList<RawSheetIndex>();
        }

        public void setData(List<RawSheetIndex> sheetRecords) {
        	mSheetRecords = sheetRecords;
            notifyDataSetChanged();
        }

        public List<RawSheetIndex> getUploadData() {
            List<RawSheetIndex> uploadDataList = new ArrayList<RawSheetIndex>();
            if (mSheetRecords != null && mSheetRecords.size() > 0) {
                for(RawSheetIndex record : mSheetRecords) {
                    if (record.isChecked()) {
                        uploadDataList.add(record);
                    }
                }
            }
            return uploadDataList;
        }

        @Override
        public int getCount() {
            return mSheetRecords.size();
        }

        @Override
        public Object getItem(int position) {
            return mSheetRecords.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void revertCheck(int position) {
        	RawSheetIndex sheetData = mSheetRecords.get(position);
            sheetData.setChecked(!sheetData.isChecked());
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	RawSheetIndex record = mSheetRecords.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_section_item, null);
                RawSheetDataHolder holder = new RawSheetDataHolder();
                holder.tvCreatedTime = (TextView)convertView.findViewById(R.id.section_time);
                holder.tvIsUploaded = (TextView)convertView.findViewById(R.id.section_is_uploaded);
                holder.ivIsChecked = (ImageView)convertView.findViewById(R.id.checked_switch);
                convertView.setTag(holder);
            }
            bindView(record, convertView);
            return convertView;
        }

        private void bindView(RawSheetIndex data, View convertView) {
            RawSheetDataHolder holder = (RawSheetDataHolder)convertView.getTag();
            holder.tvCreatedTime.setText(CrtbUtils.formatDate(data.getCreateTime()));
            switch (data.getUploadStatus()) {
			case 1:
				holder.tvIsUploaded.setText("未上传");
				break;
			case 2:
				holder.tvIsUploaded.setText("已上传");
				break;
			case 3:
				holder.tvIsUploaded.setText("部分上传");
				break;
			default:
				holder.tvIsUploaded.setText("异常");
				break;
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

    private class LoadTask extends AsyncTask<Void, Void, List<RawSheetIndex>> {

		@Override
		protected List<RawSheetIndex> doInBackground(Void... params) {
			return RawSheetIndexDao.defaultDao().queryAllSubsidenceSectionRawSheetIndex();
		}
		
		@Override
		protected void onPostExecute(List<RawSheetIndex> result) {
			mAdapter.setData(result);
		}
    }
}
