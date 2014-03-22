package com.jbsoft.musync.services;

import com.jbsoft.musync.R;
import com.jbsoft.musync.utilities.Song;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/* DO NOT USE THIS, IT IS GARBAGE
 * REWRITE A SERVICE TO PLAY THE MUSIC BUT USE A BOUNDED SERVIC INSTEAD
 * REGISTER ANY LISTENING ACTIVITY/FRAGMENT AS A CLIENT  USING A BINDER AND SERVICE CONNECTION
 * AND USE A HANDLER TO POST MESSAGES TO THE CLIENT(s)
 * 
 */
public class PlayerService extends Service implements OnCompletionListener {

	private static final String TAG = "PlayerService";

	public static final String SERVICE_STARTED = "STARTED";

	public static final String SERVICE_STOPPED = "STOPPED";

	public static final String SONG_IS_PLAYING = "Playing";

	public static final String SONG_PAUSED = "Paused";

	public static final String SONG_STOPPED = "Stopped";

	public static final String SONG_CHANGED = "Changed";

	public static final int seekForwardTime = 5000;

	public static final int seekBackwardTime = 5000;

	public int currentDuration;

	public int currentPosition;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */

	public static PlayerService sInstance;

	public MediaPlayer mp;

	public static String currentSongTitle;

	public static String currentArtist;

	public String currentAlbum;

	public int currentSongIndex = 0;

	public boolean isRepeatOnce = false;

	public boolean isRepeatAll = false;

	public boolean isShuffle = false;

	private ArrayList<String> songPathsList = new ArrayList<String>();

	private ArrayList<Song> mSongs = new ArrayList<Song>();

	private String currentSongPath;

	private ArrayList<String> AllSongTitles;

	private ArrayList<String> AllSongArtists;

	private ArrayList<String> AllAlbumTitles;

	public boolean isPaused;

	public boolean isPlaying;

	private Bitmap bitmap;

	private ArrayList<String> songTitles = new ArrayList<String>();

	private ArrayList<String> artistNames = new ArrayList<String>();

	private ArrayList<String> songPaths = new ArrayList<String>();

	private ArrayList<String> albumNames = new ArrayList<String>();

	private ArrayList<Bitmap> mBitmaps = new ArrayList<Bitmap>();

	private Thread thread;

	private Bitmap currentSongArt;

	private boolean mRunning;

	@Override
	public void onCreate() {
		super.onCreate();

		sInstance = this;
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		mp.reset();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

		// Worker thread that will get the full list of song paths on the user's
		// storage
		thread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (mRunning) {
					getAllSongsFromSDCARD();
				}

			}

			public void getAllSongsFromSDCARD() {
				Cursor cursor;
				String[] STAR = { "*" };
				Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
				String orderBy = MediaStore.Audio.Media.TITLE
						+ " COLLATE NOCASE";

				ContentResolver cr = getContentResolver();
				cursor = cr.query(allsongsuri, STAR, selection, null, orderBy);

				if (cursor != null) {
					if (cursor.moveToFirst()) {
						do {
							String song_name = cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

							// Remove the file extension from the song name
							song_name = song_name.replace(song_name.substring(
									song_name.lastIndexOf("."),
									song_name.length()), "");

							// regex to remove track number, hyphen and artist
							// name from song title(if applicable)
							song_name = song_name.replaceFirst(
									"^\\d+\\.?\\s*-(?:.*?-)?\\s*", "");

							songTitles.add(song_name);

							cursor.getInt(cursor
									.getColumnIndex(MediaStore.Audio.Media._ID));

							String fullpath = cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.DATA));
							songPaths.add(fullpath);

							String album_name = cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.ALBUM));

							albumNames.add(album_name);

