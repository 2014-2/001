package com.byd.player;

import java.util.LinkedList;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	public static LinkedList<MovieInfo> playList = new LinkedList<MovieInfo>();
	public class MovieInfo{
		String displayName;  
		String path;
	}
	private Uri videoListUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        getVideoList();
        
        //VideoPlayActivity.playVideoItem(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void getVideoList(){
    	if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
        	Cursor cursor = getContentResolver().query(videoListUri, new String[]{"_display_name","_data"}, null, null, null);
            int n = cursor.getCount();
            cursor.moveToFirst();
            LinkedList<MovieInfo> playList2 = new LinkedList<MovieInfo>();
            for(int i = 0 ; i != n ; ++i){
            	MovieInfo mInfo = new MovieInfo();
            	mInfo.displayName = cursor.getString(cursor.getColumnIndex("_display_name"));
            	mInfo.path = cursor.getString(cursor.getColumnIndex("_data"));
            	playList2.add(mInfo);
            	cursor.moveToNext();
            }
            
            if(playList2.size() > playList.size()){
            	playList = playList2;
            }
        }
        
        mInflater = getLayoutInflater();
        ListView myListView = (ListView) findViewById(R.id.media_list);
		myListView.setAdapter(new BaseAdapter(){

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return playList.size();
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}

			@Override
			public View getView(int arg0, View convertView, ViewGroup arg2) {
				// TODO Auto-generated method stub
				if(convertView==null){
					convertView = mInflater.inflate(R.layout.list, null);
				}
				TextView text = (TextView) convertView.findViewById(R.id.text);
				text.setText(playList.get(arg0).displayName);
				
				return convertView;   
			}
			
		});
    }
    
}
