
package com.jbsoft.musync.adapters;

import com.jbsoft.musync.R;
import com.jbsoft.musync.application.MuSyncApplication;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SingleAlbumAdapter extends CursorAdapter {

    private static final String TAG = "SingleAlbumAdapter";

    LayoutInflater inflater;

    Context mContext;

    private TextView songName;

    private TextView songDuration;

    private ArrayList<String> mSongNames = new ArrayList<String>();

    public SingleAlbumAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        mSongNames.add(name);
        Log.d(TAG, "Song Names --> " + mSongNames.toString());

        if (songName != null) {

            // removes the extension from the song name
            name = name.replace(name.substring(name.lastIndexOf("."), name.length()), "");

            // regex to remove track number, hyphen and artist name from song
            // title(if applicable)
            name = name.replaceFirst("^\\d+\\.?\\s*-(?:.*?-)?\\s*", "");

            songName.setTypeface(MuSyncApplication.Fonts.ROBOTO_ITALIC);
            songName.setText(name);

        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View v = inflater.inflate(R.layout.album_list_row, parent, false);
        songName = (TextView)v.findViewById(R.id.tvSongName);
        songDuration = (TextView)v.findViewById(R.id.tvSongDuration);

        return v;
    }

}
