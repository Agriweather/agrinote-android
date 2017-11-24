package tw.com.agrinote.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import tw.com.agrinote.R;
import tw.com.agrinote.model.Answer;

/**
 * Created by orc59 on 2017/11/24.
 */

public class LogContentAdapter extends RecyclerView.Adapter<LogContentAdapter.ViewHolder> {

    private static final String TAG = "LogContentAdapter";
    private Answer mAns;

    private int mTextSize;
    private boolean mBold;

    public LogContentAdapter(Answer answer, int textSize, boolean bold) {
        this.mAns = answer;
        this.mTextSize = textSize;
        this.mBold = bold;
    }

    public void refreshContent(Answer ans) {
        this.mAns = ans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_log_content, parent, false);
        return new LogContentAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (null != mAns) {
            Field[] fields = mAns.getClass().getDeclaredFields();
            return fields.length - 3;
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mText;

        public ViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.log_content_item);
            mText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
            mText.setTypeface(null, mBold ? Typeface.BOLD : Typeface.NORMAL);
        }

        public void bind(int position) {
            switch (position) {
                case 0:
                    mText.setText(mAns.displayMainItem());
                    break;
                case 1:
                    mText.setText(mAns.displayNote());
                    break;
                case 2:
                    mText.setText(mAns.displayUsageAmount());
                    break;
                case 3:
                    mText.setText(mAns.displayUnit());
                    break;
                case 4:
                    mText.setText(mAns.displayDilution());
                    break;
                case 5:
                    mText.setText(mAns.displayItem());
                    break;
                case 6:
                    mText.setText(mAns.displayItemCost());
                    break;
                case 7:
                    mText.setText(mAns.displayIncome());
                    break;
                case 8:
                    mText.setText(mAns.displayMachine());
                    break;

            }
        }
    }
}
