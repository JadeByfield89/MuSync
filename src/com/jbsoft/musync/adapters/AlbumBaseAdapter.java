
package com.jbsoft.musync.adapters;

import com.jbsoft.musync.R;
import com.jbsoft.musync.application.MuSyncApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AlbumBaseAdapter extends BaseAdapter {

    private ArrayList<String> mSongNames = new ArrayList<String>();

    private ArrayList<String> mDurations = new ArrayList<String>();

    private LayoutInflater inflater;

    private Context mContext;

    public AlbumBaseAdapter(Context c, ArrayList<String> mSongNames, ArrayList<String> mDurations) {

        this.mSongNames = mSongNames;
        this.mDurations = mDurations;
        this.mContext = c;
        this.inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {

        return mSongNames.size();
    }

    @Override
    public Object getItem(int pos) {

        return mSongNames.get(pos);
    }

    @Override
    public long getItemId(int arg0) {

        return 0;
    }

    private class ViewHolder {

        TextView songName;

        TextView songDuration;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.album_list_row, parent, false);

            holder.songName = (TextView)convertView.findViewById(R.id.tvSongName);
            holder.songDuration = (TextView)convertView.findViewById(R.id.tvSongDuration);

            convertView.setTag(holder);

        }

        else
            holder = (ViewHolder)convertView.getTag();

        // removes the extension from the song name
        String name = mSongNames.get(pos);
        name = name.replace(name.substring(name.lastIndexOf("."), name.length()), "");

        // regex to remove track number, hyphen and artist name from song
        // title(if applicable)
        name = name.replaceFirst("^\\d+\\.?\\s*-(?:.*?-)?\\s*", "");

        holder.songName.setTypeface(MuSyncApplication.Fonts.ROBOTO_ITALIC);
        holder.songName.setText(name);
        holder.songDuration.setText("0:00");

        return convertView;
    }

}
