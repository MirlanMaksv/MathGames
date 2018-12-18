package com.example.menubuttonlib;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by Mirlan on 08.01.2017.
 */

public class MenuButton extends RelativeLayout implements View.OnClickListener {

    private CirclesClose mMenuFab;
    private ExpandCollapse mExpandCollapse;
    private ImageButton mIvStat, mIvLeaderBoard, mIvSettings, mIvLike;
    private boolean mIsExpanded, mAreRunning;

    private OnMenuItemClick mOnMenuItemClick;

    public MenuButton(Context context) {
        super(context);
        init();
    }

    public MenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        TypedValue outValue = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        }
        else
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);

        View rootView = inflate(getContext(), R.layout.menu_btn, this);

        mMenuFab = (CirclesClose) rootView.findViewById(R.id.menuFab);
        mExpandCollapse = (ExpandCollapse) rootView.findViewById(R.id.expandable);

        mIvSettings = (ImageButton) rootView.findViewById(R.id.ivSettings);
        mIvStat = (ImageButton) rootView.findViewById(R.id.ivStat);
        mIvLeaderBoard = (ImageButton) rootView.findViewById(R.id.ivLeaderBoard);
        mIvLike = (ImageButton) rootView.findViewById(R.id.ivLike);

        mMenuFab.setOnClickListener(this);
        mIvSettings.setOnClickListener(this);
        mIvStat.setOnClickListener(this);
        mIvLeaderBoard.setOnClickListener(this);
        mIvLike.setOnClickListener(this);

        mIvSettings.setBackgroundResource(outValue.resourceId);
        mIvStat.setBackgroundResource(outValue.resourceId);
        mIvLeaderBoard.setBackgroundResource(outValue.resourceId);
        mIvLike.setBackgroundResource(outValue.resourceId);

        enableMenuButtons(false);
    }

    public void setOnMenuItemClick(OnMenuItemClick click) {
        mOnMenuItemClick = click;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.menuFab)
            animateMenu();
        else
            mOnMenuItemClick.onMenuItemClick(view.getId());
    }

    public void animateMenu() {
        if (!mAreRunning) {
            mAreRunning = true;
            int resId = mIsExpanded ? R.animator.animator_scale_down : R.animator.animator_scale_up;
            AnimatorSet stat = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), resId);
            AnimatorSet settings = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), resId);
            AnimatorSet like = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), resId);
            AnimatorSet leaderBoard = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), resId);
            if (mIsExpanded) {
                stat.setStartDelay(25);
                like.setStartDelay(25);
                morph(true);
            }
            else {
                leaderBoard.setStartDelay(175);
                settings.setStartDelay(175);
                stat.setStartDelay(150);
                like.setStartDelay(150);
                morph(false);
            }
            stat.setTarget(mIvStat);
            leaderBoard.setTarget(mIvLeaderBoard);
            settings.setTarget(mIvSettings);
            like.setTarget(mIvLike);
            stat.start();
            leaderBoard.start();
            settings.start();
            like.start();
        }
    }

    private void morph(boolean withDelay/*is false when menu is expanded*/) {
        enableMenuButtons(!withDelay);
        int delayMorph = withDelay ? 100 : 0;
        int delayScales = withDelay ? 500 : 400;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mMenuFab.morph();
                mExpandCollapse.morph();
                mIsExpanded = !mIsExpanded;
            }
        }, delayMorph);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAreRunning = false;
            }
        }, delayScales);
    }

    private void enableMenuButtons(boolean enable) {
        // Unable or enable clicks
        mIvSettings.setEnabled(enable);
        mIvStat.setEnabled(enable);
        mIvLike.setEnabled(enable);
        mIvLeaderBoard.setEnabled(enable);
    }
}
