package com.lika85456.levnepivo.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * loads image from url and displays it on an image view
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    final ImageView bmImage;

    @SuppressWarnings("deprecation")
    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    // @see https://stackoverflow.com/questions/13486758/android-bitmap-from-url-always-null
    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}