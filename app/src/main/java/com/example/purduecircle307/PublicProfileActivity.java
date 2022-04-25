package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicProfileActivity extends AppCompatActivity {

    private TextView userName, userProfileName, userBio, userDob, userMajor, userGender, userCountry, userGraduationDate;
    private CircleImageView userProfileImage;

    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;
    private boolean isGuestUser = false;

    private Button ViewCreatedPostsButton;
    private Button ViewSavedPostsButton;
    private Button ViewInteractionsButton;

    private String downloadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser().isAnonymous())  {
            isGuestUser = true;
        }
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        userName = (TextView) findViewById(R.id.public_profile_username);
        userProfileName = (TextView) findViewById(R.id.public_profile_full_name);
        userBio = (TextView) findViewById(R.id.public_profile_status);
        //userDob = (TextView) findViewById(R.id.public_profile_dob);
        userMajor = (TextView) findViewById(R.id.public_profile_major);
        userGender = (TextView) findViewById(R.id.public_profile_Gender);
        //userCountry = (TextView) findViewById(R.id.public_profile_Country);
        userGraduationDate = (TextView) findViewById(R.id.public_profile_graduationDate);
        userProfileImage = (CircleImageView) findViewById(R.id.public_profile_image);
        ViewCreatedPostsButton = (Button) findViewById(R.id.view_posts_button); //vpb
        ViewSavedPostsButton = (Button) findViewById(R.id.view_saved_button);
        ViewInteractionsButton = (Button) findViewById(R.id.view_interactions_button);


        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    downloadUrl = snapshot.child("profileImage").getValue().toString();
                    String myUsername = snapshot.child("username").getValue().toString();
                    String myName = snapshot.child("name").getValue().toString();
                    String myBio = snapshot.child("bio").getValue().toString();
                    //String myDob = snapshot.child("dob").getValue().toString();
                    String myMajor = snapshot.child("major").getValue().toString();
                    String myGraduationDate = snapshot.child("graduationDate").getValue().toString();
                    String myGender = snapshot.child("gender").getValue().toString();
                    //String myCountry = snapshot.child("country").getValue().toString();

                    Picasso.with(PublicProfileActivity.this).load(downloadUrl).into(userProfileImage);
                    userName.setText("@" + myUsername);
                    userProfileName.setText(myName);
                    userBio.setText(myBio);
                    //userDob.setText("DOB: " + myDob);
                    userMajor.setText("Major: " + myMajor);
                    userGender.setText("Gender: " + myGender);
                    //userCountry.setText("Country: " + myCountry);
                    userGraduationDate.setText("Graduation Date: " + myGraduationDate);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ViewCreatedPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPostActivity();
            }
        });

        ViewSavedPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedPostActivity();
            }
        });

        ViewInteractionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToInteractionsActivity();
            }
        });


    }

    private void sendUserToInteractionsActivity() {
        if (isGuestUser) {
            Toast.makeText(PublicProfileActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent userInteractionsIntent = new Intent(PublicProfileActivity.this, UserInteractions.class);
            userInteractionsIntent.putExtra("visit_user_id", currentUserId);
            startActivity(userInteractionsIntent);
        }
    }

    private void sendUserToPostActivity() {
        if (isGuestUser) {
            Toast.makeText(PublicProfileActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent userPostIntent = new Intent(PublicProfileActivity.this, UserProfilePostActivity.class);
            userPostIntent.putExtra("visit_user_id", currentUserId);
            startActivity(userPostIntent);
        }
    }

    private void savedPostActivity() {
        if (isGuestUser) {
            Toast.makeText(PublicProfileActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent userPostIntent = new Intent(PublicProfileActivity.this, SavedActivity.class);
            userPostIntent.putExtra("visit_user_id", currentUserId);
            startActivity(userPostIntent);
        }
    }
}