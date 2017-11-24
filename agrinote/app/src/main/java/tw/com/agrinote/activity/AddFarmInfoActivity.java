package tw.com.agrinote.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.wangyuwei.flipshare.FlipShareView;
import me.wangyuwei.flipshare.ShareItem;
import tw.com.agrinote.R;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.fragment.WorkaroundMapFragment;
import tw.com.agrinote.model.DateTime;
import tw.com.agrinote.model.Farm;
import tw.com.agrinote.model.User;
import tw.com.agrinote.utils.PermissionUtils;

/**
 * Created by orc59 on 2017/11/3.
 */

public class AddFarmInfoActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        OnRequestPermissionsResultCallback,
        View.OnClickListener,
        LocationListener,
        GoogleMap.OnMarkerDragListener {

    private static final String TAG = "AddFarmInfoActivity";


    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;

    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_SELECT_PICTURE = 1;
    private static final int REQ_CAMERA_PERMISSION = 0;
    private static final int REQ_SD_CARD_PERMISSION = 1;
    private static final int REQ_LOCATION_PERMISSION = 3;

    private boolean mPermissionDenied = false;

    private LocationManager mLocationManager;

    private LatLng mFarmLatLng;

    private String mCurrentImgPath;
    private DateTime mDateTime;
    private Farm mFarm;
    private GoogleMap mMap;
    private User mUser;
    private Context mContext;
    private AgrinoteDBHelper mDB;

    private ScrollView mScrollView;
    private ImageView mFarmImg;
    private EditText mFarmName;
    private EditText mFarmLandId;
    private TextView mRecordDate;

    private TextView mLatLng;
    private EditText mAddress;

    private FrameLayout btnSave;
    private FrameLayout btnCancel;
    private FrameLayout btnDelete;

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                AddFarmInfoActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);

        dpd.setAccentColor("#ff4081");
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                AddFarmInfoActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.setAccentColor("#ff4081");
        tpd.enableSeconds(true);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    private void toGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), REQ_SELECT_PICTURE);
    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, REQ_CAMERA_PERMISSION,
                    Manifest.permission.CAMERA, true);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location is missing.
                PermissionUtils.requestPermission(this, REQ_SD_CARD_PERMISSION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        mCurrentImgPath = photoFile.getAbsolutePath();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "tw.com.agrinote.fileprovider",
                                photoFile);
                        Log.i(TAG, photoURI.toString());
                        Log.i(TAG, mCurrentImgPath);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQ_TAKE_PICTURE);
                    }
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        Log.i(TAG, storageDir.getAbsolutePath());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private void setPic(ImageView img, String imgURI) {
        // Get the dimensions of the View
        int targetW = img.getWidth();
        int targetH = img.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgURI, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imgURI, bmOptions);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setImageBitmap(bitmap);
    }

    private String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    private void showFlipMenu() {
        FlipShareView share = new FlipShareView.Builder(AddFarmInfoActivity.this, mFarmImg)
                .addItem(new ShareItem("相機", Color.WHITE, 0xff43549C, BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_camera)))
                .addItem(new ShareItem("圖片庫", Color.WHITE, 0xff4999F0, BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_gallery)))
                .setBackgroundColor(0x60000000)
                .setItemDuration(500)
                .setSeparateLineColor(0x30000000)
                .setAnimType(FlipShareView.TYPE_HORIZONTAL)
                .create();
        share.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        takePicture();
                        break;
                    case 1:
                        toGallery();
                        break;
                }
            }

            @Override
            public void dismiss() {

            }
        });
    }


    private void saveFarm() {
        mFarm = new Farm();
        mFarm.setName(mFarmName.getText().toString());
        mFarm.setLandId(mFarmLandId.getText().toString());
        mFarm.setStartDate(mRecordDate.getText().toString());
        mFarm.setLatitude(mFarmLatLng.latitude);
        mFarm.setLongtiude(mFarmLatLng.longitude);
        mFarm.setAddress(mAddress.getText().toString());
        mFarm.setPicPath("");
        mFarm.setUserId(mUser.getId());

        long id = mDB.createFarm(mFarm);
        Log.i(TAG, "farm id ::" + id);
        onBackPressed();
    }

    private void getUserInfo() {
        String json = getIntent().getStringExtra("user");
        Gson gson = new Gson();
        Log.i(TAG, json);
        if (null != json && !"".equals(json)) {
            mUser = gson.fromJson(json, User.class);
            Log.i(TAG, mUser.toString());
        } else {
            // error msg & back to login page
        }
    }

    private void getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled)) {
            // Snackbar.make(this, R.string.error_location_provider, Snackbar.LENGTH_INDEFINITE).show();
        } else {
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, this);
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, this);
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }

        if (location != null) {
            Log.d(TAG, String.format("getCurrentLocation(%f, %f)", location.getLatitude(),
                    location.getLongitude()));
            drawMarker(location);
            setPositionInTextView(location);
            setAddressInEditText(location);
        }
    }

    private void setPositionInTextView(Location location) {
        mFarmLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        setPositionImp(location.getLatitude(), location.getLongitude());
    }

    private void setPositionInTextView(LatLng location) {
        mFarmLatLng = location;
        setPositionImp(location.latitude, location.longitude);
    }

    private void setPositionImp(double lat, double lng) {
        mLatLng.setText(String.format("[ %f, %f ]", lat, lng));
    }

    private void setAddressInEditText(LatLng location) {
        setAddressImp(location.latitude, location.longitude);
    }

    private void setAddressInEditText(Location location) {
        setAddressImp(location.getLatitude(), location.getLongitude());
    }

    private void setAddressImp(double lat, double lng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            mAddress.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawMarker(Location location) {
        if (mMap != null) {
            mMap.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(gps)
                    //.title(String.format("農地定位 [ %f, %f ]",location.getLatitude(), location.getLongitude()))
                    .draggable(true));
            marker.hideInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 12));

        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_farm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("農田設定");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getUserInfo();
        mContext = this;
        mDB = AgrinoteDBHelper.getInstance(mContext);

        mFarmName = (EditText) findViewById(R.id.farm_name);
        mFarmLandId = (EditText) findViewById(R.id.farm_land_id);
        mRecordDate = (TextView) findViewById(R.id.record_date);
        mRecordDate.setOnClickListener(this);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mRecordDate.setText(sdf.format(new Date()));

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);
        WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                mScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
        mLatLng = (TextView) findViewById(R.id.latlng);
        mLatLng.setOnClickListener(this);
        mAddress = (EditText) findViewById(R.id.address);

        mFarmImg = (ImageView) findViewById(R.id.farm_img);
        mFarmImg.setOnClickListener(this);

        btnSave = (FrameLayout) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        btnCancel = (FrameLayout) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);

        btnDelete = (FrameLayout) findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMarkerDragListener(this);
        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, REQ_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mDateTime = new DateTime();
        mDateTime.setYear(year);
        mDateTime.setMonth(monthOfYear + 1);
        mDateTime.setDay(dayOfMonth);
        showTimePicker();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        mDateTime.setHour(hourOfDay);
        mDateTime.setMinute(minute);
        mDateTime.setSecond(second);
        mRecordDate.setText(mDateTime.getDateTime());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "requestCode ::  " + requestCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    setPic(mFarmImg, mCurrentImgPath);
                    break;
                case REQ_SELECT_PICTURE:
                    Uri selectedImageUri = data.getData();
                    Log.i(TAG, "selectedImageUri :: " + selectedImageUri.toString());
                    mCurrentImgPath = getRealPathFromURI_API19(mContext, selectedImageUri);
                    Log.i(TAG, "selected image path :: " + mCurrentImgPath);
                    setPic(mFarmImg, mCurrentImgPath);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQ_CAMERA_PERMISSION) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.CAMERA)) {
                // Enable the my location layer if the permission has been granted.
                takePicture();
            } else {
                // Display the missing permission error dialog when the fragments resume.

            }
        } else if (requestCode == REQ_SD_CARD_PERMISSION) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Enable the my location layer if the permission has been granted.
                takePicture();
            } else {
                // Display the missing permission error dialog when the fragments resume.
            }
        } else if (requestCode == REQ_LOCATION_PERMISSION) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Enable the my location layer if the permission has been granted.
                enableMyLocation();
            } else {
                // Display the missing permission error dialog when the fragments resume.
                mPermissionDenied = true;
            }
        } else {
            Log.i(TAG, "expection permission request :: " + requestCode);
            return;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.farm_img:
                showFlipMenu();
                break;
            case R.id.record_date:
                showDatePicker();
                break;
            case R.id.latlng:
                getCurrentLocation();
                break;
            case R.id.btn_save:
                saveFarm();
                break;
            case R.id.btn_cancel:
                Toast.makeText(this, "cancel  click", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_delete:
                Toast.makeText(this, "delete_item", Toast.LENGTH_LONG).show();

                break;
        }
    }

    // LocationManager block
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d(TAG, String.format("%f, %f", location.getLatitude(), location.getLongitude()));
            drawMarker(location);
            mLocationManager.removeUpdates(this);
        } else {
            Log.d(TAG, "Location is null");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    // Loaction Manager block end

    // MarkerDragListener block
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng position = marker.getPosition();
        setPositionInTextView(position);
        setAddressInEditText(position);
    }

    //MarkerDragListener Block end


}
