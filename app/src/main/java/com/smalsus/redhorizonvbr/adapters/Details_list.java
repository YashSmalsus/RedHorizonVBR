package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smalsus.redhorizonvbr.GlideUtils;
import com.smalsus.redhorizonvbr.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Details_list extends BaseAdapter {
    private LayoutInflater inflter;
    private Context context;
   List<MemberList> members_name;


    public Details_list(Context context, List<MemberList> membersname) {
        this.context = context;
        inflter = (LayoutInflater.from(context));
        this.members_name=membersname;
    }
    @Override
    public int getCount() {
        return members_name.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.eventdetails_item, null);
        ImageView member_image=view.findViewById(R.id.image_member);
        TextView member_name=view.findViewById(R.id.member_name);
        member_name.setText(members_name.get(i).getName());
        Picasso.get().load(members_name.get(i).getImage()).into(member_image);
        return view;
    }
}
