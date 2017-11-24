package tw.com.agrinote.fragment;

import android.animation.Animator;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import tw.com.agrinote.R;
import tw.com.agrinote.adapter.ItemFarmAdapter;
import tw.com.agrinote.adapter.LogContentAdapter;
import tw.com.agrinote.adapter.RecordCardAdapter;
import tw.com.agrinote.model.Answer;
import tw.com.agrinote.model.Crop;
import tw.com.agrinote.model.Farm;

/**
 * Created by orc59 on 2017/11/8.
 */

public class FarmingDialogFragment extends DialogFragment {

    private LogContentAdapter mLogContentAdapter;
    private Answer mAnswer;
    private Farm mFarm;
    private Crop mCrop;


    public void setAdapter(LogContentAdapter adapter){
        this.mLogContentAdapter = adapter;
    }

    public Answer getAnswer(){
        return this.mAnswer;
    }

    public void setFarm(Farm farm){
        this.mFarm = farm;
    }

    public void setCrop(Crop crop){
        this.mCrop = crop;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialog_theme);
        mAnswer = new Answer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment, container);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.problem_list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext()){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        final RecordCardAdapter mAdapter = new RecordCardAdapter(view.getContext(), mFarm, mCrop);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter.setCardEndListener(new RecordCardAdapter.CardEndListener() {
            @Override
            public void onEnd(Answer answer) {
                mAnswer = answer;
                mLogContentAdapter.refreshContent(answer);
                dismiss();
            }
        });

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageButton btn = (ImageButton) view.findViewById(R.id.cancel_btn);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }


}
