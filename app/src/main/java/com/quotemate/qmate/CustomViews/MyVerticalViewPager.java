package com.quotemate.qmate.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import me.kaelaela.verticalviewpager.VerticalViewPager;

/**
 * Created by anji kinnara on 7/14/17.
 */

public class MyVerticalViewPager extends VerticalViewPager {
    private float x1, x2, y1, y2;
    static final int MIN_DISTANCE = 250;
    public MyVerticalViewPager(Context context) {
        super(context);
    }
    public MyVerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                float deltaY = y2 - y1;
                float deltaX = x2-x1;
                if(Math.abs(deltaY) > MIN_DISTANCE || Math.abs(deltaX) > MIN_DISTANCE) {
                    break;
                } else {
                    return false;
                }
        }
        return super.onTouchEvent(event);
    }
}
