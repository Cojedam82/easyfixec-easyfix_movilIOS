package com.easyfix.client.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfix.client.R;
import com.easyfix.client.models.AuthResponse;
import com.easyfix.client.models.Profile;
import com.easyfix.client.models.User;
import com.easyfix.client.util.AuthService;
import com.easyfix.client.util.ServiceGenerator;
import com.easyfix.client.util.Util;
import com.hbb20.CountryCodePicker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SignupActivity extends AppCompatActivity {

    private EditText mFirstNameView, mLastNameView, mEmailView, mPasswordView, mPhoneNumberView;
    private CountryCodePicker mPhoneCodeView;
    private TextView mCountryView, mLinkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirstNameView = (EditText) findViewById(R.id.txt_first_name);
        mLastNameView = (EditText) findViewById(R.id.txt_last_name);
        mEmailView = (EditText) findViewById(R.id.txt_email);
        mPasswordView = (EditText) findViewById(R.id.txt_password);
        mCountryView = (TextView) findViewById(R.id.txt_country);
        mLinkView = findViewById(R.id.txt_legacy);

        mPhoneNumberView = (EditText) findViewById(R.id.txt_phone_number);
        mPhoneNumberView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    Util.hideSoftKeyboard(getApplicationContext(), getCurrentFocus());
                    attemptSignUp(null);
                    return true;
                }
                return false;
            }
        });

        mPhoneCodeView = (CountryCodePicker) findViewById(R.id.txt_phone_code);
        //mPhoneCodeView.registerCarrierNumberEditText(mPhoneNumberView);
        mPhoneCodeView.setCountryForNameCode("EC");
        mCountryView.setText(mPhoneCodeView.getDefaultCountryName());
        mPhoneCodeView.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                mCountryView.setText(mPhoneCodeView.getSelectedCountryName());
            }
        });

        stripUnderlines(mLinkView);
        mLinkView.setMovementMethod(LinkMovementMethod.getInstance());

        populateInformation();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    /**
     * Fill Information
     */
    private void populateInformation(){

        // Select country by default

        if(getIntent().getExtras()!=null){

            Bundle bundle = getIntent().getExtras();
            mFirstNameView.setText(bundle.getString("first_name"));
            mLastNameView.setText(bundle.getString("last_name"));
            mEmailView.setText(bundle.getString("email"));

            Toast.makeText(getApplicationContext(),
                    "Complete sus datos porfavor!",
                    Toast.LENGTH_LONG).show();
        }
    }


    public void attemptSignUp(View v) {

        // Reset errors.
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString();

        String phone = mPhoneNumberView.getText().toString();
        String phoneCode = mPhoneCodeView.getSelectedCountryCodeWithPlus();

        boolean cancel = false;
        View focusView = null;

        if (!Util.isPhoneValid(phone)){
            mPhoneNumberView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneNumberView;
            cancel = true;
        }else{
            if (phone.length() == 10)
                phone = phone.substring(1);
            phone = phoneCode + phone;
        }

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

        if (!Util.isNameValid(firstName)) {
            mFirstNameView.setError(getString(R.string.error_invalid_first_name));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (!Util.isNameValid(lastName)) {
            mLastNameView.setError(getString(R.string.error_invalid_last_name));
            focusView = mLastNameView;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            if (Util.isNetworkAvailable(getApplicationContext())) {

                Util.showLoading(SignupActivity.this, getString(R.string.message_signup_request));

                Profile profile = new Profile();
                profile.setPhone(phone);

                User user = new User();
                user.setEmail(email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setPassword(password);
                user.setProfile(profile);

                signupTask(user);
            } else {
                Util.longToast(getApplicationContext(),
                        getString(R.string.message_network_connectivity_failed));
            }
        }
    }

    public void login(View view){
        // Init Signup
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * Signup Task
     */
    private void signupTask(User user){

        AuthService auth = ServiceGenerator.createAuthService();

        Call<AuthResponse<User>> call = auth.signup(user);
        call.enqueue(new Callback<AuthResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse<User>> call, @NonNull Response<AuthResponse<User>> response) {

                if (response.isSuccessful()) {
                    Log.i(Util.TAG_SIGNUP, "Signup result: success!");

                    AuthResponse userResponse = response.body();
                    if (!userResponse.isError()) {

                        Log.i(Util.TAG_SIGNUP, "Signup result: success!");

                        Util.longToast(getApplicationContext(), userResponse.getMsg());

                        // Init Login
                        //Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        //startActivity(intent);
                        finish();

                    } else {
                        // Show message error
                        Util.longToast(getApplicationContext(), userResponse.getMsg());
                    }
                } else {
                    // Error response, no access to resource?
                    Log.i(Util.TAG_SIGNUP, "Signup result: " + response.toString());
                    Util.longToast(getApplicationContext(),
                            getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse<User>> call, @NonNull Throwable t) {
                // something went completely south (like no internet connection)
                Log.i(Util.TAG_SIGNUP, "Signup result: failed, " + t.getMessage());
                Util.longToast(getApplicationContext(),
                        getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    @SuppressLint("ParcelCreator")
    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }
        @Override public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
