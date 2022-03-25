package com.example.purduecircle307;

public class FindFriends {
    public String profileImage, name, status;

    public FindFriends() {

    }
    public FindFriends(String profileImage, String name, String status) {
        this.profileImage = profileImage;
        this.name = name;
        this.status = status;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
