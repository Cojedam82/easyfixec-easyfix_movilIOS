package com.easyfixapp.easyfix.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.MainActivity;
import com.easyfixapp.easyfix.adapters.AddressAdapter;
import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.easyfixapp.easyfix.widget.DividerItemDecoration;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
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

    private Reservation mReservation = null;

    public ScheduleFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule, container, false);
        mReservation = (Reservation) getArguments().getSerializable("reservation");

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
        mMonthView.setText(monthName);
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

        // Get time am or pm
        //int am_pm = calendar.get(Calendar.AM_PM);
        //mTimeFormatView.setText(am_pm == Calendar.AM? TIME_AM : TIME_PM);
    }

    private void attemptSchedule() {
        CalendarDay calendarDay = CalendarDay.today();
        int year = calendarDay.getYear();
        int month = calendarDay.getMonth();
        int day = calendarDay.getDay();
        int currentHour = calendarDay.getCalendar().get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendarDay.getCalendar().get(Calendar.MINUTE);


        if (mCalendarDay.getYear() == year && mCalendarDay.getMonth() == month &&
                mCalendarDay.getDay() == day && hourOfDay <= currentHour) {
            Util.longToast(getContext(), "Para agendar debe ingresar por lo menos una hora despues de la actual");
        } else {
            mReservation.setDate(year + "-" + month + "-" + day);
            mReservation.setTime(mTimeView.getText().toString());

            confirmAddress(getContext(), mReservation);
        }
    }

    public void confirmAddress (final Context context, final Reservation reservation) {
        Address address = null;

        Realm realm = Realm.getDefaultInstance();
        try {
            Address addressBefore = realm
                    .where(Address.class)
                    .equalTo("isDefault", true)
                    .findFirst();
            address = realm.copyFromRealm(addressBefore);

        } catch (Exception ignore){}
        finally {
            realm.close();
        }

        if (address != null) {
            AlertDialog.Builder displayAlert = new AlertDialog.Builder(context, R.style.AlertDialog);
            displayAlert.setTitle("Seleccionar dirección");
            displayAlert.setCancelable(false);
            final Address finalAddress = address;
            displayAlert.setMessage("¿Desea solicitar el tecnico a su dirección por defecto?")
                    .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            reservation.setAddress(finalAddress);
                            createReservationTask(context, reservation);
                        }
                    })
                    .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            showAddressPickup(reservation);
                        }
                    });

            displayAlert.show();
        } else {
            showAddressPickup(reservation);
        }

    }


    public void createReservationTask(final Context context, Reservation reservation){
        Util.showLoading(context, getString(R.string.reservation_message_create_request));

        final SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        ApiService apiService = ServiceGenerator.createApiService();
        Call<Reservation> call = apiService.createReservation(token, reservation);
        call.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_RESERVATION, "Reservation result: success!");
                    Util.longToast(context,
                            getString(R.string.reservation_message_create_response));

                    ((MainActivity)MainActivity.activity).clearService();

                } else {
                    Log.i(Util.TAG_RESERVATION, "Reservation result: " + response.toString());
                    Util.longToast(context,
                            getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Log.i(Util.TAG_RESERVATION, "Reservation result: failed, " + t.getMessage());
                Util.longToast(context,
                        getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }

    private void showAddressPickup(Reservation reservation) {
        AddressConfirmFragment mAddressConfirmFragment = new AddressConfirmFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("reservation", reservation);
        mAddressConfirmFragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.sub_container, mAddressConfirmFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
