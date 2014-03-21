
package com.jbsoft.musync.activities;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.jbsoft.musync.R;
import com.jbsoft.musync.adapters.AlbumBaseAdapter;
import com.jbsoft.musync.adapters.SingleAlbumAdapter;
import com.jbsoft.musync.application.MuSyncApplication;
import com.jbsoft.musync.services.PlayerService;
import com.jbsoft.musync.views.SlidingUpPanelLayout;
import com.jbsoft.musync.views.SlidingUpPanelLayout.PanelSlideListener;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AlbumsActivity extends SherlockActivity implements OnItemClickListener {

    private ActionBar actionBar;

    private View dragView;

    private Cursor cursor;

    private View albumView;

    private TextView albumTitle;

    private TextView artistName;

    private ListView mList;

    private SingleAlbumAdapter mAdapter;

    private AlbumBaseAdapter mBaseAdapter;

    private PlayerService mService;

    // Parameters to query the MediaStore and return the songs on a specific
    // album

    String album;

    String[] columns = {
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.MIME_TYPE
    };

    String where = android.provider.MediaStore.Audio.Media.ALBUM + "=?";

    String orderBy = android.provider.MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    private ArrayList<String> mSongNames = new ArrayList<String>();

    private ArrayList<String> mDurations = new ArrayList<String>();

    private ArrayList<String> songPaths = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.albums_activity);
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_color));
        actionBar.setIcon(null);

        // Get the album name that was passed in
        album = getIntent().getStringExtra("AlbumName");
        actionBar.setTitle(album);

        // Inflate the album bannner view
        albumView = findViewById(R.id.vAlbumInfo);
        albumTitle = (TextView)albumView.findViewById(R.id.tvAlbumViewTitle);
        artistName = (TextView)albumView.findViewById(R.id.tvAlbumViewArtist);

        // Set the typeface
        albumTitle.setText(album);
        albumTitle.setTypeface(MuSyncApplication.Fonts.ROBOTO_REGULAR);
        artistName.setTypeface(MuSyncApplication.Fonts.ROBOTO_ITALIC);

        // Inflate the drag view
        dragView = findViewById(R.id.dragView);

        // Set up sliding panel and dragView
        SlidingUpPanelLayout layout = (SlidingUpPanelLayout)findViewById(R.id.albums_sliding_layout);
        layout.setDragView(dragView);

        // Set up the list view
        mList = (ListView)findViewById(R.id.lvAlbumsList);
        mList.setOnItemClickListener(this);

        // Issue the query to retrieve all songs on this album
        String whereVal[] = {
            album
        };

        // Initialize the cursor
        cursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                columns, where, whereVal, orderBy);

        getAllSongsFromCursor(cursor);

        // Initialize an instance of the service
        mService = PlayerService.getInstance();

        // Set the listener for when the panel is dragged up or down
        layout.setPanelSlideListener(new PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelExpanded(View panel) {

            }

            @Override
            public void onPanelCollapsed(View panel) {

            }
        });
    }

    private void getAllSongsFromCursor(Cursor c) {

        if (c != null) {
            if (c.moveToFirst()) {

                do {
                    String name = c
                            .getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    mSongNames.add(name);

                    String fullpath = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                    songPaths.add(fullpath);
                } while (c.moveToNext());
            }
        }
        c.close();

        mBaseAdapter = new AlbumBaseAdapter(AlbumsActivity.this, mSongNames, mDurations);
        mList.setAdapter(mBaseAdapter);

    }

    private ArrayList<String> getSongPaths() {

        return songPaths;
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id) {

        /*
         * Intent intent = new Intent(AlbumsActivity.this, PlayerService.class);
         * intent.putStringArrayListExtra("SongPaths", songPaths);
         * intent.putExtra("Position", pos); startService(intent);
         */

        mService.playAlbum(songPaths, pos);
    }

}
