package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfilePostActivity extends AppCompatActivity {

    private RecyclerView postList;
    private DatabaseReference PostsRef, LikesRef, TagsRef, SavedRef;
    private FirebaseAuth mAuth;
    Boolean LikeChecker = false;
    Boolean SavedChecker = false;
    private String senderUserId;
    private String receiverUserId;
    private String userPostId;
    private String tagPostValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_post);

        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        SavedRef = FirebaseDatabase.getInstance().getReference().child("Saved");

        TagsRef = FirebaseDatabase.getInstance().getReference().child("Tags");
        senderUserId = mAuth.getCurrentUser().getUid();
        Object receiverObject = getIntent().getExtras().get("visit_user_id");
        Object userPostObject =  getIntent().getExtras().get("visit_post_id");
        Object tagPostObject = getIntent().getExtras().get("visit_tag_value");
        if (receiverObject != null ) {
            receiverUserId = receiverObject.toString();
            userPostId = null;
            tagPostValue = null;
        }
        else if (userPostObject != null) {
            receiverUserId = null;
            userPostId = userPostObject.toString();
            tagPostValue = null;
        }
        else {
            System.out.println("IN HERE");
            receiverUserId = null;
            userPostId = null;
            tagPostValue = tagPostObject.toString();
        }

        displayAllUsersPosts();
    }

    private void displayAllUsersPosts() {

        Query SortPostsInDescendingOrder;
        if (receiverUserId != null) {
            SortPostsInDescendingOrder = PostsRef.orderByChild("uid").equalTo(receiverUserId);
        }
        else if (userPostId != null) {
            SortPostsInDescendingOrder = PostsRef.orderByKey().equalTo(userPostId);
        }
        else {
            System.out.println(tagPostValue);
            SortPostsInDescendingOrder = PostsRef.orderByChild("tag").equalTo(tagPostValue);
        }

        FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(SortPostsInDescendingOrder, Posts.class)
                        .build();
        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {

                    @NonNull
                    @Override
                    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.all_posts_layout, parent, false);
                        return new PostsViewHolder(view);
                    }

                    @Override
                    //populateViewHolder
                    protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Posts model) {
                        //String PostKey = getItem(position).toString();
                        //String PostKey = PostsRef.getKey();
                        String PostKey = getRef(position).getKey();

                        holder.setName(model.getName());
                        holder.setTime(model.getTime());
                        holder.setDate(model.getDate());
                        holder.setTag(model.getTag());
                        holder.setDescription(model.getDescription());
                        holder.setProfileimage(getApplicationContext(), model.getProfileImage());
                        holder.setPostimage(getApplicationContext(), model.getPostimage());

                        holder.setLikeButtonStatus(PostKey);
                        holder.setSavedButtonStatus(PostKey);


                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent clickPostIntent = new Intent(UserProfilePostActivity.this, ClickPostActivity.class);
                                clickPostIntent.putExtra("PostKey", PostKey);
                                startActivity(clickPostIntent);
                            }
                        });

                        holder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent commentsIntent = new Intent(UserProfilePostActivity.this, CommentsActivity.class);
                                commentsIntent.putExtra("PostKey", PostKey);
                                startActivity(commentsIntent);
                            }
                        });

                        holder.LikePostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LikeChecker = true;
                                LikesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (LikeChecker.equals(true)) {
                                            if (snapshot.child(PostKey).hasChild(senderUserId)) {
                                                LikesRef.child(PostKey).child(senderUserId).removeValue();
                                                LikeChecker = false;
                                            } else {
                                                LikesRef.child(PostKey).child(senderUserId).setValue(true);
                                                LikeChecker = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        holder.SavedPostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SavedChecker = true;
                                SavedRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (SavedChecker.equals(true)) {
                                            if (snapshot.child(PostKey).hasChild(senderUserId)) {
                                                SavedRef.child(PostKey).child(senderUserId).removeValue();
                                                SavedChecker = false;
                                            } else {
                                                SavedRef.child(PostKey).child(senderUserId).setValue(true);
                                                SavedChecker = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });


                    }

                };

        firebaseRecyclerAdapter.startListening();
        postList.setAdapter(firebaseRecyclerAdapter);
        /*if (firebaseRecyclerAdapter.getItemCount() == 0) {
            Toast.makeText(UserProfilePostActivity.this, "This user has not made any posts yet.", Toast.LENGTH_SHORT).show();
        }*/

    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        ImageButton LikePostButton, CommentPostButton, SavedPostButton;
        TextView DisplayNoOfLikes;
        int countLikes;
        String currentUserId;
        DatabaseReference LikesRef;
        DatabaseReference SavedRef;


        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            LikePostButton = (ImageButton) mView.findViewById(R.id.like_button);
            SavedPostButton = (ImageButton) mView.findViewById(R.id.save_button);
            CommentPostButton = (ImageButton) mView.findViewById(R.id.comment_button);
            DisplayNoOfLikes = (TextView) mView.findViewById(R.id.display_no_of_likes);

            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            SavedRef = FirebaseDatabase.getInstance().getReference().child("Saved");

            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setLikeButtonStatus (final String PostKey) {
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(PostKey).hasChild(currentUserId)) {
                        countLikes = (int) snapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.like);
                        DisplayNoOfLikes.setText((Integer.toString(countLikes)+(" Likes")));
                    } else {
                        countLikes = (int) snapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.dislike);
                        DisplayNoOfLikes.setText((Integer.toString(countLikes)+(" Likes")));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        public void setSavedButtonStatus (final String PostKey) {
            SavedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(PostKey).hasChild(currentUserId)) {
                        SavedPostButton.setImageResource(R.drawable.save);
                    } else {
                        SavedPostButton.setImageResource(R.drawable.unsave);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public void setName(String name) {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(name);
        }

        public void setProfileimage(Context ctx, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.profile).into(image);
        }

        public void setTime(String time) {
            TextView postTime = (TextView) mView.findViewById(R.id.post_time);
            postTime.setText("   " + time);
        }

        public void setDate(String date) {
            TextView postDate = (TextView) mView.findViewById(R.id.date);
            postDate.setText("   " +  date);
        }

        public void setDescription(String description) {
            TextView postDescription = (TextView) mView.findViewById(R.id.post_description);
            postDescription.setText(description);
        }

        public void setTag(String tag) {
            TextView postTag = (TextView) mView.findViewById(R.id.post_tag);
            postTag.setText("#" + tag);
        }

        public void setPostimage (Context ctx, String postimage) {
            ImageView image = (ImageView) mView.findViewById(R.id.post_image);
            System.out.println(postimage);
            Picasso.with(ctx).load(postimage).into(image);
        }
    }
}