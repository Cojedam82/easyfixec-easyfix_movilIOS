package com.easyfixapp.easyfix.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    /**
     * LOG TAG
     **/
    public static final String TAG_KEYBOARD = "SM-KEYBOARD";
    public static final String TAG_KEY = "SM-KEY";
    public static final String TAG_LOGIN = "SM-LOGIN";
    public static final String TAG_SIGNUP = "SM-SIGNUP";
    public static final String TAG_SERVICE = "SM-SERVICE";
    public static final String TAG_NOTIFICATION = "SM-NOTIFICATION";
    public static final String TAG_SERVICE_DETAIL_IMAGE = "EF-SERVICE-DETAIL-IMAGE";


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
     * SOFT KEYBOARD
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
}
