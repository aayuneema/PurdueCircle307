package com.example.purduecircle307;

import com.google.firebase.database.core.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static org.junit.Assert.*;

public class PostActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> postActivityActivityTestRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    private ActivityScenario<MainActivity> postActivity = null;

    @Before
    public void setUp() throws Exception {
        postActivity = postActivityActivityTestRule.getScenario();
    }

    @Test
    public void testLaunch() {
        View view = postActivity.findViewById(R.id.tvMainText);
        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        postActivity = null;
    }

}