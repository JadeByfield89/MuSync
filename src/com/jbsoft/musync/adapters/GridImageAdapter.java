package com.jbsoft.musync.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jbsoft.musync.R;
import com.jbsoft.musync.activities.MainActivity;

public class GridImageAdapter extends BaseAdapter  {

	private ArrayList<ImageView> images = new ArrayList<ImageView>();
	private ArrayList<String> labels = new ArrayList<String>();

	private LayoutInflater inflater;
	private Context mContext;
	private int screenWidth;
	private int screenHeight;

	public GridImageAdapter(Context context, ArrayList<ImageView> images,
			ArrayList<String> labels) {

		mContext = context;
		this.images = images;
		this.labels = labels;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}



	@Override
	public int getCount() {

		return images.size();
	}

	@Override
	public Object getItem(int index) {

		return null;
	}

	@Override
	public long getItemId(int arg0) {

		return 0;
	}

	// viewholder pattern for storing objects
	private class ViewHolder {
		TextView label;
		ImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// get viewholder object
		ViewHolder holder;

		if (convertView == null) {

			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.genres_grid_layout, null);

			holder.image = (ImageView) convertView
					.findViewById(R.id.ivGenreCell);
			holder.label = (TextView) convertView
					.findViewById(R.id.tvGenreCellTitle);

			// holder.image.setImageResource(R.id.ivGenreCell);
			// holder.label.setText("Title");

			convertView.setTag(holder);

		}

		else

		// Get screen width and set imageview weight/height to half
		
		holder = (ViewHolder) convertView.getTag();
		holder.label.setText(labels.get(position));
		holder.image = images.get(position);

		// Set the imageview height and width to half the screen width

		//
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				screenWidth / 2, screenWidth / 2);
		holder.image.setLayoutParams(layoutParams);
		holder.image.setAdjustViewBounds(true);
		holder.image.setScaleType(ScaleType.FIT_START);
		holder.label.setTypeface(Typeface.SANS_SERIF);
		// holder.image.setLayoutParams(new GridView.LayoutParams(screenWidth /
		// 2, screenWidth/ 2));
		// holder.image.setPadding(1, 1, 1, 1);

		return convertView;
	}

	public void setScreenDimensions(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;
	}

	

}
