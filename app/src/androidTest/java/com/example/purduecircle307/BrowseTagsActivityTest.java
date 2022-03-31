package com.example.purduecircle307;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
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

public class BrowseTagsActivityTest {

    @Rule
    public ActivityTestRule<BrowseTagsActivity> browseTagsActivityTestRule = new ActivityTestRule<BrowseTagsActivity>(BrowseTagsActivity.class);

    private BrowseTagsActivity browseTagsActivity;

    @Before
    public void setUp() {
        browseTagsActivity = browseTagsActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        onView(withId(R.id.tag_list_view)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void isScrollable() {
        onView(withId(R.id.tag_list_view))
                .perform(ViewActions.swipeUp());
    }

    @Test
    public void checkTagsInsideList() {
        onData(anything()).inAdapterView(withId(R.id.tag_list_view)).atPosition(0).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        browseTagsActivity = null;
    }
}