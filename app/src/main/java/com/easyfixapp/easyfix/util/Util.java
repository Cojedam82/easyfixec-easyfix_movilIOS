package com.easyfixapp.easyfix.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by julio on 20/02/17.
 */
public class Util {

    /**
     * UI LOADING
     */
    private static ProgressDialog mLoading = null;


    /**
     * RESULT CODES
     */
    public static final int GOOGLE_SIGN_IN = 007;
    public static final int FACEBOOK_SIGN_IN = 001;
    public static final int PHONE_STATE_REQUEST_CODE = 002;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 010;
    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 011;
    public static final int CAMERA_REQUEST_CODE = 012;
    public static final int IMAGE_GALLERY_REQUEST_CODE = 013;
    public static final int IMAGE_CAMERA_REQUEST_CODE = 014;


    /**
     * LOG TAG
     */
    public static final String TAG_KEYBOARD = "SM-KEYBOARD";
    public static final String TAG_KEY = "SM-KEY";
    public static final String TAG_INIT = "SM-INITIATION";
    public static final String TAG_SIGNIN = "SM-SIGNIN";
    public static final String TAG_SIGNUP = "SM-SIGNUP";
    public static final String TAG_TRAINING = "SM-TRAINING";
    public static final String TAG_FCM_MESSAGING = "SM-FCM-MESSAGING";
    public static final String TAG_UPDATE_PROFILE = "SM-UPDATE-PROFILE";
    public static final String TAG_UPDATE_REMINDERS = "SM-UPDATE-REMINDERS";
    public static final String TAG_PAYMENT_CODE = "SM-PAYMENT-CODE";
    public static final String TAG_PAYMENT_STRIPE = "SM-PAYMENT-STRIPE";
    public static final String TAG_MEMBERSHIP = "SM-MEMBERSHIP";
    public static final String TAG_FAQ = "SM-FAQ";
    public static final String TAG_RECORD = "SM-RECORD";
    public static final String TAG_REMINDER_NOTIFICATION = "SM-REMINDER-NOTIFY";
    public static final String TAG_AUDIO = "SM-AUDIO";
    public static final String TAG_TIP = "SM-TIP";
    public static final String TAG_PROFILE = "SM-PROFILE";
    public static final String TAG_IMAGE_PROFILE = "SM-IMAGE-PROFILE";
    public static final String TAG_INTRO = "SM-INTRO";

    /**
     * FRAGMENT TAGS
     */
    public static final String READING_FRAGMENT_TAG = "training-reading";
    public static final String AUDIO_FRAGMENT_TAG = "training-audio";
    public static final String DISPLAY_FRAGMENT_TAG = "training-display";
    public static final String TASK_FRAGMENT_TAG = "training-task";
    public static final String MENU_FRAGMENT_TAG = "training-menu";
    public static final String TIP_FRAGMENT_TAG = "training-tip";

    /**
     * NOTIFICATION TAGS
     */
    public static final String NOTIFICATION_TRAINING = "notification-training";
    public static final String NOTIFICATION_TASK = "notification-task";

    /**
     * PAYMENT CODES
     */
    public static final int PAYMENT_STRIPE = 1;
    public static final int PAYMENT_CODE = 2;


    /**
     * REMINDER CODES
     */
    public static final int REMINDER_TRAINING = 100;
    public static final int REMINDER_TASK_1 = 101;
    public static final int REMINDER_TASK_2 = 102;
    public static final int REMINDER_TASK_3 = 103;


    /**
     * MEMBERSHIP
     */
    public static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    public static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    public static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    public static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    public static final char CARD_NUMBER_DIVIDER = ' ';

    public static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    public static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    public static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    public static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    public static final char CARD_DATE_DIVIDER = '/';

    public static final int CARD_CVC_TOTAL_SYMBOLS = 3;


    /**
     * PATTERNS
     */
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
     * NAME VALIDATOR
     */
    public static boolean isNameValid(String name) {
        if (TextUtils.isEmpty(name) || !(name.length()>4) || !name.replace(" ", "").matches("[a-zA-Z]+")) return false;
        return true;
    }


    /**
     * PASSWORD VALIDATOR
     */
    public static boolean isPasswordMinimumLengthValid(String password) {
        return password.length() > 4;
    }

    public static boolean isPasswordAlphanumericsValid(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9]+$");
    }


    /**
     *  HANDLER PROGRESS
     */

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
     * Hide Softf Keyboard
     **/
    public static void hideSoftKeyboard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch (Exception e){
            Log.e(TAG_KEYBOARD, "Cannot close soft keyboard");
        }
    }


    /**
     * Hide Softf Keyboard
     **/
    public static void hideSoftKey(Activity activity) {
        try {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }catch (Exception e){
            Log.e(TAG_KEY, "Cannot close soft ke");
        }
    }


    /**
     * Obtain network status
     **/
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();

        // if no network is available networkInfo will be null
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }


    /**
     * Get current time millis in integer format
     */
    public static int currentTimeMillis() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    /**
     * Build simple REST adapter
     */
    /*
    public static Api createService(Context context, Gson gson){

        GsonConverterFactory gsonConverterFactory;

        if (gson!=null)
            gsonConverterFactory = GsonConverterFactory.create(gson);
        else
            gsonConverterFactory = GsonConverterFactory.create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.api_url))
                .addConverterFactory(gsonConverterFactory)
                .build();

        // Create an instance of our Object API interface.
        return retrofit.create(Api.class);
    }*/

    /**
     * Handler error to fcm registration
     * @param response
     * @return
     */
    /*
    public static FCMError parseFCMError(Retrofit retrofit, Response<?> response) {

        Converter<ResponseBody, FCMError> converter = retrofit
                .responseBodyConverter(FCMError.class, new Annotation[0]);

        FCMError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new FCMError();
        }

        return error;
    }*/
}
