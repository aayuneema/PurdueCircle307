package com.example.purduecircle307;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.view.View;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicProfileActivityTest {

    @Rule
    public ActivityTestRule<PublicProfileActivity> publicProfileActivityTestRule = new ActivityTestRule<PublicProfileActivity>(PublicProfileActivity.class);

    private PublicProfileActivity publicProfileActivity = null;
    private DatabaseReference FriendRef;
    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;


    @Before
    public void setUp() throws Exception {
        publicProfileActivity = publicProfileActivityTestRule.getActivity();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
    }



    @Test
    public void testLaunch() {
       if (currentUserId == NULL ) {
            View view = publicProfileActivity.findViewById(R.id.public_profile_username);
            assertNull(view);
            /*view = publicProfileActivity.findViewById(R.id.public_profile_full_name);
            assertNotNull(view);
            view = publicProfileActivity.findViewById(R.id.public_profile_status);
            assertNotNull(view);
            view = publicProfileActivity.findViewById(R.id.public_profile_major);
            assertNotNull(view);
            view = publicProfileActivity.findViewById(R.id.public_profile_Gender);
            assertNotNull(view);
            view = publicProfileActivity.findViewById(R.id.public_profile_graduationDate);
            assertNotNull(view); */
        }

    }

}
