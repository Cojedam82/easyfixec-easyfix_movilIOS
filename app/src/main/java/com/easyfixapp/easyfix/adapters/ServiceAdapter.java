package com.easyfixapp.easyfix.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.Service;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by julio on 08/06/17.
 */

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> mServiceList;
    private Context mContext;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.mServiceList = serviceList;
        this.mContext = context;
    }

    public static class ServiceViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        public CircleImageView mServiceImageView;
        public TextView mNameView;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            mServiceImageView = (CircleImageView) itemView.findViewById(R.id.img_service);
            mNameView = (TextView) itemView.findViewById(R.id.txt_name);
        }

        @Override
        public void onClick(View v) {
            Log.d("click",v.toString());
        }
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        // Inflate the custom layout
        View serviceView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);

        // Return a new holder instance
        ServiceViewHolder serviceViewHolder = new ServiceViewHolder(serviceView);
        return serviceViewHolder;
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder serviceHolder, int position) {

        // Get element
        Service mService = mServiceList.get(position);

        // Set service image
        CircleImageView mServiceImageView = serviceHolder.mServiceImageView;
        Glide.with(getContext())
                .load(mService.getImage())
                .dontAnimate()
                .error(R.drawable.logo)
                .placeholder(R.drawable.logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mServiceImageView);

        // Set service name
        TextView mNameView = serviceHolder.mNameView;
        mNameView.setText(mService.getName());
    }

    @Override
    public int getItemCount() {
            return mServiceList.size();
    }

}
