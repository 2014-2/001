package com.byd.audioplayer.view;

import com.byd.audioplayer.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class SoundVolumeView extends View {

    public final static String TAG = "SoundView";

    private Context mContext;
    private Bitmap bmBubble, bmVolumeBg;
    private int bubbleWidth, bubbleHeight;
    private Rect bubbleSrcRect;
    private Rect bubbleDstRect;
    private Rect volumeBGRect;
    private Paint mPaint;
    private int currentVolumeValue = 0;
    private int VOLUME_MAX = 15;

    public interface OnVolumeChangedListener {
        public void setYourVolume(int index);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(0xff0e131d);
        mPaint.setAntiAlias(true);
    }

    public SoundVolumeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        init();
        initPaint();
    }

    public SoundVolumeView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SoundVolumeView(Context context) {
        this(context, null);
    }

    private void init() {
        bubbleSrcRect = new Rect();
        bubbleDstRect = new Rect();
        volumeBGRect = new Rect();
        VOLUME_MAX = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        bmBubble = decodeResource(mContext.getResources(), R.drawable.volume_dobber);
        bmVolumeBg = decodeResource(mContext.getResources(),
                R.drawable.sound_volume_bg);
        bubbleWidth = bmBubble.getWidth();
        bubbleHeight = bmBubble.getHeight();
        volumeBGRect.set(0, 0,
                bmVolumeBg.getWidth(), bmVolumeBg.getHeight());
        bubbleSrcRect.set(0, 0, bubbleWidth, bubbleHeight);
    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getY();
        int y = (int) event.getY();
        
        if(checkTouchEventInBackgroundBounds(x, y)) {
            return false;
        }
        
        setIndexByPercent(y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                myHandler.removeMessages(DISMISS);

                break;
            case MotionEvent.ACTION_UP:
                myHandler.sendEmptyMessageDelayed(DISMISS, DISMISS_TIME);
                break;

            default:
                break;
        }
        return true;
    }
    
    private boolean checkTouchEventInBackgroundBounds(int x, int y) {
        if(volumeBGRect == null) {
            return true;
        }
        return (x >= volumeBGRect.left && x <= volumeBGRect.right 
                && y >= volumeBGRect.top + bubbleSrcRect.bottom / 2 
                && y < volumeBGRect.bottom - bubbleSrcRect.bottom / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int reverseIndex = VOLUME_MAX - currentVolumeValue;
        
        canvas.drawBitmap(bmVolumeBg, volumeBGRect, volumeBGRect, null);
        
        int y = (int)((reverseIndex * 1.0f/VOLUME_MAX) * (volumeBGRect.bottom - bubbleHeight));
        bubbleDstRect.set(0, y, 
                bubbleWidth,
                y + bubbleHeight);
        canvas.drawBitmap(bmBubble, bubbleSrcRect, bubbleDstRect, null);
        super.onDraw(canvas);
    }

    public void setVolumeValue(int value) {
        if (value > VOLUME_MAX) {
            value = VOLUME_MAX;
        } else if (value < 0) {
            value = 0;
        }
        if (currentVolumeValue != value) {
            currentVolumeValue = value;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolumeValue, 0);
        }
        invalidate();
    }

    private void setIndexByPercent(float touchY) {
        float volumePercent = 1 - (touchY*1.0f - (bubbleSrcRect.bottom/2)) / (volumeBGRect.bottom - bubbleSrcRect.bottom);
        int newValue = (int) (volumePercent * VOLUME_MAX);
        if (newValue > VOLUME_MAX) {
            newValue = VOLUME_MAX;
        } else if (newValue < 0) {
            newValue = 0;
        }
        if (currentVolumeValue != newValue) {
            currentVolumeValue = newValue;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolumeValue, 0);
        }
        invalidate();
    }

    private final int DISMISS_TIME = 1500;
    public final static int DISMISS = 520;
    private AudioManager mAudioManager;

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.GONE) {
            myHandler.removeMessages(DISMISS);
        }
        else if (visibility == View.VISIBLE) {
            myHandler.removeMessages(DISMISS);
            myHandler.sendEmptyMessageDelayed(DISMISS, DISMISS_TIME);
        }
        super.setVisibility(visibility);

    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DISMISS:
                    setVisibility(View.GONE);
                    if (callBackHandler != null) {
                        callBackHandler.sendEmptyMessageDelayed(meg, time);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Handler callBackHandler;
    private int meg;
    private int time;

    public void setCallBackHandler(Handler callBackHandler, int meg, int time) {
        this.callBackHandler = callBackHandler;
        this.meg = meg;
        this.time = time;
    }

    public void updateVolumeView() {
        currentVolumeValue = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        setVolumeValue(currentVolumeValue);
    }

}
