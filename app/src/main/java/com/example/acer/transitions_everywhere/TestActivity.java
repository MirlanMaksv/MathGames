package com.example.acer.transitions_everywhere;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.acer.transitions_everywhere.preferences.PrefsHelper;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by Mirlan on 27.10.2016.
 */

public class TestActivity extends AppCompatActivity {

    private PrefsHelper preferences;
    private DatabaseReference ref;
    private FirebaseAuth auth;

    private ViewGroup container;
    private TextView userInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        preferences = new PrefsHelper(this);
        if (preferences.getBoolean(getString(R.string.pref_keyTheme))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        container = (ViewGroup) findViewById(R.id.testContainer);
        userInfo = (TextView) findViewById(R.id.userInfo);

//        MessageModel model = new MessageModel("Test Message", "Mirlan", null, Calendar.getInstance().getTimeZone().toString(), System.currentTimeMillis());

        ref = FirebaseDatabase.getInstance().getReference();
//        ref.child("messages").push().setValue(model);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String info = "";
            info += "User name --> " + user.getDisplayName() + "\nemail --> " + user.getEmail();
            info += "\nUniqueID --> " + user.getUid() + "\nprovider --> " + user.getProviderId();
            userInfo.setText(info);
        } else {
            Snackbar.make(container, "Sign in first", Snackbar.LENGTH_SHORT).show();
        }
    }
}
