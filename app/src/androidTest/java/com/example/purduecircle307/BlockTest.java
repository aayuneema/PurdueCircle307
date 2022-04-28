package com.example.purduecircle307;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

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
public class BlockTest {

    public ActivityTestRule<PersonProfileActivity> personProfileActivityActivityTestRule = new ActivityTestRule<>(PersonProfileActivity.class);
    private PersonProfileActivity personProfileActivity;
    private DatabaseReference FriendRef, BlockedUsersRef, AllUsersRef;

    @Before
    public void setup() {
        personProfileActivity = personProfileActivityActivityTestRule.getActivity();

        FriendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        BlockedUsersRef = FirebaseDatabase.getInstance().getReference().child("BlockedUsers");
        AllUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    //Testing to check whether user1 can block user2
    @Test
    public void block() {
        //User 33
        String userOne = "DgucizIn9UPWOg8oZivr9GqXdDr1";
        //User 34
        String userTwo = "Fwl4XtBPF2dDFHr4ARL7o9V9pF23";
        BlockedUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean works = false;
                Map<String, Object> users = (Map<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()) {
                    if (userOne.equals(entry.getKey())) {
                        Map<String, String> blockedUsers = (Map) entry.getValue();
                        for (Map.Entry<String, String> entry1 : blockedUsers.entrySet()) {
                            if (userTwo.equals(entry1.getKey())) {
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

    //Testing to check whether user1 can unblock user2
    @Test
    public void unblock() {
        String userOne = "7HhmHeAMHpWbvlKVq0dj4jI3kP53";
        String userTwo = "GThc7hhTfZUdjq4sMcCyZhPOFN32";
        BlockedUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean works = true;
                Map<String, Object> users = (Map<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()) {
                    if (userOne.equals(entry.getKey())) {
                        Map<String, String> blockedUsers = (Map) entry.getValue();
                        for (Map.Entry<String, String> entry1 : blockedUsers.entrySet()) {
                            if (userTwo.equals(entry1.getKey())) {
                                works = false;
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
        String userOne = "7HhmHeAMHpWbvlKVq0dj4jI3kP53";
        String userTwo = "0oIZa3J7T0Nu6WLYACgm9ALmK4K3";
        BlockedUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean works = false;
                Map<String, Object> users = (Map<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()) {
                    if (userOne.equals(entry.getKey())) {
                        Map<String, String> requesters = (Map) entry.getValue();
                        for (Map.Entry<String, String> entry1 : requesters.entrySet()) {
                            if (userTwo.equals(entry1.getKey())) {
                                Button DMButton = (Button) personProfileActivity.findViewById(R.id.dm_btn);
                                if (DMButton.getVisibility() == View.INVISIBLE) {
                                    works = true;
                                }
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
