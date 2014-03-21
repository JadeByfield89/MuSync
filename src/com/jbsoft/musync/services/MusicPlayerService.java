package com.jbsoft.musync.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service implements OnCompletionListener {

	private static final String TAG = "MusicPlayerService";
	// The binder that will be given to clients
	private final IBinder mBinder = new LocalBinder();
	private MediaPlayer mPlayer;

	private ArrayList<String> mSongPathsList = new ArrayList<String>();
	private String mCurrentSongPath;

	private Thread mThread;

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */

	public class LocalBinder extends Binder {

		public MusicPlayerService getService() {

			return MusicPlayerService.this;
		}
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "[SERVICE] onCreate");
		super.onCreate();

		// initialize the media player that will be used for playback
		mPlayer = new MediaPlayer();
		mPlayer.setOnCompletionListener(this);
		mPlayer.reset();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

	}

	// Once this service is bound to its client, return our binder
	@Override
	public IBinder onBind(Intent intent) {

		// for testing, play a random song once the service has started
		playRandomSong();

		return mBinder;
	}

	// public method clients can call to play a specific song the user has
	// selected
	public void playSong(String songPath) {

		try {
			mPlayer.reset();
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setDataSource(songPath);
			mPlayer.prepare();
			mPlayer.start();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void playAllSongs() {

	}

	public void playRandomSong() {

	}

	@Override
	public void onCompletion(MediaPlayer mp) {

	}
}
