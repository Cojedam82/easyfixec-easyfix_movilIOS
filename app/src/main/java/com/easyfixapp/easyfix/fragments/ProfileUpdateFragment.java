package com.easyfixapp.easyfix.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.LoginActivity;
import com.easyfixapp.easyfix.activities.SignupActivity;
import com.easyfixapp.easyfix.models.AuthResponse;
import com.easyfixapp.easyfix.models.Profile;
import com.easyfixapp.easyfix.models.User;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.AuthService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by julio on 09/10/17.
 */

public class ProfileUpdateFragment extends DialogFragment{

    private Button mUpdateView;
    private EditText mFieldView;
    private ImageView mCloseView;
    private View view;

    private String field = "", value = "";

    public ProfileUpdateFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get field type
        field = getArguments().getString("field");
        value = getArguments().getString("value");

        // Set dialog style
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_update, container, false);

        mFieldView = view.findViewById(R.id.txt_field);
        mCloseView = view.findViewById(R.id.img_close);
        mUpdateView = view.findViewById(R.id.btn_update);

        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mUpdateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        //SessionManager sessionManager = new SessionManager(getContext());
        //User user = sessionManager.getUser();
        //Profile profile = user.getProfile();

        mFieldView.setHint(field);
        mFieldView.setText(value);

        if (field.toLowerCase().equals(getResources().getString(R.string.prompt_first_name)) ||
                field.toLowerCase().equals(getResources().getString(R.string.prompt_last_name)) ||
                field.toLowerCase().equals(getResources().getString(R.string.prompt_email)) ||
                field.toLowerCase().equals(getResources().getString(R.string.prompt_password)) ||
                field.toLowerCase().equals(getResources().getString(R.string.prompt_payment_method))) {
            mFieldView.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (field.toLowerCase().equals(getResources().getString(R.string.prompt_phone))) {
            mFieldView.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (field.toLowerCase().equals(getResources().getString(R.string.prompt_password))) {
            mFieldView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    /*

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
            Util.shortToast(getApplicationContext(), phone);
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
            if (Util.isNetworkAvailable(getActivity())) {

                Util.showLoading(getActivity(), getString(R.string.message_signup_request));
                updateTask(user);
            } else {
                Util.longToast(getActivity(),
                        getString(R.string.message_network_connectivity_failed));
            }
        }
    }*/


    /**
     * Signup Task
     */
    private void updateTask(final User user){

        final SessionManager sessionManager = new SessionManager(getActivity());
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();

        Call<User> call = apiService.updateUser(user.getId(), token, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                if (response.isSuccessful()) {
                    Log.i(Util.TAG_PROFILE, "Update result: success!");
                    sessionManager.saveUser(response.body());
                    dismiss();
                } else {
                    // Error response, no access to resource?
                    Log.i(Util.TAG_PROFILE, "Update result: " + response.toString());
                    Util.longToast(getActivity(),
                            getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                // something went completely south (like no internet connection)
                Log.i(Util.TAG_PROFILE, "Update result: failed, " + t.getMessage());
                Util.longToast(getActivity(),
                        getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }
}
