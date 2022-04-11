package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity<CreateAccountActivity> extends AppCompatActivity {

    public static final String TAG = "TAG";
    Button creatButn;
    EditText userNameEditText, emailEditText, passwordEditText;
    TextView signInButton;
    ProgressBar progressBar;
    CheckBox checkBox1, checkbox2;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        creatButn = findViewById(R.id.create_acct_button);
        userNameEditText = findViewById(R.id.username_account);
        emailEditText = findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);
        signInButton = findViewById(R.id.sign_in_option);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.create_acct_progress);


//        if(fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(),MainActivity.class));
//            finish();
//        }


        //if click on already sign in -> goto sign in
        signInButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        }));

        creatButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = userNameEditText.getText().toString().trim();
                final String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                boolean ownerChecked = ((CheckBox) findViewById(R.id.checkBox1)).isChecked();
                boolean userChecked = ((CheckBox) findViewById(R.id.checkBox2)).isChecked();

                //TODO: check empty fields
//                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
//
//                }
//
//
//                //check no empty fields
//                if(TextUtils.isEmpty(username)){
//                    Toast.makeText(SignUpActivity.this, "Must choose user name", Toast.LENGTH_LONG).show();
//                }
//
//                else if(TextUtils.isEmpty(email)){
//                    Toast.makeText(SignUpActivity.this, "Must choose email", Toast.LENGTH_LONG).show();
//                }
//
//                else if(TextUtils.isEmpty(password)){
//                    Toast.makeText(SignUpActivity.this, "Must choose password", Toast.LENGTH_LONG).show();
//                }
//
//                else if(userChecked){
//                    profile = "user";
//                }
//                else if(ownerChecked){
//                    profile = "mikveh owner";
//                }
//                else{
//                    Toast.makeText(SignUpActivity.this,
//                            "Must choose profile",
//                            Toast.LENGTH_LONG)
//                            .show();
//                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // send verification link
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignUpActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                }
                            });

                            Toast.makeText(SignUpActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("Users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("userName",username);
                            user.put("email",email);
                            user.put("profile",profile);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),SignInActivity.class));

                        }else {
                            Toast.makeText(SignUpActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}
