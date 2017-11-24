package tw.com.agrinote.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

import tw.com.agrinote.R;
import tw.com.agrinote.db.AgrinoteDBHelper;
import tw.com.agrinote.model.Farm;
import tw.com.agrinote.model.User;

/**
 * Created by orc59 on 2017/11/1.
 */

public class ItemFarmAdapter extends RecyclerView.Adapter<ItemFarmAdapter.ViewHolder> {
    private static final String TAG = "ItemFarmAdapter";

    private static final int UNSELECTED = -1;
    private int selectedItem = UNSELECTED;

    private RecyclerView mRecyclerView;


    private Context mContext;
    private AgrinoteDBHelper mDB;
    private List<Farm> mDataList;
    private User mUser;

    private CropsAdapter.ItemClickListener cropsClickListener;

    public ItemFarmAdapter(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    public ItemFarmAdapter(Context context, RecyclerView recyclerView, User user) {
        this.mContext = context;
        this.mDB = AgrinoteDBHelper.getInstance(this.mContext);
        this.mRecyclerView = recyclerView;
        this.mUser = user;

        this.mDataList = this.mDB.getFarmByUserId(mUser.getId());
        Log.i(TAG, "constructor finish");
    }

    public void setCropsClickListener(CropsAdapter.ItemClickListener listener) {
        this.cropsClickListener = listener;
    }

    public void refresh() {
        mDataList.clear();
        this.mDataList = this.mDB.getFarmByUserId(mUser.getId());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_farm, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
        private ExpandableLayout mExpandableLayout;
        private TextView mFarmName;
        private TextView mFarmLandId;
        private RecyclerView mCropRecyclerView;
        private int position;
        private Farm mFarm;

        private CropsAdapter mCropsAapter;
        int numberOfColumns = 3;

        public ViewHolder(View itemView) {
            super(itemView);

            mCropRecyclerView = (RecyclerView) itemView.findViewById(R.id.crops);


            mExpandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandable_layout);
            mExpandableLayout.setInterpolator(new OvershootInterpolator());
            mExpandableLayout.setOnExpansionUpdateListener(this);
            mFarmName = (TextView) itemView.findViewById(R.id.item_farm_name);
            mFarmLandId = (TextView) itemView.findViewById(R.id.item_farm_land_id);

            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            this.position = position;
            mFarm = mDataList.get(position);
            Log.i(TAG, mFarm.toString());

            mCropRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), numberOfColumns));
            mCropsAapter = new CropsAdapter(itemView.getContext(), mFarm);
            mCropsAapter.setClickListener(cropsClickListener);
            mCropRecyclerView.setAdapter(mCropsAapter);

            mFarmName.setText(mFarm.getName());
            mFarmLandId.setText(mFarm.getLandId());

            mFarmName.setSelected(false);
            mExpandableLayout.collapse(false);
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "selectedItem :: " + selectedItem);
            ViewHolder holder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(selectedItem);
            if (holder != null) {
                holder.mFarmName.setSelected(false);
                holder.mExpandableLayout.collapse();
            }

            if (position == selectedItem) {
                selectedItem = UNSELECTED;
            } else {
                mFarmName.setSelected(true);
                mExpandableLayout.expand();

                selectedItem = position;
            }
        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            //Log.d("ExpandableLayout", "State: " + state);
            mCropRecyclerView.smoothScrollToPosition(getAdapterPosition());
        }
    }
}
