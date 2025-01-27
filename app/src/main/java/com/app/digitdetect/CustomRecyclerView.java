package com.app.digitdetect;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomRecyclerView extends RecyclerView {

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean performClick() {
        // Call the super implementation for accessibility
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Ensure parent views do not intercept touch events
        getParent().requestDisallowInterceptTouchEvent(true);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            performClick(); // Call performClick when a click is detected
        }

        return super.onTouchEvent(event);
    }
}

