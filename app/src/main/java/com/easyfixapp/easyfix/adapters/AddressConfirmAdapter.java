package com.easyfixapp.easyfix.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.MainActivity;
import com.easyfixapp.easyfix.activities.MapActivity;
import com.easyfixapp.easyfix.fragments.AddressConfirmFragment;
import com.easyfixapp.easyfix.fragments.ServiceDetailFragment;
import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by julio on 08/06/17.
 */

public class AddressConfirmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private List<Address> mAddressList;
    private Reservation reservation;
    private Context mContext;
    private int defaultIdAddress = -1;
    private int defaultPositionAddress = -1;

    public AddressConfirmAdapter(Context context, Reservation reservation, List<Address> addressList) {
        this.reservation = reservation;
        this.mAddressList = addressList;
        this.mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_address, viewGroup, false);

        if (viewType == TYPE_ITEM) {
            return new AddressViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AddressViewHolder) {
            ((AddressViewHolder) holder).bind(position);
        }else if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        return mAddressList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }

        return TYPE_ITEM;
    }


    /**
     * Custom validation
     */

    private boolean isPositionFooter(int position) {
        int size = mAddressList.size();
        return size == 0 || position == size;
    }


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

            final Address address = mAddressList.get(position);

            if (address.isDefault()) {
                defaultIdAddress = address.getId();
                defaultPositionAddress = position;
                mStatusView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_add));
            } else {
                mStatusView.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.ic_dialog_dialer));
            }

            mNameView.setText(address.getName());
            mDescriptionView.setText(address.getDescription());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                    builder.setMessage(R.string.address_message_select_dialog)
                            .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    reservation.setAddress(address);

                                    //ServiceDetailFragment serviceDetailFragment = ServiceDetailFragment.getInstance();
                                    createReservationTask(reservation);
                                }
                            })
                            .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            });

                    builder.show();
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(mContext, MapActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }

    }

    public void createReservationTask(Reservation reservation){
        Util.showLoading(mContext, mContext.getString(R.string.reservation_message_create_request));

        final SessionManager sessionManager = new SessionManager(mContext);
        String token = sessionManager.getToken();

        ApiService apiService = ServiceGenerator.createApiService();
        Call<Reservation> call = apiService.createReservation(token, reservation);
        call.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_RESERVATION, "Reservation result: success!");
                    Util.longToast(mContext,
                            mContext.getString(R.string.reservation_message_create_response));

                    ((MainActivity)MainActivity.activity).clearService();

                } else {
                    Log.i(Util.TAG_RESERVATION, "Reservation result: " + response.toString());
                    Util.longToast(mContext,
                            mContext.getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Log.i(Util.TAG_RESERVATION, "Reservation result: failed, " + t.getMessage());
                Util.longToast(mContext,
                        mContext.getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }
}
