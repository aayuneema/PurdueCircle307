package com.example.purduecircle307;

public class Posts {
    public String date;
    public String description;
    public String name;
    public String postimage;
    public String profileImage;
    public String time;
    public String uid;
    public String tag;
    public boolean isAnon;

    public Posts() {

    }

    public Posts(String date, String description, String name, String postimage, String profileImage, String time, String uid, String tag, boolean isAnon) {
        this.date = date;
        this.description = description;
        this.name = name;
        this.postimage = postimage;
        this.profileImage = profileImage;
        this.time = time;
        this.uid = uid;
        this.tag = tag;
        this.isAnon = isAnon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTag() { return tag; }

    public void setTag(String tag) { this.tag = tag; }
  
    public boolean getAnon() {
        return this.isAnon;
    }

    public void setAnon(boolean isAnon) {
        this.isAnon = isAnon;
    }
}
