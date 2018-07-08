package com.easyfixapp.easyfix.services;

import android.util.Log;

import com.easyfixapp.easyfix.fragments.NotificationFragment;
import com.easyfixapp.easyfix.fragments.TechnicalHistoryFragment;
import com.easyfixapp.easyfix.models.Notification;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;


public class MessagingService extends FirebaseMessagingService {

    private static final String IS_LOGOUT = "logout";
    private static final String IS_NOTIFICATION = "notifications";
    private static final String IS_PROVIDER = "providers";

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
            boolean isProvider = data.containsKey(IS_PROVIDER);

            if (isLogout) {
                Util.logout(getApplicationContext());
            } else if (isNotification) {
                EventBus.getDefault().post(new Notification(Util.ACTION_UPDATE));
            } else if (isProvider) {
                int idReservation = Integer.valueOf(data.get("reservation"));

                if (idReservation != 0) {
                    // Prevent presentation when app isn't in foreground
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    sessionManager.setNotificationProvider(true);
                    sessionManager.setReservationId(idReservation);

                    Reservation reservation = new Reservation();
                    reservation.setId(idReservation);
                    EventBus.getDefault().post(new Notification(Util.ACTION_PROVIDER, reservation));
                }
            }
        }
    }
}