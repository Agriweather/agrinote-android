package tw.com.agrinote.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import tw.com.agrinote.R;
import tw.com.agrinote.adapter.CropsAdapter;
import tw.com.agrinote.adapter.ItemFarmAdapter;
import tw.com.agrinote.model.Crop;
import tw.com.agrinote.model.Farm;
import tw.com.agrinote.model.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private User mUser;
    private Farm mSelectedFarm;
    private Context mContext;
    private ItemFarmAdapter mItemFarmAdapter;

    private NavigationView mNavigationView;
    private ImageView mHeadPic;
    private TextView mUserName;
    private TextView mUserEmail;
    private RecyclerView mFarmList;
    private LinearLayout mFieldStatus;


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

    private void setUserInfo() {
        mUserName.setText(mUser.getAccount());
        mUserEmail.setText(mUser.getAccount());
    }


    private void toAddFarm() {
        Gson gson = new Gson();

        Intent intent = new Intent();
        intent.setClass(this, AddFarmInfoActivity.class);
        intent.putExtra("user", gson.toJson(mUser));
        startActivity(intent);
    }

    private void toAddCrop(Farm farm) {
        Gson gson = new Gson();
        Intent intent = new Intent();

        intent.setClass(this, AddCropInfoActivity.class);
        intent.putExtra("farm", gson.toJson(farm));

        startActivity(intent);
    }

    private void toLog(Crop crop) {
        Gson gson = new Gson();
        Intent intent = new Intent();
        intent.setClass(this, LogActivity.class);
        intent.putExtra("crop", gson.toJson(crop));
        startActivity(intent);
    }

    private void showStatus(int count) {
        if (count > 0) {
            mFieldStatus.setVisibility(View.GONE);
        } else {
            mFieldStatus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toAddFarm();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mContext = this;

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        View header = mNavigationView.getHeaderView(0);
        mHeadPic = (ImageView) header.findViewById(R.id.head_pic);
        mUserName = (TextView) header.findViewById(R.id.user_name);
        mUserEmail = (TextView) header.findViewById(R.id.user_email);
        getUserInfo();
        setUserInfo();

        mFarmList = (RecyclerView) findViewById(R.id.recycler_view);
        mFarmList.setLayoutManager(new LinearLayoutManager(this));
        mItemFarmAdapter = new ItemFarmAdapter(mContext, mFarmList, mUser);
        mItemFarmAdapter.setCropsClickListener(new CropsAdapter.ItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG, "onItemClick  :: " + view.getTag());
                if (view.getTag() instanceof Farm) {
                    toAddCrop((Farm) view.getTag());
                } else {
                    toLog((Crop) view.getTag());
                }
            }
        });
        mFarmList.setAdapter(mItemFarmAdapter);
        mFieldStatus = (LinearLayout) findViewById(R.id.field_status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mItemFarmAdapter.refresh();
        mItemFarmAdapter.notifyDataSetChanged();
        showStatus(mItemFarmAdapter.getItemCount());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
