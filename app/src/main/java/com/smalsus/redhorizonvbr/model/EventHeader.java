package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

public class EventHeader {
    private String id;
    private String eventName;
    String startDate;
    EventUser eventUser;


    public EventHeader(String id, String eventName) {
        this.id = id;
        this.eventName = eventName;
    }

    public EventHeader(String id, String eventName, String startDate, EventUser eventUser) {
        this.id = id;
        this.eventName = eventName;
        this.startDate = startDate;
        this.eventUser = eventUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public String getStartDate() {
        return startDate;
    }

    public EventUser getEventUser() {
        return eventUser;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
