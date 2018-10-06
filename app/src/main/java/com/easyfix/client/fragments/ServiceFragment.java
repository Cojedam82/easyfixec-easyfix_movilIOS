package com.easyfix.client.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyfix.client.R;
import com.easyfix.client.adapters.ServiceAdapter;
import com.easyfix.client.models.Service;
import com.easyfix.client.util.ApiService;
import com.easyfix.client.util.ServiceGenerator;
import com.easyfix.client.util.SessionManager;
import com.easyfix.client.util.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceFragment extends RootFragment {

    private View view;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private ServiceAdapter mServiceAdapter;
    private TextView mWelcomeView;
    private List<Service> mServiceList = new ArrayList<>();

    public ServiceFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_service, container, false);

        mWelcomeView = view.findViewById(R.id.txt_welcome);
        mRecyclerView = view.findViewById(R.id.rv_services);

        mGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mServiceAdapter = new ServiceAdapter(this, getActivity(), mServiceList);
        mRecyclerView.setAdapter(mServiceAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SessionManager sessionManager = new SessionManager(getContext());

        // set welcome
        String firstName = sessionManager.getUser().getFirstName();
        mWelcomeView.setText("Hola " + firstName + "\n¿Qué servicio necesitas?");

        // fetch services
        if (mServiceList.isEmpty()) serviceTask();
    }

    /** Fetch services **/
    private void serviceTask(){
        Util.showProgress(getContext(), mRecyclerView, view, true);

        SessionManager sessionManager = new SessionManager(getContext());
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();

        Call<List<Service>> call = apiService.getServices(token);
        call.enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                Util.showProgress(getContext(), mRecyclerView, view, false);
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_SERVICE, "Services result: success!");
                    List<Service> serviceList = response.body();
                    if (!serviceList.isEmpty()) {
                        for (Service service : serviceList){
                            mServiceList.add(service);
                        }
                        mServiceAdapter.notifyDataSetChanged();
                    } else {
                        Util.showMessage(mRecyclerView, view,
                                getString(R.string.message_service_server_empty));
                    }

                } else {
                    Log.i(Util.TAG_SERVICE, "Services result: " + response.toString());
                    Util.showMessage(mRecyclerView, view,
                            getString(R.string.message_service_server_failed));
                }
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                Log.i(Util.TAG_SERVICE, "Services result: failed, " + t.getMessage());
                Util.showProgress(getContext(), mRecyclerView, view, false);
                Util.showMessage(mRecyclerView, view,
                        getString(R.string.message_network_local_failed));
            }
        });
    }
}