package com.example.purduecircle307;

import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

@RunWith(AndroidJUnit4ClassRunner.class)
public class SaveTest {

    private DatabaseReference SavedRef;

    @Before
    public void setup() {
        SavedRef = FirebaseDatabase.getInstance().getReference().child("Saved");
    }

    @Test
    public void save() {
        //check that save post is marked saved on firebase

        String post = "2022-04-12-21-21-230oIZa3J7T0Nu6WLYACgm9ALmK4K3";

        SavedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean works = false;
                String val;
                Map<String, Object> users = (Map<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()) {
                    if (post.equals(entry.getKey())) {
                        val = SavedRef.child(post).getKey();
                        if (val.equals("true")) {
                            works = true;
                        }
                    }
                }
                assertEquals(true, works);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Test for a friend2 is friends with friend1
    @Test
    public void unsave() {
        //check that unsaved post is marked unsaved on firebase
        String post = "2022-04-12-21-38-10stc19eF1kFSyEe1H9Dj89Ur8o7t2";
        SavedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean works = false;
                String val;
                Map<String, Object> users = (Map<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()) {
                    if (post.equals(entry.getKey())) {
                        val = SavedRef.child(post).getKey();
                        if (val.equals("false")) {
                            works = true;
                        }
                    }
                }
                assertEquals(true, works);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Test to see if friend1 is friends with friend3
    @Test
    public void viewPost() {

        String post = "2022-04-12-21-38-10stc19eF1kFSyEe1H9Dj89Ur8o7t2";
        SavedRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean works = false;
                String val;
                Map<String, Object> users = (Map<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : users.entrySet()) {
                    if (post.equals(entry.getKey())) {
                        if (!SavedRef.child(post).equals(null)) {
                            works = true;
                        }
                    }
                }
                assertEquals(true, works);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
