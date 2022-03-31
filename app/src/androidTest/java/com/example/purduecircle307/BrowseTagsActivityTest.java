package com.example.purduecircle307;

import static org.junit.Assert.*;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

    private BrowseTagsActivity browseTagsActivity = null;

    @Before
    public void setUp() throws Exception {
        browseTagsActivity = browseTagsActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View view = browseTagsActivity.findViewById(R.id.tag_list_view);
        assertTrue(view.getVisibility() == View.VISIBLE);
        assertNotNull(view);
    }

    @Test
    public void checkTagsInsideList() {
        browseTagsActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                /*List<String> testTags = new ArrayList<String>();
                for (int i = 0; i < 100; i++) {
                    testTags.add("tag" + i);
                }
                browseTagsActivity = browseTagsActivityTestRule.getActivity();
                ArrayAdapter<String> tagsAdapter = new ArrayAdapter<String>(browseTagsActivity, android.R.layout.simple_list_item_1, testTags);
                ListView tagsList = browseTagsActivity.findViewById(R.id.tag_list_view);
                tagsList.setAdapter(tagsAdapter);

                for (int i = 0; i < tagsList.getCount(); i++) {
                    tagsList.performClick(tagsList.getAdapter().getView(i, null, null),
                            position, list.getAdapter().getItemId(position))
                }*/

            }
        });
    }

    @After
    public void tearDown() throws Exception {
        browseTagsActivity = null;
    }
}