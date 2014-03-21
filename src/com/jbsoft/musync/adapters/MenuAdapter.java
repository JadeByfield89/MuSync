package com.jbsoft.musync.adapters;

import com.jbsoft.musync.R;
import com.jbsoft.musync.activities.MainActivity;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater inflater;

	private int[] icons;
	private String[] labels;

	public MenuAdapter(Context context, int[] icons, String[] labels) {

		this.icons = icons;
		this.labels = labels;
		this.mContext = context;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {

		return labels.length;
	}

	@Override
	public Object getItem(int position) {

		return labels[position];
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	public class ViewHolder {

		TextView label;
		ImageView icon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		holder = new ViewHolder();

		convertView = inflater.inflate(R.layout.menu_layout_row, null);
		
		

		holder.label = (TextView) convertView.findViewById(R.id.tvLabel);
		holder.icon = (ImageView)convertView.findViewById(R.id.ivIcon);
		
		holder.label.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_lightitalic.ttf"));
		holder.label.setText(labels[position]);
		
		
		holder.icon.setImageResource(icons[position]);
		return convertView;
	}
	
	

}
