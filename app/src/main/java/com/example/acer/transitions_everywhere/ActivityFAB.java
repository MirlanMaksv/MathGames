package com.example.acer.transitions_everywhere;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.acer.transitions_everywhere.fragments.Statistics;
import com.example.acer.transitions_everywhere.preferences.PrefFragment;
import com.example.acer.transitions_everywhere.preferences.PrefsHelper;

/**
 * Created by Mirlan on 23.09.2016.
 */
public class ActivityFAB extends AppCompatActivity {

    private int title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (PrefsHelper.getPrefsHelper() == null)
            new PrefsHelper(this);
        if (PrefsHelper.getPrefsHelper().getBoolean(getString(R.string.pref_keyTheme)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (savedInstanceState == null) {
            int id = getIntent().getIntExtra("id", 0);
            Fragment fragment = null;
            if (id == R.id.ivSettings) {
                title = R.string.tTitleSettings;
                fragment = new PrefFragment();
            } else if (id == R.id.ivStat) {
                title = R.string.tTitleStat;
                fragment = new Statistics();
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.pref_container, fragment)
                    .commit();
        } else title = savedInstanceState.getInt("titleId");
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("titleId", title);
        super.onSaveInstanceState(outState);
    }
}
