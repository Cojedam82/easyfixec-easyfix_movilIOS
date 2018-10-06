package com.easyfix.client.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyfix.client.R;
import com.easyfix.client.activities.MainActivity;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by julio on 09/10/17.
 */

public class TechnicalHistoryFragment extends RootFragment{

    private View view;
    private RecyclerView mReservationView;
    private ReservationAdapter mReservationAdapter;
    private List<Reservation> mReservationList = new ArrayList<>();

    public TechnicalHistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reservation, container, false);

        view.findViewById(R.id.txt_notification_title).setVisibility(View.GONE);
        view.findViewById(R.id.separator).setVisibility(View.GONE);

        mReservationAdapter = new ReservationAdapter(this, getActivity(),
                mReservationList, Reservation.TYPE_RECORD);

        mReservationView = view.findViewById(R.id.rv_reservation);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mReservationView.setLayoutManager(mLayoutManager);

        // Set item divider
        mReservationView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mReservationView.setItemAnimator(new DefaultItemAnimator());

        mReservationView.setAdapter(mReservationAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reservationTask();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTechnicalHistoryEvent(Notification notification) {
        int action = notification.getAction();
        if (action == Util.ACTION_UPDATE) {
            reservationTask();
        }

    }


    /** Fetch reservations **/
    private void reservationTask(){
        Util.showProgress(getContext(), mReservationView, view, true);

        SessionManager sessionManager = new SessionManager(getContext());
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();

        Call<List<Reservation>> call = apiService.getRecord(token);
        call.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                Util.showProgress(getContext(), mReservationView, view, false);

                if (response.isSuccessful()) {
                    Log.i(Util.TAG_TECHNICAL_HISTORY, "Reservation result: success!");

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
                    Log.i(Util.TAG_TECHNICAL_HISTORY, "Reservation result: " + response.toString());
                    Util.showMessage(mReservationView, view,
                            getString(R.string.message_service_server_failed));
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                Util.showProgress(getActivity(), mReservationView, view, false);
                Util.showMessage(mReservationView, view,
                        getString(R.string.message_network_local_failed));
            }
        });
    }

}
