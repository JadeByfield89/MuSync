package com.jbsoft.musync.utilities;

import android.graphics.Bitmap;




public class Song {
	
	
private String mFilePath;
private int mDuration;
private String mTitle;
private String mArtist;
private String mAlbum;  //The album this song is from, if available
private Bitmap mAlbumArt;

private boolean hasArtwork; // does this song have album artwork


public Song(String filePath){ //Constructs a new Song based on it's specified file path
	
	this.mFilePath = filePath;
}

public Song(String filePath, String mTitle, String mArtist){
	
	this.mFilePath = filePath;
	this.mTitle = mTitle;
	this.mArtist = mArtist;
	
}

public int getDuration(){
	
	return mDuration;
}

public String getTitle(){
	
	return mTitle;
}

public void setTitle(String title){
	
	this.mTitle = title;
}

public void setAlbum(String album){
	
	this.mAlbum = album;
}

public String getArtist(){
	
	return mArtist;
}

public void setArtist(String artist){
	
	this.mArtist = artist;
}

public String getAlbum(){
	
	return mAlbum;
}

public Bitmap getAlbumArt(){
	
	return mAlbumArt;
}

public void setAlumArt(Bitmap art){
	
	this.mAlbumArt = art;
}

public String getFullPath(){
	
	return mFilePath;
}

}
