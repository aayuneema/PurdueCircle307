package com.example.purduecircle307;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TagProfileActivity extends AppCompatActivity {

    private TextView tagTextView;

    private DatabaseReference UsersRef, TagsRef, UserTagsRef;
    private FirebaseAuth mAuth;

    private String senderUserId;
    private String tagId;
    String tag;
    private String CURRENT_STATE;
    private String saveCurrentDate;

    private Button FollowButton;
    private Button ViewPostsButton;

    private Boolean isGuestUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_profile);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser().isAnonymous())  {
            isGuestUser = true;
        }
        senderUserId = mAuth.getCurrentUser().getUid();
        tagId = getIntent().getExtras().get("visit_tag_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        TagsRef = FirebaseDatabase.getInstance().getReference().child("Tags");
        UserTagsRef = FirebaseDatabase.getInstance().getReference().child("UsersTags");
        FollowButton = (Button) findViewById(R.id.follow_tag_btn);
        ViewPostsButton = (Button) findViewById(R.id.view_tag_posts);

        InitializeFields();

        UsersRef.child(tagId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /*System.out.println("SNAPSHOT KEY: " + snapshot);
                if (snapshot.exists()) {
                    String tag = snapshot.getValue().toString();
                    System.out.println("TAG " + tag);
                    tagTextView.setText("#" + tag);

                    MaintainanceOfButtons();
                }
                else {
                    System.out.println("Uh oh");
                }*/
                if (TagsRef.child(tagId) != null) {
                    TagsRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if (snapshot.getKey().equals(tagId)) {
                                tag = snapshot.getValue().toString();
                                tagTextView.setText("#" + tag);
                                MaintainanceOfButtons();
                            }
                            else {
                                System.out.println(snapshot.getValue().toString());
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ViewPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToPostActivity();
            }
        });

        if (!senderUserId.equals(tagId) && !mAuth.getCurrentUser().isAnonymous()) {
            FollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FollowButton.setEnabled(false);
                    //Users are not friends
                    if (CURRENT_STATE.equals("following")) {
                        UnfollowTag();
                    }

                    //User has sent a request to the other user
                    if (CURRENT_STATE.equals("not_following")) {
                        FollowTag();
                    }
                }
            });
        }
        else {
            FollowButton.setVisibility(View.INVISIBLE);
        }
    }

    private void UnfollowTag() {
        UserTagsRef.child(senderUserId).child(tagId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FollowButton.setEnabled(true);
                            CURRENT_STATE = "not_following";
                            FollowButton.setText("Follow");
                        }
                    }
                });
    }

    private void FollowTag() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());
        UserTagsRef.child(senderUserId).child(tag).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FollowButton.setEnabled(true);
                    CURRENT_STATE = "following";
                    FollowButton.setText("Unfollow");
                }
            }
        });
    }

    private void MaintainanceOfButtons() {
        UserTagsRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(tagId)) {
                    CURRENT_STATE = "following";
                    FollowButton.setText("Unfollow");
                }
                else {
                    CURRENT_STATE = "not_following";
                    FollowButton.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void InitializeFields() {
        tagTextView = (TextView) findViewById(R.id.tag_name);
        FollowButton = (Button) findViewById(R.id.follow_tag_btn);

        CURRENT_STATE = "not_following";
    }

    private void sendUserToPostActivity() {
        if (isGuestUser) {
            Toast.makeText(TagProfileActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent tagPostIntent = new Intent(TagProfileActivity.this, UserProfilePostActivity.class);
            DatabaseReference TagRef = TagsRef.child(tagId);
            TagRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String tagValue = dataSnapshot.getValue(String.class);
                    tagPostIntent.putExtra("visit_tag_value", tagValue);
                    startActivity(tagPostIntent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}