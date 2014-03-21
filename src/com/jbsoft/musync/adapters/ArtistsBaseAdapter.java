
package com.jbsoft.musync.adapters;

import com.jbsoft.musync.R;
import com.jbsoft.musync.application.MuSyncApplication;
import com.jbsoft.musync.providers.ArtistImageProvider;
import com.jbsoft.musync.utilities.BitmapDiskWriter;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ArtistsBaseAdapter extends BaseAdapter {

    private static final String TAG = "ArtistsBaseAdapter";

    private Context c;

    private ArrayList<String> artistNames;

    private LayoutInflater inflater;

    private boolean mDirectoryExists;

    private int height;

    private int width;

    private int cacheSize;

    private ThumbnailCache mCache;

    public ArtistsBaseAdapter(Context c, ArrayList<String> artistNames) {

        this.c = c;
        this.artistNames = artistNames;
        this.inflater = LayoutInflater.from(c);

        // Get memory available
        ActivityManager am = (ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);
        int maxMemory = am.getMemoryClass() * 1024 * 1024;

        cacheSize = maxMemory / 8;
        mCache = new ThumbnailCache(cacheSize);
    }

    public void setDisplayDimensions(int width, int height) {
        this.height = height;
        this.width = width;

    }

    @Override
    public int getCount() {

        return artistNames.size();
    }

    @Override
    public Object getItem(int arg0) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    public class ViewHolder {

        ImageView image;

        View overlay;

        TextView label;

        int position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.artists_grid_row, null);

            holder.image = (ImageView)convertView.findViewById(R.id.ivArtistCell);
            holder.overlay = convertView.findViewById(R.id.vArtistOverlay);
            holder.label = (TextView)holder.overlay.findViewById(R.id.tvAlbum);
            holder.position = position;

            if (width > height) {
                // landscape mode
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width / 3,
                        width / 3);
                convertView.setLayoutParams(new GridView.LayoutParams(params));

            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width / 2,
                        width / 2);
                convertView.setLayoutParams(new GridView.LayoutParams(params));
            }

            convertView.setTag(holder);

        }

        else {

            holder = (ViewHolder)convertView.getTag();
            // holder.image.setTag(position);

        }

        holder.label.setTypeface(MuSyncApplication.Fonts.ROBOTO_REGULAR);
        holder.label.setText(artistNames.get(position));

        /*
         * // First, check if that thumbnail is already in the cache String name
         * = holder.label.getText().toString(); Bitmap bitmap =
         * mCache.get(name); Log.d(TAG, "Cache Hits " + mCache.hitCount()); if
         * (bitmap != null) { // cache hit holder.image.setImageBitmap(bitmap);
         * Log.d(TAG, "Thumbnail is in cache, key is " + name); Log.d(TAG,
         * "Bitmap size is " + mCache.sizeOf(name, bitmap)); } // Cache miss //
         * Thumbnail is not in cache, check to see if a thumbnail exists on disk
         * else { // Check if image thumbnail directory exists if
         * (checkIfFileExists(name)) { // grab the thumbnail from storage and
         * set it as the content of // the // imageview // String name =
         * holder.label.getText().toString(); new AsyncThumbnailLoader(position,
         * holder).execute(name); Log.d("ArtistsBaseAdapter",
         * "Getting thumbnail from disk"); } // Thumbnail is not in cache or on
         * disk, fetch it from internet else if
         * (!checkIfFileExists(holder.label.getText().toString())) { //
         * holder.label.getText().toString(); Log.d(TAG,
         * "No thumbnail match founding..fetching from web"); new
         * AsyncWebThumbnailLoader(position, holder).execute(name); } // if a
         * thumbnail could not be found online, use this image as a //
         * placeholder else {
         * holder.image.setImageResource(R.drawable.genre_none); } }
         */

        return convertView;
    }

    /*******************************************************************************************/

    // Searches the file system for a thumbnail of a given artist, then caches
    // it for reuse later
    public class AsyncThumbnailLoader extends AsyncTask<String, Void, Bitmap> {

        private ImageView image;

        private Bitmap bitmap;

        private ViewHolder holder;

        private int mPosition;

        public AsyncThumbnailLoader(int position, ViewHolder holder) {

            this.holder = holder;
            this.image = holder.image;
            this.mPosition = position;

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.d("ArtistsBaseAdapter", "Async doInBackground");

            // Get bitmap from storage

            String pathName = Environment.getExternalStorageDirectory()
                    + "/MuSync/ArtistThumbnails/" + params[0] + ".png";
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inSampleSize = 4;

                bitmap = BitmapFactory.decodeFile(pathName, options);
                Log.d("ArtistsBaseAdapter", "Found Bitmap on disk for " + params[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bitmap != null) {
                synchronized (params[0]) {
                    // Add bitmap to cache
                    mCache.put(params[0], bitmap);

                }
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            if (result != null && holder.position == mPosition) {

                holder.image.setImageBitmap(result);

            }

        }

    }

    /*********************************************************************************************/

    // Loads thumbnails for a given artist from the web, and stores that photo
    // to the file system
    public class AsyncWebThumbnailLoader extends AsyncTask<String, Void, Bitmap> {

        private ImageView image;

        private Bitmap bitmap;

        private int mPosition;

        private ViewHolder holder;

        public AsyncWebThumbnailLoader(int position, ViewHolder holder) {

            this.mPosition = position;
            this.holder = holder;
            this.image = holder.image;

        }

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.d(TAG, "AsyncWebThumbnailLoader doInBackground");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;

            try {

                ArtistImageProvider provider = new ArtistImageProvider(params[0]);
                String image_url = provider.getArtistPhotoUrl();

                URL url = new URL(image_url);

                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null,
                        options);
                Log.d(TAG, "Found thumbnail online for " + params[0]);

                // Save the bitmap to disk for easy retrieval later
                BitmapDiskWriter bmWriter = new BitmapDiskWriter();
                bmWriter.writeBitmapToFile(bitmap, params[0]);

            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

            if (bitmap != null) {
                synchronized (params[0]) {

                    // Put the bitmap into the cache
                    mCache.put(params[0], bitmap);

                }
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null && holder.position == mPosition) {

                image.setImageBitmap(result);
            }

        }

    }

    /********************************************************************************************/

    // Checks to see if this thumbnail exists for this artist using his/her name
    // as a key
    private boolean checkIfFileExists(String key) {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;

        boolean foundMatch = false;

        // First check if external storage is available
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = false;
            mExternalStorageWriteable = false;
        }

        // If external storage is available and we can write to it
        // check if the directory already exists, if not..create it
        if (mExternalStorageAvailable == true && mExternalStorageWriteable == true) {

            String appPath = Environment.getExternalStorageDirectory() + "/MuSync";
            String thumbnailPath = appPath + "/ArtistThumbnails";
            File mAppDir = new File(appPath);
            File thumbnailDir = new File(thumbnailPath);

            if (mAppDir.exists()) {
                if (thumbnailDir.exists()) {
                    mDirectoryExists = true;

                    File[] files = thumbnailDir.listFiles();
                    key = key.toLowerCase();

                    // Iterate through the thumbnails folder and try to find a
                    // match for the given
                    // artist name
                    for (File file : files) {
                        Log.d("ArtistsBaseAdapter", "Directory exists..searching for " + key);

                        // get the full name of the file
                        String name = file.getName();

                        // convert it to lowercase
                        name = name.toLowerCase();

                        // remove the file extension from the file name
                        name = name.replace(name.substring(name.lastIndexOf("."), name.length()),
                                "");
                        if (name.contains(key)) {

                            foundMatch = true;
                            Log.d("ArtistsBaseAdapter", "Found A Match for " + key);

                        }

                        else
                            foundMatch = false;
                    }
                }

            }

            else {

                mDirectoryExists = false;
                mAppDir.mkdir();
                if (mAppDir.isDirectory()) {
                    thumbnailDir.mkdir();
                }

            }
        }

        return foundMatch;
    }

    /***********************************************************************************************/
    // ThumbnailCache is an implementation of LRUCache
    public class ThumbnailCache extends LruCache<String, Bitmap> {

        public ThumbnailCache(int maxSize) {
            super(maxSize);

        }

        @SuppressLint("NewApi")
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {

            if (Build.VERSION.SDK_INT > 11) {

                // Log.d(TAG, "Bitmap size is " + bitmap.getByteCount() / 1024);
                return bitmap.getByteCount() / 1024;

            } else
                return bitmap.getRowBytes() * bitmap.getHeight();
        }

    }

    /***********************************************************************************************/
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float)height / (float)reqHeight);
            final int widthRatio = Math.round((float)width / (float)reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to
            // the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

}
