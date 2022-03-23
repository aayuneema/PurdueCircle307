package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView PostImage;
    private TextView PostDescription;
    private Button DeletePostButton;
    private Button EditPostButton;
    private DatabaseReference ClickPostRef;
    private String PostKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        PostImage = (ImageView)  findViewById(R.id.click_post_image);
        PostDescription = (TextView) findViewById((R.id.click_post_description));
        DeletePostButton = (Button) findViewById((R.id.clickPostDeleteButton));
        EditPostButton = (Button) findViewById((R.id.clickPostEditButton));

        PostKey = getIntent().getExtras().get("PostKey").toString();
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child("PostKey");
        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String description = snapshot.child("description").getValue().toString();
                String image = snapshot.child("postimage").getValue().toString();

                PostDescription.setText(description);
                Picasso.with(ClickPostActivity.this).load(image).into(PostImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}