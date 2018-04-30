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
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.models.AuthResponse;
import com.easyfixapp.easyfix.models.Profile;
import com.easyfixapp.easyfix.models.User;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.AuthService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.READ_CONTACTS;
import static io.realm.internal.network.OkHttpAuthenticationServer.JSON;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    LoginResult loginResultFinal;
    CallbackManager mCallbackManager;
    AccessTokenTracker accessTokenTraker;
    ProfileTracker profileTracker;
    AccessToken accessToken;
    com.facebook.Profile profilefb;
    private ProfileTracker mProfileTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);


        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        mCallbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if(com.facebook.Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(com.facebook.Profile oldProfile, com.facebook.Profile currentProfile) {
                            com.facebook.Profile.setCurrentProfile(currentProfile);
                            mProfileTracker.stopTracking();
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                }
                else {
                    com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();

                    Log.v("facebook - profile", profile.getFirstName());



                }


            }

            @Override
            public void onCancel() {
                Log.v("facebook - onCancel", "cancelled");
            }

            @Override
            public void onError(FacebookException e) {
                Log.v("facebook - onError", e.getMessage());
            }
        });


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

        Button login = (Button) findViewById(R.id.login_buton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.hideSoftKeyboard(getApplicationContext(), getCurrentFocus());
                attemptLogin(null);
            }
        });

        mPasswordView = (EditText) findViewById(R.id.password);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {
//                    Util.hideSoftKeyboard(getApplicationContext(), getCurrentFocus());
//                    attemptLogin(null);
//                    return true;
//                }
//                return false;
//            }
//        });
    }
    User user;
    String tokenauth;
    Profile profileFB;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if you don't add following block,
        // your registered `FacebookCallback` won't be called
        if (mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            // Get user
            Util.showLoading(this,"Ingresando con Facebook");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    user = new User();
                    user.setPassword("facebook_password");
                    user.setFirstName(com.facebook.Profile.getCurrentProfile().getFirstName());
                    user.setLastName(com.facebook.Profile.getCurrentProfile().getLastName());
                    user.setEmail(com.facebook.Profile.getCurrentProfile().getLinkUri().toString());
                    user.setId(9);
                    profileFB = new Profile();
                    profileFB.setImage(com.facebook.Profile.getCurrentProfile().getProfilePictureUri(800,800).toString());
                    profileFB.setPaymentMethod(1);
                    profileFB.setRole(1);
                    // Save user in shared preferences

                    //CAMBIOS
                    String registrationId = FirebaseInstanceId.getInstance().getToken();

//                    Map<String, Object> params = new HashMap<>();
//                    params.put("email", com.facebook.Profile.getCurrentProfile().getLinkUri().toString());
//                    params.put("password", "admin1234");
//                    params.put("role", Util.USER_ROLE);
//                    params.put("registration_id", registrationId);
//                    params.put("type", Util.TYPE_DEVICE);
//                    loginTaskFB(params);
//                    //CAMBIOS



                    final Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            com.google.gson.JsonObject profile;
                            com.google.gson.JsonObject social;
                            profileFB.setToken(tokenToPass);
                            user.setProfile(profileFB);
                            try {//CAMBIOS PARA FACEBOOK DESDE ACA
                                profile = new com.google.gson.JsonObject();
                                profile.addProperty("phone", user.getProfile().getPhone());
                                System.out.println(profile);
                                social = new com.google.gson.JsonObject();
                                social.addProperty("social_id", "1234");
                                social.addProperty("social_token", "555");
                                System.out.println(profile);
                                System.out.println(user.getFirstName()+" "+user.getLastName());
                                System.out.println(user.getEmail()+" ");
                                System.out.println();
                                System.out.println(profile);
                                System.out.println(social);

                            ApiService apiService = ServiceGenerator.createApiService();
                            RequestBody phone = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), profile.getAsJsonObject()+"");
                            RequestBody socialreq = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), social.getAsJsonObject()+"");
                            Call<com.google.gson.JsonObject> call = apiService.signUpSocial(
                                    user.getFirstName(),
                                    user.getLastName(),
                                    "",
                                    user.getFirstName()+user.getLastName()+"@facebook.com",
                                    user.getFirstName()+user.getLastName()+"@facebook.com",
                                    phone,
                                    socialreq);

                                System.out.println("SOLICITUD> "+ call.request());
                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    JsonObject object = response.body();
                                    try {
                                        System.out.println("RESPUESTA> " + object);
                                        //parse object
                                        JsonObject data = object.getAsJsonObject("data");
                                        JsonObject profile = data.getAsJsonObject("profile");
                                        String tokenFB = profile.getAsString();
                                        tokenToPass = tokenFB;
                                        System.out.println("FUNCIONO LA LLAMDA. TOKEN> " + tokenFB);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {

                                }
                            }); //CAMBIOS PARA FACEBOOK HASTA ACA
                            Log.i("FACEBOOKTOKEN ", "El token es "+profileFB.getToken());
                            SessionManager sessionManager = new SessionManager(getApplicationContext());
                            sessionManager.saveUser(user);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1000);
                }
            }, 2000);

            return;
        }

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


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
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
//            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
//            focusView = mEmailView;
            cancel = true;
        } else if (!Util.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
//            focusView.requestFocus();
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

    @Override
    protected void onResume() {
        super.onResume();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

                    if (!userResponse.isError()) {

                        // Get user
                        User user = (User) userResponse.getData();

                        // Save user in shared preferences
                        SessionManager sessionManager = new SessionManager(getApplicationContext());
                        sessionManager.saveUser(user);

                        Log.d("FACEBOOK","El token es: "+user.getProfile().getToken());
                        // Show message success
                        Util.longToast(getApplicationContext(), userResponse.getMsg());

                        // Init Main
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Show message error
                        Util.longToast(getApplicationContext(), userResponse.getMsg());
                    }
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
    String tokenToPass;

    private void loginTaskFB(Map<String,Object> params){

        AuthService authService = ServiceGenerator.createAuthService();
        Call<AuthResponse<User>> call = authService.login(params);
        call.enqueue(new Callback<AuthResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse<User>> call, @NonNull Response<AuthResponse<User>> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_LOGIN, "Login result: success!");

                    AuthResponse userResponse = response.body();

                    if (!userResponse.isError()) {

                        // Get user
                        User userfb = (User) userResponse.getData();
                        tokenToPass = userfb.getProfile().getToken();
                        // Save user in shared preferences
//                        SessionManager sessionManager = new SessionManager(getApplicationContext());
//                        sessionManager.saveUser(user);

                        Log.d("FACEBOOK","El token es: "+tokenToPass);
                        // Show message success

                    } else {
                        // Show message error
                        Util.longToast(getApplicationContext(), userResponse.getMsg());
                    }
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

            }
        });
    }
}
