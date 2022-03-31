package com.example.purduecircle307;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;
import android.view.inputmethod.InputConnection;

import androidx.test.espresso.ViewAction;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class CreateTagActivityTest
{
    @Rule
    public ActivityTestRule<CreateTagActivity> createTagActivityTestRule = new ActivityTestRule<>(CreateTagActivity.class);

    private CreateTagActivity createTagActivity;

    @Before
    public void setup()
    {
        createTagActivity = createTagActivityTestRule.getActivity();
    }

    @Test
    public void viewsMustBeVisible()
    {
        createTagActivity = createTagActivityTestRule.getActivity();
        //InputConnection inputConnection = searchView.findViewById(R.id.search_src_text).onCreateInputConnection(new EditorInfo());

        onView(withId(R.id.create_new_tag_button)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.new_tag)).check(matches(isCompletelyDisplayed()));

        //onView(withId(R.id.create_new_tag_button)).perform(click(), replaceText("testTag!"), closeSoftKeyboard());
        onView(withId(R.id.create_new_tag_button)).perform(click());
    }

}