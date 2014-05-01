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
import com.crtb.tunnelmonitor.task.SubsidenceDataManager;
import com.crtb.tunnelmonitor.task.SubsidenceDataManager.UploadSheetData;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

@SuppressLint("ValidFragment")
public class SubsidenceSectionSheetFragment extends Fragment {
    private ListView mSheetList;
    private SheetAdapter mAdapter;

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
		SubsidenceDataManager subsidenceDataManager = new SubsidenceDataManager();
		subsidenceDataManager
				.loadData(new SubsidenceDataManager.DataLoadListener() {
					@Override
					public void done(List<UploadSheetData> uploadDataList) {
						if (uploadDataList != null && uploadDataList.size() > 0) {
							List<RawSheetData> sheetDataList = new ArrayList<RawSheetData>();
							for (UploadSheetData sheetData : uploadDataList) {
								sheetDataList.add(new RawSheetData(sheetData));
							}
							mAdapter.setSheetList(sheetDataList);
						}
					}
				});
	}

    public void refreshUI() {
        loadData();
    }

    public List<UploadSheetData> getUploadData() {
        return mAdapter.getUploadData();
    }

    class SheetAdapter extends BaseAdapter {
        private List<RawSheetData> mSheetDataList;

        SheetAdapter() {
            mSheetDataList = new ArrayList<RawSheetData>();
        }

        public void setSheetList(List<RawSheetData> sheetDataList) {
            mSheetDataList = sheetDataList;
            notifyDataSetChanged();
        }

        public List<UploadSheetData> getUploadData() {
            List<UploadSheetData> uploadDataList = new ArrayList<UploadSheetData>();
            if (mSheetDataList != null && mSheetDataList.size() > 0) {
                for(RawSheetData data : mSheetDataList) {
                    if (data.isChecked()) {
                        uploadDataList.add(data.getUploadData());
                    }
                }
            }
            return uploadDataList;
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
        private UploadSheetData mUploadData;
        private boolean mIsChecked;

        public RawSheetData(UploadSheetData uploadData) {
            mUploadData = uploadData;
        }

        public UploadSheetData getUploadData() {
            return mUploadData;
        }

        public String getCreatedTime() {
            return CrtbUtils.formatDate(mUploadData.getRawSheet().getCreateTime());
        }

        public boolean isUploaded() {
            return !mUploadData.needUpload();
        }

        public void setChecked(boolean checked) {
            mIsChecked = checked;
        }

        public boolean isChecked() {
            return mIsChecked;
        }
    }
}
