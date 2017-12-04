package com.easyfixapp.easyfix.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.ProfileUpdateActivity;
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

        final String firstName = user.getFirstName();
        mFirstNameView.setText(firstName);
        mFirstNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(getResources().getString(R.string.prompt_first_name), firstName);
            }
        });

        final String lastName = user.getLastName();
        mLastNameView.setText(lastName);
        mLastNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(getResources().getString(R.string.prompt_last_name), lastName);
            }
        });


        final String email = user.getEmail();
        mEmailView.setText(email);
        mEmailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(getResources().getString(R.string.prompt_email), email);
            }
        });

        final String phone = profile.getPhone();
        mPhoneView.setText(phone);
        mPhoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(getResources().getString(R.string.prompt_phone), phone);
            }
        });


        mPasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(getResources().getString(R.string.prompt_password), "");
            }
        });

        final int paymentMethod = profile.getPaymentMethod();
        mPaymentMethod.setText(profile.getPaymentMethodString());
        mPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(getResources().getString(R.string.prompt_payment_method),
                        String.valueOf(paymentMethod));
            }
        });
    }

    private void update(String field, String value) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("field", field);
        bundle.putSerializable("value", value);

        Intent intent = new Intent(getActivity(), ProfileUpdateActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
