package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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
    private FirebaseAuth mAuth;
    private String PostKey;
    private String CurrentUserID;
    private String DatabaseUserID;
    private String description;
    private String image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        PostImage = (ImageView)  findViewById(R.id.click_post_image);
        PostDescription = (TextView) findViewById((R.id.click_post_description));
        DeletePostButton = (Button) findViewById((R.id.clickPostDeleteButton));
        EditPostButton = (Button) findViewById((R.id.clickPostEditButton));

        DeletePostButton.setVisibility(View.INVISIBLE);
        EditPostButton.setVisibility(View.INVISIBLE);

        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                description = snapshot.child("description").getValue().toString();
                image = snapshot.child("postimage").getValue().toString();
                //image = "com.google.android.gms.tasks.zzu@31bc3de";
                DatabaseUserID = snapshot.child("uid").getValue().toString();

                PostDescription.setText(description);
                Picasso.with(ClickPostActivity.this).load(image).into(PostImage);
                System.out.println(CurrentUserID + " || " + DatabaseUserID);
                if (CurrentUserID.equals(DatabaseUserID)) {
                    DeletePostButton.setVisibility(View.VISIBLE);
                    EditPostButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}