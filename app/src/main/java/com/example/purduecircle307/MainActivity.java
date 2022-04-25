package com.example.purduecircle307;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

//import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private DatabaseReference PostsRef;
    private DatabaseReference LikesRef;
    private DatabaseReference SavedRef;
    private DatabaseReference UsersTagsRef;
    private DatabaseReference FriendsRef;
    private androidx.appcompat.widget.Toolbar mToolbar;
    private ImageButton AddNewPostButton;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    String currentUserID;
    Boolean LikeChecker = false;
    Boolean SavedChecker = false;
    boolean isGuestUser = false;
    private ArrayList<String> followedTags = new ArrayList<String>();
    private ArrayList<String> followedUsers = new ArrayList<String>();

    //Checks authentication of user
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        UserRef.child(currentUserID).child("feed").removeValue();

        //force logout
        //currentUser = null;

        //If user does not exist, navigate to sign up/log in page
        if (currentUser == null) {
            SendUserToLoginActivity();
        } else if (currentUser.isAnonymous()) {
            isGuestUser = true;
        } else {
            CheckUserExistence();
        }

        UsersTagsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followedTags = new ArrayList<String>();
                for (DataSnapshot tagChild : snapshot.getChildren()) {
                    followedTags.add(tagChild.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FriendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followedUsers = new ArrayList<String>();
                for (DataSnapshot tagChild : snapshot.getChildren()) {
                    followedUsers.add(tagChild.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        displayAllUsersPosts();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser().isAnonymous())  {
            isGuestUser = true;
        }
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        SavedRef = FirebaseDatabase.getInstance().getReference().child("Saved");
        UsersTagsRef = FirebaseDatabase.getInstance().getReference().child("UsersTags").child(currentUserID);
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserID);

        mToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);

        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout,
                mToolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);

        if (isGuestUser) {
            NavProfileUserName.setText("Guest");
            Picasso.with(MainActivity.this).load(R.drawable.profile).into(NavProfileImage);
        }
        else {
            UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if(snapshot.hasChild("name"))
                        {
                            String fullname = snapshot.child("name").getValue().toString();
                            NavProfileUserName.setText(fullname);
                        }
                        if(snapshot.hasChild("profileImage"))
                        {
                            String image = snapshot.child("profileImage").getValue().toString();
                            Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                        }
                        //String fullname = snapshot.child("name").getValue().toString();
                        //String image = snapshot.child("profileimage").getValue().toString();


                        //NavProfileUserName.setText(fullname);
                        //Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                userMenuSelector(item);
                return false;
            }
        });

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGuestUser) {
                    Toast.makeText(MainActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
                }
                else {
                    SendUsertoPostActivity();
                }
            }
        });

        displayAllUsersPosts();
    }

    private void displayAllUsersPosts() {

        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postChild : dataSnapshot.getChildren()) {
                    String postKey = postChild.getKey();
                    String tag = postChild.child("tag").getValue().toString();
                    if (followedTags.contains(tag)) {
                        UserRef.child(currentUserID).child("feed").child(postKey).updateChildren((Map<String, Object>) postChild.getValue());
                    }
                    String creator = postChild.child("uid").getValue().toString();
                    if (followedUsers.contains(creator)) {
                        UserRef.child(currentUserID).child("feed").child(postKey).updateChildren((Map<String, Object>) postChild.getValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*
        Query SortPostsInDescendingOrder;
        if (isGuestUser) {
            SortPostsInDescendingOrder = PostsRef.orderByChild("counter").limitToLast(5);
        }
        else {
            SortPostsInDescendingOrder = UserRef.child(currentUserID).child("feed").orderByChild("counter");
        }
        */
        /*Posts.class,
                R.layout.all_posts_layout,
                PostsViewHolder.class,
                PostsRef*/
        FirebaseRecyclerOptions<Posts> options =
                
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(PostsRef, Posts.class)
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
                        holder.setProfileimage(getApplicationContext(), model.getProfileimage());
                        holder.setPostimage(getApplicationContext(), model.getPostimage());

                        holder.setLikeButtonStatus(PostKey);
                        holder.setSaveButtonStatus(PostKey);


                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isGuestUser) {
                                    Toast.makeText(MainActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Intent clickPostIntent = new Intent(MainActivity.this, ClickPostActivity.class);
                                    clickPostIntent.putExtra("PostKey", PostKey);
                                    startActivity(clickPostIntent);
                                }
                            }
                        });

                        holder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isGuestUser) {
                                    Toast.makeText(MainActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Intent commentsIntent = new Intent(MainActivity.this, CommentsActivity.class);
                                    commentsIntent.putExtra("PostKey", PostKey);
                                    startActivity(commentsIntent);
                                }
                            }
                        });

                        holder.LikePostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isGuestUser) {
                                    Toast.makeText(MainActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    LikeChecker = true;
                                    LikesRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (LikeChecker.equals(true)) {
                                                if (snapshot.child(PostKey).hasChild(currentUserID)) {
                                                    LikesRef.child(PostKey).child(currentUserID).removeValue();
                                                    LikeChecker = false;
                                                } else {
                                                    LikesRef.child(PostKey).child(currentUserID).setValue(true);
                                                    LikeChecker = false;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        });

                        holder.SavedPostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isGuestUser) {
                                    Toast.makeText(MainActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    SavedChecker = true;
                                    SavedRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (SavedChecker.equals(true)) {
                                                if (snapshot.child(PostKey).hasChild(currentUserID)) {
                                                    SavedRef.child(PostKey).child(currentUserID).removeValue();
                                                    SavedChecker = false;
                                                } else {
                                                    SavedRef.child(PostKey).child(currentUserID).setValue(true);
                                                    SavedChecker = false;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        });


                    }

                };


        firebaseRecyclerAdapter.startListening();
        postList.setAdapter(firebaseRecyclerAdapter);


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

        public void setSaveButtonStatus (final String PostKey) {
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

    private void SendUsertoPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }


    private void userMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_profile:
                if (isGuestUser) {
                    Toast.makeText(MainActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
                }
                else {
                    SendUserToPublicProfileActivity();
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_friends:
                SendUserToFriendsActivity();
                Toast.makeText(this, "Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_messages:
                SendUserToFriendsActivity();
                Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_tags:
                SendUserToTagsActivity();
                Toast.makeText(this, "Tags", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_find_friends:
                SendUserToFindFriendsActivity();
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_find_tags:
                SendUserToFindTagsActivity();
                Toast.makeText(this, "Find Tags", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                if (isGuestUser) {
                    Toast.makeText(MainActivity.this, "Please sign in to use this feature.", Toast.LENGTH_SHORT).show();
                }
                else {
                    SendUserToSettingsActivity();
                    Toast.makeText(this, "Edit Profile", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_logout:
                confirmLogout();
                break;
        }
    }

    private void confirmLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirm Logout");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isGuestUser) {
                    mAuth.signOut();
                    SendUserToLoginActivity();
                }
                else {
                    mAuth.signOut();
                    SendUserToLoginActivity();
                }
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void SendUserToFriendsActivity() {
        Intent friendsIntent = new Intent(MainActivity.this, FriendsActivity.class);
        startActivity(friendsIntent);
    }

    private void SendUserToTagsActivity() {
        Intent tagsIntent = new Intent(MainActivity.this, TagsActivity.class);
        startActivity(tagsIntent);
    }

    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void SendUserToPublicProfileActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, PublicProfileActivity.class);
        startActivity(settingsIntent);
    }

    private void SendUserToFindFriendsActivity() {
        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }

    private void SendUserToFindTagsActivity() {
        Intent findTagsIntent = new Intent(MainActivity.this, FindTagsActivity.class);
        startActivity(findTagsIntent);
    }
/*
    private void SendUserToSavedActivity() {
        Intent savedIntent = new Intent(MainActivity.this, SavedActivity.class);
        startActivity(savedIntent);
    }
*/ //saved

    private void CheckUserExistence() {
        final String currentUser_id = mAuth.getCurrentUser().getUid();

        //creating reference to firebase database
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                //user is authenticated but not present in realtime firebase
                if (!datasnapshot.hasChild(currentUser_id)) {
                    SendUserToProfileActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendUserToProfileActivity() {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


}