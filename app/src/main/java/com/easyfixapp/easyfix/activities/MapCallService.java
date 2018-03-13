package com.easyfixapp.easyfix.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.adapters.AddressAdapter;
import com.easyfixapp.easyfix.adapters.AddressAdapterCallService;
import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.easyfixapp.easyfix.widget.DividerItemDecoration;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
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
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MapCallService extends AppCompatActivity implements
        OnMapReadyCallback,
        OnCameraIdleListener,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {

    private PlaceAutocompleteFragment autocompleteFragment;
    private TextView mAddressViewText;
    private EditText mReferenceView;

    private GoogleMap mMap;
    private boolean mResolvingError = false;
    private boolean isDefault = false;

    // A default location (Ecuador) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-1.831239, -78.183406);
    private static final int DEFAULT_ZOOM = 7;
    private static final int CUSTOM_ZOOM = 15;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = null;


    private boolean mLocationPermissionGranted = false;
    private boolean mPlayServicesPermissionGranted = false;

    private LatLng mLastLocation = null;
    private String mName = null;
    private String mDescription = null;

    private Address mAddress = null;

    private Service mService = null;
    private Reservation mReservation = null;

    private RecyclerView mAddressView;
    private AddressAdapterCallService mAddressAdapter;
    private List<Address> mAddressList = new ArrayList<>();
    private LinearLayout mList;
    private LinearLayout mScheduler;
    private TextView mOneHourText;
    private TextView mTwoHourText;
    private TextView mScheduleText;
    private Button mButtonService;
    private Boolean verifier = false;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_service);
        mContext = this;
        mapLayout = (RelativeLayout)findViewById(R.id.mapLayout);
        mOneHourText=(TextView)findViewById(R.id.oneHour_Scheduler);
        mTwoHourText=(TextView)findViewById(R.id.twoHour_Scheduler);
        mScheduleText=(TextView)findViewById(R.id.schedule_Scheduler);
        mList = (LinearLayout)findViewById(R.id.header);
        mScheduler = (LinearLayout)findViewById(R.id.Scheduler);
        mButtonService = (Button)findViewById(R.id.btn_update_Scheduler);
        Bundle b = getIntent().getExtras();
        String location = b.getString("location");
        if(location.equals("predetermined")){
            verifier = true;
            scheduleQuestion();
        }

        if (mService==null && mReservation == null) {
            try {
                Serializable serializable = savedInstanceState.getSerializable("service");
                if (serializable == null) {
                    mReservation = (Reservation) savedInstanceState.getSerializable("reservation");
                    mService = mReservation.getService();
                    savedInstanceState.remove("reservation");
                } else {
                    mService = (Service) serializable;
                    savedInstanceState.remove("service");
                }
            } catch (Exception ignore) {}
        }
        LinearLayout addDirection = (LinearLayout)findViewById(R.id.addDirection);
        addDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });
        CircleImageView icon = (CircleImageView)findViewById(R.id.img_status_icon);
        icon.setCircleBackgroundColorResource(R.color.com_facebook_messenger_blue);
        try {
            mAddress = (Address) getIntent().getExtras().getSerializable("address");
        } catch (Exception ignore) {}

        Button button = (Button)findViewById(R.id.btn_update);
        button.setText("Solicitar Servicio");
        mAddressViewText = findViewById(R.id.txt_autocomplete);
        mReferenceView = findViewById(R.id.txt_reference);

        findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fm.getMapAsync(this);


        mAddressView = findViewById(R.id.rv_address);
        mAddressAdapter = new AddressAdapterCallService(this, mAddressList);

        // set true if your RecyclerView is finite and has fixed size
        //mRecyclerView.setHasFixedSize(false);
        //mRecyclerView.addItemDecoration(new ItemOffsetDecoration(10));
        // Set requirements to show recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAddressView.setLayoutManager(mLayoutManager);

        // Set item divider
        mAddressView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mAddressView.setItemAnimator(new DefaultItemAnimator());

        mAddressView.setAdapter(mAddressAdapter);

        mAddressView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mAddressView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                double latitude = Double.parseDouble(mAddressList.get(position).getLatitude());
                double longitude = Double.parseDouble(mAddressList.get(position).getLongitude());
                mLastLocation = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocation, CUSTOM_ZOOM));


                scheduleQuestion();


            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }



    private String m_Text = "";
    private void scheduleQuestion(){
        mList.setVisibility(View.GONE);
        mScheduler.setVisibility(View.VISIBLE);
        mOneHourText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOneHourText.setTextColor(Color.parseColor("#428ff4"));
                mTwoHourText.setTextColor(Color.parseColor("#a3a3a3"));
                mScheduleText.setTextColor(Color.parseColor("#a3a3a3"));
            }
        });
        mTwoHourText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOneHourText.setTextColor(Color.parseColor("#a3a3a3"));
                mTwoHourText.setTextColor(Color.parseColor("#428ff4"));
                mScheduleText.setTextColor(Color.parseColor("#a3a3a3"));
            }
        });
        mScheduleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOneHourText.setTextColor(Color.parseColor("#a3a3a3"));
                mTwoHourText.setTextColor(Color.parseColor("#a3a3a3"));
                mScheduleText.setTextColor(Color.parseColor("#428ff4"));
            }
        });
        mButtonService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Util.shortToast(getApplicationContext(),"En desarrollo");

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Detalles adicionales del problema ");

