package tw.com.agrinote.activity;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.wangyuwei.flipshare.FlipShareView;
import me.wangyuwei.flipshare.ShareItem;
import tw.com.agrinote.Decoration.DividerItemDecoration;
import tw.com.agrinote.R;
import tw.com.agrinote.adapter.LogContentAdapter;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.fragment.FarmingDialogFragment;
import tw.com.agrinote.model.Answer;
import tw.com.agrinote.model.Crop;
import tw.com.agrinote.model.DateTime;
import tw.com.agrinote.model.Farm;
import tw.com.agrinote.model.WorkingLog;
import tw.com.agrinote.utils.PermissionUtils;

/**
 * Created by orc59 on 2017/11/6.
 */

public class AddLogActivity extends AppCompatActivity implements
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "AddLogActivity";

    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_SELECT_PICTURE = 1;
    private static final int REQ_CAMERA_PERMISSION = 0;
    private static final int REQ_SD_CARD_PERMISSION = 1;

    private ImageView imgLog;
    private TextView recordDate;


    private RecyclerView mRecyclerView;
    private LogContentAdapter mAdapter;
    private DividerItemDecoration mDivider;

    private FrameLayout btnAddRecord;
    private FrameLayout btnSave;
    private FrameLayout btnCancel;
    private FrameLayout btnDelete;

    private DateTime mDateTime;

    private Crop mCrop;
    private Farm mFarm;
    private WorkingLog mWorkingLog;
    private Answer mAnswer;
    private FarmingDialogFragment mFarmingDialog;
    private Context mContext;
    private AgrinoteDBHelper mDB;
    private String mCurrentPhotoPath;

    private void getWorkingLog() {
        String json = getIntent().getStringExtra("working_log");
        Log.i(TAG, "" + json);

        Gson gson = new Gson();
        if (null != json && !"".equals(json)) {
            mWorkingLog = gson.fromJson(json, WorkingLog.class);
            mAnswer = gson.fromJson(mWorkingLog.getWorking(), Answer.class);
            btnDelete.setVisibility(View.VISIBLE);
            if(null == mAnswer){
                mAnswer = new Answer();
            }
            Log.i(TAG, mWorkingLog.toString() + ", " + mAnswer.toString());
            mCrop = mDB.getCropsById(mWorkingLog.getCropId());
            mFarm = mDB.getFarmById(mCrop.getFarmId());
        } else {
            // error msg & back to login page
        }
    }

    private void getCrop() {
        String json = getIntent().getStringExtra("crop");
        Log.i(TAG, "" + json);

        Gson gson = new Gson();
        if (null != json && !"".equals(json)) {
            mCrop = gson.fromJson(json, Crop.class);
            Log.i(TAG, mCrop.toString());
            btnDelete.setVisibility(View.GONE);
            mFarm = mDB.getFarmById(mCrop.getFarmId());
        } else {
            // error msg & back to login page
        }
    }

    private void saveLog() {
        Gson gson = new Gson();
        WorkingLog tmpLog = new WorkingLog();
        tmpLog.setCropId(mCrop.getId());
        tmpLog.setPicPath(mCurrentPhotoPath);
        tmpLog.setRecordDate(mDateTime.getDateTime());
        mAnswer = mFarmingDialog.getAnswer();
        if(null == mAnswer){
            mAnswer = new Answer();
        }
        tmpLog.setWorking(gson.toJson(mAnswer));

        long id = mDB.createWorkingLog(tmpLog);
        if (id != 0) {
            onBackPressed();
        } else {
            Log.i(TAG, "error on save log");
        }
    }

    private void updateLog(){
        mWorkingLog.setPicPath(mCurrentPhotoPath);
        mWorkingLog.setRecordDate(recordDate.getText().toString());
        Answer tmpAns = mFarmingDialog.getAnswer();
        Gson gson = new Gson();
        if(null != tmpAns){
            mWorkingLog.setWorking(gson.toJson(tmpAns));
        }

        long id = mDB.updateWorkingLog(mWorkingLog);
        if (id != 0) {
            onBackPressed();
        } else {
            Log.i(TAG, "error on update log");
        }
    }

    private void deleteLog() {

        long id = mDB.deleteWorkingLog(mWorkingLog);
        if (id != 0) {
            onBackPressed();
        } else {
            Log.i(TAG, "error on delete log");
        }
    }


    private void showQuestionCard() {
        mFarmingDialog = new FarmingDialogFragment();
        mFarmingDialog.setAdapter(mAdapter);
        mFarmingDialog.setCrop(mCrop);
        mFarmingDialog.setFarm(mFarm);
        mFarmingDialog.show(getFragmentManager(), "FarmingDialog");
    }


    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                AddLogActivity.this,
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
                AddLogActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.enableSeconds(true);
        tpd.setAccentColor("#ff4081");
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
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "tw.com.agrinote.fileprovider",
                                photoFile);
                        Log.i(TAG, photoURI.toString());
                        Log.i(TAG, mCurrentPhotoPath);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQ_TAKE_PICTURE);
                    }
                }
            }
        }
    }

    private void setPic(ImageView img, String imgURI) {
        // Get the dimensions of the View
        int targetW = img.getWidth();
        int targetH = img.getHeight();

        Log.i(TAG, "ImageView :: " + targetW + " x " + targetW);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgURI, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        Log.i(TAG, "photo :: " + photoW + " x " + photoH);
        if (targetW == 0) {
            targetW = 100;
        }
        if (targetH == 0) {
            targetH = 100;
        }

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imgURI, bmOptions);
        img.setPadding(0, 0, 0, 0);
        //img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setImageBitmap(bitmap);
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

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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

    private void setLogContent(){
        mAdapter.refreshContent(mAnswer);
        mCurrentPhotoPath = mWorkingLog.getPicPath();
        setPic(imgLog, mWorkingLog.getPicPath());
        recordDate.setText(mWorkingLog.getRecordDate());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_log);

        mContext = this;
        mDB = AgrinoteDBHelper.getInstance(this);

        imgLog = (ImageView) findViewById(R.id.img_log);
        recordDate = (TextView) findViewById(R.id.record_date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mDateTime = new DateTime();
        recordDate.setText(sdf.format(new Date()));

        mAnswer = new Answer();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mDivider = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST);
        mRecyclerView.addItemDecoration(mDivider);

        mAdapter = new LogContentAdapter(mAnswer, 15, true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        btnAddRecord = (FrameLayout) findViewById(R.id.btn_add_record);
        btnSave = (FrameLayout) findViewById(R.id.btn_save);
        btnCancel = (FrameLayout) findViewById(R.id.btn_cancel);
        btnDelete = (FrameLayout) findViewById(R.id.btn_delete);

        mFarmingDialog = new FarmingDialogFragment();
        getCrop();
        getWorkingLog();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(null != mWorkingLog ? "修改田間日誌" : "新增田間日誌");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(null != mWorkingLog){
            setLogContent();
        }


        imgLog.setOnClickListener(this);
        recordDate.setOnClickListener(this);
        btnAddRecord.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    private void showFlipMenu() {
        FlipShareView share = new FlipShareView.Builder(AddLogActivity.this, imgLog)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "requestCode ::  " + requestCode);
        if (resultCode == RESULT_OK) {
            if (null != data && null != data.getData()) {
                Log.i(TAG, data.getData().toString());
            }

            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    setPic(imgLog, mCurrentPhotoPath);
                    break;
                case REQ_SELECT_PICTURE:
                    Uri selectedImageUri = data.getData();

                    Log.i(TAG, "selectedImageUri :: " + selectedImageUri.toString());
                    mCurrentPhotoPath = getRealPathFromURI_API19(mContext, selectedImageUri);
                    Log.i(TAG, "selected image path :: " + mCurrentPhotoPath);
                    setPic(imgLog, mCurrentPhotoPath);
                    break;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_log:
                showFlipMenu();
                break;

            case R.id.record_date:
                showDatePicker();
                break;

            case R.id.btn_add_record:
                showQuestionCard();
                break;

            case R.id.btn_save:
                if(null == mWorkingLog){
                    saveLog();
                }else{
                    updateLog();
                }

                break;

            case R.id.btn_cancel:
                break;

            case R.id.btn_delete:
                deleteLog();
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Log.i(TAG, year + "-" + monthOfYear + "-" + dayOfMonth);
        mDateTime = new DateTime();
        mDateTime.setYear(year);
        mDateTime.setMonth(monthOfYear + 1);
        mDateTime.setDay(dayOfMonth);
        showTimePicker();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Log.i(TAG, hourOfDay + ":" + minute + ":" + second);
        mDateTime.setHour(hourOfDay);
        mDateTime.setMinute(minute);
        mDateTime.setSecond(second);
        recordDate.setText(mDateTime.getDateTime());
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
        } else {
            Log.i(TAG, "expection permission request :: " + requestCode);
            return;
        }
    }
}
