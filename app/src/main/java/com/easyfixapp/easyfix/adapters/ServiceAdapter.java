package com.easyfixapp.easyfix.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.fragments.ReservationDetailFragment;
import com.easyfixapp.easyfix.fragments.RootFragment;
import com.easyfixapp.easyfix.fragments.ServiceDetailFragment;
import com.easyfixapp.easyfix.fragments.SubServiceFragment;
import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.util.SessionManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

/**
 * Created by julio on 08/06/17.
 */

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> mServiceList;
    private Context mContext;
    private Fragment mFragment;
    private RequestOptions mOptions = new RequestOptions()
            .error(R.drawable.ic_settings)
            //.placeholder(R.drawable.ic_settings)
            .diskCacheStrategy(DiskCacheStrategy.NONE);

    private AlertDialog mAlertDialog = null;

    public ServiceAdapter(Fragment fragment, Context context, List<Service> serviceList) {
        this.mServiceList = serviceList;
        this.mContext = context;
        this.mFragment = fragment;
    }

    public class ServiceViewHolder
            extends RecyclerView.ViewHolder{

        public CircleImageView mServiceImageView;
        public TextView mNameView;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            mServiceImageView = (CircleImageView) itemView.findViewById(R.id.img_service);
            mNameView = (TextView) itemView.findViewById(R.id.txt_name);
        }

        public void bind(RequestOptions options, final Service service) {
            // Set service image
            Glide.with(itemView.getContext())
                    .load(service.getImageDrawable())
                    //.apply(options)
                    .into(mServiceImageView);

            //mServiceImageView.setImageDrawable(mContext
            // .getResources().getDrawable(service.getImageDrawable()));

            // Set service name
            mNameView.setText(service.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    List<Service> services = service.getSubServiceList();

                    if (services.size() > 0) {
                        SessionManager sessionManager = new SessionManager(mContext);
                        sessionManager.resetFragment();

                        ((RootFragment)mFragment).setBackPressedIcon();

                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("services", (ArrayList<? extends Parcelable>) services);

                        SubServiceFragment mSubServiceFragment = new SubServiceFragment();
                        mSubServiceFragment.setArguments(bundle);

                        FragmentTransaction transaction = mFragment.
                                getChildFragmentManager().beginTransaction();
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.sub_container, mSubServiceFragment);
                        transaction.commit();
                    } else {
                        Address addressDefault = null;

                        final Reservation reservation = new Reservation();
                        reservation.setService(service);

                        Realm realm = Realm.getDefaultInstance();
                        try {
                            Address address = realm
                                    .where(Address.class)
                                    .equalTo("isDefault", true)
                                    .findFirst();
                            addressDefault = realm.copyFromRealm(address);

                        } catch (Exception ignore){
                        } finally {
                            realm.close();
                        }

                        if (addressDefault != null) {
                            if (mAlertDialog == null  || !mAlertDialog.isShowing()) {
                                final Address address = addressDefault;
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                                builder.setTitle("Reservación");
                                builder.setCancelable(true);
                                builder.setMessage("¿Desea solicitar el tecnico a su dirección por defecto?")
                                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                reservation.setAddress(address);
                                                reservationDetail(reservation);
                                            }
                                        })
                                        .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                reservationDetail(reservation);
                                            }
                                        });
                                mAlertDialog = builder.show();
                            }
                        } else {
                            reservationDetail(reservation);
                        }
                    }
                }
            });
        }
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View serviceView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);

        // Return a new holder instance
        return new ServiceViewHolder(serviceView);
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder serviceHolder, int position) {
        serviceHolder.bind(mOptions, mServiceList.get(position));
    }

    @Override
    public int getItemCount() {
            return mServiceList.size();
    }

    private void reservationDetail(Reservation reservation) {
        SessionManager sessionManager = new SessionManager(mContext);
        sessionManager.resetFragment();

        ((RootFragment)mFragment).setBackPressedIcon();

        Bundle bundle = new Bundle();
        bundle.putParcelable("reservation", reservation);

        ReservationDetailFragment mReservationDetailFragment = new ReservationDetailFragment();
        mReservationDetailFragment.setArguments(bundle);

        FragmentTransaction transaction = mFragment.
                getChildFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.sub_container, mReservationDetailFragment);
        transaction.commit();
    }
}
