package com.easyfixapp.easyfix.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.models.User;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CALL_PHONE;

/**
 * Created by julio on 08/06/17.
 */

public class ReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Reservation> mReservationList;
    private Activity mActivity;
    private RequestOptions options = new RequestOptions()
            .error(R.drawable.logo)
            .placeholder(R.drawable.logo)
            .diskCacheStrategy(DiskCacheStrategy.ALL);
    private int mType;
    private String TAG_RESERVATION;

    public ReservationAdapter(Activity activity, List<Reservation> reservationList, int type) {
        this.mActivity = activity;
        this.mReservationList = reservationList;
        this.mType = type;

        if (this.mType == Reservation.TYPE_NOTIFICATION) {
            this.TAG_RESERVATION = Util.TAG_NOTIFICATION;
        } else {
            this.TAG_RESERVATION = Util.TAG_TECHNICAL_HISTORY;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_reservation, viewGroup, false);

        if (mType==Reservation.TYPE_NOTIFICATION) {
            return new NotificationViewHolder(view);
        } else if (mType==Reservation.TYPE_RECORD) {
            return new TechnicalHistoryViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotificationViewHolder) {
            ((NotificationViewHolder) holder).bind(position);
        }else if (holder instanceof TechnicalHistoryViewHolder) {
            ((TechnicalHistoryViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return mReservationList.size();
    }

    private void removeItem(int position) {
        mReservationList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mReservationList.size());
    }

    private boolean mayRequestCall() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (mActivity.checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        mActivity.requestPermissions(new String[]{CALL_PHONE}, Util.REQUEST_READ_CONTACTS);
        return false;
    }

    /**
     * View Holders
     */

    public class NotificationViewHolder
            extends RecyclerView.ViewHolder{

        private CircleImageView mProviderImageView;
        private TextView mNameView, mServiceView, mDateHourView, mDateMonthView, mHourView, mCostView;
        private ImageView mCallView, mCancelView;

        private LinearLayout mActionView;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            mProviderImageView = itemView.findViewById(R.id.img_provider);
            mNameView = itemView.findViewById(R.id.txt_provider_name);
            mServiceView = itemView.findViewById(R.id.txt_service_name);
            mDateHourView = itemView.findViewById(R.id.txt_date_hour);
            mDateMonthView = itemView.findViewById(R.id.txt_date_month);
            mHourView = itemView.findViewById(R.id.txt_hour);
            mCostView = itemView.findViewById(R.id.txt_cost);
            mCallView = itemView.findViewById(R.id.img_call);
            mCancelView = itemView.findViewById(R.id.img_cancel);
            mActionView = itemView.findViewById(R.id.ll_action);
        }

        public void bind(final int position) {

            final Reservation mReservation = mReservationList.get(position);
            Service mService = mReservation.getService();
            final User mProvider = mReservation.getProvider();

            // Set provider image
            String url = "";
            if (mProvider != null) {
                url = mProvider.getProfile().getImage();
            }
            Glide.with(mActivity)
                    .load(url)
                    .apply(options)
                    .into(mProviderImageView);


            // Set service name
            Random random = new Random();
            mServiceView.setText(mService.getName());
            mServiceView.setTextColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));


            // Show actions
            mActionView.setVisibility(View.VISIBLE);


            // Set action cancel
            mCancelView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AlertDialog);
                    builder.setMessage(R.string.reservation_message_cancel_dialog)
                            .setPositiveButton(R.string.dialog_message_yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    cancelReservationTask(position);
                                }
                            })
                            .setNegativeButton(R.string.dialog_message_no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            });

                    builder.show();
                }
            });


            // Show date-hour
            mDateHourView.setVisibility(View.VISIBLE);


            if (mReservation.getStatus() == Reservation.Pending) {
                mDateHourView.setText("En espera");
            } else {

                // Set provider name
                mNameView.setVisibility(View.VISIBLE);
                mNameView.setText(mProvider.getShortName());


                // Set date-hour
                SimpleDateFormat dateFormat =
                        new SimpleDateFormat("EEEE d", new Locale("es", "ES"));
                String date = "";
                try {
                    SimpleDateFormat dateParse =
                            new SimpleDateFormat("yyyy-MM-dd");
                    date = dateFormat.format(dateParse.parse(mReservation.getDate()));
                    date = date.substring(0, 1).toUpperCase() + date.substring(1).toLowerCase();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mDateHourView.setText(date + " - " + mReservation.getTime().substring(0,5));


                // Set action call
                mCallView.setVisibility(View.VISIBLE);
                mCallView.setOnClickListener(new View.OnClickListener() {
                    @SuppressWarnings("MissingPermission")
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AlertDialog);
                        builder.setMessage(itemView.
                                getContext().getString(R.string.reservation_message_call_dialog) +
                                " " + mProvider.getShortName())
                                .setPositiveButton(R.string.dialog_message_yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        String phone = mReservation
                                                .getProvider()
                                                .getProfile()
                                                .getPhone();
                                        Uri call = Uri.parse("tel:" + phone);

                                        if (mayRequestCall()) {
                                            Intent surf = new Intent(Intent.ACTION_DIAL, call);
                                            mActivity.startActivity(surf);
                                        } else {
                                            Intent surf = new Intent(Intent.ACTION_CALL, call);
                                            mActivity.startActivity(surf);
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.dialog_message_no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });

                        builder.show();
                    }
                });
            }
        }
    }

    public class TechnicalHistoryViewHolder
            extends RecyclerView.ViewHolder{

        private CircleImageView mProviderImageView;
        private TextView mNameView, mServiceView, mDateMonthView, mHourView, mCostView;

        private LinearLayout mInfoView;

        public TechnicalHistoryViewHolder(View itemView) {
            super(itemView);
            mProviderImageView = itemView.findViewById(R.id.img_provider);
            mNameView = itemView.findViewById(R.id.txt_provider_name);
            mServiceView = itemView.findViewById(R.id.txt_service_name);
            mDateMonthView = itemView.findViewById(R.id.txt_date_month);
            mHourView = itemView.findViewById(R.id.txt_hour);
            mCostView = itemView.findViewById(R.id.txt_cost);
            mInfoView = itemView.findViewById(R.id.ll_info);
        }

        public void bind(final int position) {

            final Reservation mReservation = mReservationList.get(position);
            Service mService = mReservation.getService();
            User mProvider = mReservation.getProvider();


            // Set provider image
            String url = "";
            if (mProvider != null) {
                url = mProvider.getProfile().getImage();
            }
            Glide.with(mActivity)
                    .load(url)
                    .apply(options)
                    .into(mProviderImageView);


            // Set provider name
            mNameView.setVisibility(View.VISIBLE);
            mNameView.setText(mProvider.getShortName());


            // Set service name
            Random random = new Random();
            mServiceView.setText(mService.getName());
            mServiceView.setTextColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));


            // Set info
            mInfoView.setVisibility(View.VISIBLE);

            // Set date-month
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("EEEE d MMM", new Locale("es", "ES"));
            String date = "";
            try {
                SimpleDateFormat dateParse =
                        new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.format(dateParse.parse(mReservation.getDate()));
                //date = date.substring(0, 1).toUpperCase() + date.substring(1).toLowerCase();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mDateMonthView.setText(date);

            // Set hour
            mHourView.setText(mReservation.getTime().substring(0,5));

            // Set cost
            mCostView.setText("$" + mReservation.getCost());
        }
    }


    /**
     * Task
     */
    private void cancelReservationTask(final int pk){
        Util.showLoading(mActivity, mActivity.getString(R.string.reservation_message_delete_request));

        SessionManager sessionManager = new SessionManager(mActivity);
        String token = sessionManager.getToken();

        ApiService apiService = ServiceGenerator.createApiService();
        Call<Reservation> call = apiService.deleteReservation(pk, token);
        call.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG_RESERVATION, "Reservation result: success!");
                    Util.longToast(mActivity,
                            mActivity.getString(R.string.reservation_message_delete_response));
                    removeItem(pk);
                } else {
                    Log.i(TAG_RESERVATION, "Reservation result: " + response.toString());
                    Util.longToast(mActivity,
                            mActivity.getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Log.i(TAG_RESERVATION, "Reservation result: failed, " + t.getMessage());
                Util.longToast(mActivity,
                        mActivity.getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }
}
