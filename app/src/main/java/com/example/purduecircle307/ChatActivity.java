package com.example.purduecircle307;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.widget.Toolbar;

public class ChatActivity extends AppCompatActivity {

    private Toolbar ChatToolBar;
    private ImageButton SendMessageButton, SendImageButton;
    private EditText userMessageInput;
    private RecyclerView userMessageList;

    private String messageReceiverId, messageReceiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();
        //messageReceiverName = getIntent().getExtras().get("userName").toString();

        //userMessageList = (RecyclerView) findViewById(R.id.messages_list_of_users);
        InitializeFields();

    }

    private void InitializeFields() {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_bar_layout);
        //setSupportActionBar(ChatToolBar);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        SendImageButton = (ImageButton) findViewById(R.id.send_image_button);

        userMessageInput = (EditText) findViewById(R.id.input_message);
    }
}