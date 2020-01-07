package com.smalsus.redhorizonvbr.view.activities;

class TestingSelectedContacts
{
    private String imageProfileUrl;
    private String firstName;
    private String LastName;

    public TestingSelectedContacts(String imageProfileUrl, String firstName, String lastName) {
        this.imageProfileUrl = imageProfileUrl;
        this.firstName = firstName;
        LastName = lastName;
    }

    public String getImageProfileUrl() {
        return imageProfileUrl;
    }

    public void setImageProfileUrl(String imageProfileUrl) {
        this.imageProfileUrl = imageProfileUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }
}
