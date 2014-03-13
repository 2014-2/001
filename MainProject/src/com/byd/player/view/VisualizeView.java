package com.byd.player.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.byd.player.R;

public class VisualizeView extends SurfaceView implements SurfaceHolder.Callback2{
    private MyThread mThread;
    private SurfaceHolder mHolder;

    public VisualizeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VisualizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VisualizeView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mThread = new MyThread(mHolder);
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mThread.isAlive()) {
            mThread = new MyThread(holder);
        }
        mThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread.interrupt();
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    private class MyThread extends Thread{
        SurfaceHolder mSurface;
        public MyThread(SurfaceHolder surface) {
            mSurface = surface;
        }
        @Override
        public void run() {
            try {
                while(!isInterrupted()) {
                    synchronized(mSurface) {
                        Canvas c = mSurface.lockCanvas();
                        Paint p = new Paint();
                        c.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                        Bitmap flash_point = ((BitmapDrawable)getResources().getDrawable(R.drawable.visualize_flash_point)).getBitmap();
                        Bitmap bg = ((BitmapDrawable)getResources().getDrawable(R.drawable.audio_pause_icon_bg)).getBitmap();
                        c.drawBitmap(bg, 0, 0, p);
                        int count1 = (int)(Math.random()*7);
                        for (int i =0; i<=count1;i++){
                            c.drawBitmap(flash_point, 0, 18-3*i, p);
                        }
                        int count2 = (int)(Math.random()*7);
                        for (int i =0; i<=count2;i++){
                            c.drawBitmap(flash_point, bg.getWidth()/2+1, 18-3*i, p);
                        }
                        mSurface.unlockCanvasAndPost(c);
                        sleep(500);
                    }
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }

    }

}
