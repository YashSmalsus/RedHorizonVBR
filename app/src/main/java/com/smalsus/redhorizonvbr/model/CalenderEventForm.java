package com.smalsus.redhorizonvbr.model;

import java.util.ArrayList;

public class CalenderEventForm {
    public String daytoDisplay;
    public int dateToDisplay;
    public ArrayList<GetEvent> eventList;
    public int isDateBiggerThenCurrent;

    public CalenderEventForm(String daytoDisplay, int dateToDisplay, ArrayList<GetEvent> eventList, int isDateBiggerThenCurrent) {
        this.daytoDisplay = daytoDisplay;
        this.dateToDisplay = dateToDisplay;
        this.eventList = eventList;
        this.isDateBiggerThenCurrent = isDateBiggerThenCurrent;
    }

    public CalenderEventForm() {

    }

    public String getDaytoDisplay() {
        return daytoDisplay;
    }

    public void setDaytoDisplay(String daytoDisplay) {
        this.daytoDisplay = daytoDisplay;
    }

    public int getDateToDisplay() {
        return dateToDisplay;
    }

    public void setDateToDisplay(int dateToDisplay) {
        this.dateToDisplay = dateToDisplay;
    }

    public ArrayList<GetEvent> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<GetEvent> eventList) {
        this.eventList = eventList;
    }

    public int getDateBiggerThenCurrent() {
        return isDateBiggerThenCurrent;
    }

    public void setDateBiggerThenCurrent(int dateBiggerThenCurrent) {
        isDateBiggerThenCurrent = dateBiggerThenCurrent;
    }

}
