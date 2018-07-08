package com.easyfixapp.easyfix.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.MainActivity;
import com.easyfixapp.easyfix.activities.MapActivity;
import com.easyfixapp.easyfix.fragments.NotificationFragment;
import com.easyfixapp.easyfix.fragments.ReservationDetailFragment;
import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by julio on 08/06/17.
 */

public class ReservationDetailAddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private List<Address> mAddressList;
    private Context mContext;
    private int defaultIdAddress = -1;
    private int defaultPositionAddress = -1;

    public ReservationDetailAddressAdapter(Context context, List<Address> addressList) {
        this.mContext = context;
        this.mAddressList = addressList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_reservation_detail_address, viewGroup, false);

        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_ITEM) {
            return new AddressViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AddressViewHolder) {
            ((AddressViewHolder) holder).bind(position);
        }else if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        return mAddressList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)){
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }


    /**
     * Custom validation
     */

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    /**
     * View Holders
     */

    public class AddressViewHolder
            extends RecyclerView.ViewHolder{

        public CircleImageView mStatusView;
        public TextView mNameView, mDescriptionView;

        public AddressViewHolder(View itemView) {
            super(itemView);
            mStatusView = itemView.findViewById(R.id.img_status);
            mNameView = itemView.findViewById(R.id.txt_name);
            mDescriptionView = itemView.findViewById(R.id.txt_address);
        }

        public void bind(final int position) {

            final Address address = mAddressList.get(position - 1);

            if (address.isDefault()) {
                defaultIdAddress = address.getId();
                defaultPositionAddress = position;
                mStatusView.setCircleBackgroundColorResource(R.color.green);
            } else {
                mStatusView.setCircleBackgroundColor(Color.GRAY);
            }

            mNameView.setText(address.getName());
            mDescriptionView.setText(address.getDescription());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Reservation reservation = new Reservation();
                    reservation.setAddress(address);
                    EventBus.getDefault().post(reservation);

                    /*
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                    builder.setMessage(R.string.address_message_select_dialog)
                            .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            });

                    builder.show();
                    */
                }
            });
        }

    }

    public class HeaderViewHolder
            extends RecyclerView.ViewHolder {

        public CircleImageView mStatusView;
        public TextView mNameView, mDescriptionView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mStatusView = itemView.findViewById(R.id.img_status);
            mNameView = itemView.findViewById(R.id.txt_name);
            mDescriptionView = itemView.findViewById(R.id.txt_address);
        }

        public void bind() {

            mNameView.setText(itemView.getContext().getString(R.string.address_message_footer));
            mNameView.setTextColor(Color.BLUE);

            mDescriptionView.setVisibility(View.GONE);

            mStatusView.setCircleBackgroundColor(Color.BLUE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(mContext, MapActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }

    }
}
