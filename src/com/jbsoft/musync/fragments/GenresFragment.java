
package com.jbsoft.musync.fragments;

import com.jbsoft.musync.R;
import com.jbsoft.musync.adapters.GenresCursorAdapter;

import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class GenresFragment extends Fragment {

    GridView grid;

    GenresCursorAdapter mAdapter;

    int mScreenWidth;

    int mScreenHeight;

    private Cursor cursor;

    private String[] projection = {
            MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME
    };

    private String selection = null;

    private Uri mGenresUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;

    private String sortOrder = MediaStore.Audio.Genres.NAME + " ASC";

    private int width;

    private int height;

    // View currentWidget;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.genres_fragment, container, false);

        grid = (GridView)v.findViewById(R.id.gvGenres);

        Log.d("GenresFragment", "OnCreate View");

        cursor = getActivity().getContentResolver().query(mGenresUri, projection, selection, null,
                sortOrder);
        mAdapter = new GenresCursorAdapter(getActivity(), cursor);
        getScreenDimensions();
        mAdapter.setDisplayDimensions(width, height);
        grid.setAdapter(mAdapter);

        return v;
    }

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

    // Get screen dimensions

    /*
     * private Point getDisplaySize(final Display display) { final Point point =
     * new Point(); try { display.getSize(point); } catch
     * (java.lang.NoSuchMethodError ignore) { // Older device point.x =
     * display.getWidth(); point.y = display.getHeight(); mScreenWidth =
     * point.x; mScreenHeight = point.y; } return point; }
     */

}
