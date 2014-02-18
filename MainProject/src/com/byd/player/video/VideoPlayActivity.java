package com.byd.player.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.byd.player.BrowserActivity;
import com.byd.player.R;
import com.byd.player.config.Constants;
import com.byd.player.history.BYDDatabase;
import com.byd.player.history.PlayRecord;
import com.byd.player.video.VideoView.MySizeChangeLinstener;
/**
 * 
 * @author Des
 *
 */
public class VideoPlayActivity extends Activity {

	public static final String POINT = "POINT";
	private final static int REPEAT_NONE = 0;
	private final static int REPEAT_ALL = 1;
	private static int repeatMode = REPEAT_ALL;

	private VideoView mMediaPlayer = null;
	private SeekBar seekBar = null;
	private TextView durationTextView = null;
	private TextView playedTextView = null;
	private GestureDetector mGestureDetector = null;

	private ImageButton btnPlayPause = null;

	private PopupWindow controlerWindow = null;
	private PopupWindow titleWindow = null;

	private static int screenWidth = 0;
	private static int screenHeight = 0;
	private static int controlHeight = 0;

	private final static int TIME = 5000;

	private boolean isControllerShow = true;
	private boolean isPaused = false;
	private boolean isFullScreen = false;
	private String mVideoUrl;
	private String mediaName;
	private int duration;

