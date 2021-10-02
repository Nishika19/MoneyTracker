package com.example.moneytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.Map;

import util.TransactionApi;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText usernameAccount, emailAccount, passwordAccount;
    private Button create;
//    private Button intent;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference =db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

            firebaseAuth = FirebaseAuth.getInstance();

            usernameAccount = findViewById(R.id.create_account_username);
            emailAccount = findViewById(R.id.create_account_email);
            passwordAccount = findViewById(R.id.create_account_password);
            create = findViewById(R.id.create_account_button);
            progressBar = findViewById(R.id.create_account_progress);
//            intent = findViewById(R.id.intentBtn);

            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull  FirebaseAuth firebaseAuth) {
                    firebaseUser = firebaseAuth.getCurrentUser();

                    if (firebaseUser!=null){

                    }else{

                    }
                }
            };

//          intent.setOnClickListener(new View.OnClickListener() {
//              @Override
//              public void onClick(View view) {
//                  startActivity(new Intent(CreateAccountActivity.this,transactionInfo.class));
//                  finish();
//              }
//          });

            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailAccount.getText().toString().trim();
                    String username = usernameAccount.getText().toString().trim();
                    String password = passwordAccount.getText().toString().trim();

                    if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
                        createUserAccount(email,password,username);
                    }else{
                        Toast.makeText(CreateAccountActivity.this,"Enter All Fields",Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
//            intent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    TransactionApi.getInstance().setUsername("nishika");
//                    TransactionApi.getInstance().setUserId("1901");
//                    startActivity(new Intent(CreateAccountActivity.this, SpendingActivity.class));
//                    finish();
//                }
//            });

    }

    private void createUserAccount(String email, String password, String username) {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)){
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                firebaseUser = firebaseAuth.getCurrentUser();
                                assert firebaseUser!=null;
                                String currentUserId = firebaseAuth.getUid();

                                Map<String, String> userObject = new HashMap<>();
                                userObject.put("username", username);
                                userObject.put("userId", currentUserId);

                                collectionReference.add(userObject)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.getResult().exists()){
                                                progressBar.setVisibility(View.INVISIBLE);
                                                //String name = task.getResult().getString("username");
                                                TransactionApi.getInstance().setUserId(currentUserId);
                                                TransactionApi.getInstance().setUsername(username);
                                                startActivity(new Intent(CreateAccountActivity.this, SpendingActivity.class));
                                                finish();
                                            }else{
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }

                                            }
                                        });
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }else{

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}