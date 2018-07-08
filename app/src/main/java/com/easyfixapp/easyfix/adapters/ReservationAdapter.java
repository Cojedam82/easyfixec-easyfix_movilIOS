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
import android.os.CountDownTimer;
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
import com.easyfixapp.easyfix.fragments.RootFragment;
import com.easyfixapp.easyfix.fragments.ServiceDetailFragment;
import com.easyfixapp.easyfix.fragments.TechnicalHistoryFragment;
import com.easyfixapp.easyfix.models.Notification;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.models.User;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CALL_PHONE;

/**
 * Created by julio on 08/06/17.
 */

public class ReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SessionManager sessionManager;
    private List<Reservation> mReservationList;
    private Activity mActivity;
    private Fragment mFragment;
    private RequestOptions options = new RequestOptions()
            .error(R.drawable.ic_empty_profile)
            .placeholder(R.drawable.ic_empty_profile)
            .diskCacheStrategy(DiskCacheStrategy.ALL);
    private int mType, mIdPostView;
    private String TAG_RESERVATION;

    private List<Timer> counterList;

    public ReservationAdapter(Fragment fragment, Activity activity, List<Reservation> reservationList, int type) {
        this.mFragment = fragment;
        this.mActivity = activity;
        this.mReservationList = reservationList;
        this.mType = type;

        if (this.mType == Reservation.TYPE_NOTIFICATION) {
            this.TAG_RESERVATION = Util.TAG_NOTIFICATION;
        } else {
            this.TAG_RESERVATION = Util.TAG_TECHNICAL_HISTORY;
        }

        this.sessionManager = new SessionManager(activity);
        this.mIdPostView = sessionManager.getServiceDetail();

        counterList = new ArrayList<>();
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

        private RatingBar mCalificationView;

        private LinearLayout mActionView;

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

            if (!TextUtils.isEmpty(url)) {
                Glide.with(itemView.getContext())
                        .load(url)
                        .apply(options)
                        .into(mProviderImageView);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mProviderImageView.setCircleBackgroundColor(itemView.getContext().getColor(android.R.color.transparent));
                } else {
                    mProviderImageView.setCircleBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.transparent));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mProviderImageView.setColorFilter(itemView.getContext().getColor(R.color.colorPrimary));
                    mProviderImageView.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_empty_profile));
                    mProviderImageView.setCircleBackgroundColor(itemView.getContext().getColor(android.R.color.transparent));
                } else {
                    mProviderImageView.setColorFilter(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                    mProviderImageView.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.ic_empty_profile));
                    mProviderImageView.setCircleBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.transparent));
                }
            }


            // Set service name
            if (!TextUtils.isEmpty(mService.getName())) {
                Random random = new Random();
                mServiceView.setText(mService.getName());
                mServiceView.setTextColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }


            // Show actions
            mActionView.setVisibility(View.VISIBLE);


            // Set action cancel
            mCancelView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AlertDialog);
                    builder.setMessage(R.string.reservation_message_cancel_dialog)
                            .setPositiveButton(R.string.dialog_message_yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    cancelReservationTask(position, mReservation.getId());
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

            if (mReservation.getStatus() == Reservation.Pending  || mProvider==null) {
                mNameView.setVisibility(View.GONE);
                mCallView.setVisibility(View.GONE);
                mDateHourView.setText("En espera");
                mCalificationView.setRating(0);

            } else {
                // Set date-hour
                if (mReservation.isScheduled()) {
                    mDateHourView.setText(Util.getDateTimeFormat(mReservation.getDate(), mReservation.getTime()));
                } else {
                    final Date dateTime = Util.getDateTime(mReservation.getDate(), mReservation.getTime());
                    long different = dateTime.getTime() - new Date().getTime();
                    mDateHourView.setText(
                            Util.getElapsedFormatTime(different) + " para que llegue tu técnico");

                    if (dateTime.after(new Date())) {
                        final Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            long different = dateTime.getTime() - new Date().getTime();

                                            if (different == 0L) {
                                                mDateHourView.setText("Tu técnico ya debe estar en el punto!");
                                                timer.cancel();
                                            } else {
                                                mDateHourView.setText(
                                                        Util.getElapsedFormatTime(different) + " para que llegue tu técnico");
                                            }
                                        } catch (Exception e) {
                                            Log.e("CounterDown", e.getMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }, 60 * 1000, 60 * 1000);
                    } else {
                        mDateHourView.setText("Tu técnico ya debe estar en el punto!");
                    }
                }

                // Set provider name
                mNameView.setVisibility(View.VISIBLE);
                mNameView.setText(mProvider.getShortName());

                // Set calification
                setRatingStarColor(itemView.getContext(), mCalificationView);
                mCalificationView.setRating(mProvider.getScore());

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
                                        if (TextUtils.isEmpty(phone)) {
                                            Uri call = Uri.parse("tel:" + phone);

                                            if (mayRequestCall()) {
                                                Intent surf = new Intent(Intent.ACTION_DIAL, call);
                                                mActivity.startActivity(surf);
                                            } else {
                                                Intent surf = new Intent(Intent.ACTION_CALL, call);
                                                mActivity.startActivity(surf);
                                            }
                                        } else {
                                            Util.longToast(mActivity, "No se encontro número de teléfono por el momento");
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mReservation.getStatus() == Reservation.Assigned) {
                        EventBus.getDefault().post(new Notification(Util.ACTION_UPDATE, mReservation));
                    } else {
                        EventBus.getDefault().post(new Notification(Util.ACTION_PROVIDER, mReservation));
                    }

                    /*
                    ((RootFragment)mFragment).setBackPressedIcon();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("reservation", mReservation);

                    ServiceDetailFragment mServiceDetailFragment = new ServiceDetailFragment();
                    mServiceDetailFragment.setArguments(bundle);

                    FragmentTransaction transaction = mFragment.
                            getChildFragmentManager().beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.sub_container, mServiceDetailFragment);
                    transaction.commit();
                    */
                }
            });

            if (mIdPostView != 0) {
                sessionManager.resetServiceDetail();
                mIdPostView = 0;

                itemView.performClick();
            }

        }
    }

    public class TechnicalHistoryViewHolder
            extends RecyclerView.ViewHolder{

        private CircleImageView mProviderImageView;
        private TextView mNameView, mServiceView, mDateMonthView, mHourView, mCostView;

        private RatingBar mCalificationView;

        private LinearLayout mInfoView;

        public TechnicalHistoryViewHolder(View itemView) {
            super(itemView);
            mProviderImageView = itemView.findViewById(R.id.img_provider);
            mCalificationView = itemView.findViewById(R.id.rb_calification);
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

            // Set calification
            setRatingStarColor(itemView.getContext(), mCalificationView);
            mCalificationView.setRating(mProvider.getScore());

            // Set service name
            Random random = new Random();
            mServiceView.setText(mService.getName());
            mServiceView.setTextColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));


            // Set info
            mInfoView.setVisibility(View.VISIBLE);

            // Set date-month
            mDateMonthView.setText(Util.getDateFormat(mReservation.getDate()));

            // Set hour
            mHourView.setText(Util.getTimeFormat(mReservation.getTime()));

            // Set cost
            mCostView.setText("$" + mReservation.getCost());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*((RootFragment)mFragment).setBackPressedIcon();

                    ServiceDetailFragment mServiceDetailFragment = new ServiceDetailFragment();
                    mServiceDetailFragment.setArguments(bundle);

                    FragmentTransaction transaction = mFragment.
                            getChildFragmentManager().beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.sub_container, mServiceDetailFragment);
                    transaction.commit(); */

                    //final Reservation reservation = mReservation;
                    //reservation.setProvider(null);

                    //Bundle bundle = new Bundle();
                    //bundle.putParcelable("reservation", reservation);

                    //Intent intent = new Intent(itemView.getContext(), ServiceDetailActivity.class);
                    //intent.putExtras(bundle);
                    //itemView.getContext().startActivity(intent);
                }
            });
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
                    EventBus.getDefault().post(Util.ACTION_UPDATE);
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(context.getResources()
                    .getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(context.getResources()
                    .getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(0).setColorFilter(context.getResources()
                    .getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
