package com.easyfixapp.easyfix.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easyfixapp.easyfix.BuildConfig;
import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.SettingsActivity;
import com.easyfixapp.easyfix.adapters.ViewPagerAdapter;
import com.easyfixapp.easyfix.listeners.OnBackPressListener;
import com.easyfixapp.easyfix.models.Profile;
import com.easyfixapp.easyfix.models.Settings;
import com.easyfixapp.easyfix.models.User;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.easyfixapp.easyfix.widget.CustomViewPager;
import com.github.chrisbanes.photoview.PhotoView;

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

public class MenuFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener {

    public static RelativeLayout mContainerView;
    public static PhotoView mImageZoomView;
    public static View mStartAnimationView;

    private CustomViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private CircleImageView mMenuSettingView;
    private TextView mTitleView;
    private MenuItem prevMenuItem;
    private View view;
    private File mFile;

    private static LinearLayout mProfileImageContainerView;
    private static CircleImageView mMenuProfileView;
    private static Rect startBounds;
    private static float startScaleFinal;
    private static Button mUpdateProfileImageView;
    private static Animator mCurrentAnimator;
    private static BottomNavigationView bottomNavigationView;
    //private static int mContainerColor;

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
        mProfileImageContainerView = view.findViewById(R.id.ll_image_zoom);
        mImageZoomView = view.findViewById(R.id.img_zoom);
        mUpdateProfileImageView = view.findViewById(R.id.btn_update_image);

        //mContainerColor = getContext().getResources().getColor(R.color.black_35);

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
                hideProfileImgeZoom(null);
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
                    mStartAnimationView = v;
                    showProfileImageZoom(mStartAnimationView);
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
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("Menu Tab", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

                setBackPressedIcon();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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

    public static void showProfileImageZoom(final View mStartAnimationView) {

        //mContainerView.setBackgroundColor(mContainerColor);
        mContainerView.setVisibility(View.VISIBLE);

        /*
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        mMenuProfileView.getGlobalVisibleRect(startBounds);
        mContainerView.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        //mStartAnimationView.setAlpha(0f);
        mContainerView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        mImageZoomView.setPivotX(0f);
        mImageZoomView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(mImageZoomView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(mImageZoomView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(mImageZoomView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(mImageZoomView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(400);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        startScaleFinal = startScale;*/
    }

    public static boolean hideProfileImgeZoom(final View mStartAnimationView) {


        if(mContainerView.getVisibility() == View.VISIBLE) {

            mContainerView.setVisibility(View.GONE);
            //mContainerView.setBackgroundColor(mContainerColor);

            /*
            if (mCurrentAnimator != null) {
                mCurrentAnimator.cancel();
            }

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            AnimatorSet set = new AnimatorSet();
            set.play(ObjectAnimator
                    .ofFloat(mImageZoomView, View.X, startBounds.left))
                    .with(ObjectAnimator
                            .ofFloat(mImageZoomView,
                                    View.Y, startBounds.top))
                    .with(ObjectAnimator
                            .ofFloat(mImageZoomView,
                                    View.SCALE_X, startScaleFinal))
                    .with(ObjectAnimator
                            .ofFloat(mImageZoomView,
                                    View.SCALE_Y, startScaleFinal));
            set.setDuration(400);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //mStartAnimationView.setAlpha(1f);
                    mContainerView.setVisibility(View.GONE);
                    mCurrentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    //mStartAnimationView.setAlpha(1f);
                    mContainerView.setVisibility(View.GONE);
                    mCurrentAnimator = null;
                }
            });
            set.start();
            mCurrentAnimator = set;
            */

            return true;
        }

        return false;
    }
}