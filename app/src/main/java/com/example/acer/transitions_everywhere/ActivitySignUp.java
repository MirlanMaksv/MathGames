package com.example.acer.transitions_everywhere;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.acer.transitions_everywhere.models.UserModel;
import com.example.acer.transitions_everywhere.preferences.PrefsHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Mirlan on 27.10.2016.
 */

public class ActivitySignUp extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<AuthResult> {

    private DatabaseReference reference;
    private FirebaseAuth mFirebaseAuth;

    private EditText etUserName, etEmail, etPassword;
    private ViewGroup container;
    private ProgressDialog progressDialog;
    private String username, email, password;
    private Button signUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (PrefsHelper.getPrefsHelper() == null)
            new PrefsHelper(this);
        if (PrefsHelper.getPrefsHelper().getBoolean(getString(R.string.pref_keyTheme)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUp = (Button) findViewById(R.id.btnSignUp);
        signUp.setOnClickListener(this);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        container = (ViewGroup) findViewById(R.id.sign_in_layout);

        mFirebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSignUp) {
            checkTextFields();
        }
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(container.getApplicationWindowToken(), 0);
    }

    private void checkTextFields() {
        username = etUserName.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        if (username.length() < 3) {
            showSnackBar(getString(R.string.usernameLengthError));
            return;
        }
        if (email.isEmpty()) {
            showSnackBar(getString(R.string.emptyEmail));
            return;
        }
        if (password.length() < 6) {
            showSnackBar(getString(R.string.passwordLengthError));
            return;
        }
        progressDialog = ProgressDialog.show(this, "Creating new account", "Please wait...");
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        progressDialog.dismiss();
        if (task.isSuccessful()) {
            FirebaseUser user = mFirebaseAuth.getCurrentUser();
            if (user != null) {
                UserModel model = new UserModel(username, email, null);
                reference.child("users").child(user.getUid()).push().setValue(model);
            }
//            Toast.makeText(this, "Registration completed", Toast.LENGTH_SHORT).show();
            showSnackBar("Registration completed");
            setResult(RESULT_OK);
            finish();
        } else {
            Log.e("ERROR", task.getException().toString());
            showSnackBar(task.getException().getMessage());
        }
    }

    private void showSnackBar(String text) {
        Snackbar.make(container, text, Snackbar.LENGTH_SHORT).show();
    }
}

