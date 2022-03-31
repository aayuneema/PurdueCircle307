package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BrowseTagsActivity extends AppCompatActivity {

    private ListView tagListView;

    private DatabaseReference tagRef;
    private List<String> databaseTags = new ArrayList<String>();
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_tags);

        tagListView = (ListView) findViewById(R.id.tag_list_view);

        tagRef = FirebaseDatabase.getInstance().getReference().child("Tags");
        ArrayAdapter<String> tagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, databaseTags);
        tagListView.setAdapter(tagsAdapter);
        loadingBar = new ProgressDialog(this);

        tagRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> databaseValues = (HashMap<String,Object>) dataSnapshot.getValue();
                Collection<Object> databaseObjectTags = databaseValues.values();
                for (Object objectTag : databaseObjectTags) {
                    databaseTags.add(Objects.toString(objectTag, null));
                }
                tagsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String message = error.getMessage();
                Toast.makeText(BrowseTagsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });

        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                loadingBar.setTitle("Selecting tag");
                loadingBar.setMessage("Loading. Please wait as your tag is being added");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                String selectedTag = databaseTags.get(i);
                Intent sendTagIntent = new Intent();
                sendTagIntent.putExtra(Intent.EXTRA_TEXT, selectedTag);
                setResult(RESULT_OK, sendTagIntent);
                loadingBar.dismiss();
                finish();
            }
        });
    }
}