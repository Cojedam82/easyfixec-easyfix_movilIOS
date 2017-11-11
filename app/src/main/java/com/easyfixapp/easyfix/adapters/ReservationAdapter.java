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
import android.text.TextUtils;
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

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private List<Reservation> mReservationList;
    private Activity mActivity;
    private int mType;
    private RequestOptions options = new RequestOptions()
            .error(R.drawable.logo)
            .placeholder(R.drawable.logo)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ReservationAdapter(Activity activity, List<Reservation> reservationList, int type) {
        this.mActivity = activity;
        this.mReservationList = reservationList;
        mType = type;
    }

    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_reservation, viewGroup, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReservationViewHolder holder, int position) {
        holder.bind(position);
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

    public class ReservationViewHolder
            extends RecyclerView.ViewHolder{

        public CircleImageView mProviderImageView;
        public TextView mNameView, mServiceView, mDateHourView, mDateMonthView, mHourView, mCostView;
        public ImageView mCallView, mCancelView;

        public LinearLayout mInfoView, mActionView;

        public ReservationViewHolder(View itemView) {
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

            mInfoView = itemView.findViewById(R.id.ll_info);
            mActionView = itemView.findViewById(R.id.ll_action);
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
            if (mReservation.getStatus() == Reservation.Assigned) {
                mNameView.setVisibility(View.VISIBLE);
                mNameView.setText(mProvider.getShortName());
            }

            // Set service name
            Random random = new Random();
            mServiceView.setText(mService.getName());
            mServiceView.setTextColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));

            // Set by type
            if (mType == Reservation.TYPE_NOTIFICATION) {
                mActionView.setVisibility(View.VISIBLE);

                // Set date-hour
                // Set provider name
                if (mReservation.getStatus() == Reservation.Pending) {
                    mDateHourView.setText("En espera");
                } else if(TextUtils.isEmpty(mReservation.getDate())) {
                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("EEEE d", new Locale("es", "ES"));
                    String date = null;
                    try {
                        SimpleDateFormat dateParse =
                                new SimpleDateFormat("yyyy-MM-dd");
                        date = dateFormat.format(dateParse.parse(mReservation.getDate()));
                        date = date.substring(0, 1).toUpperCase() + date.substring(1).toLowerCase();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mDateHourView.setText(date + " - " + mReservation.getTime());
                }

                // Set actions
                if (mReservation.getStatus() == Reservation.Assigned) {
                    mCallView.setOnClickListener(new View.OnClickListener() {
                        @SuppressWarnings("MissingPermission")
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AlertDialog);
                            builder.setMessage(R.string.reservation_message_call_dialog)
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
            } else {
                mInfoView.setVisibility(View.VISIBLE);

                // Set date-month
                if(TextUtils.isEmpty(mReservation.getDate())) {
                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("EEEE d MMM", new Locale("es", "ES"));
                    String date = null;
                    try {
                        SimpleDateFormat dateParse =
                                new SimpleDateFormat("yyyy-MM-dd");
                        date = dateFormat.format(dateParse.parse(mReservation.getDate()));
                        //date = date.substring(0, 1).toUpperCase() + date.substring(1).toLowerCase();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mDateHourView.setText(date);
                }

                // Set hour
                mHourView.setText(mReservation.getTime());

                // Set cost
                mCostView.setText(mReservation.getCost());
            }

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
                    Log.i(Util.TAG_RESERVATION, "Reservation result: success!");
                    Util.longToast(mActivity,
                            mActivity.getString(R.string.reservation_message_delete_response));
                    removeItem(pk);
                } else {
                    Log.i(Util.TAG_RESERVATION, "Reservation result: " + response.toString());
                    Util.longToast(mActivity,
                            mActivity.getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Log.i(Util.TAG_RESERVATION, "Reservation result: failed, " + t.getMessage());
                Util.longToast(mActivity,
                        mActivity.getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }
}