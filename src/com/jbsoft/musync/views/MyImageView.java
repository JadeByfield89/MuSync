package com.jbsoft.musync.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyImageView extends android.widget.ImageView
{
    public MyImageView(Context context)
    {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN && isEnabled())
            setColorFilter(Color.parseColor("#44ff4015"), PorterDuff.Mode.MULTIPLY); //your color here

         if(event.getAction() == MotionEvent.ACTION_UP)
           // setColorFilter(Color.TRANSPARENT);
           clearColorFilter();

        return super.onTouchEvent(event);
    }
}
