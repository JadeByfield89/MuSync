package com.jbsoft.musync.activities;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jbsoft.musync.R;
import com.jbsoft.musync.adapters.MenuAdapter;
import com.jbsoft.musync.adapters.MyPagerAdapter;
import com.jbsoft.musync.application.MuSyncApplication;
import com.jbsoft.musync.fragments.AlbumsFragment;
import com.jbsoft.musync.fragments.ArtistsFragment;
import com.jbsoft.musync.fragments.GenresFragment;
import com.jbsoft.musync.fragments.NowPlayingFragment;
import com.jbsoft.musync.fragments.SongsFragment;
import com.jbsoft.musync.fragments.SongsFragment.OnSongSelectedListener;
import com.jbsoft.musync.services.PlayerService;
import com.jbsoft.musync.utilities.Song;
import com.pheelicks.visualizer.VisualizerView;
import com.pheelicks.visualizer.renderer.CircleRenderer;
import com.viewpagerindicator.TitlePageIndicator;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Vector;

public class MainActivity extends SherlockFragmentActivity implements
		ViewPager.OnPageChangeListener, OnItemClickListener,
		OnSongSelectedListener, OnClickListener {

	private static final String TAG = "MainActivity";

	private NowPlayingFragment npFragment;

	// Receiver that will listen for messages from the Service
	private SongUpdateReceiver mReceiver;

	// Views for the now playing widgeta at the bottom of th screen
	private ImageView mAlbumArt;

	private TextView mCurrentArtist;

	private TextView mCurrentSong;

	private TextView mCurrentAlbum;

	// Drawer Layout
	private DrawerLayout mDrawer;

	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;

	private CharSequence mTitle;

	public static View playControls;

	public static ImageButton play;

	private ListView mDrawerList;

	private MenuAdapter mAdapter;

	private String[] labels = { "Search", "Equalizer", "Add to Playlist",
			"Playlists", "Grab Album Art", "Get Lyrics", "View on Youtube",
			"Visualizer", "Rate App", "About", "Settings" };

	private int[] icons = { R.drawable.icon_search, R.drawable.icon_equalizer,
			R.drawable.icon_add_playlist, R.drawable.icon_playlist,
			R.drawable.icon_albumart, R.drawable.music_note_menu,
			R.drawable.icon_play, R.drawable.icon_visualizer,
			R.drawable.icon_rating_good, R.drawable.icon_about,
			R.drawable.icon_settings };

	ActionBar actionBar;

	// private static final String NOWPLAYING_TAB = 0;

	// View Pager to swipe between tabs
	ViewPager pager;

	// Tab page indicator
	TitlePageIndicator tabPager;

	MyPagerAdapter mPagerAdapter;

	FragmentManager fm;

	FragmentTransaction ft;

	View main, frag;

	View parent;

	View wrapper;

	// Views and children for the Now Playing widget
	// displayed at bottom of screen
	public static View currentWidget;

	public static TextView currentSongTitle;

	public static TextView currentSongArtist;

	public static ImageView albumArt;

	public static ImageButton playButton;

	public static ImageButton nextButton;

	public static ImageButton backButton;

	// Album art overlay that will be displayed
	private static View albumOverlay;

	AudioManager am;

	private String currentSongName;

	private String currentArtistName;

	private static boolean isPaused;

	private VisualizerView mVisualizerView;

	public static Bitmap album;

	private View mVisualizerParent;

	private ImageView mCancel;

	private boolean isLinked;

	public static boolean isPlaying;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout);

		// The visualizer layout and its parent
		mVisualizerParent = findViewById(R.id.vVisualizer);
		// mVisualizerView =
		mVisualizerView = (VisualizerView) mVisualizerParent
				.findViewById(R.id.visualizerView);
		mCancel = (ImageView) mVisualizerParent
				.findViewById(R.id.ivCancelVisualizer);
		mCancel.setOnClickListener(this);

		actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.action_bar_color));
		actionBar.setTitle("");
		actionBar.setIcon(R.drawable.icon_headphones);

		initWidget();

		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set up adapter
		mAdapter = new MenuAdapter(this, icons, labels);
		mDrawerList.setAdapter(mAdapter);
		mDrawerList.setOnItemClickListener(this);

		mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// Initialize ViewPager and Fragments

		initializePaging();
		npFragment = new NowPlayingFragment();
		// mService = (PlayerService) PlayerService.getInstance();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}

	private void initWidget() {
		currentWidget = findViewById(R.id.vCurrentWidget);
		currentSongTitle = (TextView) currentWidget
				.findViewById(R.id.tvNowPlayingSong);
		currentSongArtist = (TextView) currentWidget
				.findViewById(R.id.tvNowPlayingArtist);
		playButton = (ImageButton) currentWidget
				.findViewById(R.id.ivNowPlayingPlay);
		backButton = (ImageButton) currentWidget
				.findViewById(R.id.ivNowPlayingBack);
		nextButton = (ImageButton) currentWidget
				.findViewById(R.id.ivNowPlayingNext);
		albumArt = (ImageView) currentWidget
				.findViewById(R.id.ivNowPlayingIcon);

		playButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);

	}

	private void initializePaging() {
		List<Fragment> fragments = new Vector<Fragment>();

		fragments.add(Fragment.instantiate(this,
				NowPlayingFragment.class.getName()));
		fragments
				.add(Fragment.instantiate(this, GenresFragment.class.getName()));
		fragments
				.add(Fragment.instantiate(this, AlbumsFragment.class.getName()));
		fragments.add(Fragment.instantiate(this,
				ArtistsFragment.class.getName()));
		fragments
				.add(Fragment.instantiate(this, SongsFragment.class.getName()));

		this.mPagerAdapter = new MyPagerAdapter(
				super.getSupportFragmentManager(), fragments);
		//
		this.pager = (ViewPager) findViewById(R.id.vpPager);
		this.pager.setAdapter(this.mPagerAdapter);
		this.pager.setOnPageChangeListener(this);
		this.pager.setOffscreenPageLimit(5);

		tabPager = (TitlePageIndicator) findViewById(R.id.tabs);
		tabPager.setViewPager(pager);
		tabPager.setFooterColor(Color.parseColor("#ff4015"));
		tabPager.setBackgroundColor(Color.parseColor("#201c1c"));
		tabPager.setOnPageChangeListener(this);

	}

	// Options menu for the ActionBar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.musicplayer_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menuSocial:

			// Navigate to Social Activity
			// Toast.makeText(this, "Touched", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(MainActivity.this, SocialActivity.class);
			startActivity(intent);

			return true;

		case R.id.menuPopular:

			// Navigate to Popular Activity

			break;

		case R.id.menuMore:

			// Check if drawer is currently open
			// if not, open it
			// if so, close it

			operateDrawer();

			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void operateDrawer() {
		// Opens or Closes the drawer based on
		// it's current status
		if (mDrawer.isDrawerOpen(GravityCompat.START)) {
			mDrawer.closeDrawer(GravityCompat.START);
		}

		else if (!mDrawer.isDrawerOpen(GravityCompat.START)) {
			mDrawer.openDrawer(GravityCompat.START);

		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	// User has scrolled to Genres or another page of viewpager
	// Remove search View by setting visibility to View.GONE
	// set the title page indicator to visible again
	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {

		if (position > 0) {
			currentWidget.setVisibility(View.VISIBLE);
		} else
			currentWidget.setVisibility(View.GONE);

	}

	// //User has scrolled to Genres page of viewpager
	// Remove search View by setting visibility to View.GONE
	// set the title page indicator to visible again
	@Override
	public void onPageSelected(int position) {

		if (position > 0) {
			currentWidget.setVisibility(View.VISIBLE);
		} else
			currentWidget.setVisibility(View.GONE);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {

			operateDrawer();

		}
		return super.onKeyDown(keyCode, event);
	}

	// OnClick for the DrawerList
	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long id) {

		mDrawer.closeDrawers();

		switch (position) {

		case 0:

			break;

		case 7:

			showVisualizerView();

			break;

		case 9:

			showAboutDialog();

			break;

		}

	}

	private void showAboutDialog() {

		Dialog dialog = new Dialog(MainActivity.this);
		getLayoutInflater().inflate(R.layout.dialog_layout, null);
		dialog.setTitle("About");
		dialog.setContentView(R.layout.dialog_layout);
		dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;

		dialog.show();

	}

	private void showVisualizerView() {

		mVisualizerParent.setVisibility(View.VISIBLE);

		if (PlayerService.getInstance().mp != null) {
			if (isLinked == false) {

				mVisualizerView.link(PlayerService.getInstance().mp);
				addCircleRenderer();
				addCircleRenderer();
				isLinked = true;

			}

			else if (isLinked == true) {
				mVisualizerView = (VisualizerView) mVisualizerParent
						.findViewById(R.id.visualizerView);
				mVisualizerView.link(PlayerService.getInstance().mp);
				addCircleRenderer();
				addCircleRenderer();

			}

		}

	}

	// Adds a circle renderer to the visualizer

	private void addCircleRenderer() {
		Paint paint = new Paint();
		paint.setStrokeWidth(3f);
		paint.setAntiAlias(true);
		paint.setColor(Color.argb(255, 222, 92, 143));
		CircleRenderer circleRenderer = new CircleRenderer(paint, true);
		mVisualizerView.addRenderer(circleRenderer);
	}

	public static Bitmap getAlbumArt() {

		return album;
	}

	@Override
	public void OnSongSelected(Song song) {

		// Store the current song title and artist name is global
		// fields so we can use them to update the widget
		currentSongName = song.getTitle();
		currentArtistName = song.getArtist();

		// Set the current title and artist
		currentSongTitle.setText(song.getTitle());
		Log.d(TAG, "Song -  " + song.getTitle() + " is now playing");
		currentSongArtist.setText(song.getArtist());
		Log.d(TAG, "Artist - " + song.getArtist());

		// Set the albuma art
		album = song.getAlbumArt();
		albumArt.setImageBitmap(album);
		Log.d(TAG, "Album art " + album);

		npFragment.setAlbumArt(album);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.ivNowPlayingPlay:

			break;

		case R.id.ivNowPlayingNext:

			break;

		case R.id.ivNowPlayingBack:

			break;

		case R.id.ivCancelVisualizer:

			cleanUpVisualizer();

			break;
		}

	}

	private void cleanUpVisualizer() {
		mVisualizerView.release();
		mVisualizerView.clearRenderers();
		mVisualizerView = null;
		mVisualizerParent.setVisibility(View.GONE);

	}

	// Updates the now playing widget view at the bottom of the screen
	// to reflect the currently playing song
	private void updateNowPlayingWidget() {

		currentSongTitle.setText(PlayerService.getInstance()
				.getCurrentSongName());
		currentSongTitle.setTypeface(MuSyncApplication.Fonts.ROBOTO_ITALIC);
		currentSongArtist.setText(PlayerService.getInstance()
				.getCurrentArtist());
		currentSongArtist.setTypeface(MuSyncApplication.Fonts.ROBOTO_REGULAR);

		albumArt.setImageBitmap(PlayerService.getInstance().getCurrentSongArt());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {

		super.onPause();
		try {
			if (mReceiver != null) {
				unregisterReceiver(mReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {

		super.onResume();
		// update the widget with the currently playing song/artist(if any)
		updateNowPlayingWidget();

		// if receiver has not been registered, register it
		if (mReceiver == null) {

			mReceiver = new SongUpdateReceiver();
			IntentFilter intentFilter = new IntentFilter(
					PlayerService.SONG_CHANGED);

			registerReceiver(mReceiver, intentFilter);
			// updateNowPlayingWidget();

		}
	}

	/***********************************
	 * BROADCAST RECEIVER***********************************************
	 * 
	 * @author Jade Byfield This receiver will receive messages from the Service
	 */
	public class SongUpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			/*
			 * if (intent.getAction().equals(PlayerService.SONG_CHANGED)) {
			 * 
			 * // The song has been changed // update the view(s) to reflect
			 * this
			 * 
			 * updateNowPlayingWidget(); npFragment.setPlayButton(true); //
			 * npFragment.updateOverLayView(); npFragment.resetSeekBar();
			 * npFragment.updateSeekBar(); npFragment.setTimeLeft();
			 * npFragment.setTime(); Log.d("SongUpdateReceiver", "onReceive");
			 * 
			 * }
			 */

		}

	}
}
