package com.easyfixapp.easyfix.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.MainActivity;
import com.easyfixapp.easyfix.adapters.ReservationAdapter;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.easyfixapp.easyfix.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends RootFragment {

    private static View view = null;
    private static RecyclerView mReservationView = null;
    private static ReservationAdapter mReservationAdapter = null;
    private static List<Reservation> mReservationList = new ArrayList<>();

    private static Context context = null;

    public NotificationFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reservation, container, false);


        mReservationAdapter = new ReservationAdapter(getActivity(),
                mReservationList, Reservation.TYPE_NOTIFICATION);

        mReservationView = view.findViewById(R.id.rv_reservation);

        // Set requirements to show recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mReservationView.setLayoutManager(mLayoutManager);

        // Set item divider
        mReservationView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mReservationView.setItemAnimator(new DefaultItemAnimator());

        // Set adapter
        mReservationView.setAdapter(mReservationAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        context = getContext();
        reservationTask();
    }


    /** Fetch reservations **/
    private static void reservationTask(){
        Util.showProgress(context, mReservationView, view, true);

        SessionManager sessionManager = new SessionManager(context);
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();

        Call<List<Reservation>> call = apiService.getNotifications(token);
        call.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                Util.showProgress(context, mReservationView, view, false);

                if (response.isSuccessful()) {
                    Log.i(Util.TAG_NOTIFICATION, "Notification result: success!");

                    List<Reservation> reservationList = response.body();
                    if (!reservationList.isEmpty()) {
                        mReservationList.clear();

                        for (Reservation reservation : reservationList){
                            mReservationList.add(reservation);
                        }
                        mReservationAdapter.notifyDataSetChanged();

                    } else {

                        Util.showMessage(mReservationView, view,
                                context.getString(R.string.message_service_server_empty));
                    }
                } else {
                    Log.i(Util.TAG_NOTIFICATION, "Notification result: " + response.toString());
                    Util.showMessage(mReservationView, view,
                            context.getString(R.string.message_service_server_failed));
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                Log.i(Util.TAG_NOTIFICATION, "Notification result: failed, " + t.getMessage());
                Util.showProgress(context, mReservationView, view, false);
                Util.showMessage(mReservationView, view,
                        context.getString(R.string.message_network_local_failed));
            }
        });
    }

    public static void updateReservations() {
        final Activity activity = MainActivity.activity;

        try {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    reservationTask();
                }
            });
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
