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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
    private FirebaseAuth mAuth;

    @Before
    public void setUp() {
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            mAuth.signInWithEmailAndPassword("aayu123@gmail.com", "test123!")
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                fail();
                            }
                        }
                    });
        }

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

    @After
    public void tearDown() throws Exception {
        mAuth.signOut();
        browseTagsActivity = null;
    }
}