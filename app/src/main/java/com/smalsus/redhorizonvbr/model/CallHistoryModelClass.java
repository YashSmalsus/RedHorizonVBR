package com.smalsus.redhorizonvbr.model;

import java.io.Serializable;

public class CallHistoryModelClass implements Serializable {

   private int historyId,callType;
   private String groupName,callTime,callDuration,callParticipate;

    public CallHistoryModelClass( String groupName, String callTime, String callDuration, String callParticipate,int callType) {
        this.groupName = groupName;
        this.callTime = callTime;
        this.callDuration = callDuration;
        this.callParticipate = callParticipate;
        this.callType=callType;
    }

    public CallHistoryModelClass() {
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getCallParticipate() {
        return callParticipate;
    }

    public void setCallParticipate(String callParticipate) {
        this.callParticipate = callParticipate;
    }
}
