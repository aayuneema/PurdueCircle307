package com.example.purduecircle307;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView PostImage;
    private TextView PostDescription;
    private Button DeletePostButton;
    private Button EditPostButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        PostImage = (ImageView)  findViewById(R.id.click_post_image);
        PostDescription = (TextView) findViewById((R.id.click_post_description));
        DeletePostButton = (Button) findViewById((R.id.clickPostDeleteButton));
        EditPostButton = (Button) findViewById((R.id.clickPostEditButton));

    }
}