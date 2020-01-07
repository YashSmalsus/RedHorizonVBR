package com.smalsus.redhorizonvbr.model;

import java.io.Serializable;
import java.util.List;

public class EventInfo implements Serializable {
    public int id;
    public String eventName;
    List<EventUser> members;
    String location;
    String startDate;
    String endDate;
    String desc;
    String reminder;
    String fName;
    String createdAt;

    public EventInfo(String createdAt, String fName, String endDate, String desc, String reminder, int id, String eventName, List<EventUser> members, String location, String startDate) {
        this.id = id;
        this.eventName = eventName;
        this.members = members;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.desc = desc;
        this.reminder = reminder;
        this.fName = fName;
        this.createdAt = createdAt;

    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<EventUser> getMembers() {
        return members;
    }

    public void setMembers(List<EventUser> members) {
        this.members = members;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


}
