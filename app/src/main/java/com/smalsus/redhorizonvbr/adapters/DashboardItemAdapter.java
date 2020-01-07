package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.DashBoardItem;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.view.activities.WebViewActivity;
import com.smalsus.redhorizonvbr.view.fragments.DashboardFragment;

import java.util.List;

public class DashboardItemAdapter extends RecyclerView.Adapter<DashboardItemAdapter.ViewHolder> {

    private Context mainActivityContext;
    private List<DashBoardItem> dashBoardItems;


//TODO(14): Create one constructor with three parameters which will passed from MainActivity class

    public DashboardItemAdapter(Context mainActivityContext, List<DashBoardItem> dashBoardItems) {
        this.mainActivityContext = mainActivityContext;
        this.dashBoardItems = dashBoardItems;

    }

//TODO(15): Complete each method as mentioned below


    @Override
    public DashboardItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Used to connect our custom UI to our recycler view

        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.dashboard_grid_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DashboardItemAdapter.ViewHolder holder, int position) {
        holder.dashBoardTitle.setText(dashBoardItems.get(position).getDashboardTitle());
        holder.dashboardImageIc.setImageResource(dashBoardItems.get(position).getDashboardItemIcon());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dashboardUrlAddress = DashboardFragment.dashboardUrl[position];
                Intent intent=new Intent(mainActivityContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.urlString, dashboardUrlAddress);
                mainActivityContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //Returns total number of rows inside recycler view

        return dashBoardItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dashBoardTitle;
        ImageView dashboardImageIc;
        View itemView;

        public ViewHolder(View view) {
            super(view);
            this.itemView=view;
            dashBoardTitle = view.findViewById(R.id.dashboard_item_title);
            dashboardImageIc = view.findViewById(R.id.dashboard_item_ic);

        }
    }
}