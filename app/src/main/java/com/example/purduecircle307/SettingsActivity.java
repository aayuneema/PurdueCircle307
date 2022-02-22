package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText userName, userProfname, userBio, userDob;
    private Button UpdateAccountSettingsButton;
    private Button deleteAccountButton;
    private CircleImageView userProfImage;

    private DatabaseReference SettingsuserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsuserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        mToolbar = findViewById(R.id.settings_toolbar);
        //setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.settings_username);
        userProfname = (EditText) findViewById(R.id.settings_profile_full_name);
        userBio = (EditText) findViewById(R.id.settings_bio);
        userDob = (EditText) findViewById(R.id.settings_dob);
        userProfImage = (CircleImageView) findViewById(R.id.settings_profile_image);
        UpdateAccountSettingsButton = (Button) findViewById(R.id.update_account_settings_buttons);
        deleteAccountButton = (Button) findViewById(R.id.delete_account);

        SettingsuserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //String myProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    String myName = dataSnapshot.child("name").getValue().toString();
                    String myBio = dataSnapshot.child("bio").getValue().toString();
                    String myDob = dataSnapshot.child("dob").getValue().toString();

                    //Picasso.with(SettingsActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    userName.setText(myUsername);
                    userProfname.setText(myName);
                    userBio.setText(myBio);
                    userDob.setText(myDob);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UpdateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                ValidateAccountInfo();
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //TODO: delete account
            }
        });

        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("Image/*");
                startActivityForResult(galleryIntent, Gallery_Pick );

            }
        });
    }


    private void ValidateAccountInfo() {
        String username = userName.getText().toString();
        String name = userProfname.getText().toString();
        String bio = userBio.getText().toString();
        String dob = userDob.getText().toString();
        //add profile image once variable added to firebase

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this,"Please write your username.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please write your full name.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(bio)){
            Toast.makeText(this,"Please write your bio.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(dob)){
            Toast.makeText(this,"Please write your date of birth.", Toast.LENGTH_SHORT).show();
        } else {
            UpdateAccountInfo(username, name, bio, dob);
        }
    }

    private void UpdateAccountInfo(String username, String name, String bio, String dob) {
        HashMap userMap = new HashMap();
        userMap.put("username", username);
        userMap.put("name", name);
        userMap.put("bio", bio);
        userMap.put("dob", dob);

        SettingsuserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    sendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Account Settings Successfully Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Error occured while updating account settings", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}