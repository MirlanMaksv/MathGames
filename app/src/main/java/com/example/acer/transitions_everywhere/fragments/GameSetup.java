package com.example.acer.transitions_everywhere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.example.acer.transitions_everywhere.ActivityPlay;
import com.example.acer.transitions_everywhere.ActivitySelectType;
import com.example.acer.transitions_everywhere.Const;
import com.example.acer.transitions_everywhere.R;

import java.util.ArrayList;

public class GameSetup extends Fragment implements View.OnClickListener, RangeBar.OnRangeBarChangeListener {

    private ActivitySelectType upperAct;
    private RangeBar numSeekBar, timeSeekBar;
    private TextView tvLeftRange, tvRightRange, tvTime;
    private ImageView ivTypeTime, ivTypeTrue, ivAdd, ivSub, ivMult, ivDiv;
    private boolean blockListeners;
    private ArrayList<Integer> operators;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gamesetup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        operators = new ArrayList<>();
        operators.add(0);

        ivTypeTime = (ImageView) view.findViewById(R.id.typeTime);
        ivTypeTrue = (ImageView) view.findViewById(R.id.typeTrueFalse);
        ivAdd = (ImageView) view.findViewById(R.id.typeAddition);
        ivSub = (ImageView) view.findViewById(R.id.typeSubtraction);
        ivMult = (ImageView) view.findViewById(R.id.typeMultiplication);
        ivDiv = (ImageView) view.findViewById(R.id.typeDivision);

        ivTypeTime.setOnClickListener(this);
        ivTypeTrue.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        ivSub.setOnClickListener(this);
        ivMult.setOnClickListener(this);
        ivDiv.setOnClickListener(this);

        ivTypeTime.setSelected(true);
        ivTypeTrue.setSelected(false);

        ivAdd.setSelected(true);
        ivSub.setSelected(false);
        ivMult.setSelected(false);
        ivDiv.setSelected(false);


        ImageView ivRight = (ImageView) view.findViewById(R.id.increment);
        ImageView ivLeft = (ImageView) view.findViewById(R.id.decrement);
        ivRight.setOnClickListener(this);
        ivLeft.setOnClickListener(this);

        tvLeftRange = (TextView) view.findViewById(R.id.tvLeftRange);
        tvRightRange = (TextView) view.findViewById(R.id.tvRightRange);

        tvTime = (TextView) view.findViewById(R.id.tvTimeSelected);
        timeSeekBar = (RangeBar) view.findViewById(R.id.timeSeekBar);
        timeSeekBar.setOnRangeBarChangeListener(this);
        timeSeekBar.setRangePinsByValue(10, 150);

        numSeekBar = (RangeBar) view.findViewById(R.id.numSeekBar);
        numSeekBar.setOnRangeBarChangeListener(this);
        numSeekBar.setRangePinsByValue(1, 50);

