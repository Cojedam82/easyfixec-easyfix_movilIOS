package com.easyfixapp.easyfix.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.LoginActivity;
import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by julio on 08/06/17.
 */

public class AddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private List<Address> mAddressList;
    private Context mContext;
    private int defaultAddress = -1;

    public AddressAdapter(Context context, List<Address> addressList) {
        this.mAddressList = addressList;
        this.mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_address, viewGroup, false);

        //if (viewType == TYPE_ITEM) {
            return new AddressViewHolder(view);
        //} else if (viewType == TYPE_FOOTER) {
        //    return new FooterViewHolder(view);
        // }

        //throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ((AddressViewHolder) holder).bind(position);
        //if (holder instanceof AddressViewHolder) {
        //    ((AddressViewHolder) holder).bind(position);
        //}else if (holder instanceof FooterViewHolder) {
        //    ((FooterViewHolder) holder).bind();
        //}
    }

    @Override
    public int getItemCount() {
            //return mAddressList.size() + 1;
        return mAddressList.size();
    }

    //@Override
    //public int getItemViewType(int position) {
        //if (isPositionFooter(position)) {
        //    return TYPE_FOOTER;
        //}

    //    return TYPE_ITEM;
    //}

    /**
     * View Holders
     */

    public class AddressViewHolder
            extends RecyclerView.ViewHolder{

        public CircleImageView mStatusView;
        public TextView mNameView, mDescriptionView;
        public ImageView mActionView;

        public AddressViewHolder(View itemView) {
            super(itemView);
            mStatusView = itemView.findViewById(R.id.img_status);
            mNameView = itemView.findViewById(R.id.txt_name);
            mDescriptionView = itemView.findViewById(R.id.txt_address);
            mActionView = itemView.findViewById(R.id.img_action);
        }

        public void bind(final int position) {

            Address address = mAddressList.get(position);

            if (address.isActive()) {
                defaultAddress = position;
                mStatusView.setCircleBackgroundColor(mContext.getResources().getColor(R.color.green));
            }

            mNameView.setText(address.getName());
            mDescriptionView.setText(address.getDescription());

            mActionView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                    builder.setMessage(R.string.address_message_delete_dialog)
                            .setPositiveButton(R.string.dialog_message_yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteAddressTask(position);
                                }
                            })
                            .setNegativeButton(R.string.dialog_message_no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            });

                    builder.show();
                }
            });

            mActionView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                    builder.setMessage(R.string.address_message_update_dialog)
                            .setPositiveButton(R.string.dialog_message_yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    updateAddressTask(position);
                                }
                            })
                            .setNegativeButton(R.string.dialog_message_no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            });

                    builder.show();

                    return true;
                }
            });
        }

    }

    public class FooterViewHolder
            extends RecyclerView.ViewHolder {

        public TextView mNameView, mDescriptionView;
        public ImageView mActionView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mNameView = itemView.findViewById(R.id.txt_name);
            mDescriptionView = itemView.findViewById(R.id.txt_address);
            mActionView = itemView.findViewById(R.id.img_action);
        }

        public void bind() {

            mNameView.setText(itemView.getContext().getString(R.string.address_message_footer));
            mDescriptionView.setVisibility(View.GONE);

            mActionView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_navigate_next));
            mActionView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }

    }

    /**
     * Custom validation
     */

    private boolean isPositionFooter(int position) {
        int size = mAddressList.size();
        return size == 0 || position > size;
    }


    private void removeItem(int position) {
        mAddressList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mAddressList.size());
    }


    /**
     * Task
     */
    private void deleteAddressTask(final int pk){
        Util.showLoading(mContext, mContext.getString(R.string.address_message_delete_request));

        SessionManager sessionManager = new SessionManager(mContext);
        String token = sessionManager.getToken();

        ApiService apiService = ServiceGenerator.createApiService();
        Call<Address> call = apiService.deleteAddress(pk, token);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_ADDRESS, "Address result: success!");
                    Util.longToast(mContext,
                            mContext.getString(R.string.address_message_delete_response));
                    removeItem(pk);
                } else {
                    Log.i(Util.TAG_ADDRESS, "Address result: " + response.toString());
                    Util.longToast(mContext,
                            mContext.getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Log.i(Util.TAG_ADDRESS, "Address result: failed, " + t.getMessage());
                Util.longToast(mContext,
                        mContext.getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }

    private void updateAddressTask(final int pk){
        Util.showLoading(mContext, mContext.getString(R.string.address_message_update_request));

        SessionManager sessionManager = new SessionManager(mContext);
        String token = sessionManager.getToken();

        ApiService apiService = ServiceGenerator.createApiService();
        Call<Address> call = apiService.deleteAddress(pk, token);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_ADDRESS, "Address result: success!");
                    Util.longToast(mContext,
                            mContext.getString(R.string.address_message_update_response));

                    // Update
                    Realm realm = Realm.getDefaultInstance();
                    try {
                        Address addressBefore = realm
                                .where(Address.class)
                                .equalTo("id", defaultAddress)
                                .findFirst();

                        Address addressAfter = realm
                                .where(Address.class)
                                .equalTo("id", pk)
                                .findFirst();

                        realm.beginTransaction();
                        addressBefore.setActive(false);
                        addressAfter.setActive(true);
                        realm.commitTransaction();

                        RealmResults<Address> result = realm.where(Address.class)
                                .findAllSorted("id", Sort.DESCENDING);

                        // Clean address
                        mAddressList.clear();

                        // Copy persist query
                        realm.copyFromRealm(mAddressList);

                        // Notify data
                        notifyDataSetChanged();

                    } catch (Exception ignore){
                    } finally {
                        realm.close();
                    }
                } else {
                    Log.i(Util.TAG_ADDRESS, "Address result: " + response.toString());
                    Util.longToast(mContext,
                            mContext.getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Log.i(Util.TAG_ADDRESS, "Address result: failed, " + t.getMessage());
                Util.longToast(mContext,
                        mContext.getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }
}
