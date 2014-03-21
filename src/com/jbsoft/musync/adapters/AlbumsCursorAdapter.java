
package com.jbsoft.musync.adapters;

import com.jbsoft.musync.R;
import com.jbsoft.musync.application.MuSyncApplication;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class AlbumsCursorAdapter extends CursorAdapter {

    public static final String TAG = "AlbumsCursorAdapter";

    private LayoutInflater inflater;

    private Context mContext;

    private ArrayList<Bitmap> mBitmaps = new ArrayList<Bitmap>();

    private View view;

    private int height;

    private int width;

    private ViewHolder holder;

    private int cacheSize;

    private ThumbnailCache mCache;

    private Bitmap bitmap;

    private ArrayList<String> mAlbumNames = new ArrayList<String>();

    private String name;

    public AlbumsCursorAdapter(Context context, Cursor c) {
        super(context, c);

        mContext = context;
        inflater = LayoutInflater.from(context);

        // Get memory available
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        int maxMemory = am.getMemoryClass() * 1024 * 1024;

        cacheSize = maxMemory / 8;
        mCache = new ThumbnailCache(cacheSize);

    }

    public void setDisplayDimensions(int width, int height) {
        this.height = height;
        this.width = width;

    }

    @Override
    public void bindView(View view, Context context, Cursor c) {

        int albumId = c.getInt(c.getColumnIndex(MediaStore.Audio.Albums._ID));
        // albumId++;

        String albumName = c.getString(c.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
        // name = albumName;

        // get the viewholder
        holder = (ViewHolder)view.getTag();

        // check if bitmap is already in cache
        Bitmap bitmap = mCache.get(albumId);
        if (bitmap != null) {
            // Cache hit
            Log.d(TAG, "Cache Hits " + mCache.hitCount());
            holder.mAlbumArt.setImageBitmap(bitmap);
        } else {

            // Cache miss
            Log.d(TAG, "Cache Miss " + mCache.missCount());
            LoadAlbumArtInBackground LAA = new LoadAlbumArtInBackground(holder);
            name = albumName;
            mAlbumNames.add(albumName);
            Log.d(TAG, "Album Names ---> " + mAlbumNames.toString());
            LAA.execute(albumId);

        }

        if (width > height) {
            // landscape mode
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width / 3,
                    width / 3);
            view.setLayoutParams(new GridView.LayoutParams(params));

        } else {
            // portrait mode
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width / 2,
                    width / 2);
            view.setLayoutParams(new GridView.LayoutParams(params));
        }

        if (albumName != null) {
            holder.mAlbumName.setText(albumName);

        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        view = inflater.inflate(R.layout.albums_grid_layout, parent, false);

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    // ViewHolder pattern to prevent unnecessary calls to findviewbyId
    public class ViewHolder {

        View overlay;

        ImageView mAlbumArt;

        TextView mAlbumName;

        View options;

        ViewHolder(View base) {

            this.mAlbumArt = (ImageView)base.findViewById(R.id.ivAlbumCell);
            this.overlay = base.findViewById(R.id.vAlbumsOverlay);
            this.mAlbumName = (TextView)overlay.findViewById(R.id.tvAlbum);
            this.mAlbumName.setTypeface(MuSyncApplication.Fonts.ROBOTO_REGULAR);
        }

    }

    /*******************************************************************************************/

    private class LoadAlbumArtInBackground extends AsyncTask<Integer, Void, Bitmap> {

        private ImageView image;

        private int mPosition;

        private TextView label;

        public LoadAlbumArtInBackground(ViewHolder holder) {

            this.image = holder.mAlbumArt;
            this.label = holder.mAlbumName;

        }

        @Override
        protected void onPreExecute() {

            mPosition = image.getId();
            super.onPreExecute();
            // image.setTag(this);

        }

        @Override
        protected Bitmap doInBackground(Integer... params) {

            // Bitmap bitmap;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            InputStream is;

            try {

                // Get album artwork using album ID
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, params[0]);

                is = mContext.getContentResolver().openInputStream(albumArtUri);

                bitmap = BitmapFactory.decodeStream(is, null, options);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
                bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.genre_none);
                Log.d("AlbumsCursorAdapter", "Bitmap not found " + params[0].toString());
            }

            // if the bitmap is not null
            // add it to the cache

            if (bitmap != null) {
                synchronized (params[0]) {
                    mCache.put(params[0], bitmap);

                    Log.d(TAG, "Size of " + params[0] + " is " + mCache.sizeOf(params[0], bitmap));
                    Log.d(TAG, "Adding bitmap to cache...");
                    Log.d(TAG, "KEY is " + params[0] + ", VALUE is " + bitmap);
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);

            if (result != null && mPosition == image.getId()) {

                // image.setTag(null);
                image.setImageBitmap(result);
            }

        }

    }

    private void setBitmap() {

    }

    /*******************************************************************************************/

    // Cache to store Album art thumbnails
    public class ThumbnailCache extends LruCache<Integer, Bitmap> {

        public ThumbnailCache(int maxSize) {
            super(maxSize);
        }

        @SuppressLint("NewApi")
        @Override
        protected int sizeOf(Integer key, Bitmap bitmap) {

            if (android.os.Build.VERSION.SDK_INT > 11) {

                return bitmap.getByteCount() / 1024;

            } else

                return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

}
