package com.easyfixapp.easyfix.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easyfixapp.easyfix.BuildConfig;
import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.SettingsActivity;
import com.easyfixapp.easyfix.adapters.ReservationAdapter;
import com.easyfixapp.easyfix.adapters.ReservationProviderAdapter;
import com.easyfixapp.easyfix.adapters.ViewPagerAdapter;
import com.easyfixapp.easyfix.listeners.OnBackPressListener;
import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.models.Notification;
import com.easyfixapp.easyfix.models.Profile;
import com.easyfixapp.easyfix.models.ProviderReservation;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.models.Settings;
import com.easyfixapp.easyfix.models.User;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.easyfixapp.easyfix.widget.CustomViewPager;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.maps.SupportMapFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MenuFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener {

    public static RelativeLayout mContainerView;
    public static PhotoView mImageZoomView;

    private CustomViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private CircleImageView mMenuSettingView;
    private TextView mTitleView;
    private MenuItem prevMenuItem;
    private View view;
    private File mFile;

    //private static LinearLayout mProfileImageContainerView;
    private static CircleImageView mMenuProfileView;
    private static Button mUpdateProfileImageView;
    private static BottomNavigationView bottomNavigationView;

    private List<ProviderReservation> mProviderReservationList = new ArrayList<>();
    private RecyclerView mProviderReservationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String title = item.getTitle().toString();
            switch (item.getItemId()) {
                case R.id.navigation_service:
                    mViewPager.setCurrentItem(0);
                    mTitleView.setText(title);
                    return true;
                case R.id.navigation_notification:
                    mViewPager.setCurrentItem(1);
                    mTitleView.setText(title);
                    return true;
                case R.id.navigation_account:
                    mViewPager.setCurrentItem(2);
                    mTitleView.setText(title);
                    return true;
            }
            return false;
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        mViewPager = view.findViewById(R.id.view_pager);
        mMenuProfileView = view.findViewById(R.id.img_menu_profile);
        mMenuSettingView = view.findViewById(R.id.img_menu_setting);
        mTitleView = view.findViewById(R.id.txt_toolbar_title);

        mContainerView = view.findViewById(R.id.rl_container);
        //mProfileImageContainerView = view.findViewById(R.id.ll_image_zoom);
        mImageZoomView = view.findViewById(R.id.img_zoom);
        mUpdateProfileImageView = view.findViewById(R.id.btn_update_image);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Update Profile Image */
        setProfileImage();
        setProfileImageZoom();

        mContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideProfileImgeZoom();
            }
        });

        mUpdateProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionPicture();
            }
        });

        mMenuProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getChildFragmentCount() != 0)
                    getActivity().onBackPressed();
                else {
                    showProfileImageZoom();
                }
            }
        });

        /* Settings */
        mMenuSettingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open right drawer
                DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.END);
            }
        });
        NavigationView navigationView = (NavigationView) view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        addNavigationAction(navigationView);

        /* Menu */
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.navigation_service);

        /* View Pager */
        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),
                bottomNavigationView.getMenu().size());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setPagingEnabled(false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                final View mReservationSearchView = view.findViewById(R.id.rl_reservation_search);
                final View mReservationProviderView = view.findViewById(R.id.rl_reservation_provider);
                final View mReservationSuccessView = view.findViewById(R.id.rl_reservation_success);
                if (mReservationSearchView.getVisibility() == View.VISIBLE) {
                    mReservationSearchView.setVisibility(View.GONE);
                } else if (mReservationProviderView.getVisibility() == View.VISIBLE) {
                    mReservationProviderView.setVisibility(View.GONE);
                } else if (mReservationSuccessView.getVisibility() == View.VISIBLE) {
                    mReservationSuccessView.setVisibility(View.GONE);
                } else {
                    if (prevMenuItem != null) {
                        if(position == 0){
                            EventBus.getDefault().post(new Address());
                        }
                        prevMenuItem.setChecked(false);
                    } else {
                        bottomNavigationView.getMenu().getItem(0).setChecked(false);
                    }
                    Log.d("Menu Tab", "onPageSelected: " + position);
                    bottomNavigationView.getMenu().getItem(position).setChecked(true);
                    prevMenuItem = bottomNavigationView.getMenu().getItem(position);

                    setBackPressedIcon();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Prevent pending provider
        checkPendingReservationProviders();
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

    @Override
    public void onResume() {
        super.onResume();

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
        super.onStop();
    }

    /**
     * Retrieve the currently visible Tab Fragment and propagate the onBackPressed callback
     *
     * @return true = if this fragment and/or one of its associates Fragment can handle the backPress
     */
    public boolean onBackPressed() {
        DrawerLayout drawer = view.findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
            return true;
        } else {
            final View mReservationSearchView = view.findViewById(R.id.rl_reservation_search);
            final View mReservationProviderView = view.findViewById(R.id.rl_reservation_provider);
            final View mReservationSuccessView = view.findViewById(R.id.rl_reservation_success);
            if (mReservationSearchView.getVisibility() == View.VISIBLE) {
                mReservationSearchView.setVisibility(View.GONE);
                return true;
            } else if (mReservationProviderView.getVisibility() == View.VISIBLE) {
                mReservationProviderView.setVisibility(View.GONE);
                return true;
            } else if (mReservationSuccessView.getVisibility() == View.VISIBLE) {
                mReservationSuccessView.setVisibility(View.GONE);
                return true;
            } else {
                // currently visible tab Fragment
                OnBackPressListener currentFragment = (OnBackPressListener)
                        mViewPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());

                if (currentFragment != null) {
                    // lets see if the currentFragment or any of its childFragment can handle onBackPressed
                    boolean isBackPressed = currentFragment.onBackPressed();
                    setBackPressedIcon();

                    return isBackPressed;
                }
            }
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        handleSettings(id);

        DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    private void addNavigationAction(NavigationView navigationView) {
        for (int i=0; i<navigationView.getMenu().size(); i++)
            navigationView.getMenu().getItem(i).setActionView(R.layout.item_arrow);
    }

    /**
     *
     * Settings Actions
     *
     **/
    private void handleSettings(int action) {

        Settings settings = null;

        if (action == R.id.nav_policies) {
            settings = Settings.createPolicies(getContext());
        } else if (action == R.id.nav_terms) {
            settings = Settings.createTerms(getContext());
        } else if (action == R.id.nav_help) {
            settings = Settings.createHelp(getContext());
        } else if (action == R.id.nav_logout) {
            logout();
        }

        if (settings != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("settings", settings);
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
        builder.setMessage(R.string.message_logout_dialog)
                .setPositiveButton(R.string.dialog_message_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Util.logout(getContext());
                    }
                })
                .setNegativeButton(R.string.dialog_message_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        builder.show();
    }

    /**
     * Preserve icon in base fragment
     **/
    public int getChildFragmentCount(){
        try {
            Fragment fragment = mViewPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
            return fragment.getChildFragmentManager().getBackStackEntryCount();
        } catch (Exception ignore) {}

        return 0;
    }

    public void setBackPressedIcon() {

        if (getChildFragmentCount() == 0) {
            setProfileImage();
        } else {
            mMenuProfileView.setBorderWidth(0);
            setIcon(R.drawable.ic_navigate_previous, 15);
        }
    }

    public void setIcon(int icon, int padding) {
        mMenuProfileView.setImageDrawable(getResources().getDrawable(icon));
        mMenuProfileView.setPadding(0,0,padding,0);
    }

    private void setProfileImage() {
        // restart border width
        mMenuProfileView.setBorderWidth(2);

        SessionManager sessionManager = new SessionManager(getContext());
        User user = sessionManager.getUser();
        String url = user.getProfile().getImage();

        if (TextUtils.isEmpty(url)) {
            setIcon(R.drawable.ic_empty_profile, 0);
        } else {
            // preserve size
            mMenuProfileView.setPadding(0,0,0,0);

            // Set  profile image
            RequestOptions options = new RequestOptions()
                    .error(R.drawable.ic_empty_profile)
                    .placeholder(R.drawable.ic_empty_profile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(getContext())
                    .load(user.getProfile().getImage())
                    .apply(options)
                    .into(mMenuProfileView);
        }
    }

    private void setProfileImageZoom() {

        SessionManager sessionManager = new SessionManager(getContext());
        User user = sessionManager.getUser();
        String url = user.getProfile().getImage();

        if (!TextUtils.isEmpty(url)) {

            // Set  profile image zoom
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(getContext())
                    .load(user.getProfile().getImage())
                    .apply(options)
                    .into(mImageZoomView);
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

                intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
            ExifInterface exif = new ExifInterface(file.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(file),
                    null, options);

            bitmap = scaleImage(bmp, matrix);

            ByteArrayOutputStream outstudentstreamOutputStream = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    outstudentstreamOutputStream);

        } catch (IOException e) {
            Log.w(Util.TAG_MENU, "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.w(Util.TAG_MENU, "-- OOM Error in setting image");
        }

        return bitmap;
    }

    private Bitmap scaleImage(Bitmap bitmap) {
        int targetWidth = 800;
        int targetHeight = 800;
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }

    private Bitmap scaleImage(Bitmap bmp, Matrix matrix) {
        int targetWidth = 800;
        int targetHeight = 800;
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                bmp.getHeight(), matrix, true);
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

                    // Set profile image
                    RequestOptions options = new RequestOptions()
                            .error(R.drawable.ic_empty_profile)
                            .placeholder(R.drawable.ic_empty_profile)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);

                    Glide.with(getContext())
                            .load(user.getProfile().getImage())
                            .apply(options)
                            .into(mMenuProfileView);

                    try {
                        Glide.with(getContext())
                                .load(user.getProfile().getImage())
                                .apply(options)
                                .into(ProfileFragment.mProfileView);
                    } catch (Exception ignore){}


                    // Set  profile image zoom
                    RequestOptions options2 = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL);

                    Glide.with(getContext())
                            .load(user.getProfile().getImage())
                            .apply(options2)
                            .into(mImageZoomView);

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

    public static void setSelectedTab(int idTab){
        bottomNavigationView.setSelectedItemId(idTab);
    }

    public static void showProfileImageZoom() {
        mContainerView.setVisibility(View.VISIBLE);
    }

    public static boolean hideProfileImgeZoom() {
        if(mContainerView.getVisibility() == View.VISIBLE) {
            mContainerView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @SuppressLint("RestrictedApi")
    public void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BottomNav", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BottomNav", "Unable to change value of shift mode", e);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMenu1Event(Notification notification) {
        setSelectedTab(R.id.navigation_notification);

        if (notification.getObject() != null) {
            int action = notification.getAction();
            if (action == Util.ACTION_CREATE) {
                showReservationSearch((Reservation) notification.getObject());
            } else if (action == Util.ACTION_PROVIDER) {
                showReservationProviders((Reservation) notification.getObject());
            } else if (action == Util.ACTION_UPDATE) {
                showReservationSuccess((Reservation) notification.getObject());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMenu2Event(ProviderReservation pr) {
        onBackPressed();
        reservationTask(pr);
    }

    private int getScreenHeight() {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;

    }

    private void showReservationSearch(Reservation reservation) {
        Random random = new Random();

        final View mReservationSearchView = view.findViewById(R.id.rl_reservation_search);
        mReservationSearchView.setVisibility(View.VISIBLE);

        final View mReservationSearchMsgView = view.findViewById(R.id.rl_reservation_search_msg);

        final TextView mReservationSearchNameView = view.findViewById(R.id.txt_service_name_reservation_search);
        mReservationSearchNameView.setText(reservation.getService().getName());
        mReservationSearchNameView.setTextColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));

        final ImageView mCloseView = view.findViewById(R.id.img_close_reservation_search);
        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReservationSearchView.setVisibility(View.GONE);
                mReservationSearchNameView.setText("");
            }
        });

        final int screenHeight = getScreenHeight();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mReservationSearchMsgView,
                "y", screenHeight, 0.0f);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mCloseView.performClick();
            }
        }, 5000);
    }

    private void showReservationProviders(Reservation reservation) {

        SessionManager sessionManager = new SessionManager(getContext());
        if (sessionManager.isNotificationProvider()) {
            sessionManager.clearPendingReservation();
        }

        final View mReservationSearchView = view.findViewById(R.id.rl_reservation_provider);

        if (mReservationSearchView.getVisibility() == View.GONE) {
            mReservationSearchView.setVisibility(View.VISIBLE);

            mProviderReservationView = view.findViewById(R.id.rv_provider_reservation_provider);

            final View mReservationSearchMsgView = view.findViewById(R.id.rl_reservation_provider_msg);

            ReservationProviderAdapter mReservationAdapter = new ReservationProviderAdapter(
                    getContext(), mProviderReservationList);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mProviderReservationView.setLayoutManager(mLayoutManager);
            mProviderReservationView.setItemAnimator(new DefaultItemAnimator());
            mProviderReservationView.setAdapter(mReservationAdapter);

            final int screenHeight = getScreenHeight();
            ObjectAnimator animator = ObjectAnimator.ofFloat(mReservationSearchView,
                    "y", screenHeight, 0.0f);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        }

        providerReservationTask(reservation.getId());
    }

    private void showReservationSuccess(Reservation reservation) {
        final View mReservationSuccessView = view.findViewById(R.id.rl_reservation_success);
        if (mReservationSuccessView.getVisibility() == View.GONE) {
            mReservationSuccessView.setVisibility(View.VISIBLE);

            Random random = new Random();
            final TextView mReservationSuccessNameView = view.findViewById(R.id.txt_service_name_reservation_success);
            mReservationSuccessNameView.setText(reservation.getService().getName());
            mReservationSuccessNameView.setTextColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));

            final TextView mReservationSuccessMsgView = view.findViewById(R.id.txt_msg_reservation_success);
            mReservationSuccessMsgView.setText(
                    reservation.isScheduled()? "Tu servicio se encuentra agendado!" : "Tu técnico esta en camino!");

            final RatingBar mReservationSuccessRate = view.findViewById(R.id.rb_calification_reservation_success);
            mReservationSuccessRate.setRating(reservation.getProvider().getScore());

            final CircleImageView mProviderImageView = view.findViewById(R.id.img_profile_reservation_success);
            if (reservation.getProvider().getProfile() != null) {
                Glide.with(getContext())
                        .load(reservation.getProvider().getProfile().getImage())
                        .apply(new RequestOptions()
                                .error(R.drawable.ic_empty_profile)
                                .placeholder(R.drawable.ic_empty_profile)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(mProviderImageView);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mProviderImageView.setColorFilter(getContext().getColor(R.color.white));
                } else {
                    mProviderImageView.setColorFilter(getContext().getResources().getColor(R.color.white));
                }
            }

            final ImageView mCloseView = view.findViewById(R.id.img_close_reservation_success);
            mCloseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mReservationSuccessView.setVisibility(View.GONE);
                    mReservationSuccessNameView.setText("");
                    mReservationSuccessMsgView.setText("");
                    mReservationSuccessRate.setRating(0);
                    mProviderImageView.setImageDrawable(null);
                }
            });

            final int screenHeight = getScreenHeight();
            ObjectAnimator animator = ObjectAnimator.ofFloat(mReservationSuccessView,
                    "y", screenHeight, 0.0f);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();


            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mCloseView.performClick();
                }
            }, 5000);
        }
    }

    private void providerReservationTask(int id){
        final View childView = view.findViewById(R.id.rl_reservation_provider);
        Util.showProgress(getContext(), mProviderReservationView, childView, true);

        SessionManager sessionManager = new SessionManager(getContext());
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();

        Call<List<ProviderReservation>> call = apiService.getProviders(id, token);
        call.enqueue(new Callback<List<ProviderReservation>>() {
            @Override
            public void onResponse(Call<List<ProviderReservation>> call, Response<List<ProviderReservation>> response) {
                Util.showProgress(getContext(), mProviderReservationView, childView, false);

                if (response.isSuccessful()) {
                    Log.i(Util.TAG_MENU, "Provider Reservation result: success!");

                    List<ProviderReservation> providerReservationList = response.body();
                    if (!providerReservationList.isEmpty()) {
                        mProviderReservationList.clear();

                        for (ProviderReservation pr : providerReservationList){
                            mProviderReservationList.add(pr);
                        }
                        mProviderReservationView.getAdapter().notifyDataSetChanged();

                    } else {
                        Util.showMessage(mProviderReservationView, childView, "Buscando técnicos");
                    }
                } else {
                    Log.i(Util.TAG_MENU, "Provider Reservation result: " + response.toString());
                    Util.showMessage(mProviderReservationView, childView,
                            getString(R.string.message_service_server_failed));
                }
            }

            @Override
            public void onFailure(Call<List<ProviderReservation>> call, Throwable t) {
                Util.showProgress(getActivity(), mProviderReservationView, childView, false);
                Util.showMessage(mProviderReservationView, childView,
                        getString(R.string.message_network_local_failed));
            }
        });
    }

    private void reservationTask(ProviderReservation pr) {
        Util.showLoading(getContext(), "Asignando técnico....");

        SessionManager sessionManager = new SessionManager(getContext());
        ApiService apiService = ServiceGenerator.createApiService();
        String token = sessionManager.getToken();

        HashMap<String, Object> params = new HashMap<>();
        params.put("provider", pr.getProvider().getId());
        params.put("status", Reservation.Assigned);

        Call<Reservation> call = apiService.updateReservation(
                pr.getReservation().getId(), token, params);
        call.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_MENU, "Reservation result: success!");

                    Reservation reservation = response.body();
                    if (reservation != null) {
                        EventBus.getDefault().post(new Notification(Util.ACTION_UPDATE, reservation));
                    } else {
                        Util.longToast(getContext(),
                                getString(R.string.message_service_server_empty));
                    }
                } else {
                    Log.i(Util.TAG_MENU, "Reservation result: " + response.toString());
                    Util.longToast(getContext(),
                            getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Util.longToast(getContext(),
                        getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }

    private void checkPendingReservationProviders() {

        SessionManager sessionManager = new SessionManager(getContext());

        if (sessionManager.isNotificationProvider()) {
            setSelectedTab(R.id.navigation_notification);

            int idReservation = sessionManager.getReservationId();
            if (idReservation != 0) {
                Reservation reservation = new Reservation();
                reservation.setId(idReservation);
                showReservationProviders(reservation);
            }

            sessionManager.clearPendingReservation();
        }
    }
}