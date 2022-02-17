package com.example.purduecircle307;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SignUp extends AppCompatActivity {

    private EditText UserEmail;
    private EditText UserPassword;
    private EditText UserConfirmPassword;
    private Button CreateAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        UserEmail = (EditText) findViewById(R.id.signUp_email);
        UserPassword = (EditText) findViewById(R.id.signUp_password);
        UserConfirmPassword = (EditText) findViewById(R.id.signUp_confirmPassword);
        CreateAccountButton = (Button) findViewById(R.id.signUp_createAccount);
    }
}