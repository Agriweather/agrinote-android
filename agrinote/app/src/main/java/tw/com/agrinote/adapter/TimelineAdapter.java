package tw.com.agrinote.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.google.gson.Gson;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import tw.com.agrinote.Decoration.DividerItemDecoration;
import tw.com.agrinote.R;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.model.Answer;
import tw.com.agrinote.model.WorkingLog;

/**
 * Created by orc59 on 2017/11/6.
 */

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private static final String TAG = "TimelineAdapter";
    private static final int UNSELECTED = -1;

    private RecyclerView recyclerView;
    private int selectedItem = UNSELECTED;

    private List<WorkingLog> mFeedList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private AgrinoteDBHelper mDB;
    private int mCropId;

    private LogUpdateListener mListener;


    public void setLogUpdateListener(LogUpdateListener listener) {
        this.mListener = listener;
    }

    public TimelineAdapter(RecyclerView recyclerView, Context context, int cropId) {
        this.mCropId = cropId;
        this.recyclerView = recyclerView;
        mContext = context;
        mDB = AgrinoteDBHelper.getInstance(mContext);
        mFeedList = mDB.getWorkingLogByCropId(cropId);
    }

    public void refresh() {
        this.notifyItemRangeRemoved(0, mFeedList.size());
        mFeedList.clear();
        this.mFeedList = this.mDB.getWorkingLogByCropId(mCropId);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mLayoutInflater = LayoutInflater.from(mContext);
        View view = mLayoutInflater.inflate(R.layout.timeline_item_log, parent, false);

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        WorkingLog workingLog = mFeedList.get(position);

        holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorPrimary));

        if (!workingLog.getRecordDate().isEmpty()) {
            holder.mDate.setVisibility(View.VISIBLE);
            holder.mDate.setText(workingLog.getRecordDate());
        } else
            holder.mDate.setVisibility(View.GONE);

        Gson gson = new Gson();
        Answer ans = gson.fromJson(workingLog.getWorking(), Answer.class);
        if (null == ans) {
            ans = new Answer();
        }
        holder.mMessage.setText(ans.displayMainItem());
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (mFeedList != null ? mFeedList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {

        @BindView(R.id.text_timeline_date)
        TextView mDate;
        @BindView(R.id.text_timeline_title)
        TextView mMessage;
        @BindView(R.id.time_marker)
        TimelineView mTimelineView;

        private ExpandableLayout expandableLayout;
        private int position;

        private CircleImageView mLogImg;
        private ImageView mUpdateImg;
        private WorkingLog mLog;
        private Answer mAns;
        private ImageView mPhoto;
        private RecyclerView mRecyclerView;
        private LogContentAdapter mLogContentAdapter;
        private DividerItemDecoration mDivider;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            mTimelineView.initLine(viewType);

            expandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandable_layout);
            expandableLayout.setInterpolator(new LinearInterpolator());
            expandableLayout.setOnExpansionUpdateListener(this);
            expandableLayout.setOnClickListener(this);

            itemView.setOnClickListener(this);

            mPhoto = (ImageView) itemView.findViewById(R.id.log_photo);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
            mLogImg = (CircleImageView) itemView.findViewById(R.id.attach_image);
            mUpdateImg = (ImageView) itemView.findViewById(R.id.update_image);
            mUpdateImg.setOnClickListener(this);
        }

        public void bind(int position) {
            this.position = position;
            expandableLayout.collapse(false);
            mLog = mFeedList.get(position);
            Log.i(TAG, mLog.toString());

            if (null != mLog.getPicPath()) {
                setPic(mLogImg, mLog.getPicPath());
            }

            Gson gson = new Gson();
            mAns = gson.fromJson(mLog.getWorking(), Answer.class);
            if (null == mAns) {
                mAns = new Answer();
            }
            Log.i(TAG, mAns.toString());
            mDivider = new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL_LIST);
            mRecyclerView.addItemDecoration(mDivider);
            mLogContentAdapter = new LogContentAdapter(mAns, 12, false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.setAdapter(mLogContentAdapter);

            mUpdateImg.setTag(mFeedList.get(position));
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
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setImageBitmap(bitmap);
        }

        private void dispatchEvent(View v) {
            if (null != mListener) {
                mListener.onClick(v);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.attach_image:
                    Log.i(TAG, "attach image click");
                    break;

                case R.id.update_image:
                    Log.i(TAG, "update image click");
                    dispatchEvent(v);
                    break;

                default:
                    Log.i(TAG, "timeline item click");
                    TimelineAdapter.ViewHolder holder = (TimelineAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
                    if (holder != null) {
                        holder.expandableLayout.collapse();
                        mMessage.setVisibility(View.VISIBLE);
                        mLogImg.setVisibility(View.VISIBLE);
                        mUpdateImg.setVisibility(View.GONE);
                    }

                    if (position == selectedItem) {
                        selectedItem = UNSELECTED;
                    } else {
                        expandableLayout.expand();
                        selectedItem = position;
                        mMessage.setVisibility(View.GONE);
                        mLogImg.setVisibility(View.GONE);
                        mUpdateImg.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            //Log.d("ExpandableLayout", "State: " + state);
            recyclerView.smoothScrollToPosition(getAdapterPosition());
            if (state == 3) {
                mMessage.setVisibility(View.GONE);
                mLogImg.setVisibility(View.GONE);
                mUpdateImg.setVisibility(View.VISIBLE);
                if (null != mLog.getPicPath()) {
                    setPic(mPhoto, mLog.getPicPath());
                    setPic(mLogImg, mLog.getPicPath());
                }
            }

            if (state == 1) {
                mMessage.setVisibility(View.VISIBLE);
                mLogImg.setVisibility(View.VISIBLE);
                mUpdateImg.setVisibility(View.GONE);
            }
        }
    }

    public interface LogUpdateListener {
        public void onClick(View v);
    }
}
