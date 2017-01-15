package in.co.krishnaconsultancy.cookmymeal.utility;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImagesUtility extends AsyncTask<ImageView, Void, Bitmap> {

    ImageView imageView = null;
    
    public DownloadImagesUtility(int position, ImageView imageView) {

    }

    @Override
    protected Bitmap doInBackground(ImageView... imageViews) {
        this.imageView = imageViews[0];
        Bitmap bmp = null;
        try{
            URL ulrn = new URL((String)imageView.getTag());
            HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
            InputStream is = con.getInputStream();
            bmp = BitmapFactory.decodeStream(is);
            if (null != bmp)
                return bmp;
        }catch(Exception e){}
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }
}