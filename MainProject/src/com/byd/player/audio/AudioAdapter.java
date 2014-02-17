package com.byd.player.audio;

import java.util.ArrayList;
import java.util.List;

import com.byd.player.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AudioAdapter extends BaseAdapter {
    private List<Song> mData = new ArrayList<Song>();
    private Context mContext = null;
    private LayoutInflater mInflater;

    public AudioAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mData = AudioManager.getInstance().getSongs();
    }

    public void setData(List<Song> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Song getItem(int pos) {
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

        viewHolder.mIamgeAlbum.setImageResource(R.drawable.test_image_album);
        viewHolder.mTextAudioName.setText(R.string.text_name);
        viewHolder.mTextAudioSinger.setText(R.string.text_singer);
        return convertView;
    }

    private final class ViewHolder {
        ImageView mIamgeAlbum;
        TextView mTextAudioName;
        TextView mTextAudioSinger;

        // ImageView mBtnDelete;
    }
}
