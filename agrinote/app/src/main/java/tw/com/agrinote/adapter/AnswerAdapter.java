package tw.com.agrinote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.zagum.switchicon.SwitchIconView;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import tw.com.agrinote.R;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.model.FarmingItem;
import tw.com.agrinote.model.MachineItem;
import tw.com.agrinote.model.WorkingItem;


/**
 * Created by orc59 on 2017/11/19.
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    public static final int TYPE_WORKING_ITEM = 0;
    public static final int TYPE_FARMING_ITEM = 1;
    public static final int TYPE_MACHINE_ITEM = 2;

    private static final String TAG = "AnswerAdapter";
    private AgrinoteDBHelper mDB;
    private List<?> mDataList;
    private int mType;
    private RecyclerView mRecyclerView;
    private float mScale;

    private OnItemClickListener mOnItemClickListener;

    public AnswerAdapter(Context ctx, int type, RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        mDB = AgrinoteDBHelper.getInstance(ctx);
        mType = type;
        switch (type) {
            case TYPE_WORKING_ITEM:
                mDataList = mDB.getWorkingItem(null);
                break;
            case TYPE_FARMING_ITEM:
                mDataList = mDB.getFarmingItem(null);
                break;
            case TYPE_MACHINE_ITEM:
                mDataList = mDB.getAllMachineItem();
                break;
        }
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public void setmOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void refreshList(Object obj) {
        if (obj instanceof WorkingItem) {
            WorkingItem tmp = (WorkingItem) obj;
            mDataList = mDB.getWorkingItem(tmp.getId());
            notifyDataSetChanged();
        } else {
            FarmingItem tmp = (FarmingItem) obj;
            mDataList = mDB.getFarmingItem(tmp.getId());
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_answer_text, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AnswerAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mAnser;
        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            mAnser = (TextView) itemView.findViewById(R.id.answer_item);
            mRecyclerView.setPadding(calDP(25), calDP(25), calDP(35), calDP( 15));
        }

        public void bind(int position) {
            this.position = position;
            Log.i(TAG, "count :: " + position);
            switch (mType) {
                case TYPE_WORKING_ITEM:
                    mAnser.setText(((WorkingItem) mDataList.get(position)).getName());
                    mAnser.setTag((mDataList.get(position)));
                    break;
                case TYPE_FARMING_ITEM:
                    mAnser.setText(((FarmingItem) mDataList.get(position)).getName());
                    mAnser.setTag(mDataList.get(position));

                    break;
                case TYPE_MACHINE_ITEM:
                    mAnser.setText(((MachineItem) mDataList.get(position)).getName());
                    mAnser.setTag(mDataList.get(position));

                    break;
            }
            mAnser.setOnClickListener(this);
        }

        private int calDP(int src){
            return (int) (src * mScale + 0.5f);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "item click :: " + v.getTag());
            if (null != mOnItemClickListener) {
                mOnItemClickListener.onClick(v);
            }
        }
    }

    public interface OnItemClickListener {
        public void onClick(View v);

    }
}

