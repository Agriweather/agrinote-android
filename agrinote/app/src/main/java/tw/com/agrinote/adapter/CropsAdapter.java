package tw.com.agrinote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tw.com.agrinote.R;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.model.Answer;
import tw.com.agrinote.model.Crop;
import tw.com.agrinote.model.DateTime;
import tw.com.agrinote.model.Farm;
import tw.com.agrinote.model.WorkingLog;


/**
 * Created by orc59 on 2017/11/5.
 */

public class CropsAdapter extends RecyclerView.Adapter<CropsAdapter.ViewHolder> {

    private static String TAG = "CropsAdapter";

    private static final int DEFAULT_COUNT = 1;

    private AgrinoteDBHelper mDB;
    private Context mContext;
    private Farm mFarm;
    private List<Crop> mData;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;


    public CropsAdapter(Context context, Farm farm) {
        this.mContext = context;
        this.mFarm = farm;
        this.mInflater = LayoutInflater.from(context);
        this.mDB = AgrinoteDBHelper.getInstance(context);
        this.mData = mDB.getCropsByFarmId(mFarm.getId());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        Crop item = null;
        Log.i(TAG, "viewType  ::" + viewType + ", mData.size() :: " + mData.size());

        if ((viewType + 1) == (mData.size() + DEFAULT_COUNT)) {
            view = mInflater.inflate(R.layout.item_add_item, parent, false);
            return new ViewHolder(view, mFarm);
        } else {
            view = mInflater.inflate(R.layout.item, parent, false);
            return new ViewHolder(view, mData.get(viewType));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size() + DEFAULT_COUNT;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int position;
        private Crop mCrop;

        private TextView mDate;
        private ImageView mCropIcon;
        private TextView mCropName;
        private TextView mLogDate;
        private TextView mLogMainItem;
        private Answer mAnswer;
        private WorkingLog mLog;

        ViewHolder(View itemView, Object item) {
            super(itemView);

            mDate = (TextView) itemView.findViewById(R.id.start_date);
            mCropIcon = (ImageView) itemView.findViewById(R.id.crop_icon);
            mCropName = (TextView) itemView.findViewById(R.id.crop_name);
            mLogDate = (TextView) itemView.findViewById(R.id.day);
            mLogMainItem = (TextView) itemView.findViewById(R.id.work);


            itemView.setOnClickListener(this);
            itemView.setTag(item);
        }

        public void bind(int position) {
            this.position = position;
            if (position >= mData.size()) {
                Log.i(TAG, "no Data :: " + mData.size());
                return;
            }

            mCrop = mData.get(position);
            Log.i(TAG, mCrop.toString());
            Log.i(TAG, "How many day before :: " + DayBefore(mCrop.getStartDate()));
            mDate.setText(String.format("%02d", DayBefore(mCrop.getStartDate())));
            mCropName.setText(mCrop.getItem2());

            mLog = mDB.getNewestWorkingLogByCropId(mCrop.getId());
            Log.i(TAG, null == mLog ? "mLog is null" : mLog.toString());

            if (null == mLog) {

                mLogDate.setText(String.format("%02d日前", DayBefore(mCrop.getStartDate())));
                mLogMainItem.setText("尚未有田間紀錄");
            } else {
                Gson gson = new Gson();
                mAnswer = gson.fromJson(mLog.getWorking(), Answer.class);
                if(null == mAnswer){
                    Log.i(TAG, "mAnswer is null, " + mLog.getWorking());
                    mAnswer = new Answer();
                }else{
                    Log.i(TAG, mAnswer.toString());

                }

                Log.i(TAG, "Log -> How many day before :: " + DayBefore(mLog.getRecordDate()));
                mLogDate.setText(String.format("%02d日前", DayBefore(mLog.getRecordDate())));
                mLogMainItem.setText(mAnswer.getMainItem());
            }
        }

        private long DayBefore(String dateStart) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date d1 = null;
            Date d2 = null;
            long diffDays = 0;
            try {
                d1 = format.parse(dateStart);
                d2 = Calendar.getInstance().getTime();

                //in milliseconds
                long diff = d2.getTime() - d1.getTime();
                diffDays = (diff / (24 * 60 * 60 * 1000)) + 1;

                Log.i(TAG, diffDays + " days, ");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return diffDays;
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "crop item click");
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
