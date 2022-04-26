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
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ChatActivityTest {

    @Rule
    public ActivityTestRule<ChatActivity> chatActivityChatActivityTestRule = new ActivityTestRule(ChatActivity.class);

    private ChatActivity chatActivity = null;

    @Before
    public void setUp() throws Exception {
        chatActivity = chatActivityChatActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {

        chatActivity = chatActivityChatActivityTestRule.getActivity();
        onView(withId(R.id.send_image_button)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.send_message_button)).perform(click());
        /*
        View view = chatActivity.findViewById(R.id.send_image_button);
        assertNotNull(view);
        view = chatActivity.findViewById(R.id.send_message_button);
        assertNotNull(view);
        view = chatActivity.findViewById(R.id.input_message);
        assertNotNull(view);
        view = chatActivity.findViewById(R.id.messages_list_of_users);
        assertNotNull(view);
        view = chatActivity.findViewById(R.id.chat_bar_layout);
        assertNotNull(view);

         */
    }


    @Test
    public void testScroll() {
        onView(withId(R.id.messages_list_of_users)).perform(ViewActions.swipeUp());
    }

    @After
    public void tearDown() throws Exception {
        chatActivity = null;
    }

    @Test
    public void onCreate() {
    }
}