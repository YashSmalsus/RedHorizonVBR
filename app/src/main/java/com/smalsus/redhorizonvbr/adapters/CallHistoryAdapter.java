package com.smalsus.redhorizonvbr.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.smalsus.redhorizonvbr.GlideUtils;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.CallHistoryModelClass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {

    private ArrayList<CallHistoryModelClass> recordingArrayList;
    private Context context;


    public CallHistoryAdapter(Context context, ArrayList<CallHistoryModelClass> recordingArrayList) {
        this.context = context;
        this.recordingArrayList = recordingArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.call_history_item_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        setUpData(holder, position);
    }

    @Override
    public int getItemCount() {
        return recordingArrayList.size();
    }


    private void setUpData(ViewHolder holder, int position) {

        CallHistoryModelClass recording = recordingArrayList.get(position);
        if(recording.getGroupName().length()>2)
        holder.textViewName.setText(recording.getGroupName().substring(0,recording.getGroupName().length()-1));
        holder.textViewCallTime.setText(new StringBuilder().append("Time :").append(recording.getCallTime()).toString());
        holder.textViewDuration.setText(new StringBuilder().append("Duration :").append(recording.getCallDuration()).toString());
        String participate=recording.getCallParticipate();
        if(participate.length()>5){
            participate=participate.substring(0,participate.length()-1);
            String[] imageviewArray=participate.split(",");
            GlideUtils.loadImage(context,imageviewArray[0], holder.multiImageView, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDuration, textViewCallTime;
        ImageView multiImageView;
        ViewHolder holder;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tvGroupName);
            textViewDuration = itemView.findViewById(R.id.tvDuration);
            textViewCallTime = itemView.findViewById(R.id.tvCallTime);
            multiImageView=itemView.findViewById(R.id.multiImageView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();


                    return false;
                }
            });


        }
    }

    private void showConfirmMessage(int position, final String uri) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Do you  want to delete this file  ");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        File fdelete = new File(uri);
                        fdelete.delete();
                        if (fdelete.exists()) {
                            try {
                                fdelete.getCanonicalFile().delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (fdelete.exists()) {
                                context.getApplicationContext().deleteFile(fdelete.getName());
                            }
                        }
                        recordingArrayList.remove(position);
                        notifyDataSetChanged();
                    }
                });
        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public interface OnAudioPlayStopListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}