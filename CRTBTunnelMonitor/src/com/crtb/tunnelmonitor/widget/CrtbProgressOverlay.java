package com.crtb.tunnelmonitor.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crtb.tunnelmonitor.activity.R;

@SuppressLint("ValidFragment")
public class CrtbProgressOverlay extends PopupWindow {

	public CrtbProgressOverlay(Activity owner, ViewGroup root) {
		super(root, android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		mRoot = root;
		mToken = owner;
		initProgressOverlay();
	}

	private Activity mToken;
	private ViewGroup mRoot;
	private LinearLayout mProgressOverlay;
	private ProgressBar mUploadProgress;
	private ImageView mUploadStatusIcon;
	private TextView mUploadStatusText;

	public boolean showing() {
		if (mProgressOverlay == null) {
			return false;
		}
		return mProgressOverlay.getVisibility() == View.VISIBLE;
	}

	private boolean isUploading;

	public boolean isUploading() {
		return isUploading;
	}

	public void setUploading(boolean isUploading) {
		this.isUploading = isUploading;
	}

	public void show() {
		try
		{
		   showAtLocation(mToken.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initProgressOverlay() {
		mProgressOverlay = (LinearLayout) mRoot.findViewById(R.id.progress_overlay);
		mUploadProgress = (ProgressBar) mRoot.findViewById(R.id.progressbar);
		mUploadStatusIcon = (ImageView) mRoot.findViewById(R.id.upload_status_icon);
		mUploadStatusText = (TextView) mRoot.findViewById(R.id.upload_status_text);
		mProgressOverlay.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!isUploading()) {
					hideProgressOverlay();
				}
				return true;
			}
		});
	}

	public void showProgressOverlay(String notice) {
		mProgressOverlay.setVisibility(View.VISIBLE);
		mUploadProgress.setIndeterminate(true);
		mUploadStatusIcon.setVisibility(View.GONE);
		mUploadStatusText.setText(notice);
		isUploading = true;
		show();
	}

	public void hideProgressOverlay() {
		mProgressOverlay.setVisibility(View.GONE);
	}

	public void uploadFinish(boolean isSuccess, String notice) {
		isUploading = false;
		mUploadStatusIcon.setVisibility(View.VISIBLE);
		mUploadProgress.setIndeterminate(false);
		mUploadProgress.setProgress(100);
		if (isSuccess) {
			mUploadStatusIcon.setImageResource(R.drawable.success);
		} else {
			mUploadStatusIcon.setImageResource(R.drawable.fail);
		}
		mUploadStatusText.setText(notice);
		show();
	}
}
