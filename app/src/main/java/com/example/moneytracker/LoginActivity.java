package com.example.moneytracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.moneytracker.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import util.TransactionApi;

public class LoginActivity extends AppCompatActivity {

    private Button createAccountBtn;
    private Button loginButton;
    private EditText password;
    private ProgressBar progressBar;
    private AutoCompleteTextView email;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        firebaseAuth = FirebaseAuth.getInstance();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(currentUser != null){
                    currentUser = firebaseAuth.getCurrentUser();
                    assert currentUser != null;
                    String currentUserId = currentUser.getUid();

                    collectionReference.whereEqualTo("userId",currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if(error!=null){
                                        return;
                                    }
                                    assert value != null;
                                    if(!value.isEmpty()){
                                        for(QueryDocumentSnapshot snapshot: value){
                                            TransactionApi transactionApi = TransactionApi.getInstance();
                                            transactionApi.setUserId(snapshot.getString("userId"));
                                            transactionApi.setUsername(snapshot.getString("username"));
                                            startActivity(new Intent(LoginActivity.this,SpendingActivity.class));
                                            finish();

                                        }

                                    }
                                }
                            });
                }
            }
        };
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.login_progress_bar);
        loginButton = findViewById(R.id.sign_in_button);
        createAccountBtn = findViewById(R.id.create_account_button_login);

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmailPassword(email.getText().toString().trim(),password.getText().toString().trim());
            }
        });
    }

    private void loginEmailPassword(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password)){
            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user!=null){
                                String currentUserId = user.getUid();

                                collectionReference.whereEqualTo("userId",currentUserId)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                                if(error!=null){

                                                }

                                                assert value != null;
                                                if(!value.isEmpty()) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    for (QueryDocumentSnapshot snapshot : value) {
                                                        TransactionApi transactionApi = TransactionApi.getInstance();
                                                        transactionApi.setUsername(snapshot.getString("username"));
                                                        transactionApi.setUserId(currentUserId);

                                                        startActivity(new Intent(LoginActivity.this, SpendingActivity.class));
                                                        finish();
                                                    }
                                                }
                                            }
                                        });

                            }else{
                                    Toast.makeText(LoginActivity.this,"This account doesn't exist",Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }else{
                 progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this,"Enter All Fields",Toast.LENGTH_LONG)
                    .show();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}