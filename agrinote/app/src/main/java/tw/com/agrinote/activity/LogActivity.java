package tw.com.agrinote.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import me.relex.circleindicator.CircleIndicator;
import tw.com.agrinote.R;
import tw.com.agrinote.adapter.CropLogPagerAdapter;
import tw.com.agrinote.adapter.CropsAdapter;
import tw.com.agrinote.adapter.TimelineAdapter;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.model.Crop;
import tw.com.agrinote.model.User;
import tw.com.agrinote.model.WorkingLog;

/**
 * Created by orc59 on 2017/11/6.
 */

public class LogActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LogActivity";

    private Context mContext;
    private Crop mCrop;


    private FrameLayout mPagerBack;
    private FrameLayout mPagerForward;
    private FloatingActionButton mAddLogBtn;
    private ViewPager mPagerView;

    private AgrinoteDBHelper mDB;

    private int mPage = 2;
    private CropLogPagerAdapter mPageAdapter;
    private CircleIndicator mIndicator;



    private void toAddLog() {
        Gson gson = new Gson();
        int position = mPagerView.getCurrentItem();
        mCrop = mPageAdapter.getCurrentCrop(position);

        Intent intent = new Intent();
        intent.setClass(this, AddLogActivity.class);
        intent.putExtra("crop", gson.toJson(mCrop));
        startActivity(intent);
    }

    private void toAddLog(View v) {
        Gson gson = new Gson();
        WorkingLog log = (WorkingLog) v.getTag();

        Intent intent = new Intent();
        intent.setClass(this, AddLogActivity.class);
        intent.putExtra("working_log", gson.toJson(log));
        startActivity(intent);
    }

    private void getCropInfo() {
        String json = getIntent().getStringExtra("crop");
        Gson gson = new Gson();
        Log.i(TAG, json);
        if (null != json && !"".equals(json)) {
            mCrop = gson.fromJson(json, Crop.class);
            Log.i(TAG, mCrop.toString());
        } else {
            // error msg & back to login page
        }
    }

    private void moveStatus(int position) {
        Log.i(TAG, (position - 1) + " < 0 :: " + ((position - 1) < 0));
        if ((position - 1) < 0) {
            mPagerBack.setEnabled(false);
        } else {
            mPagerBack.setEnabled(true);
        }
        Log.i(TAG, "position :: " + position);

        Log.i(TAG, position + " < " + (mPageAdapter.getCount() - 1) + " :: " + (position < (mPageAdapter.getCount() - 1)));
        if (position < (mPageAdapter.getCount() - 1)) {
            mPagerForward.setEnabled(true);
        } else {
            mPagerForward.setEnabled(false);
        }
    }

    private void movePage(boolean next) {
        int position = mPagerView.getCurrentItem();
        Log.i(TAG, "movePage position before :: " + position);
        if (next) {
            position++;
        } else {
            position--;
        }
        Log.i(TAG, "movePage position after :: " + position);

        mPagerView.setCurrentItem(position);
        moveStatus(position);
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("田間紀錄");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getCropInfo();

        mAddLogBtn = (FloatingActionButton) findViewById(R.id.fab);
        mAddLogBtn.setOnClickListener(this);


        mPagerView = (ViewPager) findViewById(R.id.viewpager);
        mIndicator = (CircleIndicator) findViewById(R.id.indicator);
        mPageAdapter = new CropLogPagerAdapter(this, mCrop.getFarmId(), new TimelineAdapter.LogUpdateListener() {
            @Override
            public void onClick(View v) {
                toAddLog(v);
            }
        });

        mPagerView.setAdapter(mPageAdapter);
        mPagerView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(TAG, "position :: " + position + ", positionOffset :: " + positionOffset + ", positionOffsetPixels :: " + positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "position :: " + position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG, "state :: " + state);
                moveStatus(state);

            }
        });
        mIndicator.setViewPager(mPagerView);

        mPagerBack = (FrameLayout) findViewById(R.id.pager_back);
        mPagerBack.setOnClickListener(this);
        mPagerForward = (FrameLayout) findViewById(R.id.pager_forward);
        mPagerForward.setOnClickListener(this);
        mPage = mPagerView.getCurrentItem();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pager_back:
                movePage(false);
                break;
            case R.id.pager_forward:
                movePage(true);
                break;
            case R.id.fab:
                toAddLog();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageAdapter.refreshList();
        mPageAdapter.notifyDataSetChanged();
        moveStatus(mPagerView.getCurrentItem());

    }


}
