package com.easyfix.client.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easyfix.client.R;
import com.easyfix.client.activities.MapActivity;
import com.easyfix.client.models.Address;
import com.easyfix.client.models.ProviderReservation;
import com.easyfix.client.models.Reservation;
import com.easyfix.client.models.User;
import com.easyfix.client.util.Util;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by julio on 08/06/17.
 */

public class ReservationProviderAdapter extends RecyclerView.Adapter<ReservationProviderAdapter.UserViewHolder> {

    private List<ProviderReservation> mProviderReservationList;
    private Context mContext;
    private RequestOptions options = new RequestOptions()
            .error(R.drawable.ic_empty_profile)
            .placeholder(R.drawable.ic_empty_profile)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ReservationProviderAdapter(Context context, List<ProviderReservation> providerReservationList) {
        this.mContext = context;
        this.mProviderReservationList = providerReservationList;
    }


    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_provider, viewGroup, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mProviderReservationList.size();
    }

    /**
     * View Holders
     */

    public class UserViewHolder
            extends RecyclerView.ViewHolder{

        private CircleImageView mProviderImageView;
        private TextView mProviderNameView, mHourView;
        private RatingBar mRatingView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mProviderImageView = itemView.findViewById(R.id.img_provider_item_reservation_provider);
            mProviderNameView = itemView.findViewById(R.id.txt_provider_name_item_reservation_provider);
            mRatingView = itemView.findViewById(R.id.rb_calification_item_reservation_provider);
            mHourView = itemView.findViewById(R.id.txt_hour_item_reservation_provider);
        }

        public void bind(final int position) {
            final ProviderReservation pr = mProviderReservationList.get(position);
            final User user = pr.getProvider();

            mProviderNameView.setText(user.getShortName());
            mRatingView.setRating(user.getScore());

            if (pr.getReservation().isScheduled()){
                mHourView.setVisibility(View.GONE);
            } else {
                mHourView.setText(Util.getTimeFormat(pr.getTime()));
            }


            int itemColor, textColor, imgColor, rbBackgroundColor=0, rbProgressColor=0;

            if (position%2 == 0) {
                itemColor = R.color.white;
                imgColor = R.color.colorPrimary;
                textColor = R.color.black;

                rbBackgroundColor = android.R.color.transparent;
                rbProgressColor = R.color.colorPrimary;
            } else {
                itemColor = R.color.colorPrimary;
                imgColor = R.color.white;
                textColor = R.color.white;

                rbBackgroundColor = android.R.color.transparent;
                rbProgressColor = R.color.white;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                itemView.setBackgroundColor(mContext.getColor(itemColor));

                mProviderNameView.setTextColor(mContext.getColor(textColor));
                mHourView.setTextColor(mContext.getColor(textColor));

                rbBackgroundColor = mContext.getColor(rbBackgroundColor);
                rbProgressColor = mContext.getColor(rbProgressColor);
            } else {
                itemView.setBackgroundColor(mContext.getResources().getColor(itemColor));

                mProviderNameView.setTextColor(mContext.getResources().getColor(textColor));
                mHourView.setTextColor(mContext.getResources().getColor(textColor));

                rbBackgroundColor = mContext.getResources().getColor(rbBackgroundColor);
                rbProgressColor = mContext.getResources().getColor(rbProgressColor);
            }

            // Set provider image
            String url = "";
            if (user.getProfile() != null) {
                url = user.getProfile().getImage();
            }

            if (!TextUtils.isEmpty(url)) {
                Glide.with(mContext)
                        .load(url)
                        .apply(options)
                        .into(mProviderImageView);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mProviderImageView.setColorFilter(mContext.getColor(imgColor));
                    mProviderImageView.setBorderColor(mContext.getColor(imgColor));
                } else {
                    mProviderImageView.setColorFilter(mContext.getResources().getColor(imgColor));
                    mProviderImageView.setBorderColor(mContext.getResources().getColor(imgColor));
                }
            }

            // Set rating
            setRatingStarColor(mRatingView, rbBackgroundColor, rbProgressColor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                    builder.setMessage("¿Esta seguro de asignar este técnico?")
                            .setPositiveButton(R.string.dialog_message_yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    EventBus.getDefault().post(pr);
                                }
                            })
                            .setNegativeButton(R.string.dialog_message_no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            });

                    builder.show();
                }
            });
        }

        private void setRatingStarColor(RatingBar ratingBar, int colorBG, int colorFG) {
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();

            stars.getDrawable(2).setColorFilter(colorFG, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(colorFG, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(0).setColorFilter(colorBG, PorterDuff.Mode.SRC_ATOP);
        }

    }
}
