package com.byd.player.audio;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.byd.player.R;
import com.byd.player.audio.AudioLoaderManager.DataListener;
import com.byd.player.utils.ToastUtils;

public class AudioAdapter extends BaseAdapter implements DataListener {

    private List<AudioItem> mData = new ArrayList<AudioItem>();
    private Context mContext = null;
    private LayoutInflater mInflater;

    private int mMode = -1;

    public AudioAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        AudioLoaderManager.getInstance().addDataListener(this);
    }

    public void setData(List<Song> data) {
        mData.clear();
        if (data == null || data.isEmpty()) {
        } else {
            for (Song song : data) {
                mData.add(new AudioItem(song));
            }
        }
        notifyDataSetChanged();
    }

    public void setMode(int mode) {
        if (mMode == mode) {
            return;
        }
        mMode = mode;
        onDataChange();
        if (AudioListActivity.MODE_EDIT != mMode) {
            for (AudioItem item : mData) {
                item.setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    public boolean isNormalMode() {
        return mMode == AudioListActivity.MODE_NORMAL;
    }

    public boolean isEditMode() {
        return mMode == AudioListActivity.MODE_EDIT;
    }

    public boolean isSearchMode() {
        return mMode == AudioListActivity.MODE_SEARCH;
    }

    public void setItemSelected(int pos) {
        if (isEditMode()) {
            AudioItem item = getItem(pos);
            item.setSelected(!item.isSelected());
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public AudioItem getItem(int pos) {
        return mData.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.audio_item, null);

            viewHolder = new ViewHolder();
            viewHolder.mIamgeAlbum = (ImageView) convertView.findViewById(R.id.audio_album);
            viewHolder.mTextAudioName = (TextView) convertView.findViewById(R.id.audio_name);
            viewHolder.mTextAudioSinger = (TextView) convertView.findViewById(R.id.audio_singer);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.audio_checkbox);
            // construct an item tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AudioItem item = getItem(pos);
        if (item.getAlbum() != null) {
            viewHolder.mIamgeAlbum.setImageBitmap(item.getAlbum());
        } else {
            viewHolder.mIamgeAlbum.setImageResource(R.drawable.ablum_null);
        }
        viewHolder.mTextAudioName.setText(item.getAudioName());
        viewHolder.mTextAudioSinger.setText(item.getSinger());

        if (isEditMode()) {
            viewHolder.mCheckBox.setVisibility(View.VISIBLE);
            viewHolder.mCheckBox.setChecked(item.isSelected());
        } else {
            viewHolder.mCheckBox.setVisibility(View.GONE);
        }

        return convertView;
    }

    private final class ViewHolder {
        ImageView mIamgeAlbum;
        TextView mTextAudioName;
        TextView mTextAudioSinger;
        CheckBox mCheckBox;
    }

    @Override
    public void onDataChange() {
        List<Song> result = null;
        if (!isSearchMode()) {
            result = AudioLoaderManager.getInstance().getViewSongs();
        }
        if (result == null || result.isEmpty()) {
            ToastUtils.showToast(mContext, "未找到相关歌曲");
        }
        setData(result);
    }

    public List<Song> getSeletedSongs() {
        List<Song> songs = new ArrayList<Song>();
        if (isEditMode()) {
            for (AudioItem item : mData) {
                if (item.isSelected()) {
                    songs.add(item.getSong());
                }
            }
        }

        return songs;
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<Song>();
        if (isEditMode()) {
            for (AudioItem item : mData) {
                songs.add(item.getSong());
            }
        }
        return songs;
    }
}
