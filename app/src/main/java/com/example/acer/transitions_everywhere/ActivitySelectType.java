package com.example.acer.transitions_everywhere;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.acer.transitions_everywhere.fragments.GameSetup;
import com.example.acer.transitions_everywhere.fragments.SelectType;
import com.example.acer.transitions_everywhere.preferences.PrefsHelper;

/**
 * Created by Mirlan on 19.10.2016.
 */

public class ActivitySelectType extends AppCompatActivity {

    public static int REQCODE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (PrefsHelper.getPrefsHelper() == null)
            new PrefsHelper(this);

        if (PrefsHelper.getPrefsHelper().getBoolean(getString(R.string.pref_keyTheme)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, new SelectType())
                    .commit();
        }
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof
                    GameSetup ? R.string.gameTypeFree : R.string.tTitleGameType);
            bar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQCODE && resultCode == RESULT_OK) {/*RESULT_OK when user goes to main screen*/
            Log.e("TAG", "intent is null --> " + (data == null));
            if (data == null)
                finish();
            else startActivityForResult(data, REQCODE);
        }
        else {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof GameSetup) {
                getSupportFragmentManager().popBackStack();
            }
            else
                finish();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    public void setTitle(int id) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(id);
        }
    }
}
