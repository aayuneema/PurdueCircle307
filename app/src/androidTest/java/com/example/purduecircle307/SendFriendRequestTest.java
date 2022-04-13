package com.example.purduecircle307;

import static org.junit.Assert.assertEquals;

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

import java.util.Map;

@RunWith(AndroidJUnit4ClassRunner.class)
public class SendFriendRequestTest {

    private DatabaseReference FriendRequestRef;

    @Before
    public void setup() {
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
    }

    //Test for a user sending a request
    @Test
    public void sendRequest() {
        String senderID = "zeF5hqL5FnWOOJrv8FOkKN9ct7n2";
        String receiverID = "mw0HYxn7Ykb0WXbYVc7xoRBIXl62";
        FriendRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean works = false;
                Map<String, Object> users = (Map<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()) {
                    if (senderID.equals(entry.getKey())) {
                        Map<String, String> requesters = (Map) entry.getValue();
                        for (Map.Entry<String, String> entry1 : requesters.entrySet()) {
                            if (receiverID.equals(entry1.getKey())) {
                                works = true;
                            }
                        }
                    }
                }
                assertEquals(true, works);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Test for a user receiving a request
    @Test
    public void receiveRequest() {
        String senderID = "zeF5hqL5FnWOOJrv8FOkKN9ct7n2";
        String receiverID = "mw0HYxn7Ykb0WXbYVc7xoRBIXl62";
        FriendRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean works = false;
                Map<String, Object> users = (Map<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()) {
                    if (receiverID.equals(entry.getKey())) {
                        Map<String, String> requesters = (Map) entry.getValue();
                        for (Map.Entry<String, String> entry1 : requesters.entrySet()) {
                            if (senderID.equals(entry1.getKey())) {
                                works = true;
                            }
                        }
                    }
                }
                assertEquals(true, works);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Test for users that are friends
    @Test
    public void alreadyFriends() {
        String userOne = "7HhmHeAMHpWbvlKVq0dj4jI3kP53";
        String userTwo = "GThc7hhTfZUdjq4sMcCyZhPOFN32";
        FriendRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean works = false;
                Map<String, Object> users = (Map<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()) {
                    if (userOne.equals(entry.getKey())) {
                        Map<String, String> requesters = (Map) entry.getValue();
                        for (Map.Entry<String, String> entry1 : requesters.entrySet()) {
                            if (userTwo.equals(entry1.getKey())) {
                                works = true;
                            }
                        }
                    }
                }
                assertEquals(false, works);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
