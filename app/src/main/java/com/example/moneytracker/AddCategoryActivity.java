package com.example.moneytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moneytracker.model.NewCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import util.TransactionApi;

public class AddCategoryActivity extends AppCompatActivity {

    private Button doneButton;
    private EditText newCategory;


    private FirebaseAuth firebaseAuth;
        private FirebaseUser firebaseUser;
        private FirebaseAuth.AuthStateListener authStateListener;

        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        private CollectionReference collectionReference = db.collection("Category");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        firebaseAuth = FirebaseAuth.getInstance();
        doneButton = findViewById(R.id.doneButton);
        newCategory = findViewById(R.id.category_edittext);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = newCategory.getText().toString();
                if(!category.equals("")){
                addToCollection(category);
                }
                else{
                    Toast.makeText(AddCategoryActivity.this,"Enter All Fields",Toast.LENGTH_LONG)
                            .show();
                }

            }
        });
    }

    private void addToCollection(String category) {
         if(category!=null) {
                NewCategory categoryList = new NewCategory(category, R.drawable.ic_baseline_widgets_24,
                        TransactionApi.getInstance().getUserId());

                collectionReference
                        .document(String.valueOf(Timestamp.now().getSeconds()))
                        .set(categoryList)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                 Toast.makeText(AddCategoryActivity.this,"Done",Toast.LENGTH_LONG)
                                .show();


                                 Intent intent = getIntent();
                                 intent.putExtra("NEW_CATEGORY", category);
                                 setResult(RESULT_OK,intent);
                                 finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
    }
}