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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccount extends AppCompatActivity {

    private Button deleteAccountButton;
    private EditText deleteEmail;
    private EditText deletePassword;
    private ProgressDialog loadingBar;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        currentUserID = user.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        deleteAccountButton = (Button) findViewById(R.id.deletebutton);
        deleteEmail = (EditText) findViewById(R.id.email_deleteAccount);
        deletePassword = (EditText) findViewById(R.id.password_deleteAccount);
        loadingBar = new ProgressDialog(this);

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCredentials();
            }
        });
    }

    private void checkCredentials() {
        String email = deleteEmail.getText().toString();
        String password = deletePassword.getText().toString();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please include your email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please include your password", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Deleting account");
            loadingBar.setMessage("Loading. Please wait as your account is getting deleted");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                deleteAccount();
                            }
                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(DeleteAccount.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void deleteAccount() {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    String message = task.getException().getMessage();
                    Toast.makeText(DeleteAccount.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });

        usersRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    mAuth.signOut();
                    SendUserToLoginActivity();
                    Toast.makeText(DeleteAccount.this, "Account was successfully deleted", Toast.LENGTH_SHORT).show();
                }
                else {
                    String message = task.getException().getMessage();
                    Toast.makeText(DeleteAccount.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
                loadingBar.dismiss();
            }
        });
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(DeleteAccount.this, Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}