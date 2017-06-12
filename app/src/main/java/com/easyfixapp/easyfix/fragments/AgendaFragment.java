package com.easyfixapp.easyfix.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.adapters.AgendaAdapter;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaFragment extends Fragment {

    private ListView mListView;
    private AgendaAdapter mAgendaAdapter;
    private List<Reservation> mReservationList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.agenda_fragment, container, false);


        mAgendaAdapter = new AgendaAdapter(getActivity().getApplicationContext(), mReservationList);

        mListView = (ListView) rootview.findViewById(R.id.lv_agenda);
        mListView.setAdapter(mAgendaAdapter);

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mReservationList.isEmpty()) {
            // fetch agenda
            reservationTask();
        }


    }


    /** Fetch reservations **/
    private void reservationTask(){
        Util.showLoading(getActivity(), getString(R.string.message_reservation_agenda_request));

        SessionManager sessionManager = new SessionManager(getContext());
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();

        Call<List<Reservation>> call = apiService.getAgenda(token);
        call.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_AGENDA, "Agenda result: success!");

                    List<Reservation> reservationList = response.body();
                    if (!reservationList.isEmpty()) {
                        mReservationList.clear();

                        for (Reservation reservation : reservationList){
                            mReservationList.add(reservation);
                        }
                        mAgendaAdapter.notifyDataSetChanged();

                    } else {
                        Util.longToast(getContext(),
                                getString(R.string.message_service_server_empty));
                    }
                    Util.hideLoading();

                } else {
                    Log.i(Util.TAG_AGENDA, "Agenda result: " + response.toString());
                    Util.longToast(getContext(),
                            getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                Log.i(Util.TAG_AGENDA, "Agenda result: failed, " + t.getMessage());
                Util.longToast(getContext(),
                        getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }
}