							int album_id = cursor.getInt(cursor
									.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

							// Get album artwork using album ID
							Uri sArtworkUri = Uri
									.parse("content://media/external/audio/albumart");

							Uri albumArtUri = ContentUris.withAppendedId(
									sArtworkUri, album_id);

							try {
								bitmap = MediaStore.Images.Media.getBitmap(cr,
										albumArtUri);
								bitmap = Bitmap.createScaledBitmap(bitmap, 50,
										50, true); //
								mBitmaps.add(bitmap);
							} catch (FileNotFoundException exception) {
								exception.printStackTrace();
								bitmap = BitmapFactory.decodeResource(
										getApplicationContext().getResources(),
										R.drawable.genre_default);
								mBitmaps.add(bitmap);
							} catch (IOException e) {
								e.printStackTrace();
							}

							String artist_name = cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.ARTIST));
							if (artist_name.equals("<unknown>")) {

								artist_name = "Unknown Artist";
							}
							artistNames.add(artist_name);

							cursor.getInt(cursor
									.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));

							// Create a song object out of the data we've
							// received from
							// the Cursor
							Song song = new Song(fullpath, song_name,
									artist_name);
							song.setAlumArt(bitmap);

							// Add the song to the Songs ArrayList
							mSongs.add(song);
							// mBitmaps.add(bitmap);

						} while (cursor.moveToNext());

					}
					cursor.close();
				}

			}
		});
		thread.start();

	}

	public PlayerService() {

	}

	@Override
	public void onLowMemory() {

		super.onLowMemory();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mRunning = false;

		if (thread != null) {

			thread.interrupt();
			thread = null;
		}

	}

	public static PlayerService getInstance() {

		if (sInstance == null) {

			sInstance = new PlayerService();
		}

		return sInstance;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d("PlayerService", "Bitmaps size is " + mBitmaps.size());
		Log.d("PlayerService", "SongNames size is " + songTitles.size());
		// If the Service is being started from the SongsFragment
		// Then begin playing all songs beginning with the currently
		// selected one

		try {
			if (intent.getStringExtra("FLAG").equals("SongsFragment")) {

				playAllSongs(intent);
				sendBroadcast(new Intent(PlayerService.SONG_CHANGED));

			}

			// HANDLE LOGIC IF SERVICE IS BEING STARTED FROM ANOTHER FRAGMENT
			else {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return START_NOT_STICKY;
	}

	private void playAllSongs(Intent intent) {
		// Get information for the current song that was selected
		currentSongPath = intent.getStringExtra("SongPath");
		currentSongTitle = intent.getStringExtra("SongName");
		currentArtist = intent.getStringExtra("SongArtist");

		currentSongIndex = intent.getIntExtra("SongIndex", 0);

		// Get information for all songs on device
		AllSongTitles = intent.getStringArrayListExtra("SongTitles");
		AllSongArtists = intent.getStringArrayListExtra("ArtistList");
		AllAlbumTitles = intent.getStringArrayListExtra("AlbumTitles");
		// AllAlbumArtwoks = intent.getParcelableArrayListExtra("Artworks");

		songPathsList = intent.getStringArrayListExtra("SongsList");
		new Song(currentSongPath, currentSongTitle, currentArtist);
		playSong(songPathsList, currentSongIndex);
		isPlaying = true;

	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	// Music playback methods

	public void setCurrentSong(String title) {

	}

	public void getCurrentSong() {

	}

	public void playAlbum(ArrayList<String> songPaths, int index) {

		if (mRunning) {

			try {
				mp.reset();
				mp.setDataSource(songPaths.get(index));
				mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mp.prepare();
				mp.start();
				index++;

				mp.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {

						// playAlbum(songPaths, index);
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// mp = new MediaPlayer();
			// playAlbum(songPaths, index);
		}
	}

	public void playSong(ArrayList<String> songPaths, int songIndex) {

		try {
			mp.reset();
			// songs = songsList;
			mp.setDataSource(songPaths.get(songIndex));
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.prepare();

			mp.start();
			// currentSongArt = mBitmaps.get(songIndex);
			// currentDuration = mp.getDuration();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (IllegalStateException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void pauseSong() {

		if (mp.isPlaying()) {
			mp.pause();
			isPaused = true;
			isPlaying = false;

		}

		else
			continuePlaying();

	}

	public void continuePlaying() {

		if (isPaused == true) {

			mp.start();
			isPlaying = true;
			isPaused = false;
		}

	}

	public Bitmap getCurrentSongArt() {

		return currentSongArt;
	}

	public void goFowardInSong() {

		mp.seekTo(5000);
	}

	public void goBackwardInSong() {

		mp.seekTo(-5000);
	}

	public void stopSong() {

	}

	public void repeatCurrentSong() {

	}

	public void repeatAllSongs() {

	}

	public long getDuration() {

		currentDuration = mp.getDuration();

		return currentDuration;
	}

	public int getCurrentPosition() {

		currentPosition = mp.getCurrentPosition();

		return currentPosition;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// When the current song is completed, automatically go to the next song
		// if repeat
		// is OFF

		if (isRepeatOnce) {
			// REPEAT CURRENT SONG ONCE
		} else if (isRepeatAll) {
			// REPEAT
		} else if (isShuffle) {
			// PLAY A RANDOM SONG
		}

		else {
			// Play the next song in the music library
			playNextInEntireLibrary();
			// Send a broadcast to the activity that the song has changed
			sendBroadcast(new Intent(PlayerService.SONG_CHANGED));
		}

	}

	// Plays the next song in the entire library of songs(ordered A-Z)
	public void playNextInEntireLibrary() {

		// If there exists a next song to be played
		if (currentSongIndex < songPaths.size() - 1) {
			++currentSongIndex;
			playSong(songPaths, currentSongIndex);
			Log.d(TAG, "Song Index is " + currentSongIndex);
			Log.d(TAG, "SongPaths List size is " + songPaths.size());
			currentSongTitle = songTitles.get(currentSongIndex);
			currentArtist = artistNames.get(currentSongIndex);
			currentSongArt = mBitmaps.get(currentSongIndex);
			// Send a broadcast to the activity that the song has changed
			sendBroadcast(new Intent(PlayerService.SONG_CHANGED));

		}

		else {

		}

	}

	// Plays the previous song in the entire library of songs(ordered A-Z)
	public void playPreviousSongInEntireLirary() {

		// If there exists a previous song to go back to
		if (currentSongIndex > 0) {
			--currentSongIndex;
			playSong(songPaths, currentSongIndex);
			Log.d(TAG, "Song Index is " + currentSongIndex);
			currentSongTitle = songTitles.get(currentSongIndex);
			currentArtist = artistNames.get(currentSongIndex);
			currentSongArt = mBitmaps.get(currentSongIndex);
			sendBroadcast(new Intent(PlayerService.SONG_CHANGED));
		} else {

		}

	}

	// Returns the name of the song that is currently playing
	public String getCurrentSongName() {

		return currentSongTitle;
	}

	// Returns the name of the artist who's currently playing
	public String getCurrentArtist() {

		return currentArtist;
	}

}
