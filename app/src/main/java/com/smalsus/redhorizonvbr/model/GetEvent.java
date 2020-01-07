package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetEvent implements Serializable {
    @SerializedName("id")
    String eventID;
    @SerializedName("name")
    String name;
    @SerializedName("topic")
    String topic;
    @SerializedName("subTopic")
    String subTopic;
    @SerializedName("chatRoomName")
    String chatRoomName;
    @SerializedName("startDate")
    String startDate;
    @SerializedName("endDate")
    String endDate;
    @SerializedName("createdAt")
    String createdAt;
    @SerializedName("availability")
    Integer availability;
    @SerializedName("location")
    String location;
    @SerializedName("typeColor")
    String typeColor;
    @SerializedName("reminder")
    String reminder;
    @SerializedName("hashTag")
    List<String> hashTag;
    @SerializedName("isAllDay")
    Boolean isAllDay;
    @SerializedName("isPrivate")
    Boolean isPrivate;
    @SerializedName("desc")
    String desc;
    @SerializedName("isActive")
    Boolean isActive;
    @SerializedName("by")
    EventUser eventUser;
    @SerializedName("members")
    List<EventUser> eventmember;

    public GetEvent(String eventID, String name, String topic, String subTopic, String chatRoomName, String startDate, String endDate, String createdAt, Integer availability, String location, String typeColor, String reminder, List<String> hashTag, Boolean isAllDay, Boolean isPrivate, String desc, Boolean isActive, EventUser eventUser, List<EventUser> eventmember) {
        this.eventID = eventID;
        this.name = name;
        this.topic = topic;
        this.subTopic = subTopic;
        this.chatRoomName = chatRoomName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.availability = availability;
        this.location = location;
        this.typeColor = typeColor;
        this.reminder = reminder;
        this.hashTag = hashTag;
        this.isAllDay = isAllDay;
        this.isPrivate = isPrivate;
        this.desc = desc;
        this.isActive = isActive;
        this.eventUser = eventUser;
        this.eventmember = eventmember;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTypeColor() {
        return typeColor;
    }

    public void setTypeColor(String typeColor) {
        this.typeColor = typeColor;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public List<String> getHashTag() {
        return hashTag;
    }

    public void setHashTag(List<String> hashTag) {
        this.hashTag = hashTag;
    }

    public Boolean getAllDay() {
        return isAllDay;
    }

    public void setAllDay(Boolean allDay) {
        isAllDay = allDay;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public EventUser getEventUser() {
        return eventUser;
    }

    public void setEventUser(EventUser eventUser) {
        this.eventUser = eventUser;
    }

    public List<EventUser> getEventmember() {
        return eventmember;
    }

    public void setEventmember(List<EventUser> eventmember) {
        this.eventmember = eventmember;
    }
    /*public GetEvent(String eventID, String name, String topic, String subTopic, String chatRoomName, String startDate, String endDate, String createdAt, Integer availability, String location, String reminder, List<String> hashTag, Boolean isAllDay, Boolean isPrivate, String desc, Boolean isActive, EventUser eventUser, List<EventUser> eventmember) {
        this.eventID = eventID;
        this.name = name;
        this.topic = topic;
        this.subTopic = subTopic;
        this.chatRoomName = chatRoomName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.availability = availability;
        this.location = location;
        this.reminder = reminder;
        this.hashTag = hashTag;
        this.isAllDay = isAllDay;
        this.isPrivate = isPrivate;
        this.desc = desc;
        this.isActive = isActive;
        this.eventUser = eventUser;
        this.eventmember = eventmember;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public List<String> getHashTag() {
        return hashTag;
    }

    public void setHashTag(List<String> hashTag) {
        this.hashTag = hashTag;
    }

    public Boolean getAllDay() {
        return isAllDay;
    }

    public void setAllDay(Boolean allDay) {
        isAllDay = allDay;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public EventUser getEventUser() {
        return eventUser;
    }

    public void setEventUser(EventUser eventUser) {
        this.eventUser = eventUser;
    }

    public List<EventUser> getEventmember() {
        return eventmember;
    }

    public void setEventmember(List<EventUser> eventmember) {
        this.eventmember = eventmember;
    }*/
}
