package com.jbsoft.musync.fragments;

import com.jbsoft.musync.R;
import com.jbsoft.musync.services.PlayerService;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.text.DecimalFormat;

public class NowPlayingFragment extends Fragment implements OnClickListener,
		OnSeekBarChangeListener {

	public static final String TAG = "NowPlayingFragment";

	public static ImageButton playButton, forwardButton, nextButton,
			backwardButton, previousButton;

	private static ImageButton repeatButton, likeButton, dislikeButton,
			shareButton, shuffleButton;

	public static SeekBar seekBar;

	private static TextView timeLeft, time;

	public View overlayView;

	public static TextView overlayArtist;

	public static TextView overlayAlbum;

	public static TextView overlaySong;

	private View playControls;

	static ImageView albumArt;

	PlayerService service;

	Handler handler;

	View v;

	private double elapsedMinutes;

	private boolean STATE_PAUSED;

	private boolean STATE_PLAYING;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.now_playing_fragment, container, false);

		initViews();

		Log.d("NowPlayingFragment", "OnCreate View");

		return v;
	}

	@Override
	public void onResume() {

		super.onResume();

	}

	private void initViews() {

		playControls = v.findViewById(R.id.lPlayControls);
		playButton = (ImageButton) playControls.findViewById(R.id.bPlay);

		forwardButton = (ImageButton) playControls.findViewById(R.id.bForward);
		nextButton = (ImageButton) playControls.findViewById(R.id.bNext);
		backwardButton = (ImageButton) playControls
				.findViewById(R.id.bBackward);
		previousButton = (ImageButton) playControls
				.findViewById(R.id.bPrevious);

		repeatButton = (ImageButton) v.findViewById(R.id.ibRepeat);
		likeButton = (ImageButton) v.findViewById(R.id.ibRateGood);
		dislikeButton = (ImageButton) v.findViewById(R.id.ibRateBad);
		shareButton = (ImageButton) v.findViewById(R.id.ibShare);
		shuffleButton = (ImageButton) v.findViewById(R.id.ibShuffle);

		seekBar = (SeekBar) v.findViewById(R.id.sbProgress);
		seekBar.setOnSeekBarChangeListener(this);

		timeLeft = (TextView) v.findViewById(R.id.tvTimeLeft);
		time = (TextView) v.findViewById(R.id.tvTimeElapsed);
		albumArt = (ImageView) v.findViewById(R.id.ivAlbumArt);

		albumArt.setOnClickListener(this);

		/*
		 * overlayView = (View) v.findViewById(R.id.vOverlay); //overlayArtist =
		 * (TextView) overlayView .findViewById(R.id.tvAlbumOverlayArtist);
		 * //overlayAlbum = (TextView) overlayView
		 * .findViewById(R.id.tvAlbumOverlayAlbum); //overlaySong = (TextView)
		 * overlayView .findViewById(R.id.tvAlbumOverlaySong);
		 */

		playButton.setOnClickListener(this);
		forwardButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		backwardButton.setOnClickListener(this);
		previousButton.setOnClickListener(this);
		repeatButton.setOnClickListener(this);
		likeButton.setOnClickListener(this);
		dislikeButton.setOnClickListener(this);
		shareButton.setOnClickListener(this);
		shuffleButton.setOnClickListener(this);

	}

	public static Fragment newInstance() {

		NowPlayingFragment fragment = new NowPlayingFragment();
		Bundle args = new Bundle();
		args.putBoolean("play", true);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		//
		if (STATE_PLAYING) {
			outState.putBoolean("play", STATE_PLAYING);
		}

		else if (STATE_PAUSED) {
			outState.putBoolean("pause", STATE_PAUSED);
		}
		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSavedInstanceState");

	}

	public void setAlbumArt(Bitmap art) {
		try {
			albumArt.setImageBitmap(art);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ImageView getAlbumArt() {

		return albumArt;

	}

	public void updateOverLayView() {

		// overlayArtist.setTypeface(Typeface.createFromAsset(getA.getAssets(),
		// "fonts/roboto_regular.ttf"));
		overlayArtist.setText(service.getCurrentArtist());

		// overlaySong.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
		// "fonts/roboto_lightitalic.ttf"));
		overlaySong.setText(service.getCurrentSongName());

		// overlayAlbum.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
		// "fonts/roboto_lightitalic.ttf"));
		// overlayAlbum.setText(service.getCurrent());

	}

	public void setPlayButton(boolean state) {
		// 0 is paused
		// 1 is playing
		if (state) {
			playButton.setBackgroundResource(R.drawable.playbutton_selected);
		}

		else
			playButton.setBackgroundResource(R.drawable.playbutton_normal);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.bPlay:

			break;

		case R.id.bPrevious:

			break;

		case R.id.bNext:

			break;

		case R.id.bBackward:

			break;

		case R.id.bForward:

			break;
		}

	}

	public void resetSeekBar() {

		seekBar.setProgress(0);
	}

	@Override
	public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar sb) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar sb) {

	}

	public void updateSeekBar() {

		// Worker thread that will update the seekbar as each song is playing
		Thread t = new Thread() {
			Handler handler = new Handler();

			@Override
			public void run() {

				int total = (int) PlayerService.getInstance().getDuration();
				seekBar.setMax(total);
				while (PlayerService.getInstance().mp != null
						&& PlayerService.getInstance().mp.getCurrentPosition() < total) {
					try {

						Thread.sleep(1000);
						PlayerService.getInstance().mp.getCurrentPosition();
					} catch (InterruptedException e) {
						return;
					} catch (Exception e) {
						return;
					}

					seekBar.setProgress(PlayerService.getInstance().mp
							.getCurrentPosition());

					handler.post(new Runnable() {

						@Override
						public void run() {
							setTimeLeft();
							setTime();

						}
					});

				}

			}

		};
		t.start();

	}

	public void setTimeLeft() {
		int mSeconds = PlayerService.getInstance().mp.getDuration();
		double seconds = mSeconds / 1000;
		double minutes = seconds / 60;
		double durationMinutes = Double.parseDouble(new DecimalFormat("#.##")
				.format(minutes));

		durationMinutes = durationMinutes - elapsedMinutes;

		durationMinutes = Double.parseDouble(new DecimalFormat("#.##")
				.format(durationMinutes));
		String left = " " + durationMinutes;

		timeLeft.setText(left);
	}

	public void setTime() {

		int mSeconds = PlayerService.getInstance().mp.getCurrentPosition();
		double seconds = mSeconds / 1000;
		double minutes = seconds / 60;
		elapsedMinutes = Double.parseDouble(new DecimalFormat("#.##")
				.format(minutes));
		String s = " " + elapsedMinutes;
		time.setText(s);
	}
}
