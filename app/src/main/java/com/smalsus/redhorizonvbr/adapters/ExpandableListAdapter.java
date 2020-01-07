package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.interfaces.CustomButtonListener;
import com.smalsus.redhorizonvbr.model.EventHeader;
import com.smalsus.redhorizonvbr.model.GetEvent;
import com.smalsus.redhorizonvbr.utils.CalenderUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<EventHeader> _listDataHeader; // header titles
    private HashMap<EventHeader, GetEvent> _listDataChild;
    private CustomButtonListener customButtonListener;

    public ExpandableListAdapter(Context context, List<EventHeader> listDataHeader,
                                 HashMap<EventHeader, GetEvent> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    public void setCustomButtonListner(CustomButtonListener listener) {
        this.customButtonListener = listener;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View view, ViewGroup parent) {
        final GetEvent childObject = (GetEvent) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.event_list_child_item_view, null);
        }
        View childDevider = view.findViewById(R.id.childDevider);
        childDevider.setVisibility(View.VISIBLE);
        TextView agendatext = view.findViewById(R.id.agendatext);
        TextView topicTv = view.findViewById(R.id.topic_tv);
        TextView subTopicTV = view.findViewById(R.id.subtopic_tv);
        TextView no_of_participants = view.findViewById(R.id.no_of_participants);
        TextView meeting_time = view.findViewById(R.id.meeting_time);
        ImageButton call_btn = view.findViewById(R.id.call_btn);
        ImageView location_image = view.findViewById(R.id.location_image);
        LinearLayout mainlayout = view.findViewById(R.id.mainlayout_profile);
        ImageButton vbr_btn = view.findViewById(R.id.vbr_btn);
        no_of_participants.setText(String.valueOf(childObject.getEventmember().size()));


        topicTv.setText(childObject.getTopic());
        subTopicTV.setText(childObject.getSubTopic());

        if (childObject.getLocation() != null) {
            if (childObject.getLocation().equalsIgnoreCase("skype")) {
                location_image.setImageResource(R.drawable.skype);
            } else {
                location_image.setImageResource(R.drawable.locationhr);

            }
        }
        vbr_btn.setOnClickListener(view1 -> {
            if (customButtonListener != null) {
                if (CalenderUtils.isTimeBetweenDate(childObject.getStartDate(), childObject.getEndDate()) == 1) {
                    ArrayList<String> videolIDist = new ArrayList<>();
                    for (int a = 0; a < childObject.getEventmember().size(); a++) {
                        String videoIDv = childObject.getEventmember().get(a).getVideoId();
                        videolIDist.add(videoIDv);
                    }
                    customButtonListener.onButtonClickListner(videolIDist, 3, childObject);
                }
            }else{
                Toast.makeText(_context,"Event has not started yet",Toast.LENGTH_LONG).show();
            }
        });
        mainlayout.setOnClickListener(view12 -> {
            if (customButtonListener != null) {
                ArrayList<String> videolIDist = new ArrayList<>();
                for (int a = 0; a < childObject.getEventmember().size(); a++) {
                    String videoIDv = childObject.getEventmember().get(a).getVideoId();
                    videolIDist.add(videoIDv);
                }
                customButtonListener.onButtonClickListner(videolIDist, 2, childObject);
            }
        });
        agendatext.setText(String.format("%s...", childObject.getDesc()));
        if (childObject.getStartDate().length() > 16 && childObject.getEndDate().length() > 16) {
            String starttime = childObject.getStartDate();
            String endtime = childObject.getEndDate();
            String meetingTime = String.format("%s TO %s", CalenderUtils.convertTimeDateFormat(starttime), CalenderUtils.convertTimeDateFormat(endtime));
            meeting_time.setText(meetingTime);
        }

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int size = this._listDataChild.get(this._listDataHeader.get(groupPosition)) != null ? 1 : 0;
        return size;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        EventHeader header = (EventHeader) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.event_list_header, null);
        }
        TextView lblListHeader = convertView
                .findViewById(R.id.lblListHeader);
        TextView eventDate = convertView.findViewById(R.id.eventCreatedDate);
        TextView eventTime = convertView.findViewById(R.id.eventCreateTime);
        eventDate.setText(CalenderUtils.getDate(header.getStartDate()));
        eventTime.setText(CalenderUtils.getTime(header.getStartDate()));
        lblListHeader.setText(header.getEventName());
        TextView eventBy = convertView.findViewById(R.id.eventCreatedBy);
        eventBy.setText(String.format("by %s", header.getEventUser().getfName() + " " + header.getEventUser().getlName()));
        ImageView groupIndicator = convertView.findViewById(R.id.groupIndicator);
        View gropuDevider = convertView.findViewById(R.id.groupDevider);
        RelativeLayout mainListItem = convertView.findViewById(R.id.topHeaderView);
        if (isExpanded) {
            gropuDevider.setVisibility(View.GONE);
            mainListItem.setBackgroundResource(R.drawable.event_header_bg_layout);
            //  groupIndicator.setImageResource(R.drawable.);
        } else {
            gropuDevider.setVisibility(View.VISIBLE);
            mainListItem.setBackgroundResource(R.drawable.event_list_itel_collapse_bg);
            //  groupIndicator.setImageResource(R.drawable.group_up);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
