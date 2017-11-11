package com.easyfixapp.easyfix.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.fragments.RootFragment;
import com.easyfixapp.easyfix.fragments.ServiceDetailFragment;
import com.easyfixapp.easyfix.fragments.SubServiceFragment;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.util.Util;

import java.io.Serializable;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by julio on 08/06/17.
 */

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> mServiceList;
    private Context mContext;
    private Fragment mFragment;
    private RequestOptions mOptions = new RequestOptions()
            .error(R.drawable.ic_settings)
            .placeholder(R.drawable.ic_settings)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ServiceAdapter(Fragment fragment, Context context, List<Service> serviceList) {
        this.mServiceList = serviceList;
        this.mContext = context;
        this.mFragment = fragment;
    }

    static class ServiceViewHolder
            extends RecyclerView.ViewHolder{

        public CircleImageView mServiceImageView;
        public TextView mNameView;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            mServiceImageView = (CircleImageView) itemView.findViewById(R.id.img_service);
            mNameView = (TextView) itemView.findViewById(R.id.txt_name);
        }

        public void bind(final Fragment fragment, RequestOptions options, final Service service) {
            // Set service image
            Glide.with(itemView.getContext())
                    .load(Util.BASE_URL + service.getImage())
                    .apply(options)
                    .into(mServiceImageView);

            // Set service name
            mNameView.setText(service.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    ((RootFragment)fragment).setBackPressedIcon();
                    List<Service> services = service.getSubServiceList();

                    FragmentTransaction transaction = fragment.
                            getChildFragmentManager().beginTransaction();

                    // Store the Fragment in stack
                    transaction.addToBackStack(null);

                    if (services.size() > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("services", (Serializable) services);
                        SubServiceFragment mSubServiceFragment = new SubServiceFragment();
                        mSubServiceFragment.setArguments(bundle);
                        transaction.replace(R.id.sub_container, mSubServiceFragment);

                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("service", service);
                        ServiceDetailFragment mServiceDetailFragment = new ServiceDetailFragment();
                        mServiceDetailFragment.setArguments(bundle);
                        transaction.replace(R.id.sub_container, mServiceDetailFragment);
                    }

                    // commit transaction
                    transaction.commit();
                }
            });
        }
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        // Inflate the custom layout
        View serviceView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);

        // Return a new holder instance
        ServiceViewHolder serviceViewHolder = new ServiceViewHolder(serviceView);
        return serviceViewHolder;
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder serviceHolder, int position) {
        serviceHolder.bind(mFragment, mOptions, mServiceList.get(position));
    }

    @Override
    public int getItemCount() {
            return mServiceList.size();
    }

}
