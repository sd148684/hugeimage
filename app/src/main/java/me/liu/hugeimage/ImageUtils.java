package me.liu.hugeimage;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;

public class ImageUtils {

    public static void loadSampling(final GestureImageView view, final String imagePath,
                                    @Nullable final ContentLoadingProgressBar progressBar) {
        final int reqHeight = view.getMeasuredHeight();
        final int reqWidth = view.getMeasuredWidth();
        if(progressBar != null) {
            progressBar.show();
        }
        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... params) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(params[0], options);
                int sampleSize = 1;

                long freeMemory = getAvailableMemoryAmount();

                if(options.outHeight > reqHeight || options.outWidth > reqWidth) {
                    int halfHeight = options.outHeight / 2;
                    int halfWidth = options.outWidth / 2;

                    long memoryUsage = ((options.outHeight * options.outWidth) / (sampleSize * sampleSize)) * 4;

                    while(((halfHeight / sampleSize) >= reqHeight
                            && (halfWidth / sampleSize) >= reqWidth)
                                || freeMemory < memoryUsage) {
                        sampleSize *= 2;
                    }
                }
                Log.i("ImageUtils", "requestSize " + reqHeight + "*" + reqWidth);
                Log.i("ImageUtils", "outSize " + options.outHeight + "*" + options.outWidth);
                Log.i("ImageUtils", "sampling " + sampleSize);
                options.inJustDecodeBounds = false;
                options.inSampleSize = sampleSize;
                return BitmapFactory.decodeFile(params[0], options);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                view.setImageBitmap(bitmap);
                if(progressBar != null) {
                    progressBar.hide();
                }
            }
        }.execute(imagePath);
    }

    private static long getAvailableMemoryAmount() {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        return runtime.maxMemory() - used;
    }
}
