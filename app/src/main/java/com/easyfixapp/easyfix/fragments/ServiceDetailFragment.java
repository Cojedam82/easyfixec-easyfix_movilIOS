package com.easyfixapp.easyfix.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easyfixapp.easyfix.BuildConfig;
import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.MainActivity;
import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.models.Artifact;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.easyfixapp.easyfix.widget.CustomRadioGroup;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by julio on 22/10/17.
 */

public class ServiceDetailFragment extends RootFragment{

    private AutoCompleteTextView mArtifactView;
    private CustomRadioGroup mCustomRadioGroup;
    private ImageView mImageServiceView, mImage1View, mImage2View, mImage3View, mImage4View;
    private TabLayout mTabLayout;
    private TextView mTitleView, mTimeView;
    private EditText mDescriptionView;
    private View view;
    private RadioButton mActionView;
    private Button mRequestView;

    private AlertDialog displayAlert = null;

    private Service mService = null;
    private Reservation mReservation = null;

    private byte[][] mImageByteList;
    private File[] mImageFileList;
    private File mFile;
    private int mFilePosition;

    private View.OnClickListener mImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.img_detail_1:
                    mFilePosition = 0;
                    actionPicture();
                    return;
                case R.id.img_detail_2:
                    mFilePosition = 1;
                    actionPicture();
                    return;
                case R.id.img_detail_3:
                    mFilePosition = 2;
                    actionPicture();
                    return;
                case R.id.img_detail_4:
                    mFilePosition = 3;
                    actionPicture();
                    return;
                default:
                    break;
            }

        }
    };

    String[] arr_text = new String[]{
            "Instalacion",
            "Reparacion",
            "Mantenimiento"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_service_detail, container, false);

        if (mService==null && mReservation == null) {
            try {
                Serializable serializable = getArguments().getSerializable("service");
                if (serializable == null) {
                    mReservation = (Reservation) getArguments().getSerializable("reservation");
                    mService = mReservation.getService();
                    getArguments().remove("reservation");
                } else {
                    mService = (Service) serializable;
                    getArguments().remove("service");
                }
            } catch (Exception ignore) {}
        }

        mTitleView = view.findViewById(R.id.txt_service_name);
        mArtifactView = view.findViewById(R.id.txt_artifact);
        mDescriptionView = view.findViewById(R.id.txt_description);
        mImageServiceView = view.findViewById(R.id.img_service);
        mTabLayout = view.findViewById(R.id.tabs);
        mImage1View = view.findViewById(R.id.img_detail_1);
        mImage2View = view.findViewById(R.id.img_detail_2);
        mImage3View = view.findViewById(R.id.img_detail_3);
        mImage4View = view.findViewById(R.id.img_detail_4);
        mTimeView = view.findViewById(R.id.txt_time);
        mCustomRadioGroup = view.findViewById(R.id.rg_actions);
        mRequestView = view.findViewById(R.id.btn_request);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.addFragment();

        init();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == Util.CAMERA_REQUEST_CODE) {
            if (permissions.length == 2 && permissions[0].equals(CAMERA)
                    && permissions[1].equals(WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                actionPicture();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != getActivity().RESULT_OK)
            return;

        Bitmap mImageBitmap = null;

        if (requestCode == Util.IMAGE_CAMERA_REQUEST_CODE) {
            mImageBitmap = adjustImage(mFile);
        } else if (requestCode == Util.IMAGE_GALLERY_REQUEST_CODE) {
            Uri mImageUri = data.getData();
            String path = mImageUri.getPath();
            mFile = new File(path);

            if (mImageUri != null) {
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(
                            getActivity().getContentResolver(), mImageUri);
                    mImageBitmap = scaleImage(mImageBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(mImageBitmap!=null) {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                mImageByteList[mFilePosition] = byteArray;
                mImageFileList[mFilePosition] = mFile;
                showImage(mImageBitmap);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /** Permission to read phone state **/
    private boolean mayRequestCamera() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else if (getActivity().checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED &&
                getActivity().checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, Util.CAMERA_REQUEST_CODE);
        }
        return false;
    }

    private void init() {
        // Init images
        mImageByteList = new byte[4][1];
        mImageFileList = new File[4];

        // Init radiobuton
        mCustomRadioGroup.check(R.id.rb_now);

        if (mService != null) {

            // Populate initial info
            populateInfo();

            // Populate top tabs
            populateTabs();

            // Fill reservation
            checkReservation();

        } else {
            Util.longToast(getContext(),
                    getResources().getString(R.string.message_service_detail_empty));
        }
    }

    private void checkReservation() {

        if (mReservation == null) {

            // Add actions
            addActions();

            // Populate artifacts
            populateArtifacts();

        } else {

            for(int i=0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);

                if (tab.getText().equals(mReservation.getType())) {
                    tab.select();
                }

                ViewGroup viewGroup = (ViewGroup) ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(i);
                viewGroup.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
            }

            mArtifactView.setText(mReservation.getArtifact());
            mArtifactView.setEnabled(false);

            mDescriptionView.setText(mReservation.getDescription());
            mDescriptionView.setEnabled(false);

            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(getContext())
                    .load(mReservation.getImage1())
                    .apply(options)
                    .into(mImage1View);
            Glide.with(getContext())
                    .load(mReservation.getImage2())
                    .apply(options)
                    .into(mImage2View);
            Glide.with(getContext())
                    .load(mReservation.getImage3())
                    .apply(options)
                    .into(mImage3View);
            Glide.with(getContext())
                    .load(mReservation.getImage4())
                    .apply(options)
                    .into(mImage4View);


            mTimeView.setVisibility(View.GONE);
            mCustomRadioGroup.setVisibility(View.GONE);

            mRequestView.setVisibility(View.GONE);
        }
    }

    private void addActions() {
        mImage1View.setOnClickListener(mImageClickListener);
        mImage2View.setOnClickListener(mImageClickListener);
        mImage3View.setOnClickListener(mImageClickListener);
        mImage4View.setOnClickListener(mImageClickListener);

        mRequestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempt2Request();
            }
        });
    }

    private void populateInfo(){
        // Set Background
        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_settings)
                .placeholder(R.drawable.ic_settings)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(getContext())
                .load(mService.getImage())
                .apply(options)
                .into(mImageServiceView);

        // Set title
        mTitleView.setText(mService.getName());

    }

    private void populateTabs(){
        mTabLayout.addTab(mTabLayout.newTab().setText(arr_text[0]));
        mTabLayout.addTab(mTabLayout.newTab().setText(arr_text[1]));
        mTabLayout.addTab(mTabLayout.newTab().setText(arr_text[2]));
        mTabLayout.getTabAt(1).select();

        for(int i=0; i < mTabLayout.getTabCount(); i++) {
            ViewGroup viewGroup = (ViewGroup) ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(i);

            // Set padding
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) viewGroup
                    .getLayoutParams();
            p.setMargins(10, 0, 10, 0);

            // Set font
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
                    "fonts/AntipastoRegular.otf");
            ((TextView) viewGroup.getChildAt(1)).setTypeface(font);

            viewGroup.requestLayout();
        }
    }

    private void populateArtifacts(){
        final ArrayAdapter<Artifact> adapter = new ArrayAdapter<Artifact>
                (getContext(), android.R.layout.select_dialog_item, mService.getArtifactList());

        //Getting the instance of AutoCompleteTextView
        //mArtifactView.setThreshold(1);//will start working from first character

        mArtifactView.setFocusable(false);
        mArtifactView.setLongClickable(false);
        mArtifactView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        mArtifactView.setAdapter(adapter);

        mArtifactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mArtifactView.showDropDown();
            }
        });
        mArtifactView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >=
                            (mArtifactView.getRight() - mArtifactView
                                    .getCompoundDrawables()[DRAWABLE_RIGHT]
                                    .getBounds()
                                    .width())) {
                        mArtifactView.showDropDown();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void attempt2Request() {

        boolean cancel = false;
        View focusView = null;

        // Reset errors.
        mArtifactView.setError(null);
        mDescriptionView.setError(null);

        String artifact = mArtifactView.getText().toString().replaceAll("\"", "");
        String description = mDescriptionView.getText().toString();

        int actionID = mCustomRadioGroup.getCheckedRadioButtonId();
        //View actionView = mCustomRadioGroup.findViewById(actionID);
        //int idx = mCustomRadioGroup.indexOfChild(actionView);
        mActionView = view.findViewById(actionID);
        String action = mActionView.getText().toString();

        if (TextUtils.isEmpty(description)) {
            mDescriptionView.setError(getString(R.string.error_field_required));
            focusView = mDescriptionView;
            cancel = true;
        }

        if (TextUtils.isEmpty(artifact)) {
            mArtifactView.setError(getString(R.string.error_field_required));
            focusView = mArtifactView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            Reservation reservation = new Reservation();
            reservation.setType(arr_text[mTabLayout.getSelectedTabPosition()]);
            reservation.setService(mService);
            reservation.setArtifact(artifact);
            reservation.setDescription(description);

            reservation.setImageByteList(mImageByteList);
            reservation.setImageFileList(mImageFileList);

            if (actionID == R.id.rb_now) {

                if (Util.isNetworkAvailable(getActivity())) {
                    confirmAddress(getActivity(), reservation);
                } else {
                    Util.longToast(getContext(), getString(R.string.message_network_connectivity_failed));
                }

            } else {
                ScheduleFragment mScheduleFragment = new ScheduleFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("reservation", reservation);
                mScheduleFragment.setArguments(bundle);

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.sub_container, mScheduleFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }


    /**
     * Image action handler
     */
    private void actionPicture() {
        List<String> opts = new ArrayList<>();
        opts.add("Desde la cámara");
        opts.add("Desde la galería");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
        builder.setTitle("Seleccione una opción");
        builder.setItems(opts.toArray(new CharSequence[opts.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    fromCamera();
                } else {
                    fromGallery();
                }

                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void fromCamera() {

        if (mayRequestCamera()) {
            try {
                Uri mImageUri = null;
                String mFileName = "easyfix-" + new Date().getTime() + ".jpg";
                String mState = Environment.getExternalStorageState();

                if (Environment.MEDIA_MOUNTED.equals(mState)) {
                    mFile = new File(Environment.getExternalStorageDirectory(), mFileName);
                } else {
                    mFile = new File(getActivity().getFilesDir(), mFileName);
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    mImageUri = Uri.fromFile(mFile);
                } else {
                    mImageUri = FileProvider.getUriForFile(getContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            mFile);
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                //intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(intent, Util.IMAGE_CAMERA_REQUEST_CODE);
            } catch (Exception e) {
                Log.e(Util.TAG_SERVICE_DETAIL_IMAGE, "Cannot take picture");
                Util.longToast(getContext(), "Hubo un problema, intente nuevamente en un momento");
            }
        }
    }

    private void fromGallery () {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Util.IMAGE_GALLERY_REQUEST_CODE);
    }

    /**
     * Image handler
     */
    private void showImage(Bitmap bitmap) {
        ImageView imageView = null;

        switch (mFilePosition) {
            case 0:
                imageView = mImage1View;
                break;
            case 1:
                imageView = mImage2View;
                break;
            case 2:
                imageView = mImage3View;
                break;
            case 3:
                imageView = mImage4View;
                break;
            default:
                break;
        }

        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    private Bitmap adjustImage(File file) {
        Bitmap bitmap = null;

        try {
            Uri mImageUri = Uri.fromFile(file);
            bitmap = BitmapFactory.decodeFile(mImageUri.getPath());

            FileOutputStream fOut = new FileOutputStream(file);
            ExifInterface exif = new ExifInterface(file.toString());
            String attr = exif.getAttribute(ExifInterface.TAG_ORIENTATION);

            if(attr.equalsIgnoreCase("6")){
                bitmap = rotate(bitmap, 90);
            } else if(attr.equalsIgnoreCase("8")){
                bitmap = rotate(bitmap, 270);
            } else if(attr.equalsIgnoreCase("3")){
                bitmap = rotate(bitmap, 180);
            } else if(attr.equalsIgnoreCase("0")){
                bitmap = rotate(bitmap, 90);
            }

            bitmap = scaleImage(bitmap);

            fOut.flush();
            fOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private Bitmap scaleImage(Bitmap bitmap) {
        int targetWidth = 800;
        int targetHeight = 800; //(int) (bitmap.getHeight() * targetWidth / (double) bitmap.getWidth());

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }

    private Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        // mtx.postRotate(degree);
        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }


    public void createReservationTask(final Context context, final Reservation reservation){
        Util.showLoading(context, getString(R.string.reservation_message_create_request));

        final SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();

        Gson gson = new Gson();

        MediaType json = MediaType.parse("application/json");
        MediaType img = MediaType.parse("image/*");
        MediaType text = MediaType.parse("text/plain");

        RequestBody type = RequestBody.create(text, reservation.getType());
        RequestBody description = RequestBody.create(text, reservation.getDescription());
        RequestBody artifact = RequestBody.create(text, reservation.getArtifact());
        RequestBody address = RequestBody.create(text, "" + reservation.getAddress().getId());
        RequestBody service = RequestBody.create(text, "" + reservation.getService().getId());

        HashMap<String, RequestBody> params = new HashMap<>();

        params.put("type", type);
        params.put("description", description);
        params.put("artifact", artifact);
        params.put("address", address);
        params.put("service", service);

        MultipartBody.Part[] images = new MultipartBody.Part[4];

        for (int i = 0; i< reservation.getImageFileList().length; i++) {
            File file = reservation.getImageFileList()[i];

            if (file != null) {

                RequestBody requestBody = RequestBody.create(img,
                        reservation.getImageByteList()[i]);
                MultipartBody.Part image = MultipartBody.Part.createFormData("image" + (i + 1),
                        file.getName(), requestBody);
                images[i] = image;
            }
        }

        ApiService apiService = ServiceGenerator.createApiService();
        Call<Reservation> call = apiService.createReservation(
                token, params, images[0], images[1], images[2], images[3]);
        call.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_RESERVATION, "Reservation result: success!");
                    Util.longToast(context,
                            getString(R.string.reservation_message_create_response));

                    // Save id of new reservation
                    Reservation r = response.body();
                    //sessionManager.setServiceDetail(r.getId());

                    // Update reservations
                    //NotificationFragment.showPostDetail(r);
                    NotificationFragment.updateReservations();

                    // Return to principal screen
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

            if (displayAlert == null  || !displayAlert.isShowing()) {

                final Address finalAddress = address;
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
                builder.setTitle("Seleccionar dirección");
                builder.setCancelable(true);
                builder.setMessage("¿Desea solicitar el tecnico a su dirección por defecto?")
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

                displayAlert = builder.show();
            }
        } else {
            showAddressPickup(reservation);
        }

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
