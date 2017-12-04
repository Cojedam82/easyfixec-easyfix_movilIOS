package com.easyfixapp.easyfix.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.PaymentMethod;
import com.easyfixapp.easyfix.models.User;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by julio on 09/10/17.
 */

public class ProfileUpdateActivity extends AppCompatActivity {

    private Button mUpdateView;
    private EditText mFieldView, mConfirmationFieldView;
    private TextView mTitle1View, mTitle2View;
    private Spinner mSpinnerView;
    private ImageView mCloseView;

    private String field = "", value = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_update);

        // Get field type
        field = getIntent().getExtras().getString("field");
        value = getIntent().getExtras().getString("value");

        mFieldView = findViewById(R.id.txt_field);
        mConfirmationFieldView = findViewById(R.id.txt_confirmation_field);
        mTitle1View = findViewById(R.id.txt_title_1);
        mTitle2View = findViewById(R.id.txt_title_2);
        mSpinnerView = findViewById(R.id.spinner);
        mCloseView = findViewById(R.id.img_close);
        mUpdateView = findViewById(R.id.btn_update);

        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.activity = this;
        init();
    }


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }


    private void init() {

        if (field.equals(getResources().getString(R.string.prompt_first_name)) ||
                field.equals(getResources().getString(R.string.prompt_last_name)) ||
                field.equals(getResources().getString(R.string.prompt_email))) {
            mTitle1View.setText(this.field);

            mFieldView.setVisibility(View.VISIBLE);
            mFieldView.setInputType(InputType.TYPE_CLASS_TEXT);
            mFieldView.setText(value);
        } else if (field.equals(getResources().getString(R.string.prompt_phone))) {
            mTitle1View.setText(this.field);

            mFieldView.setVisibility(View.VISIBLE);
            mFieldView.setInputType(InputType.TYPE_CLASS_PHONE);
            mFieldView.setText(value);
        } else if (field.equals(getResources().getString(R.string.prompt_password))) {
            mTitle1View.setText("Nueva " + this.field);

            mTitle2View.setText("Repetir Nueva " + this.field);
            mTitle2View.setVisibility(View.VISIBLE);

            mFieldView.setVisibility(View.VISIBLE);
            mFieldView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

            mConfirmationFieldView.setVisibility(View.VISIBLE);

        } else if (field.equals(getResources().getString(R.string.prompt_payment_method))) {
            mTitle1View.setText(this.field);

            mSpinnerView.setVisibility(View.VISIBLE);
            mSpinnerView.setAdapter(new ArrayAdapter<>(
                    getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    Util.getPaymentMethods())
            );
            mSpinnerView.setSelection(Integer.valueOf(value)-1);
        }
    }

    public void attemptUpdate(View v) {

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        User user = sessionManager.getUser();

        // Reset errors.
        mFieldView.setError(null);
        mConfirmationFieldView.setError(null);

        String field = mFieldView.getText().toString();
        String confirmationField = mConfirmationFieldView.getText().toString();

        boolean cancel = false;
        View focusView = mFieldView;

        if (this.field.equals(getResources().getString(R.string.prompt_payment_method))) {
            PaymentMethod paymentMethod = (PaymentMethod) mSpinnerView.getSelectedItem();
            user.getProfile().setPaymentMethod(paymentMethod.getId());
        }

        else if (this.field.equals(getResources().getString(R.string.prompt_first_name))) {

            if (TextUtils.isEmpty(field)) {
                mFieldView.setError(getString(R.string.error_field_required));
                cancel = true;
            } else if (!Util.isNameValid(field)) {
                mFieldView.setError(getString(R.string.error_invalid_first_name));
                cancel = true;
            } else {
                user.setFirstName(field);
            }
        } else if (this.field.equals(getResources().getString(R.string.prompt_last_name))) {
            if (TextUtils.isEmpty(field)) {
                mFieldView.setError(getString(R.string.error_field_required));
                cancel = true;
            } else if (!Util.isNameValid(field)) {
                mFieldView.setError(getString(R.string.error_invalid_last_name));
                cancel = true;
            } else {
                user.setLastName(field);
            }
        } else if (this.field.equals(getResources().getString(R.string.prompt_email))) {

            // Check for a valid email address.
            if (TextUtils.isEmpty(field)) {
                mFieldView.setError(getString(R.string.error_field_required));
                cancel = true;
            } else if (!Util.isEmailValid(field)) {
                mFieldView.setError(getString(R.string.error_invalid_email));
                cancel = true;
            } else {
                user.setEmail(field);
            }
        } else if (this.field.equals(getResources().getString(R.string.prompt_phone))) {
            if (TextUtils.isEmpty(field)) {
                mFieldView.setError(getString(R.string.error_invalid_email));
                mFieldView.setError(getString(R.string.error_field_required));
                cancel = true;
            } else {
                user.getProfile().setPhone(field);
            }
        } else if (this.field.equals(getResources().getString(R.string.prompt_password))) {

            // Check for a valid password, if the user entered one.
            if (TextUtils.isEmpty(field)) {
                mFieldView.setError(getString(R.string.error_field_required));
                cancel = true;
            } else if (TextUtils.isEmpty(field) || Util.isPasswordMinimumLengthValid(field)) {
                mFieldView.setError(getString(R.string.error_invalid_password));
                cancel = true;
            }

            if (!field.equals(confirmationField)) {
                mConfirmationFieldView.setError(getString(R.string.error_confirmation_password));
                cancel = true;
            } else {
                user.setPassword(field);
            }

        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (Util.isNetworkAvailable(getApplicationContext())) {

                Util.showLoading(this,
                        getString(R.string.acount_message_update_request));
                updateTask(user);
            } else {
                Util.longToast(this,
                        getString(R.string.message_network_connectivity_failed));
            }
        }
    }


    /**
     * Signup Task
     */
    private void updateTask(final User user){

        final SessionManager sessionManager = new SessionManager(getApplicationContext());
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();

        Call<User> call = apiService.updateUser(user.getId(), token, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                if (response.isSuccessful()) {
                    Log.i(Util.TAG_PROFILE, "Update result: success!");
                    sessionManager.saveUser(response.body());
                    finish();

                    Util.longToast(getApplicationContext(), getString(R.string.acount_message_update_response));
                } else {
                    // Error response, no access to resource?
                    Log.i(Util.TAG_PROFILE, "Update result: " + response.toString());
                    Util.longToast(getApplicationContext(),
                            getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                // something went completely south (like no internet connection)
                Log.i(Util.TAG_PROFILE, "Update result: failed, " + t.getMessage());
                Util.longToast(getApplicationContext(),
                        getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }
}
