package tw.com.agrinote.activity;

import android.Manifest;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.wangyuwei.flipshare.FlipShareView;
import me.wangyuwei.flipshare.ShareItem;
import tw.com.agrinote.R;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.model.Crop;
import tw.com.agrinote.model.CropItem;
import tw.com.agrinote.model.DateTime;
import tw.com.agrinote.model.Farm;
import tw.com.agrinote.utils.PermissionUtils;

/**
 * Created by orc59 on 2017/11/6.
 */

public class AddCropInfoActivity extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        View.OnFocusChangeListener {

    private static final String TAG = "AddCropInfoActivity";
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_SELECT_PICTURE = 1;
    private static final int REQ_CAMERA_PERMISSION = 0;
    private static final int REQ_SD_CARD_PERMISSION = 1;

    private Context mContext;
    private AgrinoteDBHelper mDB;
    private DateTime mDateTime;
    private Farm mFarm;
    private String mCurrentImgPath;
    private CropItem mItem1Obj;
    private CropItem mItem2Obj;
    private CropItem mItem3Obj;

    private ImageView mCropImg;
    private EditText mItem1;
    private EditText mItem2;
    private EditText mItem3;
    private EditText mPeriod;
    private TextView mStartDate;

    private FrameLayout mSaveBtn;
    private FrameLayout mCancelBtn;
    private FrameLayout mDeleteBtn;

