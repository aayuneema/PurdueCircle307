package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CreateTagActivity extends AppCompatActivity {

    private Button createTagButton;
    private EditText newTag;
    private ProgressDialog loadingBarCreation;

    private DatabaseReference tagRef;
    private List<String> databaseTags = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tag);

        tagRef = FirebaseDatabase.getInstance().getReference().child("Tags");
        tagRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> databaseValues = (HashMap<String,Object>) dataSnapshot.getValue();
                Collection<Object> databaseObjectTags = databaseValues.values();
                for (Object objectTag : databaseObjectTags) {
                    databaseTags.add(Objects.toString(objectTag).toLowerCase());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        createTagButton = (Button) findViewById(R.id.create_new_tag_button);
        newTag = (EditText) findViewById(R.id.new_tag);
        loadingBarCreation = new ProgressDialog(this);

        createTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmTag();
            }
        });
    }

    private void confirmTag() {
        String createdTag = newTag.getText().toString();

        if (TextUtils.isEmpty(createdTag)) {
            Toast.makeText(this, "Please include a new tag to include in your post.", Toast.LENGTH_SHORT).show();
        }
        else if (createdTag.length() < 3 || !createdTag.matches(".*[a-zA-Z]+.*")) {
            Toast.makeText(this, "Please make sure that your tag contains at least 1 letter " +
                    "and is at least 3 characters long.", Toast.LENGTH_SHORT).show();
        }
        else if (databaseTags.contains(createdTag.toLowerCase())) {
            Toast.makeText(this, "This tag already exists. " +
                    "Please navigate to \"Browse For Tag\" to use this tag.", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBarCreation.setTitle("Creating new tag");
            loadingBarCreation.setMessage("Loading. Please wait as your tag is added to the database");
            loadingBarCreation.show();
            loadingBarCreation.setCanceledOnTouchOutside(true);

            tagRef.push().setValue(createdTag).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Intent sendTagIntent = new Intent();
                        sendTagIntent.putExtra(Intent.EXTRA_TEXT, createdTag);
                        setResult(RESULT_OK, sendTagIntent);
                        loadingBarCreation.dismiss();
                        finish();
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(CreateTagActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        loadingBarCreation.dismiss();
                    }
                }
            });
        }
    }
}