package com.smalsus.redhorizonvbr.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.CalenderEventForm;
import com.smalsus.redhorizonvbr.model.EventInfo;
import com.smalsus.redhorizonvbr.model.GetEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GridAdapter extends ArrayAdapter {
    private static final String TAG = GridAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private TextView eventName;
    private ArrayList<CalenderEventForm> calenderEventList;
    private int datetoday;
    private String month, year;

    private Context mContext;

    public interface GridItemClicked {
        void onGridItemClicked(ArrayList<GetEvent> eventList, View view,
                               int position);
    }

    private GridItemClicked gridItemListner;

    public GridAdapter(Context context, ArrayList<CalenderEventForm> calenderEventList, int currentDate, String currentMonth, String currentYear) {
        super(context, R.layout.horizontalitem);
        this.mContext = context;
        this.calenderEventList = calenderEventList;
        this.datetoday = currentDate;
        this.month = currentMonth;
        this.year = currentYear;
        mInflater = LayoutInflater.from(context);
    }

    public void setGridImtemClicked(GridItemClicked onItemClickListener) {
        this.gridItemListner = onItemClickListener;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.horizontalitem, parent, false);
        }
        int dayvalue = calenderEventList.get(position).getDateToDisplay();
        LinearLayout mainlayout = view.findViewById(R.id.mainlayout);
        TextView dateDays = view.findViewById(R.id.calendar_date_id);
        LinearLayout textlayout = view.findViewById(R.id.lineartexrlayout);
        TextView eventSize = view.findViewById(R.id.eventsize);
        textlayout.removeAllViews();
        dateDays.setText(String.valueOf(dayvalue));
        if (calenderEventList.get(position).eventList != null && calenderEventList.get(position).eventList.size() > 0) {
            eventSize.setText(String.valueOf(calenderEventList.get(position).eventList.size()));
        }

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM");
        String formattedDate = df.format(c);
        if (calenderEventList.get(position).isDateBiggerThenCurrent == 1) {
            dateDays.setBackgroundColor(mContext.getResources().getColor(R.color.week_header_bg_color));
            mainlayout.setBackgroundColor(mContext.getResources().getColor(R.color.calender_griditem_bg_color));
            eventSize.setTextColor(Color.WHITE);
        }
        mainlayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                if (gridItemListner != null) {
                    if (calenderEventList.size() != 0) {
                        gridItemListner.onGridItemClicked(calenderEventList.get(position).getEventList(), v, position);
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            View c = parent.getChildAt(i);
                            c.findViewById(R.id.mainlayout).setBackgroundResource(R.drawable.border);
                            ((TextView) c.findViewById(R.id.eventsize)).setTextColor(Color.BLACK);

                        }
                        mainlayout.setBackgroundColor(Color.parseColor("#E33F3E"));
                        eventSize.setTextColor(Color.WHITE);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return calenderEventList.size();
    }

    @Override
    public Object getItem(int position) {
        return calenderEventList.get(position);
    }

    @Override
    public int getPosition(Object item) {
        return calenderEventList.indexOf(item);
    }

}