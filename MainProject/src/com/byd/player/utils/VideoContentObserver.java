package com.byd.player.utils;

import android.database.ContentObserver;
import android.os.Handler;

public class VideoContentObserver extends ContentObserver {  
    public static final int INTERNAL_VIDEO_CONTENT_CHANGED = 0; 
    public static final int EXTERNAL_VIDEO_CONTENT_CHANGED = 1; 
    public static final int HISTORY_VIDEO_CONTENT_CHANGED = 3; 
      
    private Handler mHandler;  //此Handler用来更新UI线程  
    private int observer_type = 0;
      
    public VideoContentObserver(int type, Handler handler) {  
        super(handler);  
        mHandler = handler;
        observer_type = type;
    }  
  
    @Override  
    public void onChange(boolean selfChange) {  
        if(observer_type == INTERNAL_VIDEO_CONTENT_CHANGED) {
            mHandler.obtainMessage(INTERNAL_VIDEO_CONTENT_CHANGED).sendToTarget();  
        } else {
            mHandler.obtainMessage(EXTERNAL_VIDEO_CONTENT_CHANGED).sendToTarget();
        }
    }  
  
}  