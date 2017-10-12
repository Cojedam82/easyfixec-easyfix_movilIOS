package com.easyfixapp.easyfix.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.adapters.ServiceAdapter;
import com.easyfixapp.easyfix.models.Service;
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
 * A simple {@link Fragment} subclass.
 */
public class ServiceFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private ServiceAdapter mServiceAdapter;
    private TextView mWelcomeView;
    private List<Service> mServiceList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.search_fragment, container, false);

        mWelcomeView = (TextView) rootview.findViewById(R.id.txt_welcome);
        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.rv_services);

        mGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mServiceAdapter = new ServiceAdapter(getActivity().getApplication(), mServiceList);
        mRecyclerView.setAdapter(mServiceAdapter);

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();

        // set welcome
        SessionManager sessionManager = new SessionManager(getContext());
        String firstName = sessionManager.getUser().getFirstName();
        mWelcomeView.setText("Hola " + firstName + "\n¿Qué servicio necesitas?");

        // fetch services
        if (mServiceList.isEmpty()) serviceTask();
    }

    /** Fetch services **/
    private void serviceTask(){
        Util.showLoading(getActivity(), getString(R.string.message_services_request));

        SessionManager sessionManager = new SessionManager(getContext());
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();

        Call<List<Service>> call = apiService.getServices(token);
        call.enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_SEARCH, "Services result: success!");

                    List<Service> serviceList = response.body();
                    if (!serviceList.isEmpty()) {

                        for (Service service : serviceList){
                            mServiceList.add(service);
                        }
                        mServiceAdapter.notifyDataSetChanged();

                    } else {
                        Util.longToast(getContext(),
                                getString(R.string.message_service_server_empty));
                    }
                    Util.hideLoading();

                } else {
                    Log.i(Util.TAG_SEARCH, "Services result: " + response.toString());
                    Util.longToast(getContext(),
                            getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                Log.i(Util.TAG_SEARCH, "Services result: failed, " + t.getMessage());
                Util.longToast(getContext(),
                        getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }
}