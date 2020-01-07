package com.smalsus.redhorizonvbr.db.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "UserDeliveryAddress")
public class UserDeliveryAddress implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "buildingName")
    private String buildingName;
    @ColumnInfo(name = "area")
    private String area;
    @ColumnInfo(name = "City")
    private String city;
    @ColumnInfo(name = "state")
    private String state;
    @ColumnInfo(name = "pin_code")
    private String pin_code;
    @ColumnInfo(name = "phone_number")
    private String phone_number;


    public UserDeliveryAddress(String name, String buildingName, String area, String city, String state, String pin_code,String phone_number) {
        this.name = name;
        this.buildingName = buildingName;
        this.area = area;
        this.city = city;
        this.state = state;
        this.pin_code = pin_code;
        this.phone_number= phone_number;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPin_code(String pin_code) {
        this.pin_code = pin_code;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getArea() {
        return area;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPin_code() {
        return pin_code;
    }
}
