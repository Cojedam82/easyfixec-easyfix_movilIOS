package com.easyfixapp.easyfix.services;

import android.util.Log;

import com.easyfixapp.easyfix.fragments.NotificationFragment;
import com.easyfixapp.easyfix.fragments.TechnicalHistoryFragment;
import com.easyfixapp.easyfix.util.Util;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class MessagingService extends FirebaseMessagingService {

    private static final String IS_LOGOUT = "logout";
    private static final String IS_NOTIFICATION = "notifications";

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
            boolean isNotification = data.containsKey(IS_NOTIFICATION);

            if (isLogout) {
                Util.logout(getApplicationContext());
            } else if (isNotification) {
                NotificationFragment.updateReservations();
                TechnicalHistoryFragment.updateReservations();
            }
        }
    }
}