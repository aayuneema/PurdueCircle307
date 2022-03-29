package com.example.purduecircle307;

import com.google.firebase.database.core.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityActivityTestRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    private ActivityScenario<MainActivity> mainActivity = null;

    @Before
    public void setUp() throws Exception {
        mainActivity = mainActivityActivityTestRule.getScenario();
    }

    @Test
    public void testLaunch() {
        View view = mainActivity.findViewById(R.id.tvMainText);
        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
    }
}