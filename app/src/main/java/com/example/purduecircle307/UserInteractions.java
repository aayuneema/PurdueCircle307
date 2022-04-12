package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class UserInteractions extends AppCompatActivity {

    private DatabaseReference PostsRef, LikesRef;
    private FirebaseAuth mAuth;
    private String senderUserId, receiverUserId;
    private ArrayList<String[]> interactionTypeComment, interactionTypeLike, interactionTypeAll;
    private ArrayList<String> interactionTextComment, interactionTextLike, interactionTextAll;
    private ListView interactionListView;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interactions);

        mAuth = FirebaseAuth.getInstance();
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        senderUserId = mAuth.getCurrentUser().getUid();
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        interactionTypeComment = new ArrayList<String[]>();
        interactionTypeLike = new ArrayList<String[]>();
        interactionTypeAll = new ArrayList<String[]>();
        interactionTextComment = new ArrayList<String>();
        interactionTextLike = new ArrayList<String>();
        interactionTextAll = new ArrayList<String>();
        interactionListView = (ListView) findViewById(R.id.interaction_list_view);
        loadingBar = new ProgressDialog(this);

        displayAllInteractions();
    }

    private void displayAllInteractions() {
        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                interactionTextComment = new ArrayList<String>();
                interactionTypeComment = new ArrayList<String[]>();
                for(DataSnapshot postChild : dataSnapshot.getChildren()) {
                    String postKey = postChild.getKey();
                    DataSnapshot CommentsRef = postChild.child("Comments");
                    for (DataSnapshot comment : CommentsRef.getChildren()) {
                        String user = comment.child("uid").getValue(String.class);
                        if (user.equals(receiverUserId)) {
                            String[] interaction = new String[2];
                            interaction[0] = postKey;
                            interaction[1] = "Comment";
                            interactionTypeComment.add(interaction);
                            String creator = postChild.child("name").getValue().toString();
                            String date = comment.child("date").getValue(String.class);
                            String time = comment.child("time").getValue(String.class);
                            String text = "\uD83D\uDCAC Commented on " + creator + "'s post on "
                                    + date + " at " + time;
                            interactionTextComment.add(text);
                            break;
                        }
                    }
                }
                updatePosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LikesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                interactionTextLike = new ArrayList<String>();
                interactionTypeLike = new ArrayList<String[]>();
                for(DataSnapshot likeChild : snapshot.getChildren()) {
                    String postKey = likeChild.getKey();
                    Map<String, Object> databaseLikes = (HashMap<String,Object>) likeChild.getValue();
                    Collection<String> databaseLikeKeys = databaseLikes.keySet();
                    for (String likedUser : databaseLikeKeys) {
                        if (likedUser.equals(receiverUserId)) {
                            String[] interaction = new String[2];
                            interaction[0] = postKey;
                            interaction[1] = "Like";
                            interactionTypeLike.add(interaction);
                            Query getPost = PostsRef.orderByKey().equalTo(postKey);
                            getPost.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String creator = "";
                                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                        creator = userSnapshot.child("name").getValue(String.class);
                                    }
                                    int likeCount = databaseLikes.size() - 1;
                                    String text = "";
                                    if (likeCount == 0) {
                                        text = "❤️ Liked " + creator + "'s post";
                                    } else {
                                        text = "❤️ Liked " + creator + "'s post, along with "
                                                + likeCount + " other users";
                                    }
                                    int index = -1;
                                    for (int i = 0; i < interactionTypeLike.size(); i++) {
                                        if (postKey.equals(interactionTypeLike.get(i)[0])) {
                                            index = i;
                                        }
                                    }
                                    if (index != -1 && interactionTextLike.size() > index) {
                                        //DO NOTHING
                                    }
                                    else {
                                        interactionTextLike.add(text);
                                    }
                                    updatePosts();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        interactionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                loadingBar.setTitle("Fetching post");
                loadingBar.setMessage("Loading. Please wait as the post is being fetched");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                String postKey = interactionTypeAll.get(i)[0];
                Intent postInteractionIntent = new Intent(UserInteractions.this, UserProfilePostActivity.class);
                postInteractionIntent.putExtra("visit_post_id", postKey);
                startActivity(postInteractionIntent);

                loadingBar.dismiss();
            }
        });
    }

    private void updatePosts() {
        interactionTextAll = new ArrayList<String>();
        interactionTypeAll = new ArrayList<String[]>();

        interactionTextAll.addAll(interactionTextComment);
        interactionTextAll.addAll(interactionTextLike);
        interactionTypeAll.addAll(interactionTypeComment);
        interactionTypeAll.addAll(interactionTypeLike);

        ArrayAdapter<String> interactionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, interactionTextAll);
        interactionListView.setAdapter(interactionsAdapter);
    }
}