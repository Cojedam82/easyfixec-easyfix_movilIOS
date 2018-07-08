package com.easyfixapp.easyfix.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.Reservation;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jrealpe on 05/26/18.
 */

public class ReservationDetailScheduleFragment extends RootFragment {

    private View view;
    private TabLayout mTabLayoutView;
    private Button mRequestView;

    String[] schedules = new String[]{
            "1 Hora",
            "2 Horas",
            "Agendar"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_reservation_detail_schedule, container, false);

        mTabLayoutView = view.findViewById(R.id.tl_schedule);

        mRequestView = view.findViewById(R.id.btn_request);
        mRequestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = mTabLayoutView.getSelectedTabPosition();

                Reservation reservation = new Reservation();
                reservation.setScheduled(false);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());

                if ( selected == 0 ) {
                    calendar.add(Calendar.HOUR_OF_DAY, 1);

                    reservation.setTime(calendar.getTime().getTime());
                    reservation.setDate(calendar.getTime());
                } else if ( selected == 1 ) {
                    calendar.add(Calendar.HOUR_OF_DAY, 2);

                    reservation.setTime(calendar.getTime().getTime());
                    reservation.setDate(calendar.getTime());
                }
                EventBus.getDefault().post(reservation);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateTabs();
    }

    private void populateTabs(){
        mTabLayoutView.addTab(mTabLayoutView.newTab().setText(schedules[0]));
        mTabLayoutView.addTab(mTabLayoutView.newTab().setText(schedules[1]));
        mTabLayoutView.addTab(mTabLayoutView.newTab().setText(schedules[2]));
        mTabLayoutView.getTabAt(0).select();

        for(int i = 0; i < mTabLayoutView.getTabCount(); i++) {
            ViewGroup viewGroup = (ViewGroup) ((ViewGroup) mTabLayoutView.getChildAt(0)).getChildAt(i);

            // Set font
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
                    "fonts/AntipastoRegular.otf");
            ((TextView) viewGroup.getChildAt(1)).setTypeface(font);

            viewGroup.requestLayout();
        }
    }
}
