package com.example.purduecircle307;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TagsActivity extends AppCompatActivity {

    private RecyclerView myTagList;
    private DatabaseReference UsersTagsRef, TagsRef;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        UsersTagsRef = FirebaseDatabase.getInstance().getReference().child("UsersTags");
        TagsRef = FirebaseDatabase.getInstance().getReference().child("Tags");

        myTagList = (RecyclerView) findViewById(R.id.tag_list);
        myTagList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        myTagList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();
    }

    private void DisplayAllFriends() {

        Query query = UsersTagsRef.child(online_user_id); // haven't implemented a proper list sort yet.
        FirebaseRecyclerOptions<Tags> options = new FirebaseRecyclerOptions.Builder<Tags>().setQuery(query, Tags.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Tags, TagsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TagsViewHolder tagsViewHolder, @SuppressLint("RecyclerView") int position, @NonNull Tags tags) {

                //friendsViewHolder.setDate(friends.getDate());
                final String usersIDs = getRef(position).getKey();
                System.out.println("USERIDS: " + usersIDs);
                final String tag = "#" + usersIDs;
                tagsViewHolder.setTag(tag);

                tagsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tagValue = getRef(position).getKey();
                        TagsRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String visit_tag_id = "";
                                for (DataSnapshot tagSnapshot : snapshot.getChildren()) {
                                    if(tagValue.equals(tagSnapshot.getValue().toString())) {
                                        visit_tag_id = tagSnapshot.getKey();
                                        break;
                                    }
                                }
                                Intent tagIntent = new Intent(TagsActivity.this, TagProfileActivity.class);
                                tagIntent.putExtra("visit_tag_id", visit_tag_id);
                                startActivity(tagIntent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }

            public TagsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_tags_display_layout, parent ,false);
                return new TagsViewHolder(view);
            }
        };
        adapter.startListening();
        myTagList.setAdapter(adapter);
    }
    public static class TagsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TagsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTag(String tag) {
            TextView myTag = (TextView) mView.findViewById(R.id.all_tags_tag_name);
            myTag.setText(tag);
        }
    }
}