// Set up the input
                final EditText input = new EditText(mContext);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("¿Quiere darnos detalles del problema? (Si es posible incluir marca, modelo, entre otros)");
                input.setLines(4);
                input.setMaxLines(5);
                input.setGravity(Gravity.LEFT | Gravity.TOP);
                input.setHorizontalScrollBarEnabled(false);
                input.setHorizontallyScrolling(false);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("Omitir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        Intent intent = new Intent(mContext,WaitingQueue.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(mContext,WaitingQueue.class);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.show();


            }
        });


    }


    RelativeLayout mapLayout;

    private void populate(){
        Util.showLoading(this, "Cargando ...");

        // Clean address
        mAddressList.clear();

        Realm realm = Realm.getDefaultInstance();
        try {
            RealmResults<Address> result = realm.where(Address.class)
                    .equalTo("isActive", true)
                    .findAllSorted("id", Sort.DESCENDING);
            if(!result.isEmpty()){
                // Copy persist query
                for (Address address : realm.copyFromRealm(result)) {
                    mAddressList.add(address);
                }
            }
        } finally {
            realm.close();
        }
//        if(mAddressList.size()>1){
//            mapLayout.setVisibility(View.VISIBLE);
//        } else {
//            mapLayout.setVisibility(View.GONE);
//        }
        Util.hideLoading();

        // Always notify data
        mAddressAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populate();

    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Map
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);

        // Prevent any error
        showLocationByDefault();

        // Prompt the user for permission.
        getLocationPermission();
        checkPlayServices();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        init();

    }

    @Override
    public void onCameraIdle() {
        LatLng latLng = mMap.getCameraPosition().target;
        Log.i(Util.TAG_MAP, latLng.latitude + " , " + latLng.longitude);

        if (isDefault) {
            isDefault = false;
        } else {
            addressFromLatLong(latLng);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Util.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(Util.TAG_MAP, status.getStatusMessage());
                Util.longToast(getApplicationContext(), getString(R.string.address_message_create_error));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;
        if (requestCode == Util.LOCATION_REQUEST_CODE) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        } else if (requestCode == Util.PLAY_SERVICES_REQUEST_CODE) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPlayServicesPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        disconnectAPIGoogle();
    }

    @Override
    public void onLocationChanged(Location location) {

        try {
            if (mAddress == null) {
                mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocation, CUSTOM_ZOOM));

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
                connectionResult.startResolutionForResult(MapCallService.this,
                        Util.PLAY_SERVICES_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            GooglePlayServicesUtil.showErrorDialogFragment(
                    connectionResult.getErrorCode(),
                    MapCallService.this,
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

    private void init() {
        if (mAddress != null) {
            ( (Button) findViewById(R.id.btn_update)).setText("Actualizar Dirección");

            mName = mAddress.getName();
            mDescription = mAddress.getDescription();

            mAddressViewText.setText( mName + ", " + mDescription);
            mReferenceView.setText(mAddress.getReference());

            mLastLocation = new LatLng(Double.valueOf(mAddress.getLatitude()), Double.valueOf(mAddress.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocation, CUSTOM_ZOOM));



        }
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
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Util.LOCATION_REQUEST_CODE);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
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
                            if (mAddress == null) {
                                try {
                                    Location location = LocationServices.FusedLocationApi.getLastLocation(
                                            mGoogleApiClient);

                                    if(verifier){
                                        for(int i=0;i<mAddressList.size();i++){
                                            if(mAddressList.get(i).isDefault()){
                                                double latitude = Double.parseDouble(mAddressList.get(i).getLatitude());
                                                double longitude = Double.parseDouble(mAddressList.get(i).getLongitude());
                                                mLastLocation = new LatLng(latitude, longitude);
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocation, CUSTOM_ZOOM));
                                            }
                                        }
                                    }else{
                                    mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocation, CUSTOM_ZOOM));
                                    }


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
                                status.startResolutionForResult(
                                        MapCallService.this, Util.PLAY_SERVICES_REQUEST_CODE);
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

                mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
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
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            mLocationRequest = null;
        }

        if(mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()){
                mGoogleApiClient.disconnect();
                mGoogleApiClient = null;
            }
        }
    }

    private void showLocationByDefault() {
        isDefault = true;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
    }

    /**
     * When Play Services is missing or at the wrong version, the client
     * library will assist with a dialog to help the user update
     */
    private void showPlayServicesError(int errorCode) {
        // Get the error dialog from Google Play Services
        GoogleApiAvailability.getInstance()
                .showErrorDialogFragment(MapCallService.this, errorCode, 1,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                //finish();
                            }
                        });
    }

    private void checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode != ConnectionResult.SUCCESS) {
            mPlayServicesPermissionGranted = false;
            if (apiAvailability.isUserResolvableError(resultCode)) {
                showPlayServicesError(resultCode);
            }
        } else {
            mPlayServicesPermissionGranted = true;
        }
    }

    /**
     * Callback invoked when a place has been selected from the PlaceAutocompleteFragment.
     */

    public void onPlaceSelected(Place place) {
        addressFromLatLong(place.getLatLng());
        //mAddressViewText.setText(place.getAddress());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), CUSTOM_ZOOM));
    }

    public void showAutocomplete(View view) {

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                //.setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setCountry("EC")
                .build();

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);

            startActivityForResult(intent, Util.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    private void addressFromLatLong(LatLng latLng) {
        mLastLocation = latLng;

        try {
            Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<android.location.Address> addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.isEmpty()) {
                mAddressViewText.setText("Desconocido");
            } else {
                if (addresses.size() > 0) {

                    android.location.Address address = addresses.get(0);
                    mName = address.getFeatureName();
                    mDescription = address.getLocality() + ", " +
                            addresses.get(0).getAdminArea();

                    mAddressViewText.setText( mName + ", " + mDescription);

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

//    public void attemptCreate(View view) {
//
//        if (mLastLocation != null && mName != null && mDescription != null) {
//
//            final LayoutInflater adbInflater = LayoutInflater.from(getApplicationContext());
//            View v = adbInflater.inflate(R.layout.dialog_checkbox, null);
//            final CheckBox checkBox = (CheckBox) v.findViewById(R.id.location_default);
//            checkBox.setVisibility(View.GONE);
//            AlertDialog.Builder displayAlert = new AlertDialog.Builder(MapCallService.this, R.style.AlertDialog);
//
//            if (mAddress != null) {
//                if (!mAddress.isDefault()) {
//                    displayAlert.setView(v);
//                }
//            } else {
//                displayAlert.setView(v);
//            }
//            displayAlert.setCancelable(false);
//            displayAlert.setMessage("¿Cuánto puedes esperar?")
//                    .setPositiveButton(R.string.dialog_1_hour, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            //TODO agendar una hora respecto al tiempo actual
////                            Address address = new Address();
////                            address.setName(mName);
////                            address.setDescription(mDescription);
////                            address.setReference(mReferenceView.getText().toString());
////                            address.setLatitude(String.valueOf(mLastLocation.latitude));
////                            address.setLongitude(String.valueOf(mLastLocation.longitude));
////
////                            address.setActive(true);
////                            address.setDefault(checkBox.isChecked());
////
////                            if (mAddress == null) {
////                                createAddressTask(address);
////                            } else {
////                                address.setId(mAddress.getId());
////                                updateAddressTask(address);
////                            }
//                        }
//                    })
//                    .setNegativeButton(R.string.dialog_2_hours, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//
//                            //TODO agendar dos horas respecto al tiempo actual
//                        }
//                    })
//                    .setNeutralButton(R.string.action_service_detail_schedule, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//
//                    //TODO Abrir el fragment de agendar
//                }
//            }).setCancelable(true);
//
//            displayAlert.show();
//        } else {
//            Util.longToast(getApplicationContext(),
//                    getString(R.string.address_message_create_error));
//        }
//    }

    private void createAddressTask(final Address address){
        Util.showLoading(MapCallService.this, getString(R.string.address_message_create_request));

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String token = sessionManager.getToken();

        ApiService apiService = ServiceGenerator.createApiService();
        Call<Address> call = apiService.createAddress(token, address);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_ADDRESS, "Address result: success!");
                    Util.longToast(getApplicationContext(),
                            getString(R.string.address_message_create_response));

                    // Update
                    Address addressAfter = response.body();
                    Realm realm = Realm.getDefaultInstance();
                    try {

                        realm.beginTransaction();

                        if (addressAfter.isDefault()) {
                            Address addressBefore = realm
                                    .where(Address.class)
                                    .equalTo("isDefault", true)
                                    .findFirst();

                            // updating address by default
                            if (addressBefore != null) {
                                addressBefore.setDefault(false);
                                realm.copyToRealmOrUpdate(addressBefore);
                            }
                        }

                        // adding new address to local db
                        realm.copyToRealm(addressAfter);

                        realm.commitTransaction();

                        finish();

                    } catch (Exception e){
                        e.getStackTrace();
                        Log.i(Util.TAG_ADDRESS, e.getMessage());
                    } finally {
                        realm.close();
                    }
                } else {
                    Log.i(Util.TAG_ADDRESS, "Address result: " + response.toString());
                    Util.longToast(getApplicationContext(),
                            getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Log.i(Util.TAG_ADDRESS, "Address result: failed, " + t.getMessage());
                Util.longToast(getApplicationContext(),
                        getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }

    private void updateAddressTask(final Address address){
        Util.showLoading(MapCallService.this, getString(R.string.address_message_update_request));

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String token = sessionManager.getToken();

        ApiService apiService = ServiceGenerator.createApiService();
        Call<Void> call = apiService.updateAddress(address.getId(), token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_ADDRESS, "Address result: success!");
                    Util.longToast(getApplicationContext(),
                            getString(R.string.address_message_update_response));

                    // Update
                    Realm realm = Realm.getDefaultInstance();
                    try {
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(address);
                        realm.commitTransaction();

                    } catch (Exception e){
                        e.getStackTrace();
                        Log.i(Util.TAG_ADDRESS, e.getMessage());
                    } finally {
                        realm.close();
                    }

                    finish();
                } else {
                    Log.i(Util.TAG_ADDRESS, "Address result: " + response.toString());
                    Util.longToast(getApplicationContext(),
                            getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i(Util.TAG_ADDRESS, "Address result: failed, " + t.getMessage());
                Util.longToast(getApplicationContext(),
                        getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }

    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}