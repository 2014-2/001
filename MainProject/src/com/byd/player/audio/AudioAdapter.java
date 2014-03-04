package com.byd.player.audio;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.byd.player.R;
import com.byd.player.audio.AudioManager.DataListener;

public class AudioAdapter extends BaseAdapter implements DataListener {

    private List<AudioItem> mData = new ArrayList<AudioItem>();
    private Context mContext = null;
    private LayoutInflater mInflater;

    private int mMode = AudioListActivity.MODE_NORMAL;

    public AudioAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        AudioManager.getInstance().addDataListener(this);
    }

    public void setData(List<Song> data) {
        mData.clear();

        for (Song song : data) {
            mData.add(new AudioItem(song));
        }
        notifyDataSetChanged();
    }

    public void setMode(int mode) {
        if (mMode == mode) {
            return;
        }
        mMode = mode;
        if (AudioListActivity.MODE_NORMAL == mMode) {
            for (AudioItem item : mData) {
                item.setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

//    public void setDataType(int type) {
////        if (mDataType != type) {
////            mMode = AudioListActivity.MODE_NORMAL;
////            setData(AudioManager.getInstance().getSongs());
////        }
//    }

    public boolean isNormalMode() {
        return mMode == AudioListActivity.MODE_NORMAL;
    }

    public boolean isEditMode() {
        return mMode == AudioListActivity.MODE_EDIT;
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

        if (isEditMode() && item.isSelected()) {
            convertView.setSelected(item.isSelected());
            convertView.setBackgroundResource(R.drawable.audio_item_selected);
        } else {
            convertView.setSelected(item.isSelected());
            convertView.setBackgroundResource(R.drawable.audio_item_selector);
        }
        return convertView;
    }

    private final class ViewHolder {
        ImageView mIamgeAlbum;
        TextView mTextAudioName;
        TextView mTextAudioSinger;
    }

    @Override
    public void onDataChange() {
        setData(AudioManager.getInstance().getViewSongs());
    }
}
