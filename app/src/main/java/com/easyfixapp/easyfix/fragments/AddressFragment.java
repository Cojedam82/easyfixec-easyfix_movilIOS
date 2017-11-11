package com.easyfixapp.easyfix.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.adapters.AddressAdapter;
import com.easyfixapp.easyfix.models.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julio on 09/10/17.
 */

public class AddressFragment extends RootFragment{

    private RecyclerView mAddressView;
    private AddressAdapter mAddressAdapter;
    private List<Address> mAddressList = new ArrayList<>();

    public AddressFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_address, container, false);

        mAddressView = view.findViewById(R.id.rv_address);
        mAddressAdapter = new AddressAdapter(getContext(), mAddressList);

        // set true if your RecyclerView is finite and has fixed size
        //mRecyclerView.setHasFixedSize(false);
        //mRecyclerView.addItemDecoration(new ItemOffsetDecoration(10));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mAddressView.setLayoutManager(mLayoutManager);
        mAddressView.setItemAnimator(new DefaultItemAnimator());
        mAddressView.setAdapter(mAddressAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populate();
    }

    private void populate(){

        /*
        Realm realm = Realm.getDefaultInstance();
        try {
            RealmResults<Address> result = realm.where(Address.class)
                    .findAllSorted("id", Sort.DESCENDING);
            if(!result.isEmpty()){

                // Clean address
                mAddressList.clear();

                // Copy persist query
                realm.copyFromRealm(mAddressList);

            }
        } finally {
            realm.close();
        }*/

        Address address = new Address();
        address.setName("Holaa");
        address.setDescription("Conj. i04");
        address.setActive(true);

        Address address2 = new Address();
        address2.setName("Holaa");
        address2.setDescription("Conj. i04");
        address2.setActive(true);

        Address address3 = new Address();
        address3.setName("Holaa");
        address3.setDescription("Conj. i04");
        address3.setActive(true);

        Address address4 = new Address();
        address4.setName("Holaa");
        address4.setDescription("Conj. i04");
        address4.setActive(true);

        Address address5 = new Address();
        address5.setName("Holaa");
        address5.setDescription("Conj. i04");
        address5.setActive(true);

        mAddressList.add(address);
        mAddressList.add(address2);
        mAddressList.add(address3);
        mAddressList.add(address4);
        mAddressList.add(address5);

        // Always notify data
        mAddressAdapter.notifyDataSetChanged();
    }
}
