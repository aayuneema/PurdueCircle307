package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private Toolbar ChatToolBar;
    private ImageButton SendMessageButton, SendImageButton;
    private EditText userMessageInput;
    private RecyclerView userMessageList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messageAdapter;

    private String messageReceiverId, messageReceiverName, messageSenderId, saveCurrentDate, saveCurrentTime;

    private TextView receiverName;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();

        RootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("username").toString();

        //userMessageList = (RecyclerView) findViewById(R.id.messages_list_of_users);
        InitializeFields();

        //DisplayReceiverInfo(); // Video 58

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });

        FetchMessages();

    }


    private void FetchMessages() {
        RootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {
                            Messages messages = snapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messageAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void SendMessage() {
        String messageText = userMessageInput.getText().toString();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Please type a message first...", Toast.LENGTH_SHORT).show();
        } else {
            String message_sender_ref = "Messages/" + messageSenderId + "/" + messageReceiverId;
            String message_receiver_ref = "Messages/" + messageReceiverId + "/" + messageSenderId;

            DatabaseReference user_message_key = RootRef.child("Messages").child(messageSenderId)
                    .child(messageReceiverId).push(); //creates key

            String message_push_id = user_message_key.getKey();

            //get date
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calFordDate.getTime());
            //get time
            //Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveCurrentTime = currentTime.format(calFordDate.getTime());

            HashMap messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderId);

            HashMap messageBodyDetails = new HashMap();
            messageBodyDetails.put(message_sender_ref + "/" + message_push_id , messageTextBody);
            messageBodyDetails.put(message_receiver_ref + "/" + message_push_id , messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                        userMessageInput.setText("");
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(ChatActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        userMessageInput.setText("");
                    }

                    //userMessageInput.setText("");
                }
            });
        }
    }

    private void DisplayReceiverInfo() {
        receiverName.setText(messageReceiverName);

        RootRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void InitializeFields() {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_bar_layout);
        //setSupportActionBar(ChatToolBar);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        SendImageButton = (ImageButton) findViewById(R.id.send_image_button);

        userMessageInput = (EditText) findViewById(R.id.input_message);

        messageAdapter = new MessagesAdapter(messagesList);
        userMessageList = (RecyclerView) findViewById(R.id.messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setHasFixedSize(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);
    }
}