    private void showErrorDialog(String msg) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("錯誤")
                .content(msg)
                .positiveText("確定")
                .show();
    }

    private void showListDailog() {
        List<CropItem> items = mDB.getCropItems(null);
        Log.i(TAG, "root size :: " + items.size());
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                mItem1Obj = (CropItem) item.getTag();
                Log.i(TAG, mItem1Obj.toString());
                if (!mItem1Obj.getName().equals(mItem1.getText().toString())) {
                    mItem1.setText(mItem1Obj.getName());
                    mItem2.setText("");
                    mItem3.setText("");
                }
                dialog.dismiss();
            }
        });

        for (CropItem item : items) {
            adapter.add(new MaterialSimpleListItem.Builder(this)
                    .tag(item)
                    .content(item.getName())
                    .icon(R.drawable.vegetables)
                    .backgroundColor(Color.WHITE)
                    .build());
        }

        new MaterialDialog.Builder(this)
                .title(" 作物別選擇")
                .titleColorRes(android.R.color.holo_blue_light)
                .adapter(adapter, null)
                .show();
    }

    private void showListDailog2(String id) {
        List<CropItem> items = mDB.getCropItems(id);
        Log.i(TAG, "root size :: " + items.size());
        if (items.size() == 0) {
            return;
        }

        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                mItem2Obj = (CropItem) item.getTag();
                Log.i(TAG, mItem2Obj.toString());
                if (!mItem2Obj.getName().equals(mItem2.getText().toString())) {
                    mItem2.setText(mItem2Obj.getName());
                    mItem3.setText("");
                    checkLevel3(mItem2Obj.getId());
                }

                dialog.dismiss();
            }
        });

        for (CropItem item : items) {
            adapter.add(new MaterialSimpleListItem.Builder(this)
                    .tag(item)
                    .content(item.getName())
                    .icon(R.drawable.vegetables)
                    .backgroundColor(Color.WHITE)
                    .build());
        }


        new MaterialDialog.Builder(this)
                .title("品項選擇")
                .titleColorRes(android.R.color.holo_blue_light)
                .adapter(adapter, null)
                .show();
    }

    private void showListDailog3(String id) {
        List<CropItem> items = mDB.getCropItems(id);
        Log.i(TAG, "root size :: " + items.size());
        if (items.size() == 0) {
            mItem3.setEnabled(false);
            return;
        }

        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                mItem3Obj = (CropItem) item.getTag();
                Log.i(TAG, mItem3Obj.toString());
                mItem3.setText(mItem3Obj.getName());
                dialog.dismiss();
            }
        });

        for (CropItem item : items) {
            adapter.add(new MaterialSimpleListItem.Builder(this)
                    .tag(item)
                    .content(item.getName())
                    .icon(R.drawable.vegetables)
                    .backgroundColor(Color.WHITE)
                    .build());
        }

        new MaterialDialog.Builder(this)
                .title("品種選擇")
                .titleColorRes(android.R.color.holo_blue_light)
                .adapter(adapter, null)
                .show();
    }

    private void checkLevel3(String id) {
        List<CropItem> items = mDB.getCropItems(id);
        Log.i(TAG, "root size :: " + items.size());
        mItem3.setEnabled(items.size() != 0);

    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                AddCropInfoActivity.this,
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
                AddCropInfoActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.setAccentColor("#ff4081");
        tpd.enableSeconds(true);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    private void getFarmInfo() {
        String json = getIntent().getStringExtra("farm");
        Gson gson = new Gson();
        Log.i(TAG, json);
        if (null != json && !"".equals(json)) {
            mFarm = gson.fromJson(json, Farm.class);
            Log.i(TAG, mFarm.toString());
        } else {
            // error msg & back to login page
        }
    }

    private void showFlipMenu() {
        FlipShareView share = new FlipShareView.Builder(AddCropInfoActivity.this, mCropImg)
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

    private void toGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), REQ_SELECT_PICTURE);
    }


    private void saveCrop() {
        Crop crop = new Crop();

        crop.setFarmId(mFarm.getId());
        crop.setItem1(mItem1.getText().toString());
        crop.setItem2(mItem2.getText().toString());
        crop.setItem3(mItem3.getText().toString());
        crop.setPeriod(mPeriod.getText().toString());
        crop.setStartDate(mStartDate.getText().toString());
        crop.setPicPath(mCurrentImgPath);

        long id = mDB.createCrop(crop);
        Log.i(TAG, "crop id at db is " + id);
        onBackPressed();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crops);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("新增作物");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mContext = this;
        mDB = AgrinoteDBHelper.getInstance(mContext);

        getFarmInfo();

        mCropImg = (ImageView) findViewById(R.id.img_crop);
        mCropImg.setOnClickListener(this);

        mItem1 = (EditText) findViewById(R.id.item1);
        mItem1.setOnFocusChangeListener(this);
        mItem2 = (EditText) findViewById(R.id.item2);
        mItem2.setOnFocusChangeListener(this);
        mItem3 = (EditText) findViewById(R.id.item3);
        mItem3.setOnFocusChangeListener(this);
        mPeriod = (EditText) findViewById(R.id.period);

        mStartDate = (TextView) findViewById(R.id.record_date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mStartDate.setText(sdf.format(new Date()));
        mStartDate.setOnClickListener(this);

        mSaveBtn = (FrameLayout) findViewById(R.id.save_btn);
        mSaveBtn.setOnClickListener(this);
        mCancelBtn = (FrameLayout) findViewById(R.id.cancel_btn);
        mCancelBtn.setOnClickListener(this);
        mDeleteBtn = (FrameLayout) findViewById(R.id.delete_btn);
        mDeleteBtn.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "requestCode ::  " + requestCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    setPic(mCropImg, mCurrentImgPath);
                    break;
                case REQ_SELECT_PICTURE:
                    Uri selectedImageUri = data.getData();
                    Log.i(TAG, "selectedImageUri :: " + selectedImageUri.toString());
                    mCurrentImgPath = getRealPathFromURI_API19(mContext, selectedImageUri);
                    Log.i(TAG, "selected image path :: " + mCurrentImgPath);
                    setPic(mCropImg, mCurrentImgPath);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_crop:
                showFlipMenu();
                break;
            case R.id.record_date:
                showDatePicker();
                break;
            case R.id.save_btn:
                saveCrop();
                break;
            case R.id.cancel_btn:
                break;
            case R.id.delete_btn:
                break;
        }
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
        mStartDate.setText(mDateTime.getDateTime());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQ_CAMERA_PERMISSION:
                if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                        Manifest.permission.CAMERA)) {
                    // Enable the my location layer if the permission has been granted.
                    takePicture();
                } else {
                    // Display the missing permission error dialog when the fragments resume.

                }
                break;
            case REQ_SD_CARD_PERMISSION:
                if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Enable the my location layer if the permission has been granted.
                    takePicture();
                } else {
                    // Display the missing permission error dialog when the fragments resume.
                }
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.item1:
                if (hasFocus) {
                    showListDailog();
                }
                break;
            case R.id.item2:
                if (hasFocus) {
                    if (null != mItem1Obj) {
                        showListDailog2(mItem1Obj.getId());
                    } else {
                        showErrorDialog("作物別未選擇");
                    }

                }
                break;
            case R.id.item3:
                if (hasFocus) {
                    if (null != mItem2Obj) {
                        showListDailog3(mItem2Obj.getId());
                    } else {
                        showErrorDialog("品項未選擇");
                    }
                }
                break;
        }
    }
}
