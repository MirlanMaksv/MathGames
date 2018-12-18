/**
 * Copyright Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.acer.transitions_everywhere;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.acer.transitions_everywhere.models.UserModel;
import com.example.acer.transitions_everywhere.preferences.PrefsHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class ActivitySignIn extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        FacebookCallback<LoginResult>,
        OnCompleteListener<AuthResult> {

    private static final String TAG = "ActivitySignIn";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_SIGN_UP = 101;

    private GoogleSignInClient mGoogleSignInClient;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;

    // FaceBook instance variables
    private LoginManager loginManager;
    private CallbackManager mCallbackManager;

    private ViewGroup container;
    private EditText etEmail, etPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (PrefsHelper.getPrefsHelper() == null)
            new PrefsHelper(this);
        if (PrefsHelper.getPrefsHelper().getBoolean(getString(R.string.pref_keyTheme)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);

        if (!FacebookSdk.isInitialized())
            FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_sign_in);

        // Initialize Facebook Login
        loginManager = LoginManager.getInstance();
        loginManager.registerCallback(mCallbackManager, this);

        container = findViewById(R.id.sign_in_layout);
        container.setOnClickListener(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.googleSignIn:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.facebookSignIn:
                loginManager.logInWithReadPermissions(ActivitySignIn.this, Arrays.asList("email", "public_profile"));
                break;
            case R.id.twitterSignIn:
                showSnackBar(R.string.notReady, Snackbar.LENGTH_SHORT);
                break;
            case R.id.btnLogin:
                checkTextFields();
                break;
            case R.id.signUp:
                startActivityForResult(new Intent(this, ActivitySignUp.class), RC_SIGN_UP);
                break;
            case R.id.forgotPassword:
                DialogFragment dialogFragment = new PasswordDialog();
                dialogFragment.show(getSupportFragmentManager(), null);
                break;
        }
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(container.getApplicationWindowToken(), 0);
    }

    private void checkTextFields() {
        boolean login = true;
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (email.isEmpty()) {
//            showSnackBar(getString(R.string.usernameLengthError));
            login = false;
            etEmail.setError(getString(R.string.emptyEmail));
        }
        if (password.isEmpty()) {
//            showSnackBar(getString(R.string.passwordLengthError));
            login = false;
            etPassword.setError(getString(R.string.emptyPassword));
        }
        if (login) {
            firebaseAuthWithEmailPassword(email, password);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        } else if (requestCode == RC_SIGN_UP && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        Log.e("TAG", "No user ==> " + (mFirebaseAuth.getCurrentUser() == null));
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.e(TAG, "facebook:onSuccess: " + loginResult);
        firebaseAuthWithFacebook(loginResult.getAccessToken());
        showProgressDialog();
    }

    @Override
    public void onCancel() {
        Log.e(TAG, "facebook:onCancel");
        // ...
    }

    @Override
    public void onError(FacebookException error) {
        Log.e(TAG, "facebook:onError ", error);
        // ...
    }

    private void firebaseAuthWithEmailPassword(String email, String password) {
        showProgressDialog();
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this);
    }

    private void firebaseAuthWithFacebook(AccessToken accessToken) {
        Log.e(TAG, "handleFacebookAccessToken: " + accessToken.getUserId());
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, this/*onComplete*/);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.e(TAG, "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, this/*onComplete*/);
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        if (task.isSuccessful()) {
            setResult(RESULT_OK);
            finish();
        } else {
            if (task.getException().getClass().equals(FirebaseAuthUserCollisionException.class)) {
                showSnackBar(R.string.collisionException, Snackbar.LENGTH_LONG);
            }
            loginManager.logOut();
            Log.e("ERROR", task.getException().toString());
//            Toast.makeText(ActivitySignIn.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.e(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(this, "Signing in", "Please wait...");
    }

    public void showSnackBar(int stringId, int length) {
        Snackbar.make(container, stringId, length).show();
    }
}
