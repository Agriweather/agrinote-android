package tw.com.agrinote.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tw.com.agrinote.R;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.model.Crop;

/**
 * Created by orc59 on 2017/11/7.
 */

public class CropLogPagerAdapter extends PagerAdapter {

    private static final String TAG = "CropLogPagerAdapter";

    private List<TimelineAdapter> mAdapterList;
    private List<View> mViewList;
    private List<Crop> mData;

    private LayoutInflater mInflater;
    private Context mContext;
    private AgrinoteDBHelper mDB;

    private TimelineAdapter.LogUpdateListener mListener;

    public CropLogPagerAdapter(Context context, int farmId, TimelineAdapter.LogUpdateListener listene) {
        this.mContext = context;
        this.mDB = AgrinoteDBHelper.getInstance(mContext);
        this.mInflater = LayoutInflater.from(context);
        mListener = listene;

        mData = mDB.getCropsByFarmId(farmId);
        mAdapterList = new ArrayList<TimelineAdapter>();
        mViewList = new ArrayList<View>();
    }

    public void setLogUpdateListener(TimelineAdapter.LogUpdateListener listener) {
        this.mListener = listener;
    }

    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
    }

    private void initRecyclerView(Crop crop, View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(getLinearLayoutManager());
        recyclerView.setHasFixedSize(true);

        TimelineAdapter timeLineAdapter = new TimelineAdapter(recyclerView, mContext, crop.getId());
        timeLineAdapter.setLogUpdateListener(mListener);
        recyclerView.setAdapter(timeLineAdapter);
        mAdapterList.add(timeLineAdapter);

        LinearLayout fieldStatus = (LinearLayout) view.findViewById(R.id.field_status);
        fieldStatus.setVisibility(timeLineAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
    }

    public void refreshList() {
        for (int i = 0; i < mAdapterList.size(); i++) {
            mAdapterList.remove(0);

            View view = mViewList.get(i);
            LinearLayout fieldStatus = (LinearLayout) view.findViewById(R.id.field_status);



            initRecyclerView(mData.get(i), view );


        }
    }

    public Crop getCurrentCrop(int position) {
        return mData.get(position);
    }

    public int getPositionByCropId(int targetId) {
        for (int i = 0; i < mData.size(); i++) {
            Crop tmpCrop = mData.get(i);
            if (targetId == tmpCrop.getId()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup conatiner, int position) {
        View view = mInflater.inflate(R.layout.fragment_log, conatiner, false);
        Crop crop = mData.get(position);

        TextView mCropFullName = (TextView) view.findViewById(R.id.crop_full_name);
        mCropFullName.setText(crop.getCropName());


        initRecyclerView(mData.get(position), view);

        conatiner.addView(view);
        mViewList.add(view);
        return view;
    }


}
