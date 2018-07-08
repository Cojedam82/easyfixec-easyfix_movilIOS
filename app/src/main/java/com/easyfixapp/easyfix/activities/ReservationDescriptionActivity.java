package com.easyfixapp.easyfix.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;

import org.greenrobot.eventbus.EventBus;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by jrealpe on 5/26/18.
 */

public class ReservationDescriptionActivity extends AppCompatActivity {

    private EditText mDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_description);

        mDescriptionView = findViewById(R.id.txt_description);
        mDescriptionView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.txt_continue || id == EditorInfo.IME_ACTION_DONE) {
                    Util.hideSoftKeyboard(getApplicationContext(), getCurrentFocus());
                    onContinue(null);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.setReservationDetail(true);
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    public void onClose(View view) {
        finish();
    }

    public void onContinue(View view) {
        String description = mDescriptionView.getText().toString();
        if (TextUtils.isEmpty(description)) {
            Util.longToast(getApplicationContext(), "Por Favor, ingrese una descripción válida");
        } else {
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            sessionManager.setReservationDetail(true);

            finish();

            Reservation reservation = new Reservation();
            reservation.setDescription(description);
            EventBus.getDefault().postSticky(reservation);
        }
    }
}
