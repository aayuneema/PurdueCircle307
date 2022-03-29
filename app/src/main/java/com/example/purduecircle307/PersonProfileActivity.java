package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView  userName, userProfileName, userBio, userMajor, userGender, userGraduationDate;
    private CircleImageView userProfileImage;

    private DatabaseReference FriendRequestRef, UsersRef;
    private FirebaseAuth mAuth;

    private String senderUserId;
    private String receiverUserId;
    private String CURRENT_STATE;

    private Button SendFriendRequestButton, DeclineFriendRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");

        InitializeFields();

        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String myUsername = snapshot.child("username").getValue().toString();
                    String myName = snapshot.child("name").getValue().toString();
                    String myBio = snapshot.child("bio").getValue().toString();
                    String myMajor = snapshot.child("major").getValue().toString();
                    String myGraduationDate = snapshot.child("graduationDate").getValue().toString();
                    String myGender = snapshot.child("gender").getValue().toString();

                    //Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
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
        
        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
        DeclineFriendRequestButton.setEnabled(false );

        if (!senderUserId.equals(receiverUserId)) {
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendFriendRequestButton.setEnabled(false);
                    //Users are not friends
                    if (CURRENT_STATE.equals("not_friends")) {
                        SendFriendRequestToUser();
                    }
                }
            });
        }
        else {
            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }
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
        SendFriendRequestButton = (Button) findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendRequestButton = (Button) findViewById(R.id.person_decline_friend_request_btn);

        CURRENT_STATE = "not_friends";
    }
}