package com.easyfixapp.easyfix.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by julio on 10/06/17.
 */

public class AgendaAdapter extends ArrayAdapter<Reservation> {
    private List<Reservation> mReservationList;
    private Context mContext;

    public AgendaAdapter(Context context, List<Reservation> reservationList) {
        super(context, -1, reservationList);
        this.mReservationList = reservationList;
        this.mContext = context;
    }

    public Context getContext(){
        return this.mContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View rowView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.agenda_item, parent, false);

        Reservation mReservation = mReservationList.get(position);
        Service mService = mReservation.getService();
        User mProvider = mReservation.getProvider();

        // Set provider image
        CircleImageView mProviderImageView = (CircleImageView) rowView.findViewById(R.id.img_provider);
        Glide.with(getContext())
                .load(mProvider.getProfile().getImage())
                .dontAnimate()
                .error(R.drawable.logo)
                .placeholder(R.drawable.logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mProviderImageView);

        TextView nameView = (TextView) rowView.findViewById(R.id.txt_provider_name);
        nameView.setText(mProvider.getShortName());

        TextView serviceView = (TextView) rowView.findViewById(R.id.txt_service_name);
        Random random = new Random();
        serviceView.setText(mService.getName());
        serviceView.setTextColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));

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
        TextView dateView = (TextView) rowView.findViewById(R.id.txt_date);
        dateView.setText(date);

        TextView hourView = (TextView) rowView.findViewById(R.id.txt_hour);
        hourView.setText(mReservation.getTime().substring(0,5));

        return rowView;
    }
}
