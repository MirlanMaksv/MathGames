package com.example.menubuttonlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.wnafee.vector.compat.AnimatedVectorDrawable;

/**
 * Created by Mirlan on 17.09.2016.
 */

public class CirclesClose extends ImageView {

    private AnimatedVectorDrawable mDotToLineCompat, mLineToDotCompat;
    private boolean isDots;

    public CirclesClose(Context context) {
        super(context);
        init();
    }

    public CirclesClose(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CirclesClose(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CirclesClose(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mDotToLineCompat = AnimatedVectorDrawable.getDrawable(getContext(), R.drawable.anim_dot_to_line);
        mLineToDotCompat = AnimatedVectorDrawable.getDrawable(getContext(), R.drawable.anim_line_to_dot);
        setImageDrawable(mDotToLineCompat);
        setBackgroundResource(R.drawable.background);
        setScaleType(ScaleType.CENTER_INSIDE);
    }

    public void morph() {
        if (!mDotToLineCompat.isRunning() && !mLineToDotCompat.isRunning()) {
            AnimatedVectorDrawable drawable = isDots ? mLineToDotCompat : mDotToLineCompat;
            setImageDrawable(drawable);
            isDots = !isDots;
            drawable.start();
        }
    }
}
