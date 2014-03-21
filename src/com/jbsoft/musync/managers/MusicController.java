package com.jbsoft.musync.managers;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;

//This class handles all music playback

public class MusicController {

	// MediaPlayer to control playback
	private MediaPlayer mp = new MediaPlayer();

	public MusicController() {

	}

	public void playSong(String songPath) {

		try {
			mp.reset();
			mp.setDataSource(songPath);
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.prepare();

			mp.start();
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

	public void pauseSong(String songPath) {

		mp.pause();

	}

	public void pauseSong(int index) {

	}

	public void SetRepeatOn(String songPath) {

	}

	public void setRepeatOff(String songPath) {

	}

}
