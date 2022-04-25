package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Button;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText userName, userProfname, userBio, userDob, userMajor, userGender, userCountry, userGraduationDate;
    private Button UpdateAccountSettingsButton;
    private Button deleteAccountButton;
    private CircleImageView userProfImage;
    Uri imageUri;
    private String downloadUrl;
    private ProgressDialog loadingBar;

    private DatabaseReference SettingsuserRef;
    private FirebaseAuth mAuth;
    private StorageReference UserProfileImageRef;

    private String currentUserId;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsuserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        //creating folder in firebase storaged named "profile images"
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profileImage");
        loadingBar = new ProgressDialog(this);

        mToolbar = findViewById(R.id.settings_toolbar);
        //setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Profile");
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.settings_username);
        userProfname = (EditText) findViewById(R.id.settings_profile_full_name);
        userBio = (EditText) findViewById(R.id.settings_status);
        userDob = (EditText) findViewById(R.id.settings_dob);
        userMajor = (EditText) findViewById(R.id.settings_major);
        userGender = (EditText) findViewById(R.id.settings_Gender);
        userCountry = (EditText) findViewById(R.id.settings_Country);
        userGraduationDate = (EditText) findViewById(R.id.settings_graduationDate);
        userProfImage = (CircleImageView) findViewById(R.id.settings_profile_image);
        UpdateAccountSettingsButton = (Button) findViewById(R.id.update_account_settings_buttons);
        deleteAccountButton = (Button) findViewById(R.id.delete_account_settings_buttons);

        SettingsuserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    downloadUrl = dataSnapshot.child("profileImage").getValue().toString();
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    String myName = dataSnapshot.child("name").getValue().toString();
                    String myBio = dataSnapshot.child("bio").getValue().toString();
                    String myDob = dataSnapshot.child("dob").getValue().toString();
                    String myMajor = dataSnapshot.child("major").getValue().toString();
                    String mygraduationDate = dataSnapshot.child("graduationDate").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();

                    Picasso.with(SettingsActivity.this).load(downloadUrl).into(userProfImage);
                    userName.setText(myUsername);
                    userProfname.setText(myName);
                    userBio.setText(myBio);
                    userDob.setText(myDob);
                    userMajor.setText(myMajor);
                    userGender.setText(myGender);
                    userCountry.setText(myCountry);
                    userGraduationDate.setText(mygraduationDate);

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
                sendUserToDeleteAccount();
            }
        });

        //redirect user to mobile phone gallery
        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        SettingsuserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("profileImage"))
                    {
                        String image = dataSnapshot.child("profileImage").getValue().toString();
                        //System.out.println("image = " + image);
                        //String image2 = image.getResult().getStorage().getDownloadUrl().toString();
                        //Toast.makeText(SettingsActivity.this, image, Toast.LENGTH_SHORT).show();
                        Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.profile).into(userProfImage);
                    }
                    else
                    {
                        Toast.makeText(SettingsActivity.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            imageUri = data.getData();
            userProfImage.setImageURI(imageUri);

            StorageReference filepath = UserProfileImageRef.child(currentUserId + ".jpg");
            UploadTask uploadtask = filepath.putFile(imageUri);

            Task<Uri> urlTask = uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        System.out.println("Upload 1 " + downloadUri);
                        if (downloadUri != null) {

                            downloadUrl = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            System.out.println("Upload 2 " + downloadUrl);

                        }

                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(SettingsActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                    }
                    System.out.println("done");
                    //SavingPostInformationToDatabase();

                }
            });


        }

    }

    private void SavingPostInformationToDatabase() {
        SettingsuserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userFullName = dataSnapshot.child("name").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    SettingsuserRef.child("profileImage").setValue(downloadUrl);
                   /*
                    HashMap postsMap = new HashMap();
                    postsMap.put("profileImage", downloadUrl);

                    //SettingsuserRef.child(postRandomName + current_user_id).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {

                    SettingsuserRef.updateChildren("profileImage").addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                sendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "New Post is updated successfully.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                Toast.makeText(SettingsActivity.this, "Error Occured while updating your post.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    }); */
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ValidateAccountInfo() {
        String username = userName.getText().toString();
        String name = userProfname.getText().toString();
        String bio = userBio.getText().toString();
        String dob = userDob.getText().toString();
        String major = userMajor.getText().toString();
        String graduationDate = userGraduationDate.getText().toString();
        String gender = userGender.getText().toString();
        String country = userCountry.getText().toString();
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
            UpdateAccountInfo(username, name, bio, dob, major, graduationDate, gender, country );
        }
    }

    private void UpdateAccountInfo(String username, String name, String bio, String dob, String major, String graduationDate, String gender, String country ) {
        HashMap userMap = new HashMap();
        userMap.put("username", username);
        userMap.put("name", name);
        userMap.put("bio", bio);
        userMap.put("dob", dob);
        userMap.put("major", major);
        userMap.put("graduationDate", graduationDate);
        userMap.put("gender", gender);
        userMap.put("country", country);
        userMap.put("profileImage", downloadUrl);

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

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToDeleteAccount() {
        Intent mainIntent = new Intent(SettingsActivity.this, DeleteAccount.class);
        startActivity(mainIntent);
        finish();
    }
}