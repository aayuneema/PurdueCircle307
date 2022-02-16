package com.example.purduecircle307;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {
    private Button LoginButton;
    private EditText UserEmail;
    private EditText UserPassword;
    private Button NeedNewAccountLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NeedNewAccountLink = (Button) findViewById(R.id.Login_signUpButton);
        UserEmail = (EditText) findViewById(R.id.login_emailAddress);
        UserPassword = (EditText) findViewById(R.id.login_Password);
        LoginButton = (Button) findViewById(R.id.LoginButton);

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToSignUpActivity();
            }
        });
    }

    private void SendUserToSignUpActivity() {
        Intent signUpIntent = new Intent(Login.this, SignUp.class);
        startActivity(signUpIntent);
        finish();
    }
}