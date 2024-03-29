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
public class AreFriendsTest {

    private DatabaseReference FriendRef;

    @Before
    public void setup() {
        FriendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
    }

    /*REPLACE USERS WITH TEST USERS*/
    //Scenario: friend 1 is friends with friend 2.
    // friend2 is friends with friend1.
    // friend3 has no friends.

    //Test for a friend1 is friends with friend2
    @Test
    public void oneToTwo() {
        String friendOne = "7HhmHeAMHpWbvlKVq0dj4jI3kP53";
        String friendTwo = "GThc7hhTfZUdjq4sMcCyZhPOFN32";
        FriendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean works = false;
                Map<String, Object> users = (Map<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()) {
                    if (friendOne.equals(entry.getKey())) {
                        Map<String, String> requesters = (Map) entry.getValue();
                        for (Map.Entry<String, String> entry1 : requesters.entrySet()) {
                            if (friendTwo.equals(entry1.getKey())) {
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

    //Test for a friend2 is friends with friend1
    @Test
    public void twoToOne() {
        String friendOne = "7HhmHeAMHpWbvlKVq0dj4jI3kP53";
        String friendTwo = "GThc7hhTfZUdjq4sMcCyZhPOFN32";
        FriendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean works = false;
                Map<String, Object> users = (Map<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()) {
                    if (friendTwo.equals(entry.getKey())) {
                        Map<String, String> requesters = (Map) entry.getValue();
                        for (Map.Entry<String, String> entry1 : requesters.entrySet()) {
                            if (friendOne.equals(entry1.getKey())) {
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

    //Test to see if friend1 is friends with friend3
    @Test
    public void alreadyFriends() {
        String friendOne = "7HhmHeAMHpWbvlKVq0dj4jI3kP53";
        String friendThree = "0oIZa3J7T0Nu6WLYACgm9ALmK4K3";
        FriendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean works = false;
                Map<String, Object> users = (Map<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()) {
                    if (friendOne.equals(entry.getKey())) {
                        Map<String, String> requesters = (Map) entry.getValue();
                        for (Map.Entry<String, String> entry1 : requesters.entrySet()) {
                            if (friendThree.equals(entry1.getKey())) {
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
}
