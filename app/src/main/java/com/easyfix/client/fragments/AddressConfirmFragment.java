package com.easyfix.client.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyfix.client.R;
import com.easyfix.client.adapters.AddressConfirmAdapter;
import com.easyfix.client.models.Address;
import com.easyfix.client.models.Reservation;
import com.easyfix.client.util.SessionManager;
import com.easyfix.client.util.Util;
import com.easyfix.client.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by julio on 09/10/17.
 */

public class AddressConfirmFragment extends RootFragment{

    private View view;
    private RecyclerView mAddressView;
    private AddressConfirmAdapter mAddressAdapter;
    private List<Address> mAddressList = new ArrayList<>();

    private Reservation mReservation = null;

    public AddressConfirmFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_address, container, false);
        mReservation = (Reservation) getArguments().getParcelable("reservation");

        mAddressView = view.findViewById(R.id.rv_address);
        mAddressAdapter = new AddressConfirmAdapter(getContext(), mReservation, mAddressList);

        // Set requirements to show recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mAddressView.setLayoutManager(mLayoutManager);

        // Set item divider
        mAddressView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mAddressView.setItemAnimator(new DefaultItemAnimator());

        mAddressView.setAdapter(mAddressAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.addFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        populate();
    }

    private void populate(){
        Util.showProgress(getContext(), mAddressView, view, true);

        // Clean address
        mAddressList.clear();

        Realm realm = Realm.getDefaultInstance();
        try {
            RealmResults<Address> result = realm.where(Address.class)
                    .equalTo("isActive", true)
                    .sort("id", Sort.DESCENDING)
                    .findAll();
            if(!result.isEmpty()){
                // Copy persist query
                for (Address address : realm.copyFromRealm(result)) {
                    mAddressList.add(address);
                }
            }
        } finally {
            realm.close();
        }

        Util.showProgress(getContext(), mAddressView, view, false);

        // Always notify data
        mAddressAdapter.notifyDataSetChanged();
    }
}
