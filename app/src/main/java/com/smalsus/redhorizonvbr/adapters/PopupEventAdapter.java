package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.interfaces.CustomButtonListener;
import com.smalsus.redhorizonvbr.model.GetEvent;
import com.smalsus.redhorizonvbr.utils.CalenderUtils;
import com.smalsus.redhorizonvbr.view.CircleImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PopupEventAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflter;
    private List<GetEvent> eventList;
    private CustomButtonListener customListner;

    public PopupEventAdapter(Context context, List<GetEvent> eventDetail) {
        this.context = context;
        this.eventList = eventDetail;
        inflter = (LayoutInflater.from(context));
    }


    public void setCustomButtonListner(CustomButtonListener listener) {
        this.customListner = listener;
    }

    @Override
    public int getCount() {
        return eventList.size();
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
        view = inflter.inflate(R.layout.profile_item, null);
        TextView eventName = view.findViewById(R.id.eventName);
        TextView agendatext = view.findViewById(R.id.agendatext);
        TextView no_of_participants = view.findViewById(R.id.no_of_participants);
        TextView meeting_time = view.findViewById(R.id.meeting_time);
        TextView topicTV = view.findViewById(R.id.topicTV);
        TextView subTopicTV = view.findViewById(R.id.subtopic_tv);
        ImageButton call_btn = view.findViewById(R.id.call_btn);
        TextView location_image = view.findViewById(R.id.location_image);
        LinearLayout mainlayout = view.findViewById(R.id.mainlayout_profile);
        ImageButton vbr_btn = view.findViewById(R.id.vbr_btn);
        TextView eventBy = view.findViewById(R.id.eventBy);
        TextView hashTags = view.findViewById(R.id.hashTagText);
        LinearLayout hashTagLinearLayout = view.findViewById(R.id.hashTagLinearLayoutContainer);
        LinearLayout colorLinearLayout = view.findViewById(R.id.eventColor);
        //ImageView deleteEventImageView = view.findViewById(R.id.deleteEvent);
        StringBuilder stringBuilder = new StringBuilder();

        if(eventList.get(i).getHashTag().size() != 0)
        {
            String stringBuilder1;
            if(eventList.get(i).getHashTag().toString().length() == 2)
            {
                hashTagLinearLayout.setVisibility(View.GONE);
            }
            else
            {
                String hashTagString = eventList.get(i).getHashTag().toString();
                int length = eventList.get(i).getHashTag().toString().length();
                stringBuilder1 = hashTagString.substring(2, length - 2);
                String[] hashTagStringArray = stringBuilder1.split("\\s*,\\s*");
                for(int j = 0; j < hashTagStringArray.length; j++)
                {
                    stringBuilder.append("#").append(hashTagStringArray[j]).append(" ");
                }
                hashTags.setText(stringBuilder);
            }
        }
        if (eventList.get(i).getTypeColor() == null)
        {
            colorLinearLayout.setBackgroundColor(Color.parseColor("#4e73df"));
        }
        else
        {
            colorLinearLayout.setBackgroundColor(Color.parseColor(eventList.get(i).getTypeColor()));
        }
        /*SharedPreferences sharedPreferences = context.getSharedPreferences("UserID", Context.MODE_PRIVATE);
        String creatorID = eventList.get(i).getEventUser().getId();
        String loggedInUserID = sharedPreferences.getString("userID", "");
        if(creatorID.equals(loggedInUserID))
        {
            deleteEventImageView.setVisibility(View.VISIBLE);
        }
        else
        {
            deleteEventImageView.setVisibility(View.GONE);
        }*/
        eventBy.setText(String.format(String.format("by %s %s", eventList.get(i).getEventUser().getfName(), eventList.get(i).getEventUser().getlName())));
        CircleImageView circleImageView = view.findViewById(R.id.eventUserImage);
        Picasso.get().load(eventList.get(i).getEventUser().getImageUrl()).into(circleImageView);
        CircularImageView fisrtUserCircularImage = view.findViewById(R.id.firstUser);
        CircularImageView secondUserCircularImage = view.findViewById(R.id.secondUser);
        if(eventList.get(i).getEventmember().size() > 2)
        {
            Picasso.get().load(eventList.get(i).getEventmember().get(0).getImageUrl()).into(fisrtUserCircularImage);
            Picasso.get().load(eventList.get(i).getEventmember().get(1).getImageUrl()).into(secondUserCircularImage);
        }
        else secondUserCircularImage.setImageResource(R.drawable.defaultuser);
        topicTV.setText(eventList.get(i).getTopic());
        subTopicTV.setText(eventList.get(i).getSubTopic());
        no_of_participants.setText(String.valueOf(eventList.get(i).getEventmember().size()));
        if (eventList.get(i).getLocation() == null)
        {
            location_image.setText("Meeting Room");
        }
        else
        {
            location_image.setText(eventList.get(i).getLocation());
        }
        vbr_btn.setOnClickListener(view12 -> {
            if (customListner != null) {
                if (CalenderUtils.isTimeBetweenDate(eventList.get(i).getStartDate(), eventList.get(i).getEndDate()) == 1) {
                    ArrayList<String> videolIDist = new ArrayList<>();
                    for (int a = 0; a < eventList.get(i).getEventmember().size(); a++) {
                        String videoIDv = eventList.get(i).getEventmember().get(a).getVideoId();
                        videolIDist.add(videoIDv);
                    }
                    customListner.onButtonClickListner(videolIDist, 3, eventList.get(i));
                }else{
                    Toast.makeText(context,"Event has not started yet",Toast.LENGTH_LONG).show();
                }

            }
        });
        mainlayout.setOnClickListener(view1 -> {
            if (customListner != null) {
                ArrayList<String> videolIDist = new ArrayList<>();
                for (int a = 0; a < eventList.get(i).getEventmember().size(); a++) {
                    String videoIDv = eventList.get(i).getEventmember().get(a).getVideoId();
                    videolIDist.add(videoIDv);
                }
                customListner.onButtonClickListner(videolIDist, 2, eventList.get(i));
            }
        });
        agendatext.setText(String.format("%s...", eventList.get(i).getDesc()));
        eventName.setText(eventList.get(i).getName());
        if (eventList.get(i).getStartDate().length() > 16 && eventList.get(i).getEndDate().length() > 16) {
            String starttime = eventList.get(i).getStartDate();
            String endtime = eventList.get(i).getEndDate();
            String meetingTime = String.format("%s - %s", CalenderUtils.getTime(starttime), CalenderUtils.getTime(endtime));
            meeting_time.setText(meetingTime);
        }
        return view;
    }

}
