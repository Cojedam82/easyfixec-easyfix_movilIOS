package com.easyfixapp.easyfix.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easyfixapp.easyfix.BuildConfig;
import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.ProfileUpdateActivity;
import com.easyfixapp.easyfix.models.Profile;
import com.easyfixapp.easyfix.models.User;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by julio on 09/10/17.
 */

public class ProfileFragment extends RootFragment{

    private EditText mFirstNameView, mLastNameView, mEmailView,
            mPasswordView, mPhoneView, mPaymentMethod;
    private CircleImageView mProfileView;
    private View view;

    private File mFile;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        mProfileView = view.findViewById(R.id.img_profile);
        mFirstNameView = view.findViewById(R.id.txt_first_name);
        mLastNameView = view.findViewById(R.id.txt_last_name);
        mEmailView = view.findViewById(R.id.txt_email);
        mPhoneView = view.findViewById(R.id.txt_phone);
        mPasswordView = view.findViewById(R.id.txt_password);
        mPaymentMethod = view.findViewById(R.id.txt_payment_method);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setProfileImage();
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
            mFile = new File(mImageUri.getPath());

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

                if (Util.isNetworkAvailable(getContext())) {

                    Util.showLoading(getContext(),
                            getString(R.string.acount_message_update_request));
                    updateImageProfileTask(byteArray);
                } else {
                    Util.longToast(getContext(),
                            getString(R.string.message_network_connectivity_failed));
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void init() {
        SessionManager sessionManager = new SessionManager(getContext());
        User user = sessionManager.getUser();
        Profile profile = user.getProfile();

        mProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionPicture();
            }
        });

        final String firstName = user.getFirstName();
        mFirstNameView.setText(firstName);
        mFirstNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(getResources().getString(R.string.prompt_first_name), firstName);
            }
        });

        final String lastName = user.getLastName();
        mLastNameView.setText(lastName);
        mLastNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(getResources().getString(R.string.prompt_last_name), lastName);
            }
        });


        final String email = user.getEmail();
        mEmailView.setText(email);
        mEmailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(getResources().getString(R.string.prompt_email), email);
            }
        });

        final String phone = profile.getPhone();
        mPhoneView.setText(phone);
        mPhoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(getResources().getString(R.string.prompt_phone), phone);
            }
        });


        mPasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(getResources().getString(R.string.prompt_password), "");
            }
        });

        final int paymentMethod = profile.getPaymentMethod();
        mPaymentMethod.setText(profile.getPaymentMethodString());
        mPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(getResources().getString(R.string.prompt_payment_method),
                        String.valueOf(paymentMethod));
            }
        });
    }

    private void update(String field, String value) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("field", field);
        bundle.putSerializable("value", value);

        Intent intent = new Intent(getActivity(), ProfileUpdateActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setProfileImage() {
        SessionManager sessionManager = new SessionManager(getContext());
        User user = sessionManager.getUser();
        String url = user.getProfile().getImage();

        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_empty_profile)
                .placeholder(R.drawable.ic_empty_profile)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        if (TextUtils.isEmpty(url)) {
            mProfileView.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty_profile));
        } else {
            Glide.with(getContext())
                    .load(user.getProfile().getImage())
                    .apply(options)
                    .into(mProfileView);
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
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Util.IMAGE_GALLERY_REQUEST_CODE);
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


    private void updateImageProfileTask(byte[] byteArray){

        final SessionManager sessionManager = new SessionManager(getContext());
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), byteArray);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", mFile.getName(), reqFile);

        Call<User> call = apiService.updateImageProfile(token, body);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                if (response.isSuccessful()) {
                    Log.i(Util.TAG_PROFILE, "Update result: success!");

                    User user = response.body();
                    sessionManager.saveUser(user);

                    // Set image
                    RequestOptions options = new RequestOptions()
                            .error(R.drawable.ic_empty_profile)
                            .placeholder(R.drawable.ic_empty_profile)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);

                    Activity activity = getActivity();
                    CircleImageView mMenuProfileView = activity.findViewById(R.id.img_menu_profile);

                    Glide.with(getContext())
                            .load(user.getProfile().getImage())
                            .apply(options)
                            .into(mMenuProfileView);

                    Glide.with(getContext())
                            .load(user.getProfile().getImage())
                            .apply(options)
                            .into(mProfileView);

                    Util.longToast(getContext(), getString(R.string.acount_message_update_response));
                } else {
                    // Error response, no access to resource?
                    Log.i(Util.TAG_PROFILE, "Update result: " + response.toString());
                    Util.longToast(getContext(),
                            getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                // something went completely south (like no internet connection)
                Log.i(Util.TAG_PROFILE, "Update result: failed, " + t.getMessage());
                Util.longToast(getContext(),
                        getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }
}
