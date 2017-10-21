/*++

Module Name:

ImageWorker.java

Abstract:

This class implement asynchronous bitmap loading methods.

Environment:

Android


--*/
package fi.vesaeskola.vehicledatabase;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.lang.ref.WeakReference;

import utilities.Utilities;


public class ImageWorker {
    private static final String TAG = "ImageWorker";
    protected Resources mResources;
    private Bitmap mPlaceHolderBitmap;

    protected ImageWorker(Context context) {
        mResources = context.getResources();
    }

    public void loadImage(String imagePath, ImageView imageView, BitmapWorkerTask.OnImageLoadedListener listener) {

        if (cancelPotentialWork(imagePath, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, listener);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(imagePath);
            //task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Log.d(TAG, "loadImage: Already processing:  " + imagePath);
        }
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param resId
     */
    public void setLoadingImage(int resId) {
        mPlaceHolderBitmap = BitmapFactory.decodeResource(mResources, resId);
    }

    /**
     * Cancels any pending work attached to the provided ImageView.
     * @param imageView
     */
    public static void cancelWork(ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            bitmapWorkerTask.cancel(true);
            Log.d(TAG, "cancelWork - cancelled work for " + bitmapWorkerTask.mImagePath);
        }
    }


    public static boolean cancelPotentialWork(String imagePathNew, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {

            final String imagePath = bitmapWorkerTask.mImagePath;
            if (imagePath != imagePathNew) {
                bitmapWorkerTask.cancel(true);
                Log.d(TAG, "cancelPotentialWork - cancelled work for " + imagePath);
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }


    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> mBitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            mBitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return mBitmapWorkerTaskReference.get();
        }
    }
}

class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "BitmapWorkerTask";
    public String mImagePath = null;
    private final WeakReference<ImageView> mImageViewReference;
    private final OnImageLoadedListener mOnImageLoadedListener;


    public BitmapWorkerTask(ImageView imageView, OnImageLoadedListener listener) {
        mImageViewReference = new WeakReference<ImageView>(imageView);
        mOnImageLoadedListener = listener;
    }
    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        Log.d(TAG, "doInBackground: " + params[0]);
        mImagePath = params[0];
        return Utilities.loadImage(mImagePath, 489, 400);
     }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap resultBitmap) {

        if (mImageViewReference != null && resultBitmap != null) {
            final ImageView imageView = mImageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(resultBitmap);
            }
        }

        // TBD: It is not necessary to provide loaded bitmap to client, true/false succeeed
        // code is enough
        if (mOnImageLoadedListener != null) {
            mOnImageLoadedListener.onImageLoaded(resultBitmap);
        }
    }

    /**
     * Interface definition for callback on image loaded successfully.
     */
    public interface OnImageLoadedListener {

        /**
         * Called once the image has been loaded.
         * @param bitmap Loaded bitmap, if error occure while loadin null is returned.
         */
        void onImageLoaded(Bitmap bitmap);
    }

}
