package com.easyfixapp.easyfix.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.Profile;
import com.easyfixapp.easyfix.models.User;
import com.easyfixapp.easyfix.util.SessionManager;

/**
 * Created by julio on 09/10/17.
 */

public class ProfileFragment extends RootFragment{

    private EditText mFirstNameView, mLastNameView, mEmailView,
            mPasswordView, mPhoneView, mPaymentMethod;
    private View view;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        mFirstNameView = view.findViewById(R.id.txt_first_name);
        mLastNameView = view.findViewById(R.id.txt_last_name);
        mEmailView = view.findViewById(R.id.txt_email);
        mPhoneView = view.findViewById(R.id.txt_phone);
        mPasswordView = view.findViewById(R.id.txt_password);
        mPaymentMethod = view.findViewById(R.id.txt_payment_method);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        SessionManager sessionManager = new SessionManager(getContext());
        User user = sessionManager.getUser();
        Profile profile = user.getProfile();

        mFirstNameView.setText(user.getFirstName());
        mLastNameView.setText(user.getLastName());
        mEmailView.setText(user.getEmail());

        mPhoneView.setText(profile.getPhone());
    }
}
