package com.easyfix.client.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.easyfix.client.R;
import com.easyfix.client.activities.MainActivity;
import com.easyfix.client.activities.MapActivity;
import com.easyfix.client.activities.ReservationDescriptionActivity;
import com.easyfix.client.adapters.ReservationDetailViewPagerAdapter;
import com.easyfix.client.adapters.ViewPagerAdapter;
import com.easyfix.client.models.Address;
import com.easyfix.client.models.Notification;
import com.easyfix.client.models.Reservation;
import com.easyfix.client.util.ApiService;
import com.easyfix.client.util.ServiceGenerator;
import com.easyfix.client.util.SessionManager;
import com.easyfix.client.util.Util;
import com.easyfix.client.widget.CustomViewPager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Time;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jrealpe on 05/26/18.
 */

public class ReservationDetailFragment extends RootFragment
        implements OnMapReadyCallback,LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private View view;
    private TextView mTitle;
    private CustomViewPager mViewPager;
    private ReservationDetailViewPagerAdapter mViewPagerAdapter;

    private Reservation mReservation = null;

    private SupportMapFragment mMapFragment = null;
    private GoogleMap mGoogleMap = null;

    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = null;

    private boolean mResolvingError = false;
    private boolean mLocationPermissionGranted = false;
    private boolean mPlayServicesPermissionGranted = false;

    private LatLng mLastLocation = null;

    private static final int CUSTOM_ZOOM = 15;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReservation = (Reservation) getArguments().getParcelable("reservation");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_reservation_detail, container, false);

        mTitle = view.findViewById(R.id.txt_title);
        mViewPager = view.findViewById(R.id.vp_service_reservation);

        loadMap();

        return view;
    }

    @Override
    public boolean onBackPressed() {
        SessionManager sessionManager = new SessionManager(getContext());
        if (sessionManager.isReservationDetailFocused()) {
            if (mViewPager.getCurrentItem() == 1) {

                if (mGoogleMap != null) {
                    mGoogleMap.clear();
                }

                mReservation.setTime(null);
                mReservation.setDate(null);
                mReservation.setAddress(null);
                mViewPager.setCurrentItem(0);

                sessionManager.removeFragment();
                return true;
            }
        } else {
            sessionManager.removeFragment();
        }

        return super.onBackPressed();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.addFragment();
        sessionManager.setReservationDetail(true);

        mViewPagerAdapter = new ReservationDetailViewPagerAdapter(getChildFragmentManager(), 2);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setPagingEnabled(false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mTitle.setText("¿Dónde deseas al técnico?");
                } else {
                    mTitle.setText("¿Cuánto puedes esperar?");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (mReservation.getAddress() != null) {
            mViewPager.setCurrentItem(1);
            sessionManager.addFragment();
        } else {
            mViewPager.setCurrentItem(0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        disconnectAPIGoogle();
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReservationEvent(Reservation reservation) {
        if (reservation != null) {
            if (!TextUtils.isEmpty(reservation.getDescription())) {
                mReservation.setDescription(reservation.getDescription());

                if(reservation != null) {
                    EventBus.getDefault().removeStickyEvent(reservation);
                }

                createReservationTask(mReservation);
            } else if (reservation.getAddress() != null) {
                showAddrees(reservation.getAddress());

                mReservation.setAddress(reservation.getAddress());
                mViewPager.setCurrentItem(1);

                SessionManager sessionManager = new SessionManager(getContext());
                sessionManager.addFragment();
            } else if (reservation.getDate() == null && reservation.getTime() == null) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.sub_container, new ScheduleFragment());
                transaction.addToBackStack(null);
                transaction.commit();

                SessionManager sessionManager = new SessionManager(getContext());
                sessionManager.setReservationDetail(false);
            } else {
                mReservation.setTime(reservation.getTime());
                mReservation.setDate(reservation.getDate());
                mReservation.setScheduled(reservation.isScheduled());

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
                builder.setMessage("¿Quiere darnos detalles del problema?")
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SessionManager sessionManager = new SessionManager(getContext());
                                sessionManager.setReservationDetail(false);

                                Intent intent = new Intent(getActivity(), ReservationDescriptionActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                createReservationTask(mReservation);
                            }
                        });

                builder.show();
            }
        }
    }

    public void createReservationTask(Reservation reservation){
        Util.showLoading(getContext(), getString(R.string.reservation_message_create_request));

        SessionManager sessionManager = new SessionManager(getContext());
        String token = sessionManager.getToken();

        ApiService apiService = ServiceGenerator.createApiService();

        HashMap<String, Object> params = new HashMap<>();
        params.put("time", Util.getTimeFormat(reservation.getTime()));
        params.put("date", Util.getDateFormat2(reservation.getDate()));
        params.put("description", reservation.getDescription());
        params.put("address", reservation.getAddress().getId());
        params.put("service", reservation.getService().getId());
        params.put("is_scheduled", reservation.isScheduled());

        Call<Reservation> call = apiService.createReservation(token, params);
        call.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                Util.hideLoading();
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_RESERVATION, "Reservation result: success!");
                    //Util.longToast(getContext(),
                    //        getString(R.string.reservation_message_create_response));

                    Reservation r = response.body();

                    // Return to principal screen
                    ((MainActivity) MainActivity.activity).clearService();
                    EventBus.getDefault().post(new Notification(Util.ACTION_CREATE, r));
                } else {
                    Log.i(Util.TAG_RESERVATION, "Reservation result: " + response.toString());
                    Util.longToast(getContext(),
                            getString(R.string.message_service_server_failed));
                }
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Log.i(Util.TAG_RESERVATION, "Reservation result: failed, " + t.getMessage());
                Util.hideLoading();
                Util.longToast(getContext(),
                        getString(R.string.message_network_local_failed));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        showAddrees(mReservation.getAddress());
    }

    private void showAddrees(Address address) {
        if (mGoogleMap != null) {
            if (address != null) {
                mGoogleMap.clear();

                double lat = address.getLatitude();
                double lon = address.getLongitude();

                if (lat != 0 && lon != 0) {
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(address.getName()));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            } else {
                mLastLocation = new LatLng(-1.831239, -78.183406);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocation, 7));

                // Turn on the My Location layer and the related control on the map.
                getLocationPermission();
                checkPlayServices();
                updateLocationUI();
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMapEvent(Address address) {
        if(address != null) {
            if (address.getId() != 0) {
                mReservation.setAddress(address);
                mViewPager.setCurrentItem(1);

                SessionManager sessionManager = new SessionManager(getContext());
                sessionManager.addFragment();

                EventBus.getDefault().removeStickyEvent(address);
            }
        }
        loadMap();
    }

    @Override
    public void onLocationChanged(Location location) {

        try {
            if (mReservation.getAddress() == null) {
                mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocation, CUSTOM_ZOOM));

                disconnectAPIGoogle();
            }

        } catch(SecurityException e)  {
            Log.e(Util.TAG_MAP, "Exception: %s", e.getCause());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(getActivity(),
                        Util.PLAY_SERVICES_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            GooglePlayServicesUtil.showErrorDialogFragment(
                    connectionResult.getErrorCode(),
                    getActivity(),
                    null,
                    1,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            //finish();
                        }
                    }
            );
            mResolvingError = true;
        }
    }

    private void loadMap() {
        if (mMapFragment != null ) {
            getChildFragmentManager().beginTransaction().remove(mMapFragment);
            mMapFragment = null;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);

                    mMapFragment = SupportMapFragment.newInstance();
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.reservation_map, mMapFragment)
                            .commit();

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mMapFragment.getMapAsync(ReservationDetailFragment.this);
                        }
                    });
                } catch (Exception e) {
                    Log.e(Util.TAG_MAP, e.toString());
                }
            }
        }).start();
    }

    /**
     * Permissions and connection google map
     */

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Util.LOCATION_REQUEST_CODE);
        }
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e(Util.TAG_MAP, e.getMessage());
        }

        connectAPIGoogle();
    }

    private void getDeviceLocation() {

        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(3000);
            mLocationRequest.setFastestInterval(1000);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();

                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            if (mReservation.getAddress() == null && mGoogleApiClient != null) {
                                try {
                                    Location location = LocationServices.FusedLocationApi.getLastLocation(
                                            mGoogleApiClient);

                                    mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocation, CUSTOM_ZOOM));

                                    disconnectAPIGoogle();
                                } catch (SecurityException e) {
                                    Log.e(Util.TAG_MAP, "Exception: %s", e.getCause());
                                }
                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(getActivity(),
                                        Util.PLAY_SERVICES_REQUEST_CODE);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });

            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            } catch(SecurityException e)  {
                Log.e(Util.TAG_MAP, "Exception: %s", e.getCause());
            }
        }
    }

    private void connectAPIGoogle() {
        if (mPlayServicesPermissionGranted && mLocationPermissionGranted) {

            if (mGoogleApiClient == null) {

                mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            }

        }
    }

    private void disconnectAPIGoogle() {

        if (mLocationRequest != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mLocationRequest = null;
        }

        if(mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()){
                mGoogleApiClient.disconnect();
                mGoogleApiClient = null;
            }
        }
    }

    /**
     * When Play Services is missing or at the wrong version, the client
     * library will assist with a dialog to help the user update
     */
    private void showPlayServicesError(int errorCode) {
        // Get the error dialog from Google Play Services
        GoogleApiAvailability.getInstance()
                .showErrorDialogFragment(getActivity(), errorCode, 1,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                //finish();
                            }
                        });
    }

    private void checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());

        if (resultCode != ConnectionResult.SUCCESS) {
            mPlayServicesPermissionGranted = false;
            if (apiAvailability.isUserResolvableError(resultCode)) {
                showPlayServicesError(resultCode);
            }
        } else {
            mPlayServicesPermissionGranted = true;
        }
    }
}
