package com.example.acer.transitions_everywhere.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.transitions_everywhere.R;
import com.example.acer.transitions_everywhere.databinding.FragmentStatsBinding;
import com.example.acer.transitions_everywhere.preferences.PrefsHelper;

import java.util.Locale;

/**
 * Created by Mirlan on 26.09.2016.
 */
public class Statistics extends Fragment {

    private FragmentStatsBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stats, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (PrefsHelper.getPrefsHelper() == null)
            new PrefsHelper(getContext());

        int playedTimes = PrefsHelper.getPrefsHelper().getInt(PrefsHelper.PLAYED_GAMES);
        int corAns = PrefsHelper.getPrefsHelper().getInt(PrefsHelper.CORRECT_ANS);
        int inCorAns = PrefsHelper.getPrefsHelper().getInt(PrefsHelper.INCORRECT_ANS);
        int totalAns = corAns + inCorAns;

        String percCorAns = String.format(Locale.US, "%.2f",  (corAns * 100.0) / totalAns) + "%";
        String average = String.format(Locale.US, "%.2f", (playedTimes * 60.0) / corAns);

        binding.statTvTimes.setText(String.valueOf(playedTimes));
        binding.statTvCor.setText(percCorAns);
        binding.statTvAverage.setText(average);
    }
}
