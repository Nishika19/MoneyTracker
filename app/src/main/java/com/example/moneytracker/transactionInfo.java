package com.example.moneytracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneytracker.adapter.RecyclerViewAdapter;
import com.example.moneytracker.adapter.TransactionRecyclerViewAdapter;
import com.example.moneytracker.model.Transaction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import util.TransactionApi;

public class transactionInfo extends AppCompatActivity implements TransactionRecyclerViewAdapter.OnCardCLickListener {

    private static final int REQUEST_CODE = 6;
    private TextView expenseText, IncomeText;
    private RecyclerView transactionRecyclerView;
    private List<Transaction> TransactionList;
    private TransactionRecyclerViewAdapter recyclerViewAdapter;
    private Integer expense=0, income=0;
    private ImageButton spendingBtn, graphBtn, categoryBtn, delete_icon;
    private Button categoryFilter,all,confirm,cancel;
    private String categorySelected,id;
    private int flag=0;
    Dialog dialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Transaction");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_info);

         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        firebaseAuth = FirebaseAuth.getInstance();
        TransactionList = new ArrayList<>();
        spendingBtn = findViewById(R.id.spendingButtonTransaction);
        expenseText = findViewById(R.id.expenseTransactionRow);
        graphBtn = findViewById(R.id.graphButton);
        categoryBtn = findViewById(R.id.categoriesButton);
        delete_icon = findViewById(R.id.delete_image);
        IncomeText = findViewById(R.id.incomeTransactionRow);
        all = findViewById(R.id.all);
        dialog = new Dialog(transactionInfo.this);
        dialog.setContentView(R.layout.delete_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        categoryFilter = findViewById(R.id.category);
        transactionRecyclerView = findViewById(R.id.transactionRecyclerView);
        transactionRecyclerView.setHasFixedSize(true);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cancel = dialog.findViewById(R.id.cancel);
        confirm = dialog.findViewById(R.id.confirm);


        delete_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(transactionInfo.this,"Long press the list item to delete.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

        graphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(transactionInfo.this,GraphActivity.class));
            }
        });

        categoryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(transactionInfo.this,SelectCategoryActivity.class);
            intent.putExtra("No CLick2", "NO_CLICK");
            startActivity(intent);
        });

        spendingBtn.setOnClickListener(v ->{
            startActivity(new Intent(transactionInfo.this,SpendingActivity.class));
        finish();});

        all.setOnClickListener(v ->{
            if(!categoryFilter.getText() .equals("Select Category")){
                categoryFilter.setText("Select Category");
                Log.d("CATEGory", "onCreate: "+ categoryFilter.getText());
            updateTransaction();
            }
        });

        categoryFilter.setOnClickListener(v -> startActivityForResult(new Intent(transactionInfo.this,SelectCategoryActivity.class),
                REQUEST_CODE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            assert data != null;
            categorySelected = data.getStringExtra(SelectCategoryActivity.CATEGORY_SELECTED);
            categoryFilter.setText(categorySelected);
            flag =1;
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(transactionInfo.this,SpendingActivity.class));
//    }

    private void filterCategory(String categorySelected) {
        if(categorySelected!=null){
            collectionReference.whereEqualTo("userId",TransactionApi.getInstance().getUserId())
                    .whereEqualTo("category",categorySelected)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(!queryDocumentSnapshots.isEmpty()) {
                                if(transactionRecyclerView.getVisibility()==View.GONE){
                                    transactionRecyclerView.setVisibility(View.VISIBLE);
                                }
                            for (QueryDocumentSnapshot transactions : queryDocumentSnapshots) {
                                    Transaction transaction = transactions.toObject(Transaction.class);
                                    TransactionList.add(transaction);
                                    if(transaction.getType().equals("Expense")){
                                        expense += transaction.getAmount();
                                    }else{
                                        income += transaction.getAmount();
                                    }
                            }
                            expenseText.setText(NumberFormat.getCurrencyInstance().format(expense));
                            IncomeText.setText(NumberFormat.getCurrencyInstance().format(income));
                            recyclerViewAdapter = new TransactionRecyclerViewAdapter(TransactionList,transactionInfo.this::onCardCLick);
                            transactionRecyclerView.setAdapter(recyclerViewAdapter);
                            recyclerViewAdapter.notifyDataSetChanged();

                        }else{
                                expenseText.setText(NumberFormat.getCurrencyInstance().format(0));
                                IncomeText.setText(NumberFormat.getCurrencyInstance().format(0));
                                transactionRecyclerView.setVisibility(View.GONE);
                                Toast.makeText(transactionInfo.this,"No Data",Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
         TransactionList.removeAll(TransactionList);
        expense=0;
        income=0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(flag==1){
             filterCategory(categorySelected);
        }else {
            updateTransaction();
        }
    }

    private void updateTransaction() {

        collectionReference.whereEqualTo("userId", TransactionApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()) {
                            if(transactionRecyclerView.getVisibility()==View.GONE){
                                    transactionRecyclerView.setVisibility(View.VISIBLE);
                                }
                            for (QueryDocumentSnapshot transactions : queryDocumentSnapshots) {
                                    Transaction transaction = transactions.toObject(Transaction.class);
                                    TransactionList.add(transaction);
                                    if(transaction.getType().equals("Expense")){
                                        expense += transaction.getAmount();
                                    }else{
                                        income += transaction.getAmount();
                                    }
                            }
                            expenseText.setText(NumberFormat.getCurrencyInstance().format(expense));
                            IncomeText.setText(NumberFormat.getCurrencyInstance().format(income));
                            Collections.reverse(TransactionList);
                            recyclerViewAdapter = new TransactionRecyclerViewAdapter(TransactionList,transactionInfo.this::onCardCLick);
                            transactionRecyclerView.setAdapter(recyclerViewAdapter);
                            recyclerViewAdapter.notifyDataSetChanged();
//                            if(flag==1){
//                           filterCategory(categorySelected);
//                            }else {
                            //   updateTransaction();
//        }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        TransactionList.removeAll(TransactionList);
        expense=0;
        income=0;
    }

    @Override
    protected void onStop() {
        super.onStop();
         TransactionList.removeAll(TransactionList);
        expense=0;
        income=0;
    }

    @Override
    public void onCardCLick(int position) {
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction transaction = TransactionList.get(position);
                Log.d("position", "onClick: "+position);
                Date timeAdded= transaction.getDate();
                String userId = transaction.getUserId();

                collectionReference.whereEqualTo("userId",userId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(!queryDocumentSnapshots.isEmpty()){
                                   for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){

                                       if(Objects.equals(snapshot.getDate("date"), timeAdded)){
                                            Log.d("TAG3", "onSuccess: "+"done");
                                           id = snapshot.getId();
                                       }
                                       if(id!=null) {
                                           collectionReference.document(id).delete();
                                           Toast.makeText(transactionInfo.this, "Deleted", Toast.LENGTH_SHORT)
                                                   .show();
                                           recyclerViewAdapter.notifyDataSetChanged();
                                           dialog.dismiss();

                                       }
                                   }
                                    if(flag==1){
                  filterCategory(categorySelected);
                     }else {
                updateTransaction();
                 }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
    }
}