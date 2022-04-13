package com.example.purduecircle307;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FindFriendsActivityTest {

    @Rule
    public ActivityTestRule<FindFriendsActivity> findFriendsActivityRule = new ActivityTestRule<FindFriendsActivity>(FindFriendsActivity.class);

    private FindFriendsActivity findFriendsActivity = null;

    @Before
    public void setUp() throws Exception {
        findFriendsActivity = findFriendsActivityRule.getActivity();
    }

    @Test
    public void viewsMustBeVisible()
    {
        findFriendsActivity = findFriendsActivityRule.getActivity();
        onView(withId(R.id.search_friends_button)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.search_friends_button)).perform(click());
    }

    @Test
    public void testScroll() {
        onView(withId(R.id.search_result_list)).perform(ViewActions.swipeUp());
    }

    @Test
    public void findFriendsText() {
        onView(withId(R.id.search_box_input)).perform(clearText(), typeText("purduepete"), closeSoftKeyboard());
        onView(withId(R.id.search_friends_button)).perform(click());

    }
}
