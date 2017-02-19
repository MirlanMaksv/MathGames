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

public class ExpandCollapse extends ImageView {

    private AnimatedVectorDrawable mExpandCompat, mCollapseCompat;
    private boolean isExpanded;

    public ExpandCollapse(Context context) {
        super(context);
        init();
    }

    public ExpandCollapse(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandCollapse(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpandCollapse(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mExpandCompat = AnimatedVectorDrawable.getDrawable(getContext(), R.drawable.anim_expand);
        mCollapseCompat = AnimatedVectorDrawable.getDrawable(getContext(), R.drawable.anim_collapse);
        setImageDrawable(mExpandCompat);
    }

    public void morph() {
        if (!mCollapseCompat.isRunning() && !mExpandCompat.isRunning()) {
            AnimatedVectorDrawable drawable = isExpanded ? mCollapseCompat : mExpandCompat;
            setImageDrawable(drawable);
            drawable.start();
            isExpanded = !isExpanded;
        }
    }
}
