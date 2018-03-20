package com.easyfixapp.easyfix.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.ServiceDetailActivity;
import com.easyfixapp.easyfix.activities.WaitingQueue;
import com.easyfixapp.easyfix.fragments.RootFragment;
import com.easyfixapp.easyfix.fragments.ServiceDetailFragment;
import com.easyfixapp.easyfix.fragments.TechnicalHistoryFragment;
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
import static com.easyfixapp.easyfix.activities.MapCallService.messageToDisplay.message;


public class TechAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SessionManager sessionManager;
    private List<WaitingQueue.Tecnico> mTecnicosList;
    private Activity mActivity;
    private RequestOptions options = new RequestOptions()
            .error(R.drawable.logo)
            .placeholder(R.drawable.logo)
            .diskCacheStrategy(DiskCacheStrategy.ALL);
    private int mType, mIdPostView;
    private String TAG_RESERVATION;

    public TechAdapter(Activity activity, List<WaitingQueue.Tecnico> reservationList, int type) {
        this.mActivity = activity;
        this.mTecnicosList = reservationList;
        this.mType = type;

        if (this.mType == Reservation.TYPE_NOTIFICATION) {
            this.TAG_RESERVATION = Util.TAG_NOTIFICATION;
        } else {
            this.TAG_RESERVATION = Util.TAG_TECHNICAL_HISTORY;
        }

        this.sessionManager = new SessionManager(activity);
        this.mIdPostView = sessionManager.getServiceDetail();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tech_reservation, viewGroup, false);

        if (mType==Reservation.TYPE_NOTIFICATION) {
            return new NotificationViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotificationViewHolder) {
            ((NotificationViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return mTecnicosList.size();
    }

    private void removeItem(int position) {
        mTecnicosList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mTecnicosList.size());
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

        private RatingBar mCalificationView;

        private LinearLayout mActionView, mBaseLinear;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            mProviderImageView = itemView.findViewById(R.id.img_provider);
            mCalificationView = itemView.findViewById(R.id.rb_calification);
            mNameView = itemView.findViewById(R.id.txt_provider_name);
            mServiceView = itemView.findViewById(R.id.txt_service_name);
            mDateHourView = itemView.findViewById(R.id.txt_date_hour);
            mDateMonthView = itemView.findViewById(R.id.txt_date_month);
            mHourView = itemView.findViewById(R.id.txt_hour);
            mCostView = itemView.findViewById(R.id.txt_cost);
            mCallView = itemView.findViewById(R.id.img_call);
            mCancelView = itemView.findViewById(R.id.img_cancel);
            mActionView = itemView.findViewById(R.id.ll_info);
            mBaseLinear = itemView.findViewById(R.id.baseLayout);
        }

        public void bind(final int position) {
            mCalificationView.setNumStars(5);
            if(position%2!=0){
                mBaseLinear.setBackgroundColor(Color.parseColor("#FF116594"));
                mNameView.setTextColor(Color.WHITE);
                mServiceView.setTextColor(Color.WHITE);
                mDateHourView.setTextColor(Color.WHITE);
                mProviderImageView.setBackgroundDrawable(mActivity.getApplicationContext().getResources().getDrawable(R.drawable.ic_settings));
                setRatingStarColor(mActivity.getApplicationContext(), mCalificationView);
            }

            final WaitingQueue.Tecnico mTecnico = mTecnicosList.get(position);

            mServiceView.setText(mTecnico.getName());
            mDateHourView.setText(mTecnico.getHour());

            mCalificationView.setRating(mTecnico.getStars());

            // Set provider image
            String url = mTecnico.getPhoto();

            try {
                Glide.with(mActivity)
                        .load(url)
                        .apply(options)
                        .into(mProviderImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }


            // Show actions
            mActionView.setVisibility(View.VISIBLE);


            // Set action cancel
            mBaseLinear.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AlertDialog);
                    builder.setMessage(message).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mActivity.finish();
                        }
                    });

                    builder.show();
                }
            });

            // Show date-hour
            mDateHourView.setVisibility(View.VISIBLE);




        }
    }




    /**
     * Task
     */
    private void cancelReservationTask(final int position, final int pk){
        Util.showLoading(mActivity, mActivity.getString(R.string.reservation_message_delete_request));

        SessionManager sessionManager = new SessionManager(mActivity);
        String token = sessionManager.getToken();

        ApiService apiService = ServiceGenerator.createApiService();
        Call<Void> call = apiService.deleteReservation(pk, token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG_RESERVATION, "Reservation result: success!");
                    Util.longToast(mActivity,
                            mActivity.getString(R.string.reservation_message_delete_response));
                    removeItem(position);

                    // update technical history
                    TechnicalHistoryFragment.updateReservations();
                } else {
                    Log.i(TAG_RESERVATION, "Reservation result: " + response.toString());
                    Util.longToast(mActivity,
                            mActivity.getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i(TAG_RESERVATION, "Reservation result: failed, " + t.getMessage());
                Util.longToast(mActivity,
                        mActivity.getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }

    private void setRatingStarColor(Context context, RatingBar ratingBar)
    {

            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(context.getResources()
                    .getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(context.getResources()
                    .getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(0).setColorFilter(context.getResources()
                    .getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);

    }
}
