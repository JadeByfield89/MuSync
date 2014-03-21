package com.jbsoft.musync.managers;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class SongsManager {

	// All artists listed on device
	private ArrayList<String> artistNames = new ArrayList<String>();

	// All songs titles listed on device
	private ArrayList<String> songTitles = new ArrayList<String>();

	// All album titles listed on device
	private ArrayList<String> albumTitles = new ArrayList<String>();

	// All albumIDs listed on device
	private ArrayList<Integer> albumIds = new ArrayList<Integer>();
	private ArrayList<Integer> artistIds = new ArrayList<Integer>();

	// SD Card path
	final String MEDIA_PATH = Environment.getExternalStorageDirectory()
			.getPath() + "/Music";
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	// Constructor

	public SongsManager() {

	}

	// Methd to read all song files from sd card and store them in
	// an ArrayList

	public ArrayList<HashMap<String, String>> getSongsList() {

		File home = new File(MEDIA_PATH);
		if (home.listFiles(new FileExtensionFilter()).length > 0) {

			for (File file : home.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();

				song.put(
						"songTitle",
						file.getName().substring(0,
								(file.getName().length() - 4)));
				song.put("songPath", file.getPath());

				// Adding each song to SongList
				songsList.add(song);
			}
		}

		return songsList;
	}

	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}

	}
	
	

}
