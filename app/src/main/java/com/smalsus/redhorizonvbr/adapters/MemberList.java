package com.smalsus.redhorizonvbr.adapters;

import java.util.ArrayList;

public class MemberList
{
    private String name;
    private String image;

    public MemberList(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
