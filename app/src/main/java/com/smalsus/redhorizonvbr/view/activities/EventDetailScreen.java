package com.smalsus.redhorizonvbr.view.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
//import com.smalsus.redhorizonvbr.GlideUtils;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.Details_list;
import com.smalsus.redhorizonvbr.adapters.MemberList;
import com.smalsus.redhorizonvbr.model.GetEvent;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.view.fragments.EventDashboardFragment;
import com.smalsus.redhorizonvbr.view.fragments.ProfileScreenFragment;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EventDetailScreen extends AppCompatActivity implements View.OnClickListener {
    private TextView eventName, eventDateTime, reminderEvent, adminEmail, eventMember, created_date, agenda, eventHashTags;
    private ImageButton backbtn, editButton, deleteEventImageButton;
    private Intent intent;
    private Integer position;
    private GetEvent eventDetail;
    /*private ListView list_member;*/
    private CircularImageView creatorProfileImage;

    private LinearLayout linearLayout;

    //private ImageView createdby_image;
    public static String getCurrentDateTime() {
        String dateValue = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateValue = sdf.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return dateValue;
    }

    public static String parseTime(String date) {
        try {
            Date date1 = new SimpleDateFormat("hh:mm", Locale.getDefault()).parse(date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            return dateFormat.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_screen);
        //list_member = findViewById(R.id.list_member);
        editButton = findViewById(R.id.edit_event);
        editButton.setOnClickListener(this);
        created_date = findViewById(R.id.created_date);
        agenda = findViewById(R.id.meeting_agenda);
        //createdby_image = findViewById(R.id.create_image);
        intent = getIntent();
        backbtn = findViewById(R.id.cancel);
        backbtn.setOnClickListener(this);
        eventName = findViewById(R.id.event_name);
        eventDateTime = findViewById(R.id.event_date_time);
        reminderEvent = findViewById(R.id.reminder_time);
        adminEmail = findViewById(R.id.admin_emailID);
        eventMember = findViewById(R.id.event_member);
        creatorProfileImage = findViewById(R.id.create_image);
        eventHashTags = findViewById(R.id.eventDetailsHashTag);
        deleteEventImageButton = findViewById(R.id.deleteEventImageButton);

        position = intent.getIntExtra("position", 0);
        int screen = intent.getIntExtra("screen", 0);
        if (screen == 1) {
            eventDetail = (GetEvent) getIntent().getSerializableExtra("EVENT_DETAILS");
            if (eventDetail != null) {
                eventName.setText(eventDetail.getName());

                UserInfo userInfo= HRpreference.getInstance(this).getUserInfo();
                if (!userInfo.getId().equals(eventDetail.getEventUser().getId()))
                {
                    deleteEventImageButton.setVisibility(View.GONE);
                }
                else
                {
                    deleteEventImageButton.setVisibility(View.VISIBLE);
                    String token = HRpreference.getInstance(this).getUserInfo().getAuthToken();
                    deleteEventImageButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteEvent(eventDetail.getEventID(), token);
                                }
                            }
                    );
                }
                if(!userInfo.getId().equals(eventDetail.getEventUser().getId()))
                    editButton.setVisibility(View.GONE);

                reminderEvent.setText(String.format("%s min", eventDetail.getReminder()));
                adminEmail.setText(eventDetail.getEventUser().getfName());
                Picasso.get().load(eventDetail.getEventUser().getImageUrl()).into(creatorProfileImage);
                if(eventDetail.getHashTag() != null)
                {
                    String stringBuilder;
                    if(eventDetail.getHashTag().toString().length() == 2)
                    {
                        linearLayout = findViewById(R.id.hashTagDetailsContainer);
                        linearLayout.setVisibility(View.GONE);
                    }
                    else
                    {
                        String hashTagString = eventDetail.getHashTag().toString();
                        int length = eventDetail.getHashTag().toString().length();
                        stringBuilder = hashTagString.substring(2, length - 2);
                        String[] hashTagStringArray = stringBuilder.split("\\s*,\\s*");
                        StringBuilder stringBuilder1 = new StringBuilder();
                        for(int i = 0; i < hashTagStringArray.length; i++)
                        {
                            stringBuilder1.append("#").append(hashTagStringArray[i]).append(" ");
                        }
                        eventHashTags.setText(String.format("%s", stringBuilder1));
                    }
                }
                String starttime = eventDetail.getStartDate().substring(11, 16);
                String[] startdate = eventDetail.getStartDate().split("T");
                String date_event = startdate[0];
                String endtime = eventDetail.getEndDate().substring(11, 16);
                eventDateTime.setText(String.format("%s , %s to %s", date_event, parseTime(starttime), parseTime(endtime)));
                List<MemberList> members_event = new ArrayList<>();
                for (int i = 0; i < eventDetail.getEventmember().size(); i++) {

                    String membersname = eventDetail.getEventmember().get(i).getfName();
                    String profileImage = eventDetail.getEventmember().get(i).getImageUrl();
                    MemberList memberList=new MemberList(membersname,profileImage);

                    members_event.add(memberList);
                }
                String[] createdDate = eventDetail.getCreatedAt().split("T");
                String dat = createdDate[0];
                created_date.setText(dat);
                agenda.setText(eventDetail.getDesc());
                /*Details_list details_list = new Details_list(getApplicationContext(), members_event);
                list_member.setAdapter(details_list);*/
            }

        }
    }

    public void deleteEvent(String eventID, String token)
    {
        EventRequest eventRequest = new EventRequest();
        eventRequest.deleteEvent(eventID, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EventDetailScreen.this, "Deletion Operation Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200)
                {
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EventDetailScreen.this, "Event Deleted", Toast.LENGTH_LONG).show();
                                    EventDashboardFragment.isListRefresh = true;
                                    finish();
                                }
                            }
                    );
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == backbtn) {
            finish();
        } else if (view == editButton){
            Intent intent = new Intent(EventDetailScreen.this, AddEventScreen.class);
            intent.putExtra("EVENT_DETAILS", eventDetail);
            intent.putExtra("EVENT_LIST_POSITION", position);
            intent.putExtra("ISUPADTE_EVENT", true);
            startActivity(intent);
        }
    }
}

