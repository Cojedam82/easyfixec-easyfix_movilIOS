package com.easyfixapp.easyfix.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.LoginActivity;
import com.easyfixapp.easyfix.activities.MainActivity;
import com.easyfixapp.easyfix.models.PaymentMethod;
import com.easyfixapp.easyfix.models.Profile;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;

/**
 * Created by julio on 20/05/17.
 */
public class Util {

    /**
     * UI MESSAGE
     **/
    private static ProgressDialog mLoading = null;
    private static Toast mToast = null;


    /**
     * URL
     **/
    public static final String BASE_URL = "http://206.225.95.194:8000";
    public static final String API_URL = BASE_URL + "/api/v1/";

    public static final String PRIVACY_POLICIES_URL = "file:///android_asset/htmls/policies.html";
    public static final String TERMS_URL = "file:///android_asset/htmls/terms.html";
    public static final String HELP_URL = "file:///android_asset/htmls/help.html";


    /**
     * CONSTANTS
     **/
    public static final int MAX_IMAGES_SERVICE = 4;
    public static final int USER_ROLE = 1;
    public static final String TYPE_DEVICE = "android";

    /**
     * RESULT CODES
     **/
    public static final int PHONE_STATE_REQUEST_CODE = 001;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 002;
    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 003;
    public static final int CAMERA_REQUEST_CODE = 004;
    public static final int IMAGE_GALLERY_REQUEST_CODE = 005;
    public static final int IMAGE_CAMERA_REQUEST_CODE = 006;
    public static final int REQUEST_READ_CONTACTS = 007;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 8;
    public static final int LOCATION_REQUEST_CODE = 9;
    public static final int PLAY_SERVICES_REQUEST_CODE = 10;


    /**
     * LOG TAG
     **/
    public static final String TAG_KEYBOARD = "EF-KEYBOARD";
    public static final String TAG_KEY = "EF-KEY";
    public static final String TAG_LOGIN = "EF-LOGIN";
    public static final String TAG_SIGNUP = "EF-SIGNUP";
    public static final String TAG_SERVICE = "EF-SERVICE";
    public static final String TAG_NOTIFICATION = "EF-NOTIFICATION";
    public static final String TAG_TECHNICAL_HISTORY = "EF-TECHNICAL-HISTORY";
    public static final String TAG_SERVICE_DETAIL_IMAGE = "EF-SERVICE-DETAIL-IMAGE";
    public static final String TAG_ = "EF-ADDRESS";
    public static final String TAG_ADDRESS = "EF-RESERVATION";
    public static final String TAG_MENU = "EF-MENU";
    public static final String TAG_PROFILE = "EF-PROFILE";
    public static final String TAG_FCM = "EF-FCM";
    public static final String TAG_MAP = "EF-MAP";


    /**
     * PATTERNS
     **/
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    /**
     * EMAIL VALIDATOR
     **/
    public static boolean isEmailValid(String email) {
        try {
            // Compiles the given regular expression into a pattern.
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            // Match the given input against this pattern
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * PHONE VALIDATOR
     **/
    public static boolean isPhoneValid(String phone) {

        if (!TextUtils.isEmpty(phone))
            if ((phone.length() == 9 && phone.charAt(0) != '0') ||
                    (phone.length() == 10 && phone.charAt(0) == '0'))
                return true;
        return false;
    }


    /**
     * NAME VALIDATOR
     **/
    public static boolean isNameValid(String name) {
        if (TextUtils.isEmpty(name) || !(name.length()>4) || !name.replace(" ", "").matches("[a-zA-Z]+")) return false;
        return true;
    }


    /**
     * PASSWORD VALIDATOR
     **/
    public static boolean isPasswordMinimumLengthValid(String password) {
        return password.length() < 4;
    }

    public static boolean isPasswordAlphanumericsValid(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9]+$");
    }


    /**
     *  HANDLER TOAST
     **/
    public static void longToast(Context context, String message) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    public static void shortToast(Context context, String message) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.show();
    }


    /**
     *  HANDLER PROGRESS
     **/
    public static void showLoading(Context context, String message){

        if (mLoading != null)
            mLoading.dismiss();
            mLoading = null;

        mLoading = new ProgressDialog(context);
        mLoading.setCancelable(false);
        mLoading.setMessage(message);
        mLoading.show();
    }

    public static void hideLoading() {
        if (mLoading != null) {
            mLoading.dismiss();
            mLoading = null;
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(Context context,
                                    final View foregroundView,
                                    View view,
                                    final boolean show) {


        final ProgressBar mProgressView = view.findViewById(R.id.progress);

        // Show progress container
        final View mProgressContainerView = view.findViewById(R.id.ll_progress_container);
        mProgressContainerView.setVisibility(show ? View.VISIBLE : View.GONE);

        // Hide empty container
        final View mEmptyContainerView = view.findViewById(R.id.ll_empty_container);
        mEmptyContainerView.setVisibility(View.GONE);

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            foregroundView.setVisibility(show ? View.GONE : View.VISIBLE);
            /*foregroundView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    foregroundView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });*/

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            /*mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });*/
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            foregroundView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public static void showMessage(View foregroundView, View view, String message) {

        // Hide progress container
        final View mProgressContainerView = view.findViewById(R.id.ll_progress_container);
        mProgressContainerView.setVisibility(View.GONE);

        // Show layout
        final View mEmptyContainerView = view.findViewById(R.id.ll_empty_container);
        mEmptyContainerView.setVisibility(View.VISIBLE);

        TextView mMessageView = view.findViewById(R.id.txt_message);
        mMessageView.setText(message);


        // Hide foreground
        if (foregroundView.getVisibility() == View.VISIBLE) {
            foregroundView.setVisibility(View.GONE);
        }
    }


    /**
     * SOFT KEYBOARD
     **/
    public static void hideSoftKeyboard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch (Exception e){
            Log.e(TAG_KEYBOARD, "Cannot close soft keyboard");
        }
    }


    /**
     * NETWORK STATUS
     **/
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();

        // if network isn't available networkInfo will be null
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }


    public static void logout(final Context context) {

        int message;
        final Activity activity = MainActivity.activity;

        try {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    showLoading(activity, activity.getString(R.string.message_logout_request));
                }
            });
        } catch (Exception ignore){}

        // Clean preferences
        SessionManager sessionManager = new SessionManager(context);
        sessionManager.clear();

        // Reset FCM token
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    Log.e(Util.TAG_MENU, e.getMessage());
                }
            }
        }).start();

        // Clean database
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();
            realm.deleteAll();
            realm.commitTransaction();

            message = R.string.message_logout;
        } catch (Exception e){
            Log.e(Util.TAG_MENU, e.toString());
            message = R.string.message_service_server_failed;
        } finally {
            realm.close();
        }

        try {
            final int finalMessage = message;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.finish();

                    hideLoading();
                    longToast(context, context.getString(finalMessage));
                }
            });
        } catch (Exception ignore){}
    }

    public static List<PaymentMethod> getPaymentMethods() {
        List<PaymentMethod> paymentMethods = new ArrayList<>();

        paymentMethods.add(new PaymentMethod(Profile.CASH_KEY, Profile.CASH_VALUE));
        paymentMethods.add(new PaymentMethod(Profile.CREDIT_CARD_KEY, Profile.CREDIT_CARD_VALUE));

        return paymentMethods;
    }
}