	private TextView tvTitle;
	protected int statusBarHeight = 38;
	private RelativeLayout progressLayout;
	private LinearLayout mVideoView;
	private View titleView;
	private View controlView;
	private RelativeLayout rlFailedToPlay;
	private ImageView iv_sound_ctrl;
	private TextView tv_failed;
	private SoundVolumeView sv_ctrlbar;
	private View rl_volume_bar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	   requestWindowFeature(Window.FEATURE_NO_TITLE);
	   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.video_play_view);

		final TextView tv1 = (TextView) findViewById(R.id.VideoViewTest);
		tv1.post(new Runnable() {
			public void run() {
				Rect rect = new Rect();
				tv1.getWindowVisibleDisplayFrame(rect);
				statusBarHeight = rect.top;
				updateErrorIcon();
			}
		});

		getScreenSize();

		Looper.myQueue().addIdleHandler(new IdleHandler() {

			@Override
			public boolean queueIdle() {

				if (controlerWindow != null && mMediaPlayer != null && mMediaPlayer.isShown()) {
					controlerWindow.showAtLocation(mMediaPlayer, Gravity.BOTTOM, 0, 0);
					controlerWindow.update(0, 0, screenWidth, controlHeight);
				}
				if (titleWindow != null && mMediaPlayer != null && mMediaPlayer.isShown()) {
					titleWindow.showAtLocation(mMediaPlayer, Gravity.TOP, 0, 0);
					titleWindow.update(0, statusBarHeight, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
				}

				return false;
			}
		});
		
		Bundle bundle = getIntent().getBundleExtra(Constants.VIDEO_PLAY_PARAMS);
		
		mVideoUrl = bundle.getString("video_url");
		mediaName = bundle.getString("name");
		duration = bundle.getInt("duration", 0);
		
		titleView = getLayoutInflater().inflate(R.layout.header_structure, null);
		
		titleWindow = new PopupWindow(titleView);

		tvTitle = (TextView) titleView.findViewById(R.id.button_header_title);
		tvTitle.setText(mVideoUrl);

		controlView = getLayoutInflater().inflate(R.layout.video_play_view_control, null);
		controlView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});
		controlerWindow = new PopupWindow(controlView);
		durationTextView = (TextView) controlView.findViewById(R.id.duration_time);
		playedTextView = (TextView) controlView.findViewById(R.id.current_time);

		btnPlayPause = (ImageButton) controlView.findViewById(R.id.btnPlayPause);
		progressLayout = (RelativeLayout) findViewById(R.id.ll_progress);
		dismissProgress();

		ImageView btnhome = (ImageView) titleView.findViewById(R.id.button_header_left);
		btnhome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mVideoView = (LinearLayout) findViewById(R.id.mVideoView);
		mMediaPlayer = new VideoView(this);
		mVideoView.addView(mMediaPlayer);

		btnPlayPause.setOnClickListener(new OnClickListener() {
			@Override
            public void onClick(View view) {
                mHandler.removeMessages(ERROR_HAPPENED);
                // resetVideoView();
                isFirst = true;
                // seekBar.setProgress(0);
                // seekBar.setSecondaryProgress(0);
                cancelDelayHide();
                if (isPaused) {
                    if (!mMediaPlayer.isUrlEmpty()) {
                        mMediaPlayer.start();
                        btnPlayPause.setImageResource(R.drawable.button_pause);
                        hideControllerDelay();
                        isPaused = false;
                    }
                    else {
                        playVideo();
                    }
                }
                else {
                    if (!mMediaPlayer.isUrlEmpty()) {
                        mMediaPlayer.pause();
                        btnPlayPause.setImageResource(R.drawable.button_play);
                        hideControllerDelay();
                        isPaused = true;
                    }
                    else {
                        playVideo();
                    }
                }
            }
		});

		seekBar = (SeekBar) controlView.findViewById(R.id.progress_bar);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				if (fromUser) {
					if (rlFailedToPlay.getVisibility() != View.VISIBLE) {
						showProgress();
						mMediaPlayer.pause();
						seekBar.setProgress(progress);
						mHandler.removeMessages(PROGRESS_CHANGED);
						mHandler.removeMessages(SEEKTO);
						Message msg = mHandler.obtainMessage();
						msg.what = SEEKTO;
						msg.arg1 = progress;
						mHandler.sendMessageDelayed(msg, 1000);
						// mMediaPlayer.start();
					} else {
						seekBar.setProgress(0);
						seekBar.setSecondaryProgress(0);
					}

				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				mHandler.removeMessages(HIDE_CONTROLER);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
			}
		});
		
		controlView.findViewById(R.id.RL_LinearLayoutButtonsBar).setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View view) {
              cancelDelayHide();
              hideControllerDelay();
          }
		});

		mGestureDetector = new GestureDetector(new SimpleOnGestureListener() {

			@Override
			public boolean onDoubleTap(MotionEvent e) {

				if (isFullScreen) {
					setVideoScale(SCREEN_DEFAULT);
				} else {
					setVideoScale(SCREEN_FULL);
				}
				isFullScreen = !isFullScreen;

				if (isControllerShow) {
					showController();
				}
				return true;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {

				if (!isControllerShow) {
					showController();
					hideControllerDelay();
				} else {
					cancelDelayHide();
					hideController();
				}
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {

				if (isPaused) {
					mMediaPlayer.start();
					btnPlayPause.setImageResource(R.drawable.button_pause);
					cancelDelayHide();
					hideControllerDelay();
				} else {
					mMediaPlayer.pause();
					btnPlayPause.setImageResource(R.drawable.button_play);
					cancelDelayHide();
					showController();
					hideControllerDelay();
				}
				isPaused = !isPaused;
			}
		});

		mMediaPlayer.setOnErrorListener(mErrorListener);
		mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
		mMediaPlayer.setOnCompletionListener(mCompletionListener);
		mMediaPlayer.setMySizeChangeLinstener(mySizeChangeLinstener);
		mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
		int playType = bundle.getInt("repeat_mode");
		if (playType == 0) {
			repeatMode = REPEAT_ALL;
		}

		Uri uri = Uri.parse(mVideoUrl);
		if (uri != null) {
			mMediaPlayer.stopPlayback();
			mMediaPlayer.setVideoURI(uri);
			showProgress();
			isPaused = false;
			btnPlayPause.setImageResource(R.drawable.button_pause);
		} else {
			isPaused = true;
			btnPlayPause.setImageResource(R.drawable.button_play);
		}

		if (mMediaPlayer.isPlaying()) {
			dismissProgress();
			btnPlayPause.setImageResource(R.drawable.button_pause);
			isPaused = false;
			hideControllerDelay();
		}

		// Pulque edited at 2012-9-19 10:35:32am
		rlFailedToPlay = (RelativeLayout) findViewById(R.id.ll_failed_to_play);
		tv_failed = (TextView) findViewById(R.id.tv_failed);

		// Pulque edited at 2012-9-19 6:06:46pm
		iv_sound_ctrl = (ImageView) controlView.findViewById(R.id.iv_sound_ctrl);
		// mCtrlDialog = new SoundCtrlDialog(this, iv_sound_ctrl);
		// mCtrlDialog.updateSoundIcon(iv_sound_ctrl);
		// mCtrlDialog.setCallBackHandler(myHandler, HIDE_CONTROLER, TIME);
		iv_sound_ctrl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sv_ctrlbar != null) {
					if (sv_ctrlbar.getVisibility() == View.VISIBLE) {
						sv_ctrlbar.setVisibility(View.GONE);
						cancelDelayHide();
						hideControllerDelay();
					} else {
						sv_ctrlbar.updateSoundIcon();
						sv_ctrlbar.setVisibility(View.VISIBLE);
						showController();
						cancelDelayHide();
					}
				}
			}
		});

		rl_volume_bar = controlView.findViewById(R.id.rl_volume_bar);
		sv_ctrlbar = (SoundVolumeView) controlView.findViewById(R.id.sv_ctrlbar);
		sv_ctrlbar.initSoundView(iv_sound_ctrl, rl_volume_bar);
		sv_ctrlbar.setCallBackHandler(mHandler, HIDE_CONTROLER, TIME);
		sv_ctrlbar.updateSoundIcon();
		updateErrorIcon();
	}

	private void updateErrorIcon() {
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			int temp = (int) (screenHeight - (int) (statusBarHeight * 2 + 5 * dm.density) - 100 * dm.density);
			RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(
					(int) (286 * dm.density), temp > (int) (217 * dm.density) ? (int) (217 * dm.density)
							: temp);
			mLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			if (screenHeight <= 720) {
				mLayoutParams.setMargins(0, (int) (48 * dm.density), 0, (int) (10 * dm.density));
			} else {
				mLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			}

			rlFailedToPlay.setLayoutParams(mLayoutParams);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(
					(int) (286 * dm.density),
					(screenHeight - statusBarHeight - (int) (217 * dm.density) > (int) (217 * dm.density) ? (int) (217 * dm.density)
							: (screenHeight - statusBarHeight - (int) (217 * dm.density))));
			mLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			mLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			rlFailedToPlay.setLayoutParams(mLayoutParams);
		}
	}

	private void dismissProgress() {
		progressLayout.setVisibility(View.GONE);
	}

	private void showProgress() {
		progressLayout.setVisibility(View.VISIBLE);
	}

	private final static int PROGRESS_CHANGED = 0;
	private final static int HIDE_CONTROLER = 1;
	private static final int ERROR_HAPPENED = 2;
	private static final int SEEKTO = 3;
	private static final int CLICK_DELAY = 4;
	private boolean isFirst = true;
	protected int CurrentP;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case SEEKTO:
				if (mMediaPlayer != null) {
					mMediaPlayer.seekTo(msg.arg1);
				}
				break;
			case PROGRESS_CHANGED:
				int j = mMediaPlayer.getBufferPercentage();
				if (j == 100 && isFirst) {
					j = 0;
				} else {
					isFirst = false;
				}
				seekBar.setSecondaryProgress(j * seekBar.getMax() / 100);
				int i = mMediaPlayer.getCurrentPosition();
				if (i != 0) {
					CurrentP = i;
				}
				seekBar.setProgress(i);
				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;
				playedTextView.setText(String.format("%02d:%02d:%02d", hour, minute, second));

				sendEmptyMessageDelayed(PROGRESS_CHANGED, 100);
				break;

			case HIDE_CONTROLER:
				hideController();
				break;
			case ERROR_HAPPENED:
				// Pulque edited at 2012-9-12 10:38:51AM

				try {
					// if (dialog != null)
					// if (dialog.isShowing()) {
					// dialog.dismiss();
					// } else {
					// break;
					// }
					if (rlFailedToPlay != null)
						if (rlFailedToPlay.getVisibility() == View.VISIBLE) {
							rlFailedToPlay.setVisibility(View.GONE);
						} else {
							break;
						}

				} catch (Exception e) {
					Log.w("do not care", "View not attached to window manager");
				}
				break;
			case CLICK_DELAY:
			    ((View) msg.obj).setClickable(true);
			    break;
			}

			super.handleMessage(msg);
		}
	};
	private DisplayMetrics dm;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.e("onConfigurationChanged", "onConfigurationChanged");
		getScreenSize();
		updateErrorIcon();
		if (isControllerShow) {
			cancelDelayHide();
			hideController();
			showController();
			hideControllerDelay();
		}
		isFullScreen = false;
		setVideoScale(SCREEN_DEFAULT);
		if (isControllerShow) {
			showController();
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		myPause();

		super.onPause();
	}

	private void myPause() {
		btnPlayPause.setImageResource(R.drawable.button_play);
		isPaused = true;
		if (mHandler != null) {
			mHandler.removeMessages(ERROR_HAPPENED);
		}
	}
	
	@Override
	protected void onResume() {
		CurrentP = ReadSharedPreferences(mVideoUrl);
		sv_ctrlbar.updateSoundIcon();
		if (!mMediaPlayer.isUrlEmpty()) {
			if (CurrentP != 0) {
				showProgress();
				btnPlayPause.setClickable(false);
				mMediaPlayer.seekTo(CurrentP);
				mMediaPlayer.start();
				mMediaPlayer.pause();
			} else {
				showProgress();
				btnPlayPause.setClickable(true);
			}
		}
		cancelDelayHide();
		showController();
		hideControllerDelay();
		mHandler.removeMessages(0);
		super.onResume();
	}
	
	private void savePlayRecord() {
	    BrowserActivity.tabContentChanged[BrowserActivity.TAB_INDEX_HISTORY] = true;
       PlayRecord playRecord = new PlayRecord();
       MovieInfo movieInfo = new MovieInfo();
       movieInfo.path = mVideoUrl;
       movieInfo.displayName = mediaName;
       movieInfo.duration = duration;
       playRecord.setMovieInfo(movieInfo);
       playRecord.setLastPlayPosition(mMediaPlayer.getCurrentPosition());
       playRecord.setTime(System.currentTimeMillis());
       BYDDatabase.getInstance(this).insertVideoPlayRecord(playRecord);
   }

	@Override
    protected void onStop() {
        super.onStop();
        savePlayRecord();
    }

    @Override
	protected void onDestroy() {
		if (controlerWindow.isShowing()) {
			controlerWindow.dismiss();
			titleWindow.dismiss();
		}
		if (controlerWindow.isShowing()) {
			controlerWindow.dismiss();
		}

		mHandler.removeMessages(HIDE_CONTROLER);
		mHandler.removeMessages(PROGRESS_CHANGED);

		if (mMediaPlayer != null) {
			mMediaPlayer.stopPlayback();
			mMediaPlayer.setOnErrorListener(null);
			mMediaPlayer.setOnPreparedListener(null);
			mMediaPlayer.setOnCompletionListener(null);
			mMediaPlayer.setMySizeChangeLinstener(null);
			mMediaPlayer.setOnSeekCompleteListener(null);
		}
		mVideoView.removeAllViews();
		mMediaPlayer = null;
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		boolean result = mGestureDetector.onTouchEvent(event);

		if (!result) {
			if (event.getAction() == MotionEvent.ACTION_UP) {

				
				 /* if(!isControllerShow){ showController(); hideControllerDelay(); }else { cancelDelayHide();
				 * hideController(); }*/
				 
			}
			result = super.onTouchEvent(event);
		}
		return result;

	}

    private void getScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
		dm = new DisplayMetrics();
		dm = getApplicationContext().getResources().getDisplayMetrics();
		// int screenWidth = dm.widthPixels;
		// int screenHeight = dm.heightPixels;
