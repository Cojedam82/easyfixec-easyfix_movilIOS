package com.easyfix.client.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyfix.client.R;
import com.easyfix.client.adapters.ServiceAdapter;
import com.easyfix.client.models.Reservation;
import com.easyfix.client.models.Service;
import com.easyfix.client.util.SessionManager;
import com.easyfix.client.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julio on 21/10/17.
 */

public class SubServiceFragment extends RootFragment {

    private View view;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private ServiceAdapter mServiceAdapter;
    private TextView mWelcomeView;
    private List<Service> mServiceList = new ArrayList<>();

    public SubServiceFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_service, container, false);

        mWelcomeView = (TextView) view.findViewById(R.id.txt_welcome);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_services);

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

        Util.showProgress(getContext(), mRecyclerView, view, true);

        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.addFragment();

        // set welcome
        String firstName = sessionManager.getUser().getFirstName();
        mWelcomeView.setText("Hola " + firstName + "\n¿Qué servicio necesitas?");

        // populate services
        populate();
    }

    public void populate(){
        Util.showProgress(getContext(), mRecyclerView, view, false);
        if (mServiceList.isEmpty()) {
            List<Service> services = getArguments().getParcelableArrayList("services");
            for (Service service : services)
                mServiceList.add(service);
            mServiceAdapter.notifyDataSetChanged();
        } else {
            Util.showMessage(mRecyclerView, view,
                    getString(R.string.message_service_server_failed));
        }
    }
}
