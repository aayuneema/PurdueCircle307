package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.EditText;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import com.squareup.picasso.Picasso;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText userName, name, bio, dob;
    private Button UpdateAccountSettingsButton;
   // private CircleImageView userProfImage;

    private DatabaseReference SettingsuserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;

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
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.settings_username);
        name = (EditText) findViewById(R.id.settings_profile_full_name);
        bio = (EditText) findViewById(R.id.settings_bio);
        dob = (EditText) findViewById(R.id.settings_dob);
       // userProfImage = (CircleImageView) findViewById(R.id.settings_profile_image);
        UpdateAccountSettingsButton = (Button) findViewById(R.id.update_account_settings_buttons);

        SettingsuserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                  //  String myProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    String myName = dataSnapshot.child("name").getValue().toString();
                    String myBio = dataSnapshot.child("bio").getValue().toString();
                    String myDob = dataSnapshot.child("dob").getValue().toString();

                    //Picasso.with(SettingsActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    userName.setText(myUsername);
                    name.setText(myName);
                    bio.setText(myBio);
                    dob.setText(myDob);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}