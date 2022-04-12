package com.example.purduecircle307;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
            protected void onBindViewHolder(@NonNull final TagsViewHolder tagsViewHolder, int position, @NonNull Tags tags) {

                //friendsViewHolder.setDate(friends.getDate());
                final String usersIDs = getRef(position).getKey();
                System.out.println("USERIDS: " + usersIDs);
                final String tag = "#" + usersIDs;
                tagsViewHolder.setTag(tag);
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