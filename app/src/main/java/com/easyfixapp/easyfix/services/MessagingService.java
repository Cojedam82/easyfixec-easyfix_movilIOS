package com.easyfixapp.easyfix.services;

import android.telephony.SmsManager;
import android.util.Log;

import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Date;
import java.util.Map;


public class MessagingService extends FirebaseMessagingService {

    private static final String IS_LOGOUT = "logout";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.i(Util.TAG_FCM, "From: " + remoteMessage.getFrom());

        /**
         * Check if message contains a data payload.
         *
         * Structure:
         *          {
         *              'logout':<true>
         *          }
         **/

        if (remoteMessage.getData().size() > 0) {
            Log.i(Util.TAG_FCM, "Message data payload: " + remoteMessage.getData());

            Map<String, String> data = remoteMessage.getData();

            boolean isLogout = data.containsKey(IS_LOGOUT);

            if (isLogout) {
                Util.logout(getApplicationContext());
            }
        }
    }
}