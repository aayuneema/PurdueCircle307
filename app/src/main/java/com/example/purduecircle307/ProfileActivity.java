package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private EditText Name;
    private EditText UserName;
    //private EditText userMajor, userGender, userGraduationDate;
    private Button SaveProfileButton;
    private CircleImageView ProfileImage;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private DatabaseReference usernameRef;
    private DatabaseReference ProfileActivityUserRef;
    private List<String> databaseUsernames = new ArrayList<String>();


    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ProfileActivityUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        usernameRef = FirebaseDatabase.getInstance().getReference().child("Usernames");
        usernameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("profileImage"))
                    {
                        String image = dataSnapshot.child("profileImage").getValue().toString();
                        //System.out.println("image = " + image);
                        //String image2 = image.getResult().getStorage().getDownloadUrl().toString();
                        //Toast.makeText(SettingsActivity.this, image, Toast.LENGTH_SHORT).show();
                        Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.profile).into(ProfileImage);
                    }
                    /*else
                    {
                        Toast.makeText(ProfileActivity.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }*/
                }
                Map<String, Object> databaseValues = (HashMap<String,Object>) dataSnapshot.getValue();
                if (databaseValues != null) {
                    Collection<Object> databaseObjectUsernames = databaseValues.values();
                    for (Object objectUsername : databaseObjectUsernames) {
                        databaseUsernames.add(Objects.toString(objectUsername));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (mAuth.getCurrentUser() == null) {
            currentUserID = null;
            UsersRef = null;
        }
        else {
            currentUserID = mAuth.getCurrentUser().getUid();
            UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        }

        Name = (EditText) findViewById(R.id.profile_name);
        UserName = (EditText) findViewById(R.id.profile_username);
        //String myProfileImage = ProfileActivityUserRef.child("profileImage").getValue().toString();
        ProfileImage = (CircleImageView) findViewById(R.id.settings_profile_image);
        //Picasso.with(ProfileActivity.this).load(myProfileImage).into(ProfileImage);
        //userMajor = (EditText) findViewById(R.id.settings_major);
        //userGender = (EditText) findViewById(R.id.settings_Gender);
        //userGraduationDate = (EditText) findViewById(R.id.settings_graduationDate);
        SaveProfileButton = (Button) findViewById(R.id.profile_SaveButton);
        loadingBar = new ProgressDialog(this);

        SaveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAccountSetupInfo();
            }
        });
    }

    private void saveAccountSetupInfo() {
        String username = UserName.getText().toString();
        String name = Name.getText().toString();
        //String major = userMajor.getText().toString();
        //String gender = userGender.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"Name is a required field", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(username)){
            Toast.makeText(this,"Username is a required field", Toast.LENGTH_SHORT).show();
        }
        else if (databaseUsernames.contains(username)) {
            Toast.makeText(this, "This username has already been taken. " +
                    "Please choose another one", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Saving account info");
            loadingBar.setMessage("Loading. Please wait as your account info is being saved");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("name", name);
            userMap.put("bio", "Bio");
            userMap.put("dob", "Date of Birth");
            userMap.put("major", "Major");
            userMap.put("graduationDate", "Graduation Date");
            userMap.put("gender", "Gender");
            userMap.put("country", "Country");
            userMap.put("profileImage", "Profile Image");

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        sendUserToMainActivity();
                        Toast.makeText(ProfileActivity.this, "Account info was successfully saved",  Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(ProfileActivity.this, "Error: " + message,  Toast.LENGTH_SHORT).show();
                    }
                    //loadingBar.dismiss();
                }
            });

            usernameRef.push().setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        String message = task.getException().getMessage();
                        Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                    loadingBar.dismiss();
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}