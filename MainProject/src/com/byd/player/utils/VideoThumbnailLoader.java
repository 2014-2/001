package com.byd.player.utils;


import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.byd.player.R;
/**
 * 
 * @author Des
 *
 */
public class VideoThumbnailLoader {
    
    public static void loadBitmap(String urlStr, ImageView image) {  
        LoadImageTask asyncLoader = new LoadImageTask(image, mLruCache);//什么一个异步图片加载对象  
        Bitmap bitmap = asyncLoader.getBitmapFromMemoryCache(urlStr);//首先从内存缓存中获取图片  
        if (bitmap != null) {  
            image.setImageBitmap(bitmap);//如果缓存中存在这张图片则直接设置给ImageView  
        } else {  
            image.setImageResource(R.drawable.video_file);//否则先设置成默认的图片  
            asyncLoader.execute(urlStr);//然后执行异步任务AsycnTask 去网上加载图片  
        }  
    }  
    
    //获取当前应用程序所分配的最大内存
    private final static int maxMemory = (int) Runtime.getRuntime().maxMemory();  
    private final static int cacheSize = maxMemory / 4;//只分5分之一用来做图片缓存  
    private static LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(  
            cacheSize) {  
        @Override  
        protected int sizeOf(String key, Bitmap bitmap) {//复写sizeof()方法  
            // replaced by getByteCount() in API 12  
            return bitmap.getRowBytes() * bitmap.getHeight() / 1024; //这里是按多少KB来算  
        }  
    };  
   
}  

class LoadImageTask extends AsyncTask<String, Void, Bitmap> {  
    private ImageView image;  
    private static LruCache<String, Bitmap> lruCache;
  
    /** 
     * 构造方法，需要把ImageView控件和LruCache 对象传进来 
     * @param image 加载图片到此 {@code}ImageView 
     * @param lruCache 缓存图片的对象 
     */  
    public LoadImageTask(ImageView image, LruCache<String, Bitmap> cache) {  
        super();  
        this.image = image;  
        lruCache = cache;
    }  
  
    @Override  
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        String videoPath = params[0];
        bitmap = getVideoThumbnail(videoPath, 150, 120,
                MediaStore.Images.Thumbnails.MICRO_KIND);
        addBitmapToMemoryCache(params[0], bitmap);
        return bitmap;
    }  
  
    @Override  
    protected void onPostExecute(Bitmap bitmap) {  
        image.setImageBitmap(bitmap);  
    }  
    
    //调用LruCache的put 方法将图片加入内存缓存中，要给这个图片一个key 方便下次从缓存中取出来  
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {  
        if (getBitmapFromMemoryCache(key) == null) {  
            lruCache.put(key, bitmap);  
        }  
    }  
    
    //调用Lrucache的get 方法从内存缓存中去图片  
    public Bitmap getBitmapFromMemoryCache(String key) {  
        return lruCache.get(key);  
    }  
    
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
            int kind) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}