        view.findViewById(R.id.start).setOnClickListener(this);
        ((CheckBox) view.findViewById(R.id.noTime)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                timeSeekBar.setEnabled(!b);
            }
        });
    }

    @Override
    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
        if (rangeBar.getId() == R.id.numSeekBar) {
            if (!blockListeners) {
                float left = stringToInt(leftPinValue);
                float right = stringToInt(rightPinValue);
                float tickEnd = numSeekBar.getTickEnd();
                if (left < 0 || right > tickEnd) {
                    if (left < 0)
                        left = 1;
                    if (right > tickEnd)
                        right = tickEnd;
                    blockListeners = true;
                    numSeekBar.setRangePinsByValue(left, right);
                }

                String sLeft = getString(R.string.GSintervalFrom) + (left == 0 ? "  1" : "  " + (int) left);
                String sRight = getString(R.string.GSintervalTo) + "  " + (int) right;
                tvLeftRange.setText(sLeft);
                tvRightRange.setText(sRight);
                blockListeners = false;
            }
        }
        else {
            tvTime.setText(rightPinValue + " " + getString(R.string.GSseconds));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.start) {
            startGame();
        }
        else if (id == R.id.increment || id == R.id.decrement) {
            handleIncrementDecrement(id);
        }
        else {
            if (id == R.id.typeTime || id == R.id.typeTrueFalse) {
                handleGameTypeSelection(id);
            }
            else {
                handleOperatorSelection(view);
            }
        }
    }

    private void startGame() {
        int rightValue = stringToInt(numSeekBar.getRightPinValue());
        int leftValue = stringToInt(numSeekBar.getLeftPinValue());
        Intent intent = new Intent(getContext(), ActivityPlay.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Const.GAME_TYPE, 2);
        bundle.putInt(Const.GAME_CHOSEN, ivTypeTime.isSelected() ? 0 : 1);
        bundle.putIntegerArrayList(Const.OPERATORS, operators);
        bundle.putInt(Const.FROM, leftValue == 0 ? 1 : leftValue);
        bundle.putInt(Const.TO, rightValue == 0 ? 1 : rightValue);
        boolean checked = !timeSeekBar.isEnabled();
        bundle.putBoolean(Const.NOTIME, checked);
        if (!checked)
            bundle.putInt(Const.TIME, stringToInt(timeSeekBar.getRightPinValue()) * 1000);
        intent.putExtra(Const.BUNDLE, bundle);
        upperAct.startActivityForResult(intent, ActivitySelectType.REQCODE);
    }

    private void handleIncrementDecrement(int id) {
        float leftValue = stringToInt(numSeekBar.getLeftPinValue());
        float rightValue = stringToInt(numSeekBar.getRightPinValue());
        float tickEnd = numSeekBar.getTickEnd();
        double interval = numSeekBar.getTickInterval();
        // increase interval
        if (id == R.id.increment) {
            if (tickEnd == 100) {
                tickEnd = 250;
                interval = 5;
            }
            else if (tickEnd == 250) {
                tickEnd = 500;
                interval = 5;
            }
            else if (tickEnd == 500) {
                tickEnd = 750;
                interval = 10;
            }
            else if (tickEnd == 750) {
                tickEnd = 1000;
                interval = 10;
            }
        }
        // decrease interval
        else {
            if (tickEnd == 250) {
                tickEnd = 100;
                interval = 1;
            }
            else if (tickEnd == 500) {
                tickEnd = 250;
                interval = 5;
            }
            else if (tickEnd == 750) {
                tickEnd = 500;
                interval = 5;
            }
            else if (tickEnd == 1000) {
                tickEnd = 750;
                interval = 10;
            }
            if (rightValue > tickEnd)
                rightValue = tickEnd;
            if (leftValue > tickEnd)
                leftValue = tickEnd;
        }
        numSeekBar.setTickEnd(tickEnd);
        numSeekBar.setTickInterval((float) interval);
        numSeekBar.setRangePinsByValue(leftValue, rightValue);
    }

    private void handleGameTypeSelection(int id) {
        if (id == ivTypeTime.getId()) {
            if (!ivTypeTime.isSelected() && ivTypeTrue.isSelected()) {
                ivTypeTime.setSelected(true);
                ivTypeTime.setImageResource(R.drawable.ic_gametype_time_selected);
                ivTypeTrue.setSelected(false);
                ivTypeTrue.setImageResource(R.drawable.ic_gametype_true_deselected);
            }
        }
        else if (id == ivTypeTrue.getId())
            if (!ivTypeTrue.isSelected() && ivTypeTime.isSelected()) {
                ivTypeTime.setSelected(false);
                ivTypeTime.setImageResource(R.drawable.ic_gametype_time_deselected);
                ivTypeTrue.setSelected(true);
                ivTypeTrue.setImageResource(R.drawable.ic_gametype_true_selected);
            }
    }

    private void handleOperatorSelection(View view) {
        int id = view.getId();
        if (operators.size() > 1 || (operators.size() == 1 && id != getSelectedOperatorId())) {
            if (id == ivAdd.getId()) {
                setSelected(ivAdd, !ivAdd.isSelected(), ivAdd.isSelected() ?
                        R.drawable.ic_operator_add_deselected : R.drawable.ic_operator_add_selected, 0);
            }
            else if (id == ivSub.getId()) {
                setSelected(ivSub, !ivSub.isSelected(), ivSub.isSelected() ?
                        R.drawable.ic_operator_sub_deselected : R.drawable.ic_operator_sub_selected, 1);
            }
            else if (id == ivMult.getId()) {
                setSelected(ivMult, !ivMult.isSelected(), ivMult.isSelected() ?
                        R.drawable.ic_operator_mult_deselected : R.drawable.ic_operator_mult_selected, 2);
            }
            else {
                setSelected(ivDiv, !ivDiv.isSelected(), ivDiv.isSelected() ?
                        R.drawable.ic_operator_div_deselected : R.drawable.ic_operator_div_selected, 3);
            }
        }
    }

    private void setSelected(ImageView iv, boolean selected, int res, int operator) {
        iv.setSelected(selected);
        iv.setImageResource(res);
        if(selected)
            operators.add(operator);
        else operators.remove(Integer.valueOf(operator));
    }

    private int getSelectedOperatorId() {
        int id;
        if (ivAdd.isSelected())
            id = ivAdd.getId();
        else if (ivSub.isSelected())
            id = ivSub.getId();
        else if (ivMult.isSelected())
            id = ivMult.getId();
        else id = ivDiv.getId();
        return id;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (upperAct == null) {
            upperAct = (ActivitySelectType) getActivity();
            upperAct.setTitle(R.string.gameTypeFree);
        }
    }

    @Override
    public void onStop() {
        upperAct.setTitle(R.string.tTitleGameType);
        upperAct = null;
        super.onStop();
    }

    private int stringToInt(String s) {
        int res = -1;
        try {
            res = Integer.valueOf(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return res;
    }
}
