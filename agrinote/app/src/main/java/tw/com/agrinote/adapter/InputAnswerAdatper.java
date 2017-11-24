package tw.com.agrinote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import tw.com.agrinote.R;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.model.Problem;
import tw.com.agrinote.model.WorkingItem;

/**
 * Created by orc59 on 2017/11/22.
 */

public class InputAnswerAdatper extends RecyclerView.Adapter<InputAnswerAdatper.ViewHolder> {

    private static final String TAG = "InputAnswerAdatper";

    private EditText mAnserInput;

    private Problem mProblem;
    private RecyclerView mRecyclerView;
    private float mScale;

    public InputAnswerAdatper(Problem problem, RecyclerView recyclerView) {
        this.mProblem = problem;
        this.mRecyclerView = recyclerView;
    }

    public void setScale(float scale){
        this.mScale = scale;
    }

    public String getText() {
        return mAnserInput.getText().toString();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_answer_edittext, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            mAnserInput = (EditText) itemView.findViewById(R.id.editText);
            mRecyclerView.setPadding(calDP(25), calDP(10),calDP(25), calDP(10));
        }

        public void bind(int position) {
            this.position = position;
            mAnserInput.setInputType(mProblem.getInputType());
            Log.i(TAG, "count :: " + position);
        }
    }

    private int calDP(int src){
        return (int) (src * mScale + 0.5f);
    }
}
