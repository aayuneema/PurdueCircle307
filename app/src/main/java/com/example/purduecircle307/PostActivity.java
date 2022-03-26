package com.example.purduecircle307;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    //private Toolbar mToolbar;
    private ProgressDialog loadingBar;
    private ImageButton SelectPostImage;
    private Button UpdatePostButton, CreateTagButton, BrowseTagsButton;
    private EditText PostDescription;
    private TextView PostTag;
    private Button PostAnonButton;
    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private String Description, Tag;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;

    private StorageReference PostsImagesRefrence;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id;
    private boolean isAnonymous = false;

    private long countPosts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        PostsImagesRefrence = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        SelectPostImage =(ImageButton) findViewById(R.id.select_post_image);
        UpdatePostButton = (Button) findViewById(R.id.update_post_button);
        PostDescription = (EditText) findViewById(R.id.post_description);
        PostTag = (TextView) findViewById(R.id.post_tag);
        CreateTagButton = (Button) findViewById(R.id.create_tag_button);
        BrowseTagsButton = (Button) findViewById(R.id.browse_tags_button);
        PostAnonButton = (Button) findViewById(R.id.post_anon_button);
        loadingBar = new ProgressDialog(this);
/*
        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");
*/
        ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent tagData = result.getData();
                            Tag = tagData.getStringExtra(Intent.EXTRA_TEXT);
                            PostTag.setText(Tag);
                        }
                    }
                });

        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        PostAnonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeVisibilty();
            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePostInfo();
            }
        });

        CreateTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CreateTag();
                Intent createTagIntent = new Intent(PostActivity.this, CreateTagActivity.class);
                activityResultLaunch.launch(createTagIntent);
            }
        });

        BrowseTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseTagIntent = new Intent(PostActivity.this, BrowseTagsActivity.class);
                activityResultLaunch.launch(browseTagIntent);
            }
        });
    }

    private void ChangeVisibilty() {
        isAnonymous = true;
        ValidatePostInfo();
    }

    private void ValidatePostInfo() {
        Description = PostDescription.getText().toString();
        Tag = PostTag.getText().toString();
        /*if(ImageUri == null) {
            Toast.makeText(this,"Please Select an Image", Toast.LENGTH_SHORT).show();
        }
        else */if(TextUtils.isEmpty(Description)) {
            Toast.makeText(this,"Please Write a Caption", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.equals(Tag, "Click on a button below to add your tag!")) {
            Toast.makeText(this,"Please Select or Create a Tag", Toast.LENGTH_SHORT).show();
        }
        else if (Description.length() > 500) {
            Toast.makeText(this,"Please keep your description under 500 characters", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Add New Post");
            loadingBar.setMessage("Please wait, while we are updating your new post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage();
        }
    }

    private void StoringImageToFirebaseStorage() {
        //get date
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());
        //get time
        //Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordDate.getTime());


        postRandomName = saveCurrentDate + saveCurrentTime;
        //test time print!
        //Toast.makeText(this,postRandomName, Toast.LENGTH_SHORT).show();


        StorageReference filePath = PostsImagesRefrence.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        UploadTask uploadTask = filePath.putFile(ImageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filePath.getDownloadUrl();
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
                    Toast.makeText(PostActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                }
                System.out.println("done");
                SavingPostInformationToDatabase();
            }
        });
        
//        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                if (task.isSuccessful()) {
//                    downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
//                    System.out.println("DOWNLOADURL: " + downloadUrl);
//                    Toast.makeText(PostActivity.this, "Image Uploaded to Storage!", Toast.LENGTH_SHORT).show();
//                    SavingPostInformationToDatabase();
//                } else {
//                    System.out.println("**ERROR**");
//                    String message = task.getException().getMessage();
//                    Toast.makeText(PostActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }

    private void SavingPostInformationToDatabase() {
        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    countPosts = snapshot.getChildrenCount();
                }
                else {
                    countPosts = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String userFullName = dataSnapshot.child("name").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    if (isAnonymous) {
                        userFullName = "Anonymous";
                    }

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", Description);
                    postsMap.put("postimage", downloadUrl);
                    postsMap.put("profileimage", userProfileImage);
                    postsMap.put("name", userFullName);
                    postsMap.put("tag", Tag);
                    postsMap.put("counter", countPosts); 
                    PostsRef.child(current_user_id + postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(PostActivity.this, "New Post is updated successfully.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else
                            {
                                Toast.makeText(PostActivity.this, "Error Occured while updating your post.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null) {
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}