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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private EditText UserEmail;
    private EditText UserPassword;
    private EditText UserConfirmPassword;
    private Button CreateAccountButton;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        UserEmail = (EditText) findViewById(R.id.signUp_email);
        UserPassword = (EditText) findViewById(R.id.signUp_password);
        UserConfirmPassword = (EditText) findViewById(R.id.signUp_confirmPassword);
        CreateAccountButton = (Button) findViewById(R.id.signUp_createAccount);
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });
    }

    //if user is already logged in, then sending them to the main Activity
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            sendUserToMainActivity();
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SignUp.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void createNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfirmPassword.getText().toString();

        String errorMessage;
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please include your email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please include your password", Toast.LENGTH_SHORT).show();
        }
        else if ((errorMessage = checkPasswordStrength(password)) != null) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Loading. Please wait as your account is being created");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                sendUserToSetupActivity();
                                Toast.makeText(SignUp.this, "Signing up was successful!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(SignUp.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }

    private String checkPasswordStrength(String password) {
        Pattern p;
        Matcher m;
        if (password.length() < 6 || password.length() > 16) {
            return "Please keep password length between 6 and 16 characters";
        }
        p = Pattern.compile("[A-Za-z]");
        m = p.matcher(password);
        if (!m.find()) {
            return "Please include at least 1 letter in your password";
        }
        p = Pattern.compile("[0-9]");
        m = p.matcher(password);
        if (!m.find()) {
            return "Please include at least 1 number in your password";
        }
        p = Pattern.compile("[!@#$%\\^&\\*]");
        m = p.matcher(password);
        if (!m.find()) {
            return "Please include at least 1 character [!@#$%^&*] in your password";
        }
        p = Pattern.compile("[^A-Za-z0-9!@#$%\\^&\\*]");
        m = p.matcher(password);
        if (m.find()) {
            return "Only include these special characters in your password: !@#$%^&*";
        }
        return null;
    }

    private void sendUserToSetupActivity() {
        Intent setupIntent = new Intent(SignUp.this, ProfileActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}