package com.example.purduecircle307;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicProfileActivity extends AppCompatActivity {

    private EditText userName, userProfileName, userBio, userDob, userMajor, userGender, userCountry, userGraduationDate;
    private CircleImageView userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        userName = (EditText) findViewById(R.id.public_profile_username);
        userProfileName = (EditText) findViewById(R.id.public_profile_full_name);
        userBio = (EditText) findViewById(R.id.public_profile_status);
        userDob = (EditText) findViewById(R.id.public_profile_dob);
        userMajor = (EditText) findViewById(R.id.public_profile_major);
        userGender = (EditText) findViewById(R.id.public_profile_Gender);
        userCountry = (EditText) findViewById(R.id.public_profile_Country);
        userGraduationDate = (EditText) findViewById(R.id.public_profile_graduationDate);
        userProfileImage = (CircleImageView) findViewById(R.id.public_profile_image);
    }
}