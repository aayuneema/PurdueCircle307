package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    private DatabaseReference profileUserRef, UsersRef;
    private FirebaseAuth mAuth;

    private String senderUserId;
    private String receiverUserId;
    private String CURRENT_STATE;

    private Button SendFriendRequestButton, CancelFriendRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

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
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        CancelFriendRequestButton.setVisibility(View.INVISIBLE);
        CancelFriendRequestButton.setEnabled(false );

        if (!senderUserId.equals(receiverUserId)) {
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendFriendRequestButton.setEnabled(false);
                }
            });
        }
        else {
            CancelFriendRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }
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
        CancelFriendRequestButton = (Button) findViewById(R.id.person_cancel_friend_request_btn);

        CURRENT_STATE = "not_friends";
    }
}