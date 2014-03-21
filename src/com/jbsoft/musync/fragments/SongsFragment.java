package com.jbsoft.musync.fragments;

import com.jbsoft.musync.R;
import com.jbsoft.musync.adapters.AllSongsAdapter;
import com.jbsoft.musync.utilities.Song;

import android.app.Activity;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;

public class SongsFragment extends Fragment implements OnItemClickListener {

	private static final String TAG = "SongsFragment";

	private OnSongSelectedListener mListener;

	// CursorLoader that will load all songs in background thread
	CursorLoader mLoader;

	// Cursor to query to MediaStore.Audio Content Provider to retrieve song
	// data
	Cursor cursor;

	String[] STAR = { "*" };

	Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

	String orderBy = MediaStore.Audio.Media.TITLE + " COLLATE NOCASE";

	// Our custom cursor Adapter
	AllSongsAdapter songsAdapter;

	private ArrayList<Song> mSongs = new ArrayList<Song>();

	//
	private ArrayList<Bitmap> mBitmaps = new ArrayList<Bitmap>(); // album
																	// artwork

	private ArrayList<Integer> albumIds = new ArrayList<Integer>();

	private ArrayList<Integer> artistIds = new ArrayList<Integer>();

	private ArrayList<String> artistNames = new ArrayList<String>(); // list of
																		// all
																		// artist
																		// names
																		// on
																		// device

	private ArrayList<String> songTtitles = new ArrayList<String>(); // all song
																		// names

	private ArrayList<String> albumNames = new ArrayList<String>();

	public static ArrayList<String> songPaths = new ArrayList<String>(); // all
																			// song
																			// paths

	private ListView songsView;

	private Bitmap bitmap;

	private boolean mBusy = false;

	private int pos;

	private int top;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.songs_fragment, container, false);

		// Debug.startMethodTracing("SongsFragment.trace");

		songsView = (ListView) v.findViewById(R.id.lvAllSongs);
		songsView.setOnItemClickListener(this);

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("position")) {

				pos = savedInstanceState.getInt("position");
				top = savedInstanceState.getInt("top");

				songsView.setSelectionFromTop(pos, top);

				Log.d(TAG, "Returning from saved state, position is " + pos);
			}
		}

		Log.d("SongsFragment", "OnCreate View");

		new GetSongsInBackground().execute();

		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Debug.stopMethodTracing();
		int position = songsView.getFirstVisiblePosition();
		outState.putInt("position", position);
		View v = songsView.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		outState.putInt("top", top);

	}

	@Override
	public void onDestroy() {

		super.onDestroy();

	}

	// Cast our callback so that the FragmentActivity will be notified which
	// song is selected
	@Override
	public void onAttach(Activity activity) {

		try {
			mListener = (OnSongSelectedListener) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			Log.e(TAG, "Activity must implement OnSongSelected interface!");
		}
		super.onAttach(activity);
	}

	// Interface that will notify the FragmentActivity which song is selected in
	// this list
	public interface OnSongSelectedListener {

		public void OnSongSelected(Song song);
	}

	public class GetSongsInBackground extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			// getAllSongsFromSDCARD();
			cursor = getActivity().getContentResolver().query(allsongsuri,
					STAR, selection, null, orderBy);

			Log.d(TAG, "Loading Songs in background");

			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
			try {

				songsAdapter = new AllSongsAdapter(getActivity(), cursor);
				// songsAdapter.getSongsDataInBG(cursor);
				// songsView.setSelectionFromTop(pos, top);
				// songsView.setSelectionFromTop(pos, top);
				songsView.setAdapter(songsAdapter);
				Log.d(TAG, "Position is " + pos);
				Log.d(TAG, "Top is " + top);

				Log.d(TAG, "All Songs loaded");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long id) {

		// av.getChildAt(position).setBackgroundColor(Color.parseColor("#66ff4015"));

		/*
		 * Intent intent = new Intent(getActivity(), PlayerService.class);
		 * 
		 * // We're passing in the song metadata so that we // can use it to
		 * construct a new Song object in the Service
		 * intent.putExtra("SongPath",
		 * songsAdapter.getSongPaths().get(position));
		 * intent.putExtra("SongName",
		 * songsAdapter.getSongNames().get(position));
		 * intent.putExtra("SongArtist",
		 * songsAdapter.getArtists().get(position));
		 * 
		 * // Pass in list of titles, albums, artists, etc to our service
		 * intent.putExtra("AlbumTitles", songsAdapter.getAlbumNames());
		 * intent.putExtra("SongTitles", songsAdapter.getSongNames());
		 * intent.putExtra("ArtistList", songsAdapter.getArtists());
		 * intent.putExtra("SongsList", songsAdapter.getSongPaths());
		 * intent.putExtra("SongIndex", position); //
		 * intent.putExtra("Artworks", mBitmaps);
		 * 
		 * intent.putExtra("FLAG", TAG); getActivity().startService(intent);
		 * Log.d(TAG, "Player Service has STARTED"); Log.d(TAG, "Song index is "
		 * + position);
		 * 
		 * // Notify the activity which song was selected
		 */

		mListener.OnSongSelected(songsAdapter.getSongsList().get(position));

	}

}
