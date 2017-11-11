package com.easyfixapp.easyfix.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.adapters.ServiceAdapter;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julio on 21/10/17.
 */

public class SubServiceFragment extends RootFragment {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private ServiceAdapter mServiceAdapter;
    private TextView mWelcomeView;
    private List<Service> mServiceList = new ArrayList<>();

    public SubServiceFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_service, container, false);

        // prevent click under fragment
        /*view.findViewById(R.id.sub_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

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

        // set welcome
        SessionManager sessionManager = new SessionManager(getContext());
        String firstName = sessionManager.getUser().getFirstName();
        mWelcomeView.setText("Hola " + firstName + "\n¿Qué servicio necesitas?");

        // populate services
        populate();
    }

    public void populate(){
        if (mServiceList.isEmpty()) {
            List<Service> services = (List<Service>) getArguments().getSerializable("services");
            for (Service service : services)
                mServiceList.add(service);
            mServiceAdapter.notifyDataSetChanged();
        }
    }
}
