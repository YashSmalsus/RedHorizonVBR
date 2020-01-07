package com.smalsus.redhorizonvbr;

public class SelectedContactsItemsInformation
{
    private String ImageURL;
    private String firstName;
    private String lastName;

    public SelectedContactsItemsInformation(String imageURL, String firstName, String lastName) {
        ImageURL = imageURL;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
