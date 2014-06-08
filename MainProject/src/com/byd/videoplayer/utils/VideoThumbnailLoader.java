package com.byd.videoplayer.utils;


import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.ImageView;

import com.byd.videoplayer.R;
/**
 * 
 * @author Des
 *
 */
public class VideoThumbnailLoader {
    
    public static void loadBitmap(String urlStr, ImageView image) {  
        LoadImageTask asyncLoader = new LoadImageTask(image, mLruCache);//ʲôһ���첽ͼƬ���ض���  
        Bitmap bitmap = asyncLoader.getBitmapFromMemoryCache(urlStr);//���ȴ��ڴ滺���л�ȡͼƬ  
        if (bitmap != null) {  
            image.setImageBitmap(bitmap);//�����д�������ͼƬ��ֱ�����ø�ImageView  
        } else {  
            image.setImageResource(R.drawable.video_file);//���������ó�Ĭ�ϵ�ͼƬ  
            asyncLoader.execute(urlStr);//Ȼ��ִ���첽����AsycnTask ȥ���ϼ���ͼƬ  
        }  
    }  
    
    //��ȡ��ǰӦ�ó�������������ڴ�
    private final static int maxMemory = (int) Runtime.getRuntime().maxMemory();  
    private final static int cacheSize = maxMemory / 4;//ֻ��5��֮һ������ͼƬ����  
    private static LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(  
            cacheSize) {  
        @Override  
        protected int sizeOf(String key, Bitmap bitmap) {//��дsizeof()����  
            // replaced by getByteCount() in API 12  
            return bitmap.getRowBytes() * bitmap.getHeight() / 1024; //�����ǰ�����KB����  
        }  
    };  
   
}  

class LoadImageTask extends AsyncTask<String, Void, Bitmap> {  
    private ImageView image;  
    private static LruCache<String, Bitmap> lruCache;
  
    /** 
     * ���췽������Ҫ��ImageView�ؼ���LruCache ���󴫽��� 
     * @param image ����ͼƬ���� {@code}ImageView 
     * @param lruCache ����ͼƬ�Ķ��� 
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
        if(!TextUtils.isEmpty(videoPath)) {
            bitmap = getBitmapFromMemoryCache(videoPath);
            if(bitmap == null) {
                bitmap = getVideoThumbnail(videoPath, 150, 120,
                        MediaStore.Images.Thumbnails.MICRO_KIND);
                if(bitmap != null && !bitmap.isRecycled()) {
                    addBitmapToMemoryCache(videoPath, bitmap); 
                }
            }
        }
        return bitmap;
    }  
  
    @Override  
    protected void onPostExecute(Bitmap bitmap) {  
        if(bitmap != null && !bitmap.isRecycled()) {
            image.setImageBitmap(bitmap);  
        }
    }  
    
    //����LruCache��put ������ͼƬ�����ڴ滺���У�Ҫ�����ͼƬһ��key �����´δӻ�����ȡ����  
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {  
        if (getBitmapFromMemoryCache(key) == null) {  
            lruCache.put(key, bitmap);  
        }  
    }  
    
    //����Lrucache��get �������ڴ滺����ȥͼƬ  
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