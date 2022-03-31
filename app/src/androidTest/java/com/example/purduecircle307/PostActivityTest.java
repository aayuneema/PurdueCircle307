package com.example.purduecircle307;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.*;

import android.view.View;

public class PostActivityTest {

    @Rule
    public ActivityTestRule<PostActivity> postActivityTestRule = new ActivityTestRule<PostActivity>(PostActivity.class);

    private PostActivity postActivity = null;

    @Before
    public void setUp() throws Exception {
        postActivity = postActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View view = postActivity.findViewById(R.id.post_tag);
        assertNotNull(view);
        view = postActivity.findViewById(R.id.browse_tags_button);
        assertNotNull(view);
        view = postActivity.findViewById(R.id.create_tag_button);
        assertNotNull(view);
        view = postActivity.findViewById(R.id.update_post_button);
        assertNotNull(view);
        view = postActivity.findViewById(R.id.post_anon_button);
        assertNotNull(view);
        view = postActivity.findViewById(R.id.post_description);
        assertNotNull(view);
        view = postActivity.findViewById(R.id.select_post_image);
        assertNotNull(view);
    }

    @Test
    public void testScroll() {
    }

    @After
    public void tearDown() throws Exception {
        postActivity = null;
    }
}