package com.example.purduecircle307;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class ProfileActivity extends AppCompatActivity {

    private EditText Name;
    private EditText UserName;
    private Button SaveProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Name = (EditText) findViewById(R.id.profile_name);
        UserName = (EditText) findViewById(R.id.profile_username);
        SaveProfileButton = (Button) findViewById(R.id.profile_SaveButton);
    }
}