//		controlHeight = (int) (300 * dm.density);
		controlHeight = screenHeight;

	}

	private void hideController() {
		if (controlerWindow.isShowing()) {
			controlView.setVisibility(View.GONE);
			titleView.setVisibility(View.GONE);
			controlerWindow.update(0, 0, 0, 0);
			titleWindow.update(0, 0, 0, 0);
			isControllerShow = false;
		}
	}

	private void hideControllerDelay() {
		mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
	}

	private void showController() {
		controlView.setVisibility(View.VISIBLE);
		titleView.setVisibility(View.VISIBLE);
		controlerWindow.update(0, 0, screenWidth, controlHeight);
		if (isFullScreen) {
			titleWindow.update(0, 0, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
		} else {
			titleWindow.update(0, statusBarHeight, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		isControllerShow = true;
	}

	private void cancelDelayHide() {
		mHandler.removeMessages(HIDE_CONTROLER);
	}

	private final static int SCREEN_FULL = 0;
	private final static int SCREEN_DEFAULT = 1;

	private void setVideoScale(int flag) {

		switch (flag) {
		case SCREEN_FULL:

			mMediaPlayer.setVideoScale(screenWidth, screenHeight);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			break;

		case SCREEN_DEFAULT:

			int videoWidth = mMediaPlayer.getVideoWidth();
			int videoHeight = mMediaPlayer.getVideoHeight();
			int mWidth = screenWidth;
			int mHeight = screenHeight - statusBarHeight;

			if (videoWidth > 0 && videoHeight > 0) {
				if (videoWidth * mHeight > mWidth * videoHeight) {
					// Log.i("@@@", "image too tall, correcting");
					mHeight = mWidth * videoHeight / videoWidth;
				} else if (videoWidth * mHeight < mWidth * videoHeight) {
					// Log.i("@@@", "image too wide, correcting");
					mWidth = mHeight * videoWidth / videoHeight;
				} else {

				}
			}

			mMediaPlayer.setVideoScale(mWidth, mHeight);

			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			break;
		}
	}



	private void playVideo() {
		rlFailedToPlay.setVisibility(View.GONE);
		mMediaPlayer.setVideoPath(mVideoUrl);
		showProgress();
		cancelDelayHide();
		hideControllerDelay();
		isPaused = false;
		btnPlayPause.setImageResource(R.drawable.button_pause);
	}

	private void resetVideoView() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stopPlayback();
		}
		mVideoView.removeAllViews();
		mMediaPlayer = null;
		mMediaPlayer = new VideoView(VideoPlayActivity.this);
		mMediaPlayer.setOnErrorListener(mErrorListener);
		mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
		mMediaPlayer.setOnCompletionListener(mCompletionListener);
		mMediaPlayer.setMySizeChangeLinstener(mySizeChangeLinstener);
		mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
		mVideoView.addView(mMediaPlayer);
	}

	private OnErrorListener mErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			cancelDelayHide();
			showController();
			hideControllerDelay();
			mMediaPlayer.stopPlayback();
			isPaused = true;
			btnPlayPause.setImageResource(R.drawable.button_play);
			if (rlFailedToPlay != null) {
				rlFailedToPlay.setVisibility(View.VISIBLE);
			}
			dismissProgress();
			mMediaPlayer.setmUri(null);
			if (repeatMode == REPEAT_ALL) {
				mHandler.sendEmptyMessageDelayed(ERROR_HAPPENED, Constants.ERROR_DISMISS_THREE_SECONDS);
				tv_failed.setText(getString(R.string.failed_to_play_next));
			} else {
				tv_failed.setText(getString(R.string.failed_to_play));
			}
			return false;
		}

	};

	private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer arg0) {
			System.out.println("mMediaPlayer.onPrepared");
			dismissProgress();
			isFullScreen = false;
			setVideoScale(SCREEN_DEFAULT);
			isFullScreen = false;
			if (isControllerShow) {
				showController();
			}
			int i = mMediaPlayer.getDuration();
			seekBar.setMax(i);
			i /= 1000;
			int minute = i / 60;
			int hour = minute / 60;
			int second = i % 60;
			minute %= 60;
			durationTextView.setText(String.format("%02d:%02d:%02d", hour, minute, second));
			if (!isPaused) {
				CurrentP = ReadSharedPreferences(mVideoUrl);
				mMediaPlayer.seekTo(CurrentP);
				mMediaPlayer.start();
				btnPlayPause.setImageResource(R.drawable.button_pause);
				isPaused = false;
			}
			mHandler.sendEmptyMessage(PROGRESS_CHANGED);
			hideControllerDelay();
			isFirst = true;
		}
	};
	private OnCompletionListener mCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer arg0) {
			mHandler.removeMessages(PROGRESS_CHANGED);
			CurrentP = 0;
			WriteSharedPreferences(mVideoUrl, CurrentP);
			if (repeatMode == REPEAT_NONE) {
				finish();
				return;
			}
			isFirst = true;
			seekBar.setProgress(0);
			seekBar.setSecondaryProgress(0);
			btnPlayPause.setImageResource(R.drawable.button_play);
			isPaused = true;
			System.out.println(" repeatMode :  " + repeatMode);
			switch (repeatMode) {
			case REPEAT_NONE:
				break;
			case REPEAT_ALL:
				resetVideoView();
				//TODO
				//mVideoUrl = "next";
				playVideo();
				break;
			}
		}
	};
	private MySizeChangeLinstener mySizeChangeLinstener = new MySizeChangeLinstener() {

		@Override
		public void onSizeChanged() {
			getScreenSize();
			if (isControllerShow) {
				cancelDelayHide();
				hideController();
				showController();
				hideControllerDelay();
			}
			isFullScreen = false;
			setVideoScale(SCREEN_DEFAULT);
		}

	};

	OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {

		@Override
		public void onSeekComplete(MediaPlayer mp) {
			dismissProgress();
			btnPlayPause.setClickable(true);
			if (!isPaused && !mMediaPlayer.isPlaying()) {
				mMediaPlayer.start();
				mHandler.sendEmptyMessageDelayed(PROGRESS_CHANGED, 100);
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			// mCtrlDialog.updateSoundIcon(iv_sound_ctrl);
			sv_ctrlbar.updateSoundIcon();
			sv_ctrlbar.setVisibility(View.GONE);
			showController();
			cancelDelayHide();
			hideControllerDelay();
		}
		return super.onKeyDown(keyCode, event);
	}

	// Pulque edited at 2012-10-23 10:42:07am
	// /data/data/<package name>/shared_prefs/itcast.xml
	private int ReadSharedPreferences(String propId) {
		SharedPreferences user = getSharedPreferences(POINT, Context.MODE_PRIVATE);
		return user.getInt(propId, 0);
	}

	private void WriteSharedPreferences(String propId, int CurrentP) {
		SharedPreferences user = getSharedPreferences(POINT, Context.MODE_PRIVATE);
		user.edit().putInt(propId, CurrentP).commit();
	}
	
	
	public void clickDelayHandler(final View view) {
		view.setClickable(false);
		Message msg = new Message();
		msg.what = 0;
		msg.obj = view;
	}

	
	/**
	 * Just for test only
	 * @param context
	 */
	public static void playVideoItem(Context context) {
       
       String[] projection = { MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATA };
       String selection = "";
       String [] selectionArgs = null;
       Cursor videoCursor = context.getContentResolver().query( MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                    projection, selection, selectionArgs, null );
       if ( videoCursor != null ) {
           videoCursor.moveToFirst();
           videoCursor.moveToNext();
           String videoUrl = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
           Intent intent = new Intent(context, VideoPlayActivity.class);
           Bundle params = new Bundle();
           params.putString("video_url", videoUrl);
           intent.putExtra(Constants.VIDEO_PLAY_PARAMS, params);
           context.startActivity(intent);
       } else {
       }
    }
	
	
}