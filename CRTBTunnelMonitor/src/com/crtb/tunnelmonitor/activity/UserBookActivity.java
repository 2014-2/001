package com.crtb.tunnelmonitor.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class UserBookActivity extends Activity {

    private static final String TAG = "UserBookActivity";

    private static final String fileName = "dtms_userbook.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userbook_layout);
        init();
    }

    private void init() {
        TextView title = (TextView) findViewById(R.id.tv_topbar_title);
        title.setText(R.string.user_book);
    }

    public void viewUserbookClick(View v) {
        viewUserBook();
    }

    private void viewUserBook() {
        copyReadAssets();
        File file = new File(getFilesDir() + File.separator + fileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void copyReadAssets() {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;

        File file = new File(getFilesDir(), fileName);
        if (!file.exists()) {
            try {
                in = assetManager.open(fileName);
                out = openFileOutput(file.getName(),
                        Context.MODE_WORLD_READABLE);

                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        } else {
            Log.d(TAG, "userbook already there!");
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
