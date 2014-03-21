
package com.jbsoft.musync.fragments;

import com.jbsoft.musync.R;
import com.jbsoft.musync.activities.AlbumsActivity;
import com.jbsoft.musync.adapters.AlbumsCursorAdapter;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class AlbumsFragment extends Fragment implements OnScrollListener, OnItemClickListener {

    // GridView
    private GridView albumsGrid;

    private AlbumsCursorAdapter mAdapter;

    private Cursor cursor;

    public static boolean isBusy;

    //

    private String[] projection = {
            MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.ALBUM_ART,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
    };

    private String selection = null;

    private Uri allAlbumsUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

    private String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";

    private int width;

    private int height;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.albums_fragment, container, false);
        albumsGrid = (GridView)v.findViewById(R.id.gvAlbums);
        // albumsGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        cursor = getActivity().getContentResolver().query(allAlbumsUri, projection, selection,
                null, sortOrder);
        mAdapter = new AlbumsCursorAdapter(getActivity(), cursor);
        getScreenDimensions();
        mAdapter.setDisplayDimensions(width, height);
        albumsGrid.setAdapter(mAdapter);
        albumsGrid.setOnScrollListener(this);
        albumsGrid.setOnItemClickListener(this);

        Log.d("AlbumsFragment", "OnCreate View");

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

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        switch (scrollState) {

            case SCROLL_STATE_IDLE:

                isBusy = false;
                // mAdapter.notifyDataSetChanged();

                break;

            case SCROLL_STATE_FLING:

                isBusy = true;

                break;

        }

    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int position, long id) {

        String name = getAlbumNameFromQuery(position);
        Intent i = new Intent(getActivity(), AlbumsActivity.class);
        i.putExtra("AlbumName", name);

        startActivity(i);
    }

    private String getAlbumNameFromQuery(int position) {

        String name = null;

        if (cursor != null) {
            if (cursor.moveToPosition(position)) {

                name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();

            }
        }

        return name;

    }

    // private class GetAlbumArtwork extends AsyncTask<>

}
