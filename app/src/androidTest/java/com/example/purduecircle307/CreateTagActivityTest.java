package com.example.purduecircle307;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RunWith(AndroidJUnit4ClassRunner.class)
public class CreateTagActivityTest {
    @Rule
    public ActivityTestRule<CreateTagActivity> createTagActivityTestRule = new ActivityTestRule<>(CreateTagActivity.class);

    private CreateTagActivity createTagActivity;
    private DatabaseReference tagRef;
    private List<String> databaseTags;

    @Before
    public void setup() {
        createTagActivity = createTagActivityTestRule.getActivity();
        tagRef = FirebaseDatabase.getInstance().getReference().child("Tags");
        databaseTags = new ArrayList<String>();
    }

    @Test
    public void testVisibility() {
        onView(withId(R.id.create_new_tag_button)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.new_tag)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void testCreateTagSuccess() {
        onView(withId(R.id.new_tag)).perform(clearText(), typeText("testCaseTag"), closeSoftKeyboard());
        onView(withId(R.id.create_new_tag_button)).perform(click());
    }

    @Test
    public void testCreateTagFail() {
        onView(withId(R.id.new_tag)).perform(clearText(), typeText("a"), closeSoftKeyboard());
        onView(withId(R.id.create_new_tag_button)).perform(click());

        tagRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> databaseValues = (HashMap<String,Object>) dataSnapshot.getValue();
                Collection<Object> databaseObjectTags = databaseValues.values();
                for (Object objectTag : databaseObjectTags) {
                    databaseTags.add(Objects.toString(objectTag).toLowerCase());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        assertFalse(databaseTags.contains("a"));
    }

    @After
    public void tearDown() throws Exception {
        createTagActivity = null;
    }
}
