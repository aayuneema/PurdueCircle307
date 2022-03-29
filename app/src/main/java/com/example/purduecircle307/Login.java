package com.example.purduecircle307;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private Button LoginButton;
    private EditText UserEmail;
    private EditText UserPassword;
    private Button NeedNewAccountLink;
    private Button useAsGuestUser;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (mAuth != null) {
            if (mAuth.getCurrentUser().isAnonymous()) {
                mAuth.signOut();
            }
            else {
                sendUserToMainActivity();
            }
        }

        mAuth = FirebaseAuth.getInstance();

        NeedNewAccountLink = (Button) findViewById(R.id.Login_signUpButton);
        useAsGuestUser = (Button) findViewById(R.id.Login_guestButton);
        UserEmail = (EditText) findViewById(R.id.login_emailAddress);
        UserPassword = (EditText) findViewById(R.id.login_Password);
        LoginButton = (Button) findViewById(R.id.LoginButton);
        loadingBar = new ProgressDialog(this);

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToSignUpActivity();
            }
        });

        useAsGuestUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signInAnonymously()
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    sendUserToMainActivity();
                                    Toast.makeText(Login.this, "Logging in as guest...", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String message = task.getException().getMessage();
                                    Toast.makeText(Login.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loggingInUser();
            }
        });
    }

    private void loggingInUser() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please include your email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please include your password", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Logging in");
            loadingBar.setMessage("Loading. Please wait as you get logged into your account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                sendUserToMainActivity();
                                Toast.makeText(Login.this, "Logging in was successful!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(Login.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }

    //if user is already logged in, then sending them to the main Activity
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            sendUserToMainActivity();
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(Login.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToSignUpActivity() {
        Intent signUpIntent = new Intent(Login.this, SignUp.class);
        startActivity(signUpIntent);
        finish();
    }
}