package com.easyfixapp.easyfix.activities;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.adapters.ReservationAdapter;
import com.easyfixapp.easyfix.adapters.TechAdapter;
import com.easyfixapp.easyfix.fragments.RootFragment;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.easyfixapp.easyfix.widget.DividerItemDecoration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitingQueue extends Activity {

    private static View view = null;
    private static RecyclerView mReservationView = null;
    private static RelativeLayout mtechListrv = null;
    private static RelativeLayout mListView = null;
    private static RelativeLayout mWait = null;
    private static TechAdapter mReservationAdapter = null;
    private static List<Tecnico> mTecnicosList = new ArrayList<>();
    private static Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_queue);
        ImageView x = (ImageView)findViewById(R.id.x);
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mContext = this;
        mReservationAdapter = new TechAdapter(this,
                mTecnicosList, Reservation.TYPE_NOTIFICATION);
        mReservationView = findViewById(R.id.rv_reservation);
        mtechListrv = findViewById(R.id.techListrv);
        mWait = findViewById(R.id.wait);
        mListView = findViewById(R.id.list);

        // Set requirements to show recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mReservationView.setLayoutManager(mLayoutManager);

        // Set item divider
        mReservationView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mReservationView.setItemAnimator(new DefaultItemAnimator());

        //Esto es totalmente cableado
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.setVisibility(View.GONE);
                mWait.setVisibility(View.GONE);
                mtechListrv.setVisibility(View.VISIBLE);
                mReservationView.setAdapter(mReservationAdapter);
                reservationTask();
            }
        }, 3000);

    }



    @Override
    public void onResume() {
        super.onResume();
    }

    /** Fetch reservations **/
    private void reservationTask(){
//        Util.showProgress(mContext, mReservationView, view, true);

        SessionManager sessionManager = new SessionManager(mContext);
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        String dateFormatted = formatter.format(date);
        mTecnicosList.clear();
        Tecnico tecnico = new Tecnico("Pedro Perez",4,"", dateFormatted);
        mTecnicosList.add(tecnico);
        tecnico = new Tecnico("Alberto Gonzales",2,"",dateFormatted);
        mTecnicosList.add(tecnico);
        tecnico = new Tecnico("María Córdoba",5,"",dateFormatted);
        mTecnicosList.add(tecnico);
        mReservationAdapter.notifyDataSetChanged();
//
//        Call<List<Reservation>> call = apiService.getNotifications(token);
//        call.enqueue(new Callback<List<Reservation>>() {
//            @Override
//            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
////                Util.showProgress(mContext, mReservationView, view, false);
//
//                if (response.isSuccessful()) {
//                    Log.i(Util.TAG_NOTIFICATION, "Notification result: success!");
//
//                    List<Reservation> reservationList = response.body();
//                    if (!reservationList.isEmpty()) {
//
//                    } else {
//
//                        Util.showMessage(mReservationView, view,
//                                mContext.getString(R.string.message_service_server_empty));
//                    }
//                } else {
//                    Log.i(Util.TAG_NOTIFICATION, "Notification result: " + response.toString());
//                    Util.showMessage(mReservationView, view,
//                            mContext.getString(R.string.message_service_server_failed));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Reservation>> call, Throwable t) {
//                Log.i(Util.TAG_NOTIFICATION, "Notification result: failed, " + t.getMessage());
//                Util.showProgress(mContext, mReservationView, view, false);
//                Util.showMessage(mReservationView, view,
//                        mContext.getString(R.string.message_network_local_failed));
//            }
//        });
    }

    public class Tecnico{
        String name;
        int stars;
        String photo;
        String hour;

        public Tecnico(String name, int stars, String photo, String hour) {
            this.name = name;
            this.stars = stars;
            this.photo = photo;
            this.hour = hour;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getStars() {
            return stars;
        }

        public void setStars(int stars) {
            this.stars = stars;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getHour() {
            return hour;
        }

        public void setHour(String hour) {
            this.hour = hour;
        }
    }


}
