package com.crtb.tunnelmonitor.activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DataUploadActivity extends Activity {

    private TextView mTopbarTitle;

    private ImageView cursor;

    private int bmpW;

    private int offset = 0;

    private int currIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_upload);
        setTopbarTitle(getString(R.string.server_data_upload_title));
        initImageView();
    }

    protected void setTopbarTitle(String title) {

        if (mTopbarTitle == null) {
            mTopbarTitle = (TextView)findViewById(R.id.tv_topbar_title);
        }

        mTopbarTitle.setText(title);
    }

    private void initImageView() {

        cursor = (ImageView)findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.heng).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 2 - bmpW) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        ViewGroup.LayoutParams lp = cursor.getLayoutParams();
        lp.width = screenW >> 1;
        lp.height = 4;
        cursor.setLayoutParams(lp);
        cursor.setImageMatrix(matrix);
    }
}
