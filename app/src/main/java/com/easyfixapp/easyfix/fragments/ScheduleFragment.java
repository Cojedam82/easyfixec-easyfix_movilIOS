package com.easyfixapp.easyfix.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.MainActivity;
import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by julio on 09/10/17.
 */

public class ScheduleFragment extends RootFragment{

    private View view;
    private MaterialCalendarView mCalendarView;
    private TextView mYearView, mMonthView, mTimeView;

    private CalendarDay mCalendarDay = null;
    private int hourOfDay=0, minutesOfDay=0;

    public ScheduleFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule, container, false);

        mCalendarView = view.findViewById(R.id.calendar);

        mYearView = view.findViewById(R.id.txt_year);
        mMonthView = view.findViewById(R.id.txt_month);
        mTimeView = view.findViewById(R.id.txt_time);

        mCalendarView.setTopbarVisible(false);
        mCalendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .commit();
        mCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                yearEvent(date);
                monthEvent(date);
            }
        });
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    mCalendarDay = date;
                }
            }
        });

        mTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickupTime();
            }
        });

        view.findViewById(R.id.btn_schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSchedule();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.addFragment();

        init();
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.setReservationDetail(true);
        super.onDestroy();
    }

    private void init() {

        if (mCalendarDay == null) {
            mCalendarDay = CalendarDay.today();
        }

        // Set current or selected date
        mCalendarView.setCurrentDate(mCalendarDay);
        mCalendarView.setDateSelected(mCalendarDay, true);

        yearEvent(mCalendarDay);
        monthEvent(mCalendarDay);
        timeEvent();
    }

    private void pickupTime() {

        TimePickerDialog mTimePicker = new TimePickerDialog(
                getActivity(), new TimePickerDialog.OnTimeSetListener() {

            String[] time = mTimeView.getText().toString().split(":");
            final int hours = Integer.valueOf(time[0]);
            final int minutes = Integer.valueOf(time[1]);

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                hourOfDay = selectedHour;
                minutesOfDay = selectedMinute;

                timeEvent();
            }
        }, hourOfDay, minutesOfDay, true);

        mTimePicker.setTitle("Seleccione la hora de visita");
        mTimePicker.show();
    }

    private void yearEvent(CalendarDay date) {
        mYearView.setText(String.valueOf(date.getYear()));
    }

    private void monthEvent(CalendarDay date) {
        Calendar calendar = date.getCalendar();
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        mMonthView.setText(
                Character.toUpperCase(monthName.charAt(0)) +
                        monthName.substring(1, monthName.length()).toLowerCase());
    }

    private void timeEvent() {

        if (hourOfDay == 0) {
            Calendar calendar = Calendar.getInstance();
            hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        }

        // Build calendar with custom time
        Calendar calendar = Calendar.getInstance();
        calendar.set(0,0,0,hourOfDay, minutesOfDay);


        // Set up time
        mTimeView.setText((hourOfDay < 10? "0"+ hourOfDay : hourOfDay) + ":" +
                (minutesOfDay < 10? "0"+ minutesOfDay : minutesOfDay));
    }

    private void attemptSchedule() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 2);

        Calendar calendarChoose = mCalendarDay.getCalendar();
        calendarChoose.set(mCalendarDay.getYear(), mCalendarDay.getMonth(), mCalendarDay.getDay(), hourOfDay, minutesOfDay);

        Log.e("Horas", calendarChoose.toString());
        Log.e("Horas", calendar.toString());

        if (calendarChoose.before(calendar)) {
            Util.longToast(getContext(), "Para agendar debe ingresar por lo menos dos horas despues de la actual");
        } else {
            Reservation reservation = new Reservation();
            reservation.setDate(calendarChoose.getTime());
            reservation.setTime(calendarChoose.getTime().getTime());
            reservation.setScheduled(true);

            EventBus.getDefault().post(reservation);
        }
    }

}
