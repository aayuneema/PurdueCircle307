package com.example.purduecircle307;

public class FindFriends {
    public String profileImage, username, bio;

    public FindFriends() {

    }
    public FindFriends(String profileImage, String username, String status, String bio) {
        this.profileImage = profileImage;
        this.username = username;
        this.bio = bio;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
