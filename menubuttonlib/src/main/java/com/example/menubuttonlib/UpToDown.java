package com.example.menubuttonlib;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.wnafee.vector.compat.AnimatedVectorDrawable;

/**
 * Created by Mirlan on 08.10.2016.
 */

public class UpToDown extends ImageView {

    private boolean isUp;
    private AnimatedVectorDrawable mUpToDownCompat, mDownToUpCompat;

    public UpToDown(Context context) {
        super(context);
        init();
    }

    public UpToDown(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UpToDown(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UpToDown(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mUpToDownCompat = AnimatedVectorDrawable.getDrawable(getContext(), R.drawable.anim_up_to_down_3);
        mDownToUpCompat = AnimatedVectorDrawable.getDrawable(getContext(), R.drawable.anim_down_to_up_3);
        setImageDrawable(mUpToDownCompat);
    }

    public void morphDown() {
        if (!mUpToDownCompat.isRunning() && !mDownToUpCompat.isRunning()) {
            setImageDrawable(mUpToDownCompat);
            isUp = !isUp;
            mUpToDownCompat.start();
        }
    }

    public void morphUp() {
        if (!mUpToDownCompat.isRunning() && !mDownToUpCompat.isRunning()) {
            setImageDrawable(mDownToUpCompat);
            isUp = !isUp;
            mDownToUpCompat.start();
        }
    }
}
