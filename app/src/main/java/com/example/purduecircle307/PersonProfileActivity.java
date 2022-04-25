package com.example.purduecircle307;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView  userName, userProfileName, userBio, userMajor, userGender, userGraduationDate;
    private CircleImageView userProfileImage;

    private DatabaseReference FriendRequestRef, UsersRef, FriendsRef, BlockedUsersRef;
    private FirebaseAuth mAuth;

    private String senderUserId;
    private String receiverUserId;
    private String CURRENT_STATE;
    private String BLOCK_STATE;
    private String saveCurrentDate;
    private boolean isGuestUser = false;

    private Button SendFriendRequestButton, DeclineFriendRequestButton, 
            ViewPostsButton, ViewInteractionsButton, BlockButton, DMButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser().isAnonymous())  {
            isGuestUser = true;
        }
        senderUserId = mAuth.getCurrentUser().getUid();
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        BlockedUsersRef = FirebaseDatabase.getInstance().getReference().child("BlockedUsers");

        InitializeFields();

        if (receiverUserId.equals(senderUserId)) {
            ViewPostsButton.setVisibility(View.INVISIBLE);
            ViewPostsButton.setEnabled(false);
            ViewInteractionsButton.setVisibility(View.INVISIBLE);
            ViewInteractionsButton.setEnabled(false);
        }

        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String myProfileImage = snapshot.child("profileImage").getValue().toString();
                    String myUsername = snapshot.child("username").getValue().toString();
                    String myName = snapshot.child("name").getValue().toString();
                    String myBio = snapshot.child("bio").getValue().toString();
                    String myMajor = snapshot.child("major").getValue().toString();
                    String myGraduationDate = snapshot.child("graduationDate").getValue().toString();
                    String myGender = snapshot.child("gender").getValue().toString();

                    //Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    Picasso.with(PersonProfileActivity.this).load(myProfileImage).into(userProfileImage);
                    userName.setText("@" + myUsername);
                    userProfileName.setText(myName);
                    userBio.setText(myBio);
                    userMajor.setText("Major: " + myMajor);
                    userGender.setText("Gender: " + myGender);
                    userGraduationDate.setText("Graduation Date: " + myGraduationDate);

                    MaintainanceOfButtons();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ViewPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPostActivity();
            }
        });

        ViewInteractionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToInteractionsActivity();
            }
        });

        DMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToChatActivity();
            }
        });
        
        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
        DeclineFriendRequestButton.setEnabled(false);

        if (!senderUserId.equals(receiverUserId) && !mAuth.getCurrentUser().isAnonymous()) {
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendFriendRequestButton.setEnabled(false);
                    //Users are not friends
                    if (CURRENT_STATE.equals("not_friends")) {
                        SendFriendRequestToUser();
                    }

                    //User has sent a request to the other user
                    if (CURRENT_STATE.equals("request_sent")) {
                        CancelFriendRequest();
                    }

                    //User receives a request
                    if (CURRENT_STATE.equals("request_received")) {
                        AcceptFriendRequest();
                    }

                    //User no longer wants to be friends
                    if (CURRENT_STATE.equals("friends")) {
                        Unfriend();
                    }
                }
            });
            BlockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //User wants to block user
                    if (BLOCK_STATE.equals("not_blocked")) {
                        Block();
                    }

                    //User wants to unblock user
                    if (BLOCK_STATE.equals("blocked")) {
                        Unblock();
                    }
                }
            });
        }
        else {
            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void Block() {
        Unfriend();
        BlockedUsersRef.child(receiverUserId).child(senderUserId).child("blockstatus").setValue("bottom_blocked_top").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    BlockedUsersRef.child(senderUserId).child(receiverUserId).child("blockstatus").setValue("top_blocked_bottom").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SendFriendRequestButton.setEnabled(false);
                            SendFriendRequestButton.setVisibility(View.INVISIBLE);
                            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                            DeclineFriendRequestButton.setEnabled(false);
                            BlockButton.setText("Unblock User");
                            BLOCK_STATE = "blocked";
                        }
                    });
                }
            }
        });
    }

    private void Unblock() {
        Unfriend();
        BlockedUsersRef.child(receiverUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    BlockedUsersRef.child(senderUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SendFriendRequestButton.setEnabled(true);
                                SendFriendRequestButton.setVisibility(View.VISIBLE);
                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestButton.setEnabled(false);
                                ViewPostsButton.setEnabled(true);
                                ViewPostsButton.setVisibility(View.VISIBLE);
                                ViewInteractionsButton.setEnabled(true);
                                ViewInteractionsButton.setVisibility(View.VISIBLE);
                                ViewInteractionsButton.setVisibility(View.VISIBLE);
                                userName.setVisibility(View.VISIBLE);
                                userProfileName.setVisibility(View.VISIBLE);
                                userBio.setVisibility(View.VISIBLE);
                                userMajor.setVisibility(View.VISIBLE);
                                userGender.setVisibility(View.VISIBLE);
                                userGraduationDate.setVisibility(View.VISIBLE);
                                userProfileImage.setVisibility(View.VISIBLE);
                                BlockButton.setText("Block User");
                                BLOCK_STATE = "not_blocked";
                            }
                        }
                    });
                }
            }
        });
    }

    private void Unfriend() {
        FriendsRef.child(senderUserId).child(receiverUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FriendsRef.child(receiverUserId).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                SendFriendRequestButton.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                SendFriendRequestButton.setText("Send Friend Request");
                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        FriendsRef.child(senderUserId).child(receiverUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FriendsRef.child(receiverUserId).child(senderUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FriendRequestRef.child(senderUserId).child(receiverUserId).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FriendRequestRef.child(receiverUserId).child(senderUserId).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        SendFriendRequestButton.setEnabled(true);
                                                                        CURRENT_STATE = "friends";
                                                                        SendFriendRequestButton.setText("Unfriend");
                                                                        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                        DeclineFriendRequestButton.setEnabled(false);
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });
    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(senderUserId).child(receiverUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FriendRequestRef.child(receiverUserId).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                SendFriendRequestButton.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                SendFriendRequestButton.setText("Send Friend Request");
                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintainanceOfButtons() {
        FriendRequestRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(receiverUserId)) {
                    String request_type = snapshot.child(receiverUserId).child("request_type").getValue().toString();
                    if (request_type.equals("sent")) {
                        CURRENT_STATE = "request_sent";
                        SendFriendRequestButton.setText("Cancel Friend Request");
                        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                        DeclineFriendRequestButton.setEnabled(false);
                    }
                    else if (request_type.equals("received")) {
                        CURRENT_STATE = "request_received";
                        SendFriendRequestButton.setText("Accept Friend Request");
                        DeclineFriendRequestButton.setVisibility(View.VISIBLE);
                        DeclineFriendRequestButton.setEnabled(true);
                        DeclineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CancelFriendRequest();
                            }
                        });
                    }
                }
                else {
                    FriendsRef.child(senderUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(receiverUserId)) {
                                CURRENT_STATE = "friends";
                                SendFriendRequestButton.setText("Unfriend");
                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestButton.setEnabled(false);
                            }
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

        BlockedUsersRef.child(receiverUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(senderUserId)) {
                    String block_type = snapshot.child(senderUserId).child("blockstatus").getValue().toString();
                    //sender blocked receiver
                    if (block_type.equals("bottom_blocked_top")) {
                        SendFriendRequestButton.setEnabled(false);
                        SendFriendRequestButton.setVisibility(View.INVISIBLE);
                        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                        DeclineFriendRequestButton.setEnabled(false);
                        ViewPostsButton.setEnabled(true);
                        ViewPostsButton.setVisibility(View.VISIBLE);
                        ViewInteractionsButton.setEnabled(true);
                        ViewInteractionsButton.setVisibility(View.VISIBLE);
                        BlockButton.setText("Unblock User");
                        BLOCK_STATE = "blocked";
                    }
                    //receiver is blocked by sender
                    else if (block_type.equals("top_blocked_bottom")) {
                        SendFriendRequestButton.setEnabled(false);
                        SendFriendRequestButton.setVisibility(View.INVISIBLE);
                        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                        DeclineFriendRequestButton.setEnabled(false);
                        BlockButton.setEnabled(false);
                        BlockButton.setVisibility(View.INVISIBLE);
                        ViewPostsButton.setEnabled(false);
                        ViewPostsButton.setVisibility(View.INVISIBLE);
                        ViewInteractionsButton.setEnabled(false);
                        ViewInteractionsButton.setVisibility(View.INVISIBLE);
                        userName.setVisibility(View.INVISIBLE);
                        userProfileName.setVisibility(View.INVISIBLE);
                        userBio.setVisibility(View.INVISIBLE);
                        userMajor.setVisibility(View.INVISIBLE);
                        userGender.setVisibility(View.INVISIBLE);
                        userGraduationDate.setVisibility(View.INVISIBLE);
                        userProfileImage.setVisibility(View.INVISIBLE);
                        BLOCK_STATE = "blocked";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendFriendRequestToUser() {
        FriendRequestRef.child(senderUserId).child(receiverUserId).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FriendRequestRef.child(receiverUserId).child(senderUserId).child("request_type").setValue("received")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        SendFriendRequestButton.setEnabled(true);
                                        CURRENT_STATE = "request_sent";
                                        SendFriendRequestButton.setText("Cancel Friend Request");
                                        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                        DeclineFriendRequestButton.setEnabled(false);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void InitializeFields() {
        userName = (TextView) findViewById(R.id.person_profile_username);
        userProfileName = (TextView) findViewById(R.id.person_profile_full_name);
        userBio = (TextView) findViewById(R.id.person_profile_status);
        userMajor = (TextView) findViewById(R.id.person_profile_major);
        userGender = (TextView) findViewById(R.id.person_profile_Gender);
        userGraduationDate = (TextView) findViewById(R.id.person_profile_graduationDate);
        userProfileImage = (CircleImageView) findViewById(R.id.person_profile_image);
        SendFriendRequestButton = (Button) findViewById(R.id.follow_tag_btn);
        DeclineFriendRequestButton = (Button) findViewById(R.id.person_decline_friend_request_btn);
        BlockButton = (Button) findViewById(R.id.person_block_btn);
        ViewPostsButton = (Button) findViewById(R.id.view_posts_button);
        ViewInteractionsButton = (Button) findViewById(R.id.view_interactions_button);
        DMButton = (Button) findViewById(R.id.dm_btn);

        CURRENT_STATE = "not_friends";
        BLOCK_STATE = "not_blocked";
    }

    private void sendUserToPostActivity() {
        if (isGuestUser) {
            Toast.makeText(PersonProfileActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent userPostIntent = new Intent(PersonProfileActivity.this, UserProfilePostActivity.class);
            userPostIntent.putExtra("visit_user_id", receiverUserId);
            startActivity(userPostIntent);
        }
    }

    private void sendUserToInteractionsActivity() {
        if (isGuestUser) {
            Toast.makeText(PersonProfileActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent userInteractionsIntent = new Intent(PersonProfileActivity.this, UserInteractions.class);
            userInteractionsIntent.putExtra("visit_user_id", receiverUserId);
            startActivity(userInteractionsIntent);
        }
    }

    private void sendUserToChatActivity() {
        Intent messagingIntent = new Intent(PersonProfileActivity.this, ChatActivity.class);
        messagingIntent.putExtra("visit_user_id", receiverUserId);
        messagingIntent.putExtra("userName", userName.toString());
        startActivity(messagingIntent);
    }
}