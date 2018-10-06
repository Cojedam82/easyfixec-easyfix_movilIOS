package com.easyfix.client.fragments;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyfix.client.R;
import com.easyfix.client.activities.LoginActivity;
import com.easyfix.client.activities.MainActivity;
import com.easyfix.client.activities.SplashActivity;
import com.easyfix.client.adapters.ReservationAdapter;
import com.easyfix.client.models.Notification;
import com.easyfix.client.models.Reservation;
import com.easyfix.client.util.ApiService;
import com.easyfix.client.util.ServiceGenerator;
import com.easyfix.client.util.SessionManager;
import com.easyfix.client.util.Util;
import com.easyfix.client.widget.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends RootFragment {

    private View view = null;
    private RecyclerView mReservationView = null;
    private ReservationAdapter mReservationAdapter = null;
    private List<Reservation> mReservationList = new ArrayList<>();

    //private static RootFragment mRootFrament = null;

    public NotificationFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reservation, container, false);

        mReservationView = view.findViewById(R.id.rv_reservation);

        mReservationAdapter = new ReservationAdapter(this, getActivity(),
                mReservationList, Reservation.TYPE_NOTIFICATION);

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
        //this.mRootFrament = this;
        reservationTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationEvent(Notification notification) {
        int action = notification.getAction();
        if (action == Util.ACTION_CREATE || action == Util.ACTION_UPDATE) {
            reservationTask();
        }
    }

    /** Fetch reservations **/
    private void reservationTask(){
        Util.showProgress(getContext(), mReservationView, view, true);

        SessionManager sessionManager = new SessionManager(getContext());
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();

        Call<List<Reservation>> call = apiService.getNotifications(token);
        call.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                Util.showProgress(getContext(), mReservationView, view, false);

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
                                getString(R.string.message_service_server_empty));
                    }
                } else {
                    Log.i(Util.TAG_NOTIFICATION, "Notification result: " + response.toString());
                    Util.showMessage(mReservationView, view,
                            getString(R.string.message_service_server_failed));
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                Log.i(Util.TAG_NOTIFICATION, "Notification result: failed, " + t.getMessage());
                Util.showProgress(getContext(), mReservationView, view, false);
                Util.showMessage(mReservationView, view,
                        getString(R.string.message_network_local_failed));
            }
        });
    }

    /*
    public static void showPostDetail(Reservation reservation) {

        try {
            mRootFrament.setBackPressedIcon();

            Bundle bundle = new Bundle();
            bundle.putParcelable("reservation", reservation);

            ServiceDetailFragment mServiceDetailFragment = new ServiceDetailFragment();
            mServiceDetailFragment.setArguments(bundle);

            FragmentTransaction transaction = mRootFrament.
                    getChildFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.sub_container, mServiceDetailFragment);
            transaction.commit();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }*/
}
