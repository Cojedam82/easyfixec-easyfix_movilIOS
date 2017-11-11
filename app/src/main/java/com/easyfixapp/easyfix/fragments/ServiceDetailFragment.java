package com.easyfixapp.easyfix.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.Artifact;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.util.Util;
import com.easyfixapp.easyfix.widget.CustomRadioGroup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private TextView mTitleView;
    private EditText mDescriptionView;
    private View view;
    private RadioButton mActionView;
    private Button mRequestView;

    private Service mService = null;
    private List<File> mImageFileList;
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
        mService = (Service) getArguments().getSerializable("service");

        mTitleView = view.findViewById(R.id.txt_service_name);
        mArtifactView = view.findViewById(R.id.txt_artifact);
        mDescriptionView = view.findViewById(R.id.txt_description);
        mImageServiceView = view.findViewById(R.id.img_service);
        mTabLayout = view.findViewById(R.id.tabs);
        mImage1View = view.findViewById(R.id.img_detail_1);
        mImage2View = view.findViewById(R.id.img_detail_2);
        mImage3View = view.findViewById(R.id.img_detail_3);
        mImage4View = view.findViewById(R.id.img_detail_4);
        mCustomRadioGroup = view.findViewById(R.id.rg_actions);
        mRequestView = view.findViewById(R.id.btn_request);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        mImageFileList = new ArrayList<File>();

        // Init radiobuton
        mCustomRadioGroup.check(R.id.rb_now);

        if (mService != null) {

            // Add actions
            addActions();

            // Populate initial info
            populateInfo();

            // Populate top tabs
            populateTabs();

            // Populate artifacts
            populateArtifacts();

        } else {
            Util.longToast(getContext(),
                    getResources().getString(R.string.message_service_detail_empty));
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
                .load(Util.BASE_URL + mService.getImage())
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
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
            p.setMargins(10, 0, 10, 0);

            // Set font
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AntipastoRegular.otf");
            ((TextView) viewGroup.getChildAt(1)).setTypeface(font);

            viewGroup.requestLayout();
        }
    }

    private void populateArtifacts(){
        ArrayAdapter<Artifact> adapter = new ArrayAdapter<Artifact>
                (getContext(), android.R.layout.select_dialog_item, mService.getArtifactList());

        //Getting the instance of AutoCompleteTextView
        mArtifactView.setThreshold(1);//will start working from first character
        mArtifactView.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView

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

        String artifact = mArtifactView.getText().toString();
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
            /*
            if (Util.isNetworkAvailable(getApplicationContext())) {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                Util.showLoading(LoginActivity.this, getString(R.string.message_login_request));

                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                loginTask(params);
            } else {
                Util.longToast(getApplicationContext(), getString(R.string.message_network_connectivity_failed));
            }*/
        }
    }


    /**
     * Image action handler
     */
    private void actionPicture() {
        List<String> opts = new ArrayList<>();
        opts.add("Desde la cámara");
        opts.add("Desde la galería");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Seleccion una opción");
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
                    mImageUri = Uri.fromFile(mFile);
                } else {
                    mFile = new File(getActivity().getFilesDir(), mFileName);
                    mImageUri = Uri.fromFile(mFile);
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
            /*
            Glide.with(getContext())
                    .load(bitmap)
                    .into(imageView);*/
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

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getActivity().getContentResolver().query(uri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
