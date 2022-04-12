package com.example.purduecircle307;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FindTagsActivity extends AppCompatActivity {

    //private Toolbar mToolbar;
    private ImageButton searchButton;
    private EditText searchInputText;

    private RecyclerView searchResultList;

    private DatabaseReference allTagsDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_tags);

        /*mToolbar = (Toolbar) findViewById(R.id.find_friends_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");*/

        allTagsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Tags");

        searchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));

        searchButton = (ImageButton) findViewById(R.id.search_tags_button);
        searchInputText = (EditText) findViewById(R.id.search_box_input);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchBoxInput = searchInputText.getText().toString();
                searchFriends(searchBoxInput);
            }
        });
    }

    private void searchFriends(String searchBoxInput) {
        Toast.makeText(this, "Searching...", Toast.LENGTH_LONG).show();

        //Query searchFriendsQuery = allUsersDatabaseRef.orderByChild("name")
        //.startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");
        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(allTagsDatabaseRef.orderByValue().startAt(searchBoxInput)
                                .endAt(searchBoxInput + "\uf8ff"), String.class)
                        .build();

        /*Query getPost = allTagsDatabaseRef.orderByValue().startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");
        getPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String creator = "";
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    System.out.println(userSnapshot.getValue().getClass());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        System.out.println("after options");
        FirebaseRecyclerAdapter<String, FindTagsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<String, FindTagsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindTagsViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull String model) {
                        holder.tag.setText("#" + model);
                        System.out.println("TAG NAME: " + model);
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visit_tag_id = getRef(position).getKey();
                                //System.out.println(visit_tag_id);
                                Intent tagIntent = new Intent(FindTagsActivity.this, TagProfileActivity.class);
                                tagIntent.putExtra("visit_tag_id", visit_tag_id);
                                startActivity(tagIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindTagsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.all_tags_display_layout, parent, false);
                        return new FindTagsViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        searchResultList.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }

    public static class FindTagsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tag;

        public FindTagsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            tag = mView.findViewById(R.id.all_tags_tag_name);
        }

        public void setTag(String bio) {
            TextView myTag = (TextView) mView.findViewById(R.id.all_tags_tag_name);
            myTag.setText(bio);
        }
    }

}