
package com.jbsoft.musync.adapters;

import com.jbsoft.musync.R;
import com.jbsoft.musync.application.MuSyncApplication;
import com.jbsoft.musync.utilities.Song;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class AllSongsAdapter extends CursorAdapter {

    private static final String TAG = "AllSongsAdapter";

    private LayoutInflater mLayoutInflater;

    private Context mContext;

    public ArrayList<String> songNames = new ArrayList<String>();

    public ArrayList<String> songArtists = new ArrayList<String>();

    public ArrayList<String> albumNames = new ArrayList<String>();

    public ArrayList<String> songPaths = new ArrayList<String>();

    private ArrayList<Song> SongsList = new ArrayList<Song>();

    private Uri albumArtUri;

    private ImageView album;

    private Song song;

    public boolean isBusy = false;

    public Cursor mCursor;

    public boolean isLoaded = false;

    // final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);

    final int cacheSize;

    ThumbnailCache mCache;

    public ViewHolder holder;

    private int cacheHits;

    private int cacheMisses;

    public AllSongsAdapter(Context context, Cursor c) {
        super(context, c);

        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

        // Get memory available
        ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        int maxMemory = am.getMemoryClass() * 1024 * 1024;

        cacheSize = maxMemory / 8;
        mCache = new ThumbnailCache(cacheSize);

        // set the up font as a global to avoid repeated loading in bindView
        // font_regular =
        // Typeface.createFromAsset(MuSyncApplication.Fonts.ROBOTO_REGULAR);

    }

    @Override
    public void bindView(View view, Context context, Cursor c) {

        Log.d(TAG, "Cache size is " + cacheSize);

        String songName = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

        String songArtist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));

        String albumName = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
        albumNames.add(albumName);

        String fullpath = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
        songPaths.add(fullpath);
        Log.d("AllSongsAdapter", "SongPaths Length is " + songPaths.size());
        int albumId = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

        Log.d(TAG, "ALBUM ID  IS " + albumId);
        // Log.d(TAG, "POSITION IS " + position);

        ViewHolder holder = (ViewHolder)view.getTag();

        // First, use the album Id to
        // check if this bitmap is already stored in the cache
        Bitmap cacheResult = mCache.get(albumId);
        Log.d(TAG, "CACHE RESULT IS " + cacheResult);
        if (cacheResult != null) {
            // cache hit

            Log.d(TAG, "Cache Hit " + mCache.hitCount());
            holder.thumbnail.setImageBitmap(cacheResult);
        } else {

            // cache miss
            // Go to disk and retrieve it

            Log.d(TAG, "Cache Miss " + mCache.missCount());
            holder.thumbnail.setImageResource(R.drawable.genre_default);
            new AsyncThumbnailLoader(holder.thumbnail).execute(albumId);
        }

        // Removes the extension from the song name
        songName = songName.replace(
                songName.substring(songName.lastIndexOf("."), songName.length()), "");

        // regex to remove track number, hyphen and artist name from song
        // title(if applicable)
        songName = songName.replaceFirst("^\\d+\\.?\\s*-(?:.*?-)?\\s*", "");

        songNames.add(songName);

        // If the views are not null, then set their contents
        // to the data returned from the cursor
        if (songName != null) {
            holder.songName.setTypeface(MuSyncApplication.Fonts.ROBOTO_ITALIC);

            holder.songName.setText(songName);
        }

        if (songArtist.equals("<unknown>")) {
            songArtist = "Unknown Artist";
        }

        if (songArtist != null) {
            // holder.songArtist.setTypeface(Typeface.createFromAsset(context.getAssets(),
            // "fonts/roboto_lightitalic.ttf"));
            holder.songArtist.setTypeface(MuSyncApplication.Fonts.ROBOTO_REGULAR);
            holder.songArtist.setText(songArtist);
        }

        songArtists.add(songArtist);

        // Construct a Song object out of each result
        song = new Song(fullpath, songName, songArtist);
        // song.setAlumArt(fullBitmap);
        SongsList.add(song);

    }

    public class ViewHolder {

        TextView songName;

        TextView songArtist;

        ImageView thumbnail;

        public ViewHolder(View base) {
            songName = (TextView)base.findViewById(R.id.tvSongTitle);
            songArtist = (TextView)base.findViewById(R.id.tvSongArtist);
            thumbnail = (ImageView)base.findViewById(R.id.ivAlbumArt);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View v = mLayoutInflater.inflate(R.layout.songs_list_row, parent, false);

        holder = new ViewHolder(v);
        v.setTag(holder);

        return v;
    }

    public ArrayList<String> getSongNames() {

        return songNames;
    }

    public ArrayList<String> getArtists() {

        return songArtists;
    }

    public ArrayList<String> getAlbumNames() {

        return albumNames;
    }

    public ArrayList<String> getSongPaths() {

        return songPaths;
    }

    public ArrayList<Song> getSongsList() {

        return SongsList;
    }

    /*
     * public boolean isTaskRunning(){ //return AsyncThumbnailLoader. }
     */

    /*********************************** Load Album Thumbnails in background ***************************/
    public class AsyncThumbnailLoader extends AsyncTask<Integer, Integer, Bitmap> {

        private ImageView image;

        private InputStream is;

        private Bitmap scaled;

        public AsyncThumbnailLoader(ImageView image) {

            this.image = image;

        }

        @Override
        protected void onPreExecute() {
            image.setTag(this);
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {

            int key = params[0];
            Bitmap fullBitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = calculateInSampleSize(options, 50, 50);

            // Get album artwork using album ID
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

            albumArtUri = ContentUris.withAppendedId(sArtworkUri, params[0]);

            try {
                is = mContext.getContentResolver().openInputStream(albumArtUri);
                fullBitmap = BitmapFactory.decodeStream(is, null, options);

                // scaled = Bitmap.createScaledBitmap(fullBitmap, 50, 50, true);

            } catch (FileNotFoundException e) {

                fullBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.genre_default);
                e.printStackTrace();
            }

            // store bitmap in cache

            if (fullBitmap != null) {

                synchronized (params[0]) {

                    mCache.put(params[0], fullBitmap);
                    Log.d(TAG, "Size of " + key + " is " + mCache.sizeOf(key, fullBitmap));
                    Log.d(TAG, "Adding bitmap to cache...");
                    Log.d(TAG, "KEY is " + key + ", VALUE is " + fullBitmap);

                }

            }

            return fullBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            if (image.getTag() == this) {

                image.setTag(null);
                if (result != null) {
                    image.setImageBitmap(result);
                }
            }

        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            // options.inJustDecodeBounds = true;
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

    /********************** LRU Cache ************************************************************/

    public class ThumbnailCache extends LruCache<Integer, Bitmap> {

        public ThumbnailCache(int maxSize) {
            super(maxSize);

        }

        @SuppressLint("NewApi")
        @Override
        protected int sizeOf(Integer key, Bitmap bitmap) {

            if (Build.VERSION.SDK_INT > 11) {

                return bitmap.getByteCount() / 1024;
            } else
                return bitmap.getRowBytes() * bitmap.getHeight();
        }

    }
}
