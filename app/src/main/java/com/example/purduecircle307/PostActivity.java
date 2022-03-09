package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    //private Toolbar mToolbar;
    private ImageButton SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;
    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private String Description;

    private StorageReference PostsImagesRefrence;

    private String saveCurrentDate, saveCurrentTime, postRandomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        PostsImagesRefrence = FirebaseStorage.getInstance().getReference();

        SelectPostImage =(ImageButton) findViewById(R.id.select_post_image);
        UpdatePostButton = (Button) findViewById(R.id.update_post_button);
        PostDescription = (EditText) findViewById(R.id.post_description);
/*

        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");
*/
        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePostInfo();
            }
        });
    }

    private void ValidatePostInfo() {
        Description = PostDescription.getText().toString();
        if(ImageUri == null) {
            Toast.makeText(this,"Please Select an Image", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description)) {
            Toast.makeText(this,"Please Write a Caption", Toast.LENGTH_SHORT).show();
        }
        else {
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
        Toast.makeText(this,postRandomName, Toast.LENGTH_SHORT).show();


        StorageReference filePath = PostsImagesRefrence.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PostActivity.this, "Image Uploaded to Storage!", Toast.LENGTH_SHORT).show();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
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