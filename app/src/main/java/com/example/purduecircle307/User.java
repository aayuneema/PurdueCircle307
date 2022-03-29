package com.example.purduecircle307;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class User {

        private String userName; //username
        private String displayName; //full name
        private String bio;
        private String email;
        private String password;
        private boolean loggedIn;
        private String profileImage;
        private ArrayList<User> friends = new ArrayList<User>();
        private ArrayList<User> followedUsers = new ArrayList<User>();
        private ArrayList<String> followedTopics = new ArrayList<String>();
        private ArrayList<User> userRequests = new ArrayList<User>();


        public User(String userId, String displayName) {
            this.userName = userId;
            this.displayName = displayName;
        }

        public User(String userId, String displayName, String email, String password, String profileImage) {
            this.userName = userId;
            this.displayName = displayName;
            this.email = email;
            this.password = password;
            this.profileImage = profileImage;
        }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void followUser(User user) {
            followedUsers.add(user);
        }

        public void followTopic(String topic) {
            followedTopics.add(topic);
        }

        public void unfollowUser(User user) {
            if (followedUsers.contains(user)) {
                followedUsers.remove(user);
            }
        }

        public void unfollowTopic(String topic) {
            if (followedTopics.contains(topic)) {
                followedTopics.remove(topic);
            }
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return this.email;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPassword() {
            return this.password;
        }

        public void setLoggedIn(boolean loggedIn) {
            this.loggedIn = loggedIn;
        }

        public boolean getLoggedIn() {
            return this.loggedIn;
        }
}
