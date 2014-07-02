package ru.avelier.pwcats.myapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Adelier on 03.07.2014.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        // TODO think of bmImage.setImageURI()
        float scale = (float)bmImage.getHeight() / result.getHeight();
        bmImage.setScaleX( scale );
        bmImage.setScaleY( scale );
    }

    public static String getIconUrl(int id) {
        return String.format("http://www.pwdatabase.com/images/icons/generalm/%d.gif", id);
    }
}
