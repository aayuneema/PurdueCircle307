package com.example.purduecircle307;

import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;

@RunWith(AndroidJUnit4ClassRunner.class)
public class FeedTest {

    String userID = "stc19eF1kFSyEe1H9Dj89Ur8o7t2"; //Velma Dinkly
    private DatabaseReference FriendsRef, UsersTagsRef, FeedRef;

    ArrayList<String> followedTags = new ArrayList<String>();
    ArrayList<String> followedUsers = new ArrayList<String>();
    ArrayList<String> feed = new ArrayList<String>();

    @Before
    public void setup() {
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(userID);
        UsersTagsRef = FirebaseDatabase.getInstance().getReference().child("UsersTags").child(userID);
        FeedRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("feed");

        FriendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //followedUsers = new ArrayList<String>();
                for (DataSnapshot userChild : snapshot.getChildren()) {
                    followedUsers.add(userChild.getKey()); //adds UID
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UsersTagsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //followedTags = new ArrayList<String>();
                for (DataSnapshot tagChild : snapshot.getChildren()) {
                    followedTags.add(tagChild.getKey()); //adds tag value
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FeedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot feedChild : snapshot.getChildren()) {
                    feed.add(feedChild.getKey()); //adds post key
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Test
    public void unfollowedTagsFollowedUsers() {
        FeedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot posts : snapshot.getChildren()) {
                    String uid = posts.child("uid").getValue().toString();
                    String tag = posts.child("tag").getValue().toString();
                    if (!followedTags.contains(tag)) {
                        assertTrue(followedUsers.contains(uid));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Test
    public void unfollowedUsersFollowedTags() {
        FeedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot posts : snapshot.getChildren()) {
                    String uid = posts.child("uid").getValue().toString();
                    String tag = posts.child("tag").getValue().toString();
                    if (!followedUsers.contains(uid)) {
                        assertTrue(followedTags.contains(tag));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Test
    public void followedUsersFollowedTags() {
        FeedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot posts : snapshot.getChildren()) {
                    String uid = posts.child("uid").getValue().toString();
                    String tag = posts.child("tag").getValue().toString();
                    if (followedUsers.contains(uid) && followedTags.contains(tag)) {
                        assertTrue(Collections.frequency(feed, posts.getKey()) == 1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
