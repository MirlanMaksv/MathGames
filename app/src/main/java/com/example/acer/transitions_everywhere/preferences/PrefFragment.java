package com.example.acer.transitions_everywhere.preferences;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.acer.transitions_everywhere.R;
import com.example.acer.transitions_everywhere.databinding.PreferencesBinding;

/**
 * Created by Mirlan on 23.09.2016.
 */
public class PrefFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private PreferencesBinding binding;
    private boolean changeTheme;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.preferences, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (PrefsHelper.getPrefsHelper() == null)
            new PrefsHelper(getContext());
        binding.setHandlers(this);
        binding.prefSwitchKB.setOnCheckedChangeListener(this);
        binding.prefSwitchTheme.setOnCheckedChangeListener(this);
        binding.prefSwitchTheme.setChecked(PrefsHelper.getPrefsHelper()
                .getBoolean(getString(R.string.pref_keyTheme)));
        changeTheme = true;
        binding.prefSwitchKB.setChecked(PrefsHelper.getPrefsHelper()
                .getBoolean(getString(R.string.pref_keyKB)));
        setClickable(binding.prefSwitchKB.isChecked());
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.pref_tvSetupKB) {
            KeyboardDialog dialog = new KeyboardDialog();
            dialog.show(getChildFragmentManager(), null);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        if (id == R.id.pref_switchKB) {
            PrefsHelper.getPrefsHelper().savePref(getString(R.string.pref_keyKB), b);
            setClickable(b);
        } else if (id == R.id.pref_switchTheme && changeTheme) {
            PrefsHelper.getPrefsHelper().savePref(getString(R.string.pref_keyTheme), b);
            Log.e("Theme changed", b + "");
            int mode = b ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
            getActivity().recreate();
        }
    }

    private void setClickable(boolean clickable) {
        binding.prefTvSetupKB.setClickable(clickable);
        binding.prefTvSetupKB.setEnabled(clickable);
        binding.prefTvSetupKB.setTextColor(ContextCompat.getColor(getContext(),
                clickable ? R.color.colorTextPrimary : R.color.colorTextButtons));
        binding.prefTvSetupKB.setBackgroundResource(clickable ?
                R.drawable.pref_btn_background_active : R.drawable.pref_btn_background);
    }
}
