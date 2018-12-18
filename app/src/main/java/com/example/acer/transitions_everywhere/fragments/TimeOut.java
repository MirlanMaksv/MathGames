package com.example.acer.transitions_everywhere.fragments;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.transitions_everywhere.ActivityPlay;
import com.example.acer.transitions_everywhere.Const;
import com.example.acer.transitions_everywhere.R;
import com.example.acer.transitions_everywhere.ResultListDialog;
import com.example.acer.transitions_everywhere.databinding.FragmentTimeoutBinding;
import com.example.acer.transitions_everywhere.preferences.PrefsHelper;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Mirlan on 24.09.2016.
 */
public class TimeOut extends Fragment {

    public static Fragment newInstance(ArrayList<String> correct, ArrayList<String> incorrect, Bundle bundleExtra) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Const.CORRECTLIST, correct);
        bundle.putStringArrayList(Const.INCORRECTLIST, incorrect);
        bundle.putBundle(Const.BUNDLE, bundleExtra);
        Fragment fragment = new TimeOut();
        fragment.setArguments(bundle);
        return fragment;
    }

    private FragmentTimeoutBinding binding;
    private Bundle bundleExtra;
    private ArrayList<String> correct, incorrect;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setHandlers(this);
        correct = getArguments().getStringArrayList(Const.CORRECTLIST);
        incorrect = getArguments().getStringArrayList(Const.INCORRECTLIST);
        bundleExtra = getArguments().getBundle(Const.BUNDLE);
        double time = 60.0;
        if (bundleExtra != null && bundleExtra.getInt(Const.GAME_TYPE, 0) == 2)
            time = bundleExtra.getInt(Const.TIME) / 1000;

        String numCorr = String.valueOf(correct.size());
        String numIncor = String.valueOf(incorrect.size());
        String numAverage = String.format(Locale.US, "%.2f", correct.size() != 0 ? time / correct.size() : 0.0);

        binding.resKey.setText(getString(R.string.TOResKey));
//        binding.resValue.setText(correct.size() + "");
        binding.tvSolved.setText(numCorr);
        binding.tvIncorrect.setText(numIncor);
        binding.tvAverage.setText(numAverage);
        saveResults(correct.size(), incorrect.size());
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.toMain) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
        else if (id == R.id.again) {
            Intent intent = new Intent(new Intent(getContext(), ActivityPlay.class));
            intent.putExtra(Const.BUNDLE, bundleExtra);
//            startActivity(intent);
//            getActivity().finish();
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();

        }
        else if (id == R.id.containerCorr || id == R.id.containerIncor) {
            showDialog(id == R.id.containerCorr ? correct : incorrect);
        }
    }

    private void showDialog(ArrayList<String> arr) {
        DialogFragment dialog = ResultListDialog.newInstance(arr);
        dialog.show(getFragmentManager(), null);
    }

    private void saveResults(int corAns, int inCorAns) {
        if (PrefsHelper.getPrefsHelper() == null)
            new PrefsHelper(getContext());

        int playedTimes = PrefsHelper.getPrefsHelper().getInt(PrefsHelper.PLAYED_GAMES);
        int prevCorAns = PrefsHelper.getPrefsHelper().getInt(PrefsHelper.CORRECT_ANS);
        int prevInCorAns = PrefsHelper.getPrefsHelper().getInt(PrefsHelper.INCORRECT_ANS);

        PrefsHelper.getPrefsHelper().savePref(PrefsHelper.PLAYED_GAMES, playedTimes + 1);
        PrefsHelper.getPrefsHelper().savePref(PrefsHelper.CORRECT_ANS, prevCorAns + corAns);
        PrefsHelper.getPrefsHelper().savePref(PrefsHelper.INCORRECT_ANS, prevInCorAns + inCorAns);
    }
}
