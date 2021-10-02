package com.example.moneytracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneytracker.model.Category;
import com.example.moneytracker.model.Transaction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import util.TransactionApi;

public class AddTransactionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_CODE = 2;
    private EditText amount, note;
    private Button saveButton,date,category,type,cancel,usernameBtn;
    private Date transactionDate;
    private String currentUserId, currentUsername,amountType;
    Integer finalAmount;
    Calendar calendar = Calendar.getInstance();
    private ProgressBar progressBar;
    private String categorySelected,monthSelected;
    String AnyNote;

        private FirebaseAuth firebaseAuth;
        private FirebaseUser firebaseUser;
        private FirebaseAuth.AuthStateListener authStateListener;

        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        private CollectionReference collectionReference = db.collection("Transaction");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        firebaseAuth = FirebaseAuth.getInstance();

        cancel = findViewById(R.id.cancelButton);
        date = findViewById(R.id.calender_edittext);
        saveButton = findViewById(R.id.doneButton);
        category=findViewById(R.id.category_edittext);
        usernameBtn = findViewById(R.id.username_btn);
        amount = findViewById(R.id.amountEdittext);
        note = findViewById(R.id.noteEditText);
        type = findViewById(R.id.typeDetailEditText);
        progressBar = findViewById(R.id.transaction_progressBar);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser!=null){

                }else{

                }
            }
        };
        cancel.setOnClickListener(v -> {
            startActivity(new Intent(AddTransactionActivity.this,SpendingActivity.class));
            finish();
        });


        if(getIntent().getStringExtra("Type")!=null){
            amountType = getIntent().getStringExtra("Type");
        }
        type.setText(amountType);
        usernameBtn.setText(TransactionApi.getInstance().getUsername());
         if(TransactionApi.getInstance() !=null){
            currentUserId = TransactionApi.getInstance().getUserId();
            currentUsername = TransactionApi.getInstance().getUsername();
        }

        date.setShowSoftInputOnFocus(false);
        category.setShowSoftInputOnFocus(false);

        date.setOnClickListener(v -> showDatePickerDialog());
        category.setOnClickListener(v -> {
             Intent intent = new Intent(AddTransactionActivity.this,SelectCategoryActivity.class);
        startActivityForResult(intent,REQUEST_CODE);
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String transactionDate2 = date.getText().toString();
                 if(!TextUtils.isEmpty(amount.getText())) {
                     finalAmount = Integer.parseInt(amount.getText().toString());
                 }

                AnyNote = note.getText().toString().trim();
               if(!TextUtils.isEmpty(categorySelected) && !TextUtils.isEmpty(amount.getText().toString()) && !TextUtils.isEmpty(transactionDate2)){
                   saveTransaction();
               }else{
                   Toast.makeText(AddTransactionActivity.this,"Enter All Fields",Toast.LENGTH_LONG)
                           .show();
               }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
               Log.d("Integer", "onActivityResult: "+"CATEGORY");
            assert data != null;
            categorySelected = data.getStringExtra(SelectCategoryActivity.CATEGORY_SELECTED);
            category.setText(categorySelected);
        }

    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_YEAR));
        datePickerDialog.show();
    }

    private void saveTransaction() {
        progressBar.setVisibility(View.VISIBLE);

        Transaction transaction = new Transaction(transactionDate,currentUserId,currentUsername,categorySelected,
                finalAmount,AnyNote,amountType,monthSelected);

        collectionReference
                .document(String.valueOf(Timestamp.now().getSeconds()))
                .set(transaction)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                         progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(AddTransactionActivity.this,"Done",Toast.LENGTH_LONG)
                                .show();
                        Intent intent = new Intent(AddTransactionActivity.this,SpendingActivity.class);
                        intent.putExtra("category",categorySelected);
                        intent.putExtra("expense",finalAmount);
                        startActivity(intent);
                        finish();
                   }
               })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
       if(firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
       }
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year,month,dayOfMonth);
        transactionDate = calendar.getTime();
            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            SimpleDateFormat simpleDateFormat1 = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            simpleDateFormat.applyPattern("EEE, d MMMM yyyy");
            simpleDateFormat1.applyPattern("MMMM");
            monthSelected = simpleDateFormat1.format(transactionDate);
            date.setText(simpleDateFormat.format(transactionDate));

    }
}