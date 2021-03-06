package com.byd.audioplayer.audio;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.byd.audioplayer.R;
import com.byd.audioplayer.audio.AudioListActivity.DeleteDialog;
import com.byd.audioplayer.audio.AudioLoaderManager.DataListener;

public class AudioAdapter extends BaseAdapter implements DataListener {

	private final static int[] AUDIO_ITEM_BGS = new int[] {
			R.drawable.bg_audio_item1, R.drawable.bg_audio_item1,
			R.drawable.bg_audio_item1, R.drawable.bg_audio_item2,
			R.drawable.bg_audio_item2, R.drawable.bg_audio_item2, };

	private List<AudioItem> mData = new ArrayList<AudioItem>();
	private Activity mActivity = null;
	private LayoutInflater mInflater;

	private int mMode = -1;

	public AudioAdapter(Activity context, LayoutInflater inflater) {
		mActivity = context;
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
			viewHolder.mIamgeAlbum = (ImageView) convertView
					.findViewById(R.id.audio_album);
			viewHolder.mAudioStatus = (ImageView) convertView
					.findViewById(R.id.audio_status);
			viewHolder.mTextAudioName = (TextView) convertView
					.findViewById(R.id.audio_name);
			viewHolder.mTextAudioSinger = (TextView) convertView
					.findViewById(R.id.audio_singer);
			viewHolder.mDelete = (ImageView) convertView
					.findViewById(R.id.audio_delete);
			viewHolder.mDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isEditMode()) {
						FragmentManager fm = mActivity.getFragmentManager();
						List<Song> songs = getSeletedSongs();
						DeleteDialog.newInstance((AudioListActivity) mActivity,
								songs).show(fm, "DELETE_DIALOG");
					}
				}
			});
			// construct an item tag
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		AudioItem item = getItem(pos);
		if (item.getAlbumBitmap() != null) {
			viewHolder.mIamgeAlbum.setImageBitmap(item.getAlbumBitmap());
		} else {
			viewHolder.mIamgeAlbum.setImageResource(R.drawable.ablum_null);
		}
		Song song = AudioPlayerManager.getInstance().getCurrentPlaySong();
		if (AudioPlayerService.mPlayer != null && song != null
				&& song.getFilePath().equals(item.getSong().getFilePath())
				&& song.getFileName().endsWith(item.getSong().getFileName())) {
			if (AudioPlayerService.mPlayer.isPlaying()) {
				viewHolder.mAudioStatus
						.setImageResource(R.drawable.audio_item_playing);
			} else {
				viewHolder.mAudioStatus
						.setImageResource(R.drawable.audio_item_pause);
			}
			viewHolder.mAudioStatus.setVisibility(View.VISIBLE);
		} else {
			viewHolder.mAudioStatus.setVisibility(View.GONE);
		}
		viewHolder.mTextAudioName.setText(item.getAudioName());
		viewHolder.mTextAudioSinger.setText(item.getSinger());

		if (isEditMode() && item.isSelected()) {
			viewHolder.mDelete.setVisibility(View.VISIBLE);
		} else {
			viewHolder.mDelete.setVisibility(View.GONE);
		}

		final int index = pos % AUDIO_ITEM_BGS.length;
		convertView.setBackgroundResource(AUDIO_ITEM_BGS[index]);
		return convertView;
	}

	private final class ViewHolder {
		ImageView mIamgeAlbum;
		ImageView mAudioStatus;
		TextView mTextAudioName;
		TextView mTextAudioSinger;
		ImageView mDelete;
	}

	@Override
	public void onDataChange() {
		List<Song> result = null;
		if (!isSearchMode()) {
			result = AudioLoaderManager.getInstance().getViewSongs();
			setData(result);
		}
	}

	@Override
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
