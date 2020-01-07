package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.EventUser;

import java.util.List;


public class ConferenceFriendList extends BaseAdapter {
    Context context;
    LayoutInflater inflter;
    List<EventUser> friendList;

    public ConferenceFriendList(Context context, List<EventUser> friendList) {
        this.context = context;
        this.friendList = friendList;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return friendList.size();
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
        view = inflter.inflate(R.layout.userlistitem, null);
        TextView username = view.findViewById(R.id.username);

        return view;
    }

}