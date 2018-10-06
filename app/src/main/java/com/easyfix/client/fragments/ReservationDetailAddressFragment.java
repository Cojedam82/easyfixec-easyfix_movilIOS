package com.easyfix.client.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyfix.client.R;
import com.easyfix.client.adapters.ReservationDetailAddressAdapter;
import com.easyfix.client.models.Address;
import com.easyfix.client.models.Reservation;
import com.easyfix.client.util.Util;
import com.easyfix.client.widget.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by julio on 26/05/18.
 */

public class ReservationDetailAddressFragment extends RootFragment{

    private View view;
    private RecyclerView mAddressView;
    private ReservationDetailAddressAdapter mAddressAdapter;
    private List<Address> mAddressList = new ArrayList<>();

    private Reservation mReservation = null;

    public ReservationDetailAddressFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_address, container, false);

        mAddressView = view.findViewById(R.id.rv_address);
        mAddressAdapter = new ReservationDetailAddressAdapter(getContext(), mAddressList);

        // Set requirements to show recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mAddressView.setLayoutManager(mLayoutManager);

        // Set item divider
        mAddressView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mAddressView.setItemAnimator(new DefaultItemAnimator());

        mAddressView.setAdapter(mAddressAdapter);

        return view;
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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddressEvent(Address address) {
        populate();
    }
}
