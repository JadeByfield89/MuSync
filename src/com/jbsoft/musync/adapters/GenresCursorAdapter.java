
package com.jbsoft.musync.adapters;

import com.jbsoft.musync.R;
import com.jbsoft.musync.application.MuSyncApplication;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class GenresCursorAdapter extends CursorAdapter {

    private View view;

    private LayoutInflater inflater;

    private Context mContext;

    private ArrayList<String> mGenres = new ArrayList<String>();

    private ImageView artwork;

    private TextView label;

    private View overflowView;

    private ViewHolder holder;

    private String[] genresArray = {
            "Dance", "Electronic", "Electronic/Dance", "Hip Hop", "Rap", "Unknown",
            "Unknown Genre", "Country", "Classical", "Pop"
    };

    private int height;

    private int width;

    public GenresCursorAdapter(Context context, Cursor c) {
        super(context, c);

        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setDisplayDimensions(int width, int height) {
        this.height = height;
        this.width = width;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String genre = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME));
        holder = (ViewHolder)view.getTag();
        // holder.artwork.setId(cursor.getPosition());
        // holder.overlay.setId(cursor.getPosition());

        // First check orientation

        if (width > height) {
            // landscape mode
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width / 3,
                    width / 3);
            view.setLayoutParams(new GridView.LayoutParams(params));

        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width / 2,
                    width / 2);
            view.setLayoutParams(new GridView.LayoutParams(params));
        }

        if (genre != null) {
            mGenres.add(genre);
            Log.d("GenresCursorAdapter", "Genres list containts " + mGenres.toString());

            holder.label.setText(genre);
        }

        else {
            genre = "Uknown Genre";
            mGenres.add(genre);
            holder.label.setText(genre);
        }
        LoadGenresInBackground LGB = new LoadGenresInBackground(holder);
        LGB.execute(genre);

    }

    private class ViewHolder {

        private View base;

        private View overlay;

        private ImageView artwork;

        private TextView label;

        public ViewHolder(View v) {

            this.base = v;
            artwork = (ImageView)v.findViewById(R.id.ivGenreCell);
            overlay = v.findViewById(R.id.vGenresOverlay);

            label = (TextView)overlay.findViewById(R.id.tvGenre);
            label.setTypeface(MuSyncApplication.Fonts.ROBOTO_REGULAR);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        view = inflater.inflate(R.layout.genres_grid_row, parent, false);

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
    }

    /******************* ASYNCTASK TO LOAD GENRE THUMBNAIL IN BACKGROUND **********************/

    private class LoadGenresInBackground extends AsyncTask<String, Integer, String> {

        private int mPosition;

        private ImageView image;

        private TextView label;

        public LoadGenresInBackground(ViewHolder holder) {

            this.image = holder.artwork;
            this.label = holder.label;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            mPosition = image.getId();

        }

        @Override
        protected String doInBackground(String... params) {

            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            // HACKY WAY TO CHECK IF RETURNED GENRE HAS A SUITABLE MATCH FOR
            // ONE OF OUR HARDCODED GENRE IMAGES

            if (image != null && mPosition == image.getId()) {

                new GenreFilter(result);
            }

        }

        private class GenreFilter {

            public GenreFilter(String genre) {

                if (genre.toLowerCase().contains(("Electronic").toLowerCase())) {

                    image.setImageResource(R.drawable.genre_electronic);
                }

                else if (genre.toLowerCase().contains(("Electronica/Dance").toLowerCase())) {

                    image.setImageResource(R.drawable.genre_electronic);
                }

                else if (genre.toLowerCase().contains(("Hip").toLowerCase())) {

                    image.setImageResource(R.drawable.genre_hiphop);
                } else if (genre.toLowerCase().contains(("Rap").toLowerCase())) {

                    image.setImageResource(R.drawable.genre_rap);
                } else if (genre.toLowerCase().contains(("Jazz").toLowerCase())) {

                    image.setImageResource(R.drawable.genre_jazz);
                }

            }
        }

    }

}
