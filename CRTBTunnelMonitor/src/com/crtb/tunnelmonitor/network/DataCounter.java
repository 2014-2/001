package com.crtb.tunnelmonitor.network;

import android.util.Log;

public class DataCounter {
	private static final String LOG_TAG = "DataCounter";
	private final String mName;
	private final int mTotalCount;
	private int mSuccessCount;
	private int mFailedCount;
	private CounterListener mListener;
	
	public interface CounterListener {
		/**
		 * 
		 * @param success
		 */
		public void done(boolean success);
	}
	
	public DataCounter(String name, int count, CounterListener listener) {
		mTotalCount = count;
		mName = name;
		mSuccessCount = 0;
		mFailedCount = 0;
		mListener = listener;
	}

	public synchronized void increase(boolean flag) {
		if (flag) {
			mSuccessCount++;
		} else {
			mFailedCount++;
		}
		Log.d(LOG_TAG, "Counter: " + mName + ", total count: " + mTotalCount + ", failed count: " + mFailedCount + ", success count: " + mSuccessCount);
		if (mSuccessCount + mFailedCount == mTotalCount) {
			boolean success = (mFailedCount == 0) ? true : false;
			mListener.done(success);
		}
	}
	
	public synchronized void increase(boolean flag, String data) {
		if (flag) {
			mSuccessCount++;
		} else {
			mFailedCount++;
		}
		Log.d(LOG_TAG, "Counter: " + mName + "("+ data +")" +", total count: " + mTotalCount + ", failed count: " + mFailedCount + ", success count: " + mSuccessCount);
		if (mSuccessCount + mFailedCount == mTotalCount) {
			boolean success = (mFailedCount == 0) ? true : false;
			mListener.done(success);
		}
	}
	
	public void finish(String reason) {
		Log.d(LOG_TAG, "Counter: " + mName + " finished: " + reason);
		if (mListener != null) {
			mListener.done(false);
		}
	}
	
}
