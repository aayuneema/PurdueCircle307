package com.example.purduecircle307;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// User Story 3:
// page listing all the users and topics that I already follow so that I can keep track of who and what I follow
@RunWith(AndroidJUnit4ClassRunner.class)
public class FriendsActivityTest {
    @Rule
    public ActivityTestRule<FriendsActivity> friendsActivityTestRule = new ActivityTestRule<FriendsActivity>(FriendsActivity.class);

    @Rule
    public ActivityTestRule<TagsActivity> tagsActivityTestRule = new ActivityTestRule<TagsActivity>(TagsActivity.class);

    private FriendsActivity friendsActivity = null;
    private TagsActivity tagsActivity = null;
    private DatabaseReference FriendRef;

    @Before
    public void setUp() throws Exception {
        FriendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendsActivity = friendsActivityTestRule.getActivity();
        tagsActivity = tagsActivityTestRule.getActivity();
    }

    //JUnit test to check that the UI for friends Activity exits and is visible.
    @Test
    public void checkFriendsUI() {
        View view = friendsActivity.findViewById(R.id.friend_list);
        assertNotNull(view);
    }

    //JUnit test to check that the UI for Tags Activity exits and is visible.
    @Test
    public void checkTagsUI() {
        View view = tagsActivity.findViewById(R.id.tag_list);
        assertNotNull(view);
    }

    //Junit test to check that users can select a friend within the Friends page.
    @Test
    public void checkingFriend() {
        String friendOne = "7HhmHeAMHpWbvlKVq0dj4jI3kP53";
        String friendTwo = "GThc7hhTfZUdjq4sMcCyZhPOFN32";
        FriendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean clickTest = false;
                onView(withId(R.id.create_new_tag_button)).perform(click());
                assertEquals(true, clickTest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
