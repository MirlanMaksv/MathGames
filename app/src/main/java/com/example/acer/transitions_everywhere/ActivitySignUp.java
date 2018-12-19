package com.example.acer.transitions_everywhere;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.acer.transitions_everywhere.models.UserModel;
import com.example.acer.transitions_everywhere.preferences.PrefsHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Mirlan on 27.10.2016.
 */

public class ActivitySignUp extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<AuthResult>, OnSuccessListener<Void> {

    private DatabaseReference reference;
    private FirebaseAuth mFirebaseAuth;

    private EditText etUserName, etEmail, etPassword;
//    private ViewGroup container;
    private ProgressDialog progressDialog;
    private String username, email, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (PrefsHelper.getPrefsHelper() == null)
            new PrefsHelper(this);
        if (PrefsHelper.getPrefsHelper().getBoolean(getString(R.string.pref_keyTheme)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.btnSignUp).setOnClickListener(this);
        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
//        container = findViewById(R.id.sign_in_layout);

        mFirebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSignUp) {
            checkTextFields();
        }
    }

    private void checkTextFields() {
        username = etUserName.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        if (username.length() < 3) {
            showToast(getString(R.string.usernameLengthError));
            return;
        }
        if (email.isEmpty()) {
            showToast(getString(R.string.emptyEmail));
            return;
        }
        if (password.length() < 6) {
            showToast(getString(R.string.passwordLengthError));
            return;
        }
        progressDialog = ProgressDialog.show(this, "Creating new account", "Please wait...");
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this);
        // onComplete() => onSuccess()
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        progressDialog.dismiss();
        if (task.isSuccessful()) {
            FirebaseUser user = mFirebaseAuth.getCurrentUser();
            Log.e("TAG", "active => " + (user != null));
            if (user != null) {
                UserModel model = new UserModel(username, email, null);
                reference.child("users").child(user.getUid()).setValue(model).addOnSuccessListener(this);
            }
        } else {
            Log.e("ERROR", task.getException().toString());
            showToast(task.getException().getMessage());
        }
    }

    @Override
    public void onSuccess(Void aVoid) {
        showToast("Registration completed");
        setResult(RESULT_OK);
        finish();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}

