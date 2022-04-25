 package com.example.purduecircle307;

 import android.annotation.SuppressLint;
 import android.content.Context;
 import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

 public class FriendsActivity extends AppCompatActivity {

    private RecyclerView myFriendList;
    private DatabaseReference FriendsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        myFriendList = (RecyclerView) findViewById(R.id.friend_list);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();
    }

     private void DisplayAllFriends() {

         Query query = FriendsRef.orderByChild("date"); // haven't implemented a proper list sort yet.

         FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

         FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
             @Override
             protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, @SuppressLint("RecyclerView") int position, @NonNull Friends friends) {

                 //friendsViewHolder.setDate(friends.getDate());
                 final String usersIDs = getRef(position).getKey();

                 UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if (dataSnapshot.exists()) {
                             System.out.println("hehehe");
                             final String username = "@" + dataSnapshot.child("username").getValue().toString();
                             final String profileimage = dataSnapshot.child("profileimage").getValue().toString();
                             final String bio = dataSnapshot.child("bio").getValue().toString();

                             friendsViewHolder.setUsername(username);
                             friendsViewHolder.setProfileImage(getApplicationContext(), profileimage);
                             friendsViewHolder.setBio(bio);
                         }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });

                 friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         String visit_user_id = getRef(position).getKey();

                         Intent profileIntent = new Intent(FriendsActivity.this, PersonProfileActivity.class);
                         profileIntent.putExtra("visit_user_id", visit_user_id);
                         startActivity(profileIntent);
                     }
                 });
             }

             public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                 View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent ,false);
                 return new FriendsViewHolder(view);
             }
         };
         adapter.startListening();
         myFriendList.setAdapter(adapter);
     }
     public static class FriendsViewHolder extends RecyclerView.ViewHolder {

         View mView;

         public FriendsViewHolder(View itemView) {
             super(itemView);
             mView = itemView;
         }

         public void setProfileImage(Context ctx, String profileImage) {
             CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
             Picasso.with(ctx).load(profileImage).placeholder(R.drawable.profile).into(image);
         }

         public void setUsername(String username) {
             TextView myUsername = (TextView) mView.findViewById(R.id.all_users_profile_username);
             myUsername.setText(username);
         }

         public void setBio(String bio) {
             TextView myBio = (TextView) mView.findViewById(R.id.all_users_status);
             myBio.setText(bio);
         }
     }


    /*public void DisplayAllFriends() {
        Toast.makeText(this, "Finding your friends...", Toast.LENGTH_SHORT).show();
        //Query q = FriendsRef.orderByChild("date");

        //System.out.println("Query: " + q.toString());
        System.out.println("In display all friends");
        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(FriendsRef, Friends.class)
                        .build();
        System.out.println("After options");




        Query getPost = FriendsRef;
        getPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                     System.out.println(userSnapshot.getKey());
                     System.out.println(userSnapshot.child("date").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull Friends model) {
                System.out.println("hekko");
                final String usersIds = getRef(position).getKey();
                System.out.println("USERSIDS: " + usersIds);
                UsersRef.child(usersIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            final String userName = snapshot.child("username").getValue().toString();
                            final String profileImage = snapshot.child("profileImage").getValue().toString();
                            final String bio = snapshot.child("bio").getValue().toString();
                            System.out.println(userName + " // " + bio);
                            holder.userName.setText("@" + userName);
                            holder.bio.setText(bio);
                            Picasso.with(getApplicationContext()).load(profileImage).placeholder(R.drawable.profile).into(holder.profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_users_display_layout, parent, false);
                return new FriendsViewHolder(view);
            }
        };
        System.out.println(firebaseRecyclerAdapter == null);
        myFriendList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView userName;
        TextView bio;
        CircleImageView profileImage;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            userName = mView.findViewById(R.id.all_users_profile_username);
            bio = mView.findViewById(R.id.all_users_status);
            profileImage = mView.findViewById(R.id.all_users_profile_image);
        }

        public void setProfileImage(Context ctx, String profileImage) {
            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(profileImage).placeholder(R.drawable.profile).into(myImage);
        }

        public void setUsername(String username) {
            TextView myUsername = (TextView) mView.findViewById(R.id.all_users_profile_username);
            myUsername.setText(username);
        }

        public void setBio(String bio) {
            TextView myBio = (TextView) mView.findViewById(R.id.all_users_status);
            myBio.setText(bio);
        }
    }*/
}