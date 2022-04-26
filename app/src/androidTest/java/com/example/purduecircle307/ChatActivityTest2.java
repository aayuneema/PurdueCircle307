package com.example.purduecircle307;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

public class ChatActivityTest2 {

    @Rule
    public ActivityTestRule<ChatActivity> chatActivityChatActivityTestRule = new ActivityTestRule(ChatActivity.class);

    private ChatActivity chatActivity = null;

    @Test
    public void setUp() throws Exception {
        chatActivity = chatActivityChatActivityTestRule.getActivity();
    }


}
