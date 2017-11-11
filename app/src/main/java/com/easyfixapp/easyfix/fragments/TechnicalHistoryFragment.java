package com.easyfixapp.easyfix.fragments;

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
import com.easyfixapp.easyfix.adapters.ReservationAdapter;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by julio on 09/10/17.
 */

public class TechnicalHistoryFragment extends Fragment{

    private RecyclerView mReservationView;
    private ReservationAdapter mReservationAdapter;
    private List<Reservation> mReservationList = new ArrayList<>();

    public TechnicalHistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);


        mReservationAdapter = new ReservationAdapter(getActivity(),
                mReservationList, Reservation.TYPE_RECORD);

        mReservationView = view.findViewById(R.id.rv_reservation);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mReservationView.setLayoutManager(mLayoutManager);
        mReservationView.setItemAnimator(new DefaultItemAnimator());
        mReservationView.setAdapter(mReservationAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reservationTask();
    }


    /** Fetch reservations **/
    private void reservationTask(){
        Util.showLoading(getActivity(), getString(R.string.notification_message_request));

        SessionManager sessionManager = new SessionManager(getContext());
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();

        Call<List<Reservation>> call = apiService.getRecord(token);
        call.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_RESERVATION, "Reservation result: success!");

                    List<Reservation> reservationList = response.body();
                    if (!reservationList.isEmpty()) {
                        mReservationList.clear();

                        for (Reservation reservation : reservationList){
                            mReservationList.add(reservation);
                        }
                        mReservationAdapter.notifyDataSetChanged();

                    } else {
                        Util.longToast(getContext(),
                                getString(R.string.message_service_server_empty));
                    }
                    Util.hideLoading();

                } else {
                    Log.i(Util.TAG_RESERVATION, "Reservation result: " + response.toString());
                    Util.longToast(getContext(),
                            getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                Log.i(Util.TAG_RESERVATION, "Reservation result: failed, " + t.getMessage());
                Util.longToast(getContext(),
                        getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }
}