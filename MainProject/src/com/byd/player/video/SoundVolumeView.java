package com.byd.player.video;

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
import android.widget.ImageView;

import com.byd.player.R;
/**
 * 
 * @author Des
 *
 */
public class SoundVolumeView extends View {
    private Paint mPaint;
    private Context mContext;
    private Bitmap bmDobber, bmVolumeValue;
    private int bitmapWidth, bitmapHeight;
    private Rect dobberSrcRect;
    private Rect dobberDstRect;
    private Rect volumeValueRect;

    private int index = -10;

    public interface OnVolumeChangedListener {
        public void onVolumeChanged(int index);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(0xff0e131d);
        mPaint.setAntiAlias(true);
        dobberSrcRect = new Rect();
        dobberDstRect = new Rect();
        volumeValueRect = new Rect();
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
        super(context, attrs);
        mContext = context;
        mAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        init();
        initPaint();
    }

    public SoundVolumeView(Context context) {
        super(context);
        mContext = context;
        mAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        init();
        initPaint();
    }

    private void init() {
        bmDobber = decodeResource(mContext.getResources(),
                R.drawable.volume_dobber);
        bmVolumeValue = decodeResource(mContext.getResources(),
                R.drawable.volume_value);
        bitmapWidth = bmDobber.getWidth();
        bitmapHeight = bmDobber.getHeight();
        initSize();
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
        int y = (int) event.getY();
        int n = y * 15 / MY_HEIGHT;
        setIndexByTouch(15 - n);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                myHandler.removeMessages(DISMISS);
                break;
            case MotionEvent.ACTION_UP:
                myHandler.sendEmptyMessageDelayed(DISMISS,
                        VOLUME_BAR_DISMISS_PERIOD);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int reverseIndex = 15 - index;
        volumeValueRect.set(0, reverseIndex * HEIGHT + rectHeight,
                bmVolumeValue.getWidth(), bmVolumeValue.getHeight());
        canvas.drawBitmap(bmVolumeValue, volumeValueRect, volumeValueRect, null);
        if (reverseIndex == 15) {
            reverseIndex = 14;
        }
        dobberSrcRect.set(0, 0, bitmapWidth, bitmapHeight);
        dobberDstRect.set((T_WIDTH - bitmapWidth) / 2, reverseIndex * HEIGHT
                + rectHeight, (T_WIDTH - bitmapWidth) / 2 + bitmapWidth,
                reverseIndex * HEIGHT + bitmapHeight + rectHeight);
        canvas.drawBitmap(bmDobber, dobberSrcRect, dobberDstRect, null);
        super.onDraw(canvas);
    }

    private final int rectHeight = 22;
    private final static int HEIGHT = 15;
    public static int MY_HEIGHT = 165;
    public static int MY_WIDTH = 57;
    public static int T_HEIGHT = 209;
    public static int T_WIDTH = 61;

    private void initSize() {
        T_WIDTH = getResources().getDimensionPixelSize(R.dimen.sound_view_width);
        T_HEIGHT = getResources().getDimensionPixelSize(R.dimen.sound_view_height);
        MY_WIDTH = T_WIDTH - 10;
        MY_HEIGHT = T_HEIGHT - 30;
    }
    
    private void setIndex(int n) {
        if (n > 15) {
            n = 15;
        }
        else if (n < 0) {
            n = 0;
        }
        if (index != n) {
            index = n;
            updateSoundIcon(index, iv_sound_ctrl);
        }
        invalidate();
    }

    private void setIndexByTouch(int n) {
        if (n > maxVolume) {
            n = maxVolume;
        }
        else if (n < 0) {
            n = 0;
        }
        current = n;
        if (index != n) {
            index = n;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
            updateSoundIcon(index, iv_sound_ctrl);
        }
        invalidate();
    }

    private final int VOLUME_BAR_DISMISS_PERIOD = 3000;
    private final static int DISMISS = 520;
    private int maxVolume = 15;
    private int current;
    private AudioManager mAudioManager;
    private ImageView iv_sound_ctrl;
    private View bg_view;

    private void updateSoundIcon(int progress, ImageView iv_sound_ctrl) {
        int index = 0;
        float per = maxVolume / 3;
        if (progress == 0) {
            index = 0;
        }
        else if (0 < progress && progress < per) {
            index = 1;
        }
        else if (per <= progress && progress < (2 * per)) {
            index = 2;
        }
        else if ((2 * per) <= progress && progress <= maxVolume) {
            index = 3;
        }

        if (iv_sound_ctrl != null) {
            switch (index) {
                case 0:
                    // iv_sound_ctrl.setImageResource(R.drawable.selector_sound_icon_level0);
                    break;
                case 1:
                    // iv_sound_ctrl.setImageResource(R.drawable.selector_sound_icon_level1);
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.GONE) {
            bg_view.setVisibility(View.GONE);
            myHandler.removeMessages(DISMISS);
        }
        else if (visibility == View.VISIBLE) {
            bg_view.setVisibility(View.VISIBLE);
            myHandler.removeMessages(DISMISS);
            myHandler.sendEmptyMessageDelayed(DISMISS,
                    VOLUME_BAR_DISMISS_PERIOD);
        }
        super.setVisibility(visibility);
    }

    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DISMISS:
                    bg_view.setVisibility(View.GONE);
                    SoundVolumeView.this.setVisibility(View.GONE);
                    if (callBackHandler != null) {
                        callBackHandler.sendEmptyMessageDelayed(meg, time);
                    }
                    break;

            }
            super.handleMessage(msg);
        }
    };

    public void initSoundView(ImageView myiv_sound_ctrl, View view) {
        this.iv_sound_ctrl = myiv_sound_ctrl;
        this.bg_view = view;
    }

    private Handler callBackHandler;
    private int meg;
    private int time;

    public void setCallBackHandler(Handler callBackHandler, int msg, int time) {
        this.callBackHandler = callBackHandler;
        this.meg = msg;
        this.time = time;
    }

    public void updateSoundIcon() {
        current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        setIndex(current);
    }

    public void lowVolume() {
        if (current != 0) {
            current -= 1;
        }
        setIndex(current);
    }

    public void upVolume() {
        if (current != maxVolume) {
            current += 1;
        }
        setIndex(current);
    }
}
