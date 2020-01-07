package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.APIConstance.HRConstant;
import com.smalsus.redhorizonvbr.GlideUtils;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.WerableStorePlaces;
import com.squareup.picasso.Picasso;

import java.util.List;


public class WearableListAdapter extends RecyclerView.Adapter< RecyclerView.ViewHolder> {
    private Context mContext;
    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;
    private NetworkState networkState;
    private  List<WerableStorePlaces> mOldEmployeeList;




    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_PROGRESS) {
           View view=layoutInflater.inflate(R.layout.network_progress_item,parent,false);
            return new NetworkStateItemViewHolder(view);

        } else {
            View view=layoutInflater.inflate(R.layout.near_me_item_layout,parent,false);
            return new MyViewHolder(view);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof MyViewHolder) {
            ((MyViewHolder)holder).bindTo(getItem(position));
        } else {
            ((NetworkStateItemViewHolder) holder).bindView(networkState);
        }


    }

    private WerableStorePlaces getItem(int postion){
        return mOldEmployeeList.get(postion);
    }

    public WearableListAdapter(Context mContext,List<WerableStorePlaces> mOldEmployeeList) {
        this.mContext = mContext;
        this.mOldEmployeeList=mOldEmployeeList;

    }

    public void updateListItem(List<WerableStorePlaces> list) {
        final NearByListListDiffCallBack diffCallback = new NearByListListDiffCallBack(this.mOldEmployeeList, list);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.mOldEmployeeList.clear();
        this.mOldEmployeeList.addAll(list);
        diffResult.dispatchUpdatesTo(this);
    }



    private boolean hasExtraRow() {
        if (networkState != null && networkState != NetworkState.LOADED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mOldEmployeeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, address, rating,resturantDistance;
        private ImageView storeImage;


        MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            name = view.findViewById(R.id.resturantName);
            address = view.findViewById(R.id.restuantAddress);
            rating = view.findViewById(R.id.rating);
            resturantDistance=view.findViewById(R.id.resturantDistance);
            storeImage=(ImageView)view.findViewById(R.id.storeImage);
        }
        void bindTo(WerableStorePlaces article) {
            name.setText(article.name);
            address.setText(article.vicinity);
            rating.setText(article.rating);
            if(article.locationPhoto!=null && article.locationPhoto.size()>0){
                Picasso.get().load(getImageUrl(article.locationPhoto.get(0).photo_reference)).resize(150,150).into(storeImage);
            }
        }


        @Override
        public void onClick(View view) {


        }
    }

    public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar loader;
        private TextView errorText;

        NetworkStateItemViewHolder(View binding) {
            super(binding);
            loader=binding.findViewById(R.id.progress_bar);
            errorText=binding.findViewById(R.id.error_msg);
        }

        public void bindView(NetworkState networkState) {
            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                loader.setVisibility(View.VISIBLE);
            } else {
                loader.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                errorText.setVisibility(View.VISIBLE);
                errorText.setText(networkState.getMsg());
            } else {
                errorText.setVisibility(View.GONE);
            }
        }
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    private String getImageUrl(String photoRefrenced){
        StringBuilder photoURl=new StringBuilder().append(HRConstant.GooglePhotos).append("maxwidth=200").append("&photoreference=").append(photoRefrenced).append("&key=").append(HRConstant.GOOGLE_API_KEY);

        return photoURl.toString();
    }



    public class NearByListListDiffCallBack extends DiffUtil.Callback {

        private final List<WerableStorePlaces> mOldEmployeeList;
        private final List<WerableStorePlaces> mNewEmployeeList;

        public NearByListListDiffCallBack(List<WerableStorePlaces> oldEmployeeList, List<WerableStorePlaces> newEmployeeList) {
            this.mOldEmployeeList = oldEmployeeList;
            this.mNewEmployeeList = newEmployeeList;
        }

        @Override
        public int getOldListSize() {
            return mOldEmployeeList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewEmployeeList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldEmployeeList.get(oldItemPosition).getId().equals(mNewEmployeeList.get(
                    newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            final WerableStorePlaces oldEmployee = mOldEmployeeList.get(oldItemPosition);
            final WerableStorePlaces newEmployee = mNewEmployeeList.get(newItemPosition);

            return oldEmployee.getId().equals(newEmployee.getId());
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            // Implement method if you're going to use ItemAnimator
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }



}
