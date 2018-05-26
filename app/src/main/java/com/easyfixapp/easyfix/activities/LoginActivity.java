package com.easyfixapp.easyfix.activities;

import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.AuthResponse;
import com.easyfixapp.easyfix.models.User;
import com.easyfixapp.easyfix.util.AuthService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    // Facebook
    private FancyButton mFacebookLogin;
    private CallbackManager mFacebookCallbackManager;
    private LoginButton mFacebookLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {
                    Util.hideSoftKeyboard(getApplicationContext(), getCurrentFocus());
                    attemptLogin(null);
                    return true;
                }
                return false;
            }
        });

        mFacebookLogin = (FancyButton) findViewById(R.id.login_fb);
        mFacebookLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        mFacebookLoginButton.setReadPermissions("email");

        logoutFacebook();
        setupFacebookStuff();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, Util.REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, Util.REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == Util.REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    public void attemptLogin(View v) {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        //TODO: Remove this por production
        //if(email.equals("prueba")){
        //    startActivity(new Intent(getApplicationContext(),MainActivity.class));
        //}

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || Util.isPasswordMinimumLengthValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Util.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (Util.isNetworkAvailable(getApplicationContext())) {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                Util.showLoading(LoginActivity.this, getString(R.string.message_login_request));

                // Get firebase token
                String registrationId = FirebaseInstanceId.getInstance().getToken();

                Map<String, Object> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("role", Util.USER_ROLE);

                params.put("registration_id", registrationId);
                params.put("type", Util.TYPE_DEVICE);

                loginTask(params);
            } else {
                Util.longToast(getApplicationContext(), getString(R.string.message_network_connectivity_failed));
            }
        }
    }

    public void attemptRecovery(View v) {
        Intent intent = new Intent(LoginActivity.this, RecoveryPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    public void signup(View view){
        // Init Signup
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        //finish();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private void loginTask(Map<String,Object> params){

        AuthService authService = ServiceGenerator.createAuthService();
        Call<AuthResponse<User>> call = authService.login(params);
        call.enqueue(new Callback<AuthResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse<User>> call, @NonNull Response<AuthResponse<User>> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_LOGIN, "Login result: success!");

                    AuthResponse userResponse = response.body();
                    loginSuccess(userResponse);
                } else {
                    // Error response, no access to resource?
                    Log.i(Util.TAG_LOGIN, "Login result: " + response.toString());
                    Util.longToast(getApplicationContext(), getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse<User>> call, @NonNull Throwable t) {
                // Something went completely south (like no internet connection)
                Log.i(Util.TAG_LOGIN, "Login result: failed, " + t.getMessage());
                Util.longToast(getApplicationContext(), getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }


    /**
     * Facebook
     */

    public void attemptLoginFb(View v) {
        mFacebookLoginButton.performClick();
        mFacebookLogin.setEnabled(false);
    }

    private void setupFacebookStuff() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mFacebookCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String access_token = loginResult.getAccessToken().getToken();
                if (TextUtils.isEmpty(access_token)) {
                    logoutFacebook();
                } else {
                    loginFbTask(access_token);
                }
            }

            @Override
            public void onCancel() {
                logoutFacebook();
            }

            @Override
            public void onError(FacebookException error) {
                logoutFacebook();
            }
        });
    }

    private void logoutFacebook(){
        LoginManager.getInstance().logOut();
        mFacebookLogin.setEnabled(true);
    }

    private void loginFbTask(String access_token) {
        Util.showLoading(LoginActivity.this, getString(R.string.message_login_request));

        // Get firebase token
        String registrationId = FirebaseInstanceId.getInstance().getToken();

        Map<String, Object> params = new HashMap<>();
        params.put("access_token", access_token);
        params.put("registration_id", registrationId);
        params.put("type", Util.TYPE_DEVICE);

        AuthService authService = ServiceGenerator.createAuthService();
        Call<AuthResponse<User>> call = authService.loginFb(params);
        call.enqueue(new Callback<AuthResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse<User>> call, @NonNull Response<AuthResponse<User>> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_LOGIN, "Login result: success!");

                    AuthResponse userResponse = response.body();
                    loginSuccess(userResponse);
                } else {
                    // Error response, no access to resource?
                    Log.i(Util.TAG_LOGIN, "Login result: " + response.toString());
                    Util.longToast(getApplicationContext(), getString(R.string.message_service_server_failed));
                    mFacebookLogin.setEnabled(true);
                }
                logoutFacebook();
                Util.hideLoading();
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse<User>> call, @NonNull Throwable t) {
                // Something went completely south (like no internet connection)
                Log.i(Util.TAG_LOGIN, "Login result: failed, " + t.getMessage());
                logoutFacebook();
                Util.longToast(getApplicationContext(), getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }

    private void loginSuccess(AuthResponse authResponse) {
        if (!authResponse.isError()) {
            // Get user
            User user = (User) authResponse.getData();

            // Save user in shared preferences
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            sessionManager.saveUser(user);

            // Show message success
            Util.longToast(getApplicationContext(), authResponse.getMsg());

            // Init Main
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Show message error
            Util.longToast(getApplicationContext(), authResponse.getMsg());
            mFacebookLogin.setEnabled(true);
        }
    }
}
