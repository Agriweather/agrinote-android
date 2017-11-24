package tw.com.agrinote.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.zagum.switchicon.SwitchIconView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tw.com.agrinote.R;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.model.Answer;
import tw.com.agrinote.model.Crop;
import tw.com.agrinote.model.Farm;
import tw.com.agrinote.model.FarmingItem;
import tw.com.agrinote.model.MachineItem;
import tw.com.agrinote.model.Problem;
import tw.com.agrinote.model.WorkingItem;

/**
 * Created by orc59 on 2017/11/8.
 */

public class RecordCardAdapter extends RecyclerView.Adapter<RecordCardAdapter.ViewHolder> {

    private static final String TAG = "RecordCardAdapter";
    private int mDataSize = 5;

    private CardEndListener mListener;
    private Context mContext;
    private AgrinoteDBHelper mDB;

    private String[] problems = {"本次工作項目？", "工作備註？", "使用量(次/量)？", "使用單位?", "稀釋倍數?", "使用資材?", "資材使用費用?", "賣出的價格?", "使用農機具?"};

    private List<Problem> mProblemList;

    private Answer mAnswer;
    private Farm mFarm;
    private Crop mCrop;
    private float mScale;

    public RecordCardAdapter(Context context, Farm farm, Crop crop) {
        this.mContext = context;
        this.mFarm = farm;
        this.mCrop = crop;
        mDB = AgrinoteDBHelper.getInstance(context);
        mAnswer = new Answer();
        mScale = mContext.getResources().getDisplayMetrics().density;

        mProblemList = new ArrayList<Problem>();
        for (String tmp : problems) {
            Problem tmpItem = new Problem();
            tmpItem.setQuestion(tmp);
            if (problems[1].equals(tmp) || problems[2].equals(tmp) || problems[3].equals(tmp) || problems[4].equals(tmp) || problems[6].equals(tmp) || problems[7].equals(tmp)) {
                tmpItem.setInput(true);
                if (problems[4].equals(tmp) || problems[6].equals(tmp) || problems[7].equals(tmp)) {
                    tmpItem.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    tmpItem.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            } else {
                tmpItem.setInput(false);
                if (problems[0].equals(tmp)) {
                    tmpItem.setType(0);
                } else if (problems[5].equals(tmp)) {
                    tmpItem.setType(1);
                } else {
                    tmpItem.setType(2);
                }
            }
            mProblemList.add(tmpItem);
        }
        mDataSize = problems.length;
    }

    public void setCardEndListener(CardEndListener listener) {
        this.mListener = listener;
    }

    private boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.5) {
            return false; // It's a light color
        } else {
            return true; // It's a dark color
        }
    }

    private int checkSubData(Object obj) {
        if (obj instanceof WorkingItem) {
            WorkingItem tmp = (WorkingItem) obj;
            return mDB.getWorkingItem(tmp.getId()).size();
        } else if (obj instanceof FarmingItem) {
            FarmingItem tmp = (FarmingItem) obj;
            return mDB.getFarmingItem(tmp.getId()).size();
        } else {
            Log.i(TAG, "machine item");
            return 0;
        }

    }

    private boolean isOther(Object obj) {
        if (obj instanceof WorkingItem) {
            WorkingItem tmp = (WorkingItem) obj;
            return "其他".equals(tmp.getName());
        } else if (obj instanceof FarmingItem) {
            FarmingItem tmp = (FarmingItem) obj;
            return "其他".equals(tmp.getName());
        } else {
            Log.i(TAG, "machine item");
            MachineItem tmp = (MachineItem) obj;
            return "其他".equals(tmp.getName());
        }

    }

    private String getObjName(Object obj) {
        if (obj instanceof WorkingItem) {
            WorkingItem tmp = (WorkingItem) obj;
            return tmp.getName();
        } else if (obj instanceof FarmingItem) {
            FarmingItem tmp = (FarmingItem) obj;
            return tmp.getName();
        } else {
            Log.i(TAG, "machine item");
            MachineItem tmp = (MachineItem) obj;
            return tmp.getName();
        }
    }

    @Override
    public RecordCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_card_item, parent, false);
        return new RecordCardAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecordCardAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mDataSize;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerView mAnswerRecyclerView;
        private int position;

        private TextView mProblemFarmName;
        private TextView mProblemCropName;
        private TextView mProblem;

        private LinearLayout mCardTopArea;
        private SwitchIconView mCardIcon;
        private LinearLayout mPrevNext;
        private TextView mPrev;
        private TextView mNext;

        private String mMainItem;
        private List<Object> mAdapterList;


        public ViewHolder(View itemView) {
            super(itemView);
            mAnswerRecyclerView = (RecyclerView) itemView.findViewById(R.id.answer_view);
            mCardTopArea = (LinearLayout) itemView.findViewById(R.id.card_top);
            mCardIcon = (SwitchIconView) itemView.findViewById(R.id.icon_item);
            mProblemFarmName = (TextView) itemView.findViewById(R.id.problem_farm_name);
            mProblemCropName = (TextView) itemView.findViewById(R.id.problem_crop_name);
            mProblem = (TextView) itemView.findViewById(R.id.problem);
            mPrevNext = (LinearLayout) itemView.findViewById(R.id.prev_next);
            mPrev = (TextView) itemView.findViewById(R.id.prev);
            mNext = (TextView) itemView.findViewById(R.id.next);

            mAdapterList = new ArrayList<Object>();
            mMainItem = "";
        }

        public void bind(final int position) {
            Log.i(TAG, position + ": problem :: " + mProblemList.get(0));
            mProblemFarmName.setText(mFarm.getName());
            mProblemCropName.setText(mCrop.getCropName());
            this.position = position;
            setColorful();
            Problem tmpProblem = mProblemList.get(0);
            mProblem.setText(tmpProblem.getQuestion());
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            mAnswerRecyclerView.setLayoutManager(mLayoutManager);

            if (tmpProblem.getInput()) {
                InputAnswerAdatper mAdapter = new InputAnswerAdatper(tmpProblem, mAnswerRecyclerView);
                mAdapter.setScale(mScale);
                mAnswerRecyclerView.setAdapter(mAdapter);

                mAdapterList.add(mAdapter);
                mPrevNext.setVisibility(View.VISIBLE);
            } else {
                final AnswerAdapter mAdapter = new AnswerAdapter(mContext, tmpProblem.getType(), mAnswerRecyclerView);
                mAdapter.setScale(mScale);
                mAnswerRecyclerView.setAdapter(mAdapter);
                DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                        mAnswerRecyclerView.getContext(),
                        mLayoutManager.getOrientation()
                );

                mAdapterList.add(mAdapter);
                mAdapter.setmOnItemClickListener(new AnswerAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "item click :: " + v.getTag());
                        int count = checkSubData(v.getTag());
                        if (count == 0) {
                            if (isOther(v.getTag())) {
                                Log.i(TAG, "is other switch edittext");
                                Problem tmpProblem = new Problem();
                                tmpProblem.setInputType(InputType.TYPE_CLASS_TEXT);
                                InputAnswerAdatper mAdapter = new InputAnswerAdatper(tmpProblem, mAnswerRecyclerView);
                                mAdapter.setScale(mScale);
                                mAnswerRecyclerView.setAdapter(mAdapter);
                                mPrevNext.setVisibility(View.VISIBLE);
                                mAdapterList.remove(0);
                                mAdapterList.add(0, mAdapter);
                                mMainItem = "其他 - ";
                            } else {
                                if (problems[8].equals(mProblem.getText().toString())) {
                                    setAns(getObjName(v.getTag()));
                                } else {
                                    setAns(mMainItem + getObjName(v.getTag()));
                                }
                                removeAt(position);
                            }

                        } else {
                            Log.i(TAG, "list count :: " + count);
                            mMainItem = getObjName(v.getTag()) + " - ";
                            mAdapter.refreshList(v.getTag());
                        }

                    }
                });

                mPrevNext.setVisibility(View.GONE);
            }


            mPrev.setOnClickListener(this);
            mNext.setOnClickListener(this);
        }

        public void removeAt(int position) {
            mDataSize--;
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mDataSize);
            mProblemList.remove(position);

            Object mObj = mAdapterList.get(0);
            if (mObj instanceof InputAnswerAdatper) {
                String inputAns = ((InputAnswerAdatper) mObj).getText();
                Log.i(TAG, "inputAns :: " + inputAns);
                if (problems[0].equals(mProblem.getText().toString()) ||
                        problems[5].equals(mProblem.getText().toString()) ||
                        problems[8].equals(mProblem.getText().toString())) {
                    setAns(mMainItem + inputAns);
                } else {
                    setAns(inputAns);
                }

            }
            mAdapterList.remove(0);

            Log.i(TAG, "Card count :: " + getItemCount());
            if (mDataSize == 0) {
                Log.i(TAG, "no card display");
                dispatchEvent(mAnswer);
            }
        }

        private int getColor() {
            return 0xff000000 | new Random().nextInt(0x00ffffff);
        }

        private void setColorful() {
            int bg = getColor();
            while (!isColorDark(bg)) {
                bg = getColor();
            }

            mCardTopArea.setBackgroundColor(bg);
            Field f = null;
            try {
                f = mCardIcon.getClass().getDeclaredField("iconTintColor");
                f.setAccessible(true);
                f.set(mCardIcon, bg);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            mCardIcon.setIconEnabled(false);
            mCardIcon.setIconEnabled(true);
        }

        private void dispatchEvent(Answer answer) {
            if (null != mListener) {
                mListener.onEnd(answer);
            }
        }

        private void setAns(String text) {
            if (problems[0].equals(mProblem.getText().toString())) {
                mAnswer.setMainItem(text);
            } else if (problems[1].equals(mProblem.getText().toString())) {
                mAnswer.setNote(text);
            } else if (problems[2].equals(mProblem.getText().toString())) {
                mAnswer.setUsageAmount(text);
            } else if (problems[3].equals(mProblem.getText().toString())) {
                mAnswer.setUnit(text);
            } else if (problems[4].equals(mProblem.getText().toString())) {
                mAnswer.setDilution(text);
            } else if (problems[5].equals(mProblem.getText().toString())) {
                mAnswer.setItem(text);
            } else if (problems[6].equals(mProblem.getText().toString())) {
                mAnswer.setItemCost(text);
            } else if (problems[7].equals(mProblem.getText().toString())) {
                mAnswer.setIncome(text);
            } else {
                mAnswer.setMachine(text);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.next:
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    removeAt(position);
                    break;
                case R.id.prev:
                    break;
            }
        }
    }

    public interface CardEndListener {
        public void onEnd(Answer answer);
    }
}
