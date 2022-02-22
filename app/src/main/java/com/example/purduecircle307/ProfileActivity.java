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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private EditText Name;
    private EditText UserName;
    private Button SaveProfileButton;
    //private CircleImageView ProfileImage;
    private ProgressDialog loadingBar;

    private  FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        Name = (EditText) findViewById(R.id.profile_name);
        UserName = (EditText) findViewById(R.id.profile_username);
        //ProfileImage = (CircleImageView) findViewById(R.id.settings_profile_image);
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

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"Name is a required field", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(username)){
            Toast.makeText(this,"Username is a required field", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Saving account info");
            loadingBar.setMessage("Loading. Please wait as your account info is being saved");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("name", name);
            userMap.put("bio", "none");
            userMap.put("dob", "none");

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