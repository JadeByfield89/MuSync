
package com.jbsoft.musync.fragments;

import com.jbsoft.musync.R;
import com.jbsoft.musync.adapters.ArtistsBaseAdapter;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

public class ArtistsFragment extends Fragment {

    private GridView mGrid;

    private Cursor cursor;

    private String[] projection = {
            MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS, MediaStore.Audio.Artists.NUMBER_OF_TRACKS

    };

    private String selection = null;

    private Uri allArtistsUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

    private String sortOrder = MediaStore.Audio.Artists.ARTIST + " ASC";

    private ArrayList<String> mArtistNames = new ArrayList<String>();

    public boolean mDirectoryExists;

    private int width;

    private int height;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.artists_fragment, container, false);
        mGrid = (GridView)v.findViewById(R.id.gvArtist);
        new AsyncArtistsLoader().execute();

        Log.d("ArtistsFragment", "OnCreate View");
        return v;
    }

    @SuppressLint("NewApi")
    private void getScreenDimensions() {

        if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 13) {

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            width = display.getWidth();
            height = display.getHeight();

        } else {

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;

        }
    }

    private class AsyncArtistsLoader extends AsyncTask<Void, Void, ArrayList<String>> {

        boolean exists;

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            // query the MediaStore Content Provider and return a list of all
            // artist names
            cursor = getActivity().getContentResolver().query(allArtistsUri, projection, selection,
                    null, sortOrder);

            // Director for storing artist thumbnails after they are fetched
            // from internet
            // checkIfDirectoryExists();

            if (cursor != null) {
                if (cursor.moveToNext()) {
                    do {
                        String artistName = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                        mArtistNames.add(artistName);
                        Log.d("ArtistFragment",
                                "Artist Names consists of " + mArtistNames.toString());

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

            return mArtistNames;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

            // super.onPostExecute(result);
            ArtistsBaseAdapter mAdapter = new ArtistsBaseAdapter(getActivity(), result);
            getScreenDimensions();
            mAdapter.setDisplayDimensions(width, height);
            mGrid.setAdapter(mAdapter);
        }
    }

}
