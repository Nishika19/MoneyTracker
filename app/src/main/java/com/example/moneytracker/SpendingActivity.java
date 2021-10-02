package com.example.moneytracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneytracker.adapter.RecyclerViewAdapter;
import com.example.moneytracker.model.Transaction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.TransactionApi;

public class SpendingActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 10;
    private Button addExpenseBtn, addIncomeBtn,selectMonth;
    private ImageButton transaction, graph, categories,signout;
    private TextView totalExpenseAmount, totalIncomeAmount, balance;
    private int expenseAmount = 0, incomeAmount=0;
    private List<Transaction> expenseTransactionList, incomeTransactionList, monthlyExpenseList, monthlyIncomeList;
    private RecyclerView expenseRecyclerView, incomeRecyclerView;
    private RecyclerViewAdapter recyclerViewAdapterExpense,recyclerViewAdapterIncome;
    private boolean isMonth = false;
    private int checkType = 0,i=1;


     SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
     SimpleDateFormat simpleDateFormatCheck = (SimpleDateFormat) SimpleDateFormat.getDateInstance();

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Transaction");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spending_activity);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser =firebaseAuth.getCurrentUser();
        expenseTransactionList = new ArrayList<>();
        incomeTransactionList = new ArrayList<>();
        monthlyExpenseList = new ArrayList<>();
        monthlyIncomeList = new ArrayList<>();
        addExpenseBtn = findViewById(R.id.add_expense);
        addIncomeBtn = findViewById(R.id.add_income);
        totalExpenseAmount = findViewById(R.id.expense_amount);
        signout = findViewById(R.id.signout);
        balance = findViewById(R.id.totalBalance);
        totalIncomeAmount = findViewById(R.id.totalIncomeAmount);
        expenseRecyclerView = findViewById(R.id.expense_recyclerView);
        incomeRecyclerView = findViewById(R.id.income_recyclerView);
        transaction = findViewById(R.id.transactionButton);
        graph = findViewById(R.id.graphButton);
        selectMonth = findViewById(R.id.month);
        categories = findViewById(R.id.categoriesButton);
        expenseRecyclerView.setHasFixedSize(true);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        incomeRecyclerView.setHasFixedSize(true);
        incomeRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        graph.setOnClickListener(v -> startActivity(new Intent(SpendingActivity.this,GraphActivity.class)));

        categories.setOnClickListener(v ->{
            Intent intent = new Intent(SpendingActivity.this,SelectCategoryActivity.class);
            intent.putExtra("No Click","noClick");
         startActivity(intent);
        });

        selectMonth.setOnClickListener(v ->{
            Intent intent = new Intent(SpendingActivity.this,Month.class);
                startActivity(intent);
                if(!selectMonth.getText().equals("All")) {
                    //  Log.d("TAG", "onCreate: "+selectMonth.getText());
                    finish();
                }

        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              Toast toast =   Toast.makeText(SpendingActivity.this,"Long press the power icon to Sign out.",Toast.LENGTH_LONG);
                        toast.show();
            }
        });

        signout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                 if (firebaseUser != null && firebaseAuth != null) {
                     firebaseAuth.signOut();
                     startActivity(new Intent(SpendingActivity.this, LoginActivity.class));
                     finish();
                 }
                return true;
            }
        });

        if(getIntent().getStringExtra("optionSelected")!=null){
            String monthSelect = getIntent().getStringExtra("optionSelected");
            if(!monthSelect.equals("All")){
                 isMonth=true;
                 checkType=1;
            }
        }

        if(getIntent().getStringExtra("optionSingleDateSelected")!=null){
            String monthSelect = getIntent().getStringExtra("optionSingleDateSelected");
            if(!monthSelect.equals("All")){
                 isMonth=true;
                 checkType=2;
            }
        }

         if(getIntent().getStringExtra("optionWeekDateSelected")!=null){
            String monthSelect = getIntent().getStringExtra("optionWeekDateSelected");
            if(!monthSelect.equals("All")){
                 isMonth=true;
                 checkType=3;
            }
        }

           if(getIntent().getStringExtra("optionYearSelected")!=null){
            String monthSelect = getIntent().getStringExtra("optionYearSelected");
            if(!monthSelect.equals("All")){
                 isMonth=true;
                 checkType=4;
            }
        }


        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SpendingActivity.this,transactionInfo.class));
                //finish();
            }
        });

        addIncomeBtn.setOnClickListener(v -> {
            i+=1;
            Intent intent = new Intent(SpendingActivity.this, AddTransactionActivity.class);
                intent.putExtra("Type", "Income");
                startActivity(intent);
        });

        addExpenseBtn.setOnClickListener(v ->{
                Intent intent = new Intent(SpendingActivity.this,AddTransactionActivity.class);
                intent.putExtra("Type", "Expense");
                startActivity(intent);

        });

    }

    private void inflateNewTransaction(String month, int check) {
        if(month!=null){
            collectionReference.whereEqualTo("userId",TransactionApi.getInstance().getUserId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(!queryDocumentSnapshots.isEmpty()){
                                for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                                    Transaction transaction = snapshot.toObject(Transaction.class);
                                    if(check==1) {
                                        selectMonth.setText(month);
                                        if (transaction.getMonth().equals(month) && transaction.getType().equals("Expense")) {
                                            monthlyExpenseList.add(transaction);
                                            expenseAmount += transaction.getAmount();
                                        }
                                        if (transaction.getMonth().equals(month) && transaction.getType().equals("Income")) {
                                            monthlyIncomeList.add(transaction);
                                            incomeAmount += transaction.getAmount();
                                        }
                                    }

                                    if(check==2){
                                        //simpleDateFormatCheck.applyPattern("MMM d,''yy");
                                        selectMonth.setText("date: "+ month);
                                        Log.d("TAG", "onSuccess: "+month);
                                    simpleDateFormat.applyPattern("yyyy-MM-d");
                                    if(simpleDateFormat.format(transaction.getDate()).compareTo(month)==0 &&
                                            transaction.getType().equals("Expense")){
                                        monthlyExpenseList.add(transaction);
                                            expenseAmount += transaction.getAmount();
                                    }
                                    if(simpleDateFormat.format(transaction.getDate()).compareTo(month)==0 &&
                                             transaction.getType().equals("Income")){
                                            monthlyIncomeList.add(transaction);
                                            incomeAmount += transaction.getAmount();
                                    }
                                    }

                                    if(check==3){

                                        selectMonth.setText("Weekly: "+ month);
                                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
                                        simpleDateFormatCheck.applyPattern("dd/MM/yy");
                                        try {
                                           Date selected = format.parse(month);
                                           Date previous = format.parse(simpleDateFormatCheck.format(transaction.getDate()));
                                            assert selected != null;

                                            assert previous != null;
                                            long difference_In_Time = (selected.getTime()) - (previous.getTime());

                                            long difference_In_Days =( (difference_In_Time / (1000 * 60 * 60 * 24)) % 365);

                                        if((difference_In_Days <= 7) && (difference_In_Days >= 0) && transaction.getType().equals("Expense")){
                                        monthlyExpenseList.add(transaction);
                                            expenseAmount += transaction.getAmount();
                                        }
                                        if(difference_In_Days <= 7 && difference_In_Days >= 0 && transaction.getType().equals("Income")){
                                            monthlyIncomeList.add(transaction);
                                            incomeAmount += transaction.getAmount();
                                    }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if(check==4){
                                        selectMonth.setText(month);
                                        simpleDateFormat.applyPattern("yyyy");

                                        if(simpleDateFormat.format(transaction.getDate()).compareTo(month)==0 &&
                                            transaction.getType().equals("Expense")){
                                        monthlyExpenseList.add(transaction);
                                            expenseAmount += transaction.getAmount();
                                    }
                                    if(simpleDateFormat.format(transaction.getDate()).compareTo(month)==0 &&
                                             transaction.getType().equals("Income")){
                                            monthlyIncomeList.add(transaction);
                                            incomeAmount += transaction.getAmount();
                                    }
                                    }
                                }
                                  List<Transaction> incomeList = IncomeFinalList(monthlyIncomeList);
                                  List<Transaction> expenseList = IncomeFinalList(monthlyExpenseList);
                                     addTransactionList(expenseList,incomeList);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

      //  System.runFinalizersOnExit(true);
      //  android. os. Process. killProcess(android. os. Process. myPid());
        this.finishAffinity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isMonth) {
            if(checkType==1) {
                inflateNewTransaction(getIntent().getStringExtra("optionSelected"),1);
            }
            if(checkType==2){
                inflateNewTransaction(getIntent().getStringExtra("optionSingleDateSelected"),2);
            }
            if(checkType==3){
                inflateNewTransaction(getIntent().getStringExtra("optionWeekDateSelected"),3);
            }
            if(checkType==4){
                inflateNewTransaction(getIntent().getStringExtra("optionYearSelected"),4);
            }
        } else {
            updateSpending();
        }
    }

    private void updateSpending() {
        collectionReference.whereEqualTo("userId", TransactionApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot transaction: queryDocumentSnapshots){
                                Transaction transaction1 = transaction.toObject(Transaction.class);
                                if(transaction1.getType()!=null) {
                                    if (transaction1.getType().equals("Expense")) {
                                        expenseTransactionList.add(transaction1);
                                        expenseAmount += transaction1.getAmount();
                                    } else {
                                        incomeTransactionList.add(transaction1);
                                        incomeAmount += transaction1.getAmount();
                                    }
                                }
                            }
                            List<Transaction> incomeList = IncomeFinalList(incomeTransactionList);
                              List<Transaction> expenseList = IncomeFinalList(expenseTransactionList);
                            addTransactionList(expenseList,incomeList);
                        }
                    }
                })
                .addOnFailureListener(e -> { });

        expenseTransactionList.removeAll(expenseTransactionList);
        incomeTransactionList.removeAll(incomeTransactionList);
        monthlyExpenseList.removeAll(monthlyExpenseList);
        monthlyIncomeList.removeAll(monthlyIncomeList);
        expenseAmount=0;
        incomeAmount=0;

    }

    private List<Transaction> IncomeFinalList(List<Transaction> list1) {
        List<String> categoriesList = new ArrayList<>();
      loop1 :  for(Transaction obj : list1){
            for(int i = 0 ; i<categoriesList.size() ; i++){
                if(obj.getCategory().equals(categoriesList.get(i))){
                    continue loop1;
                }
            }
            categoriesList.add(obj.getCategory());
        }
//        Log.d("categories", "IncomeFinalList: " + categoriesList.toString());

      for(String category : categoriesList){
            int total_expense = 0;
            for(Transaction obj : list1){
                if(obj.getCategory().equals(category)){
                    total_expense += obj.getAmount();
                }
            }
            for(Transaction obj : list1){
                if(obj.getCategory().equals(category)){
                    obj.setAmount(total_expense);
                }
            }
    }
            List<Transaction> finalList = new ArrayList<>();
            loop1 :  for(Transaction obj : list1) {
                for (int i = 0; i < finalList.size(); i++) {
                    if (obj.getCategory().equals(finalList.get(i).getCategory())) {
                        continue loop1;
                    }
                }
                finalList.add(obj);
            }
//        for(Transaction obj : finalList) {
//            Log.d("finalList", "IncomeFinalList: " + obj.getCategory() +":" + obj.getAmount());
//        }
        return finalList;
    }

    private void addTransactionList(List<Transaction> EList1, List<Transaction> IList2) {
        totalExpenseAmount.setText(NumberFormat.getCurrencyInstance().format(expenseAmount));
        totalIncomeAmount.setText(NumberFormat.getCurrencyInstance().format(incomeAmount));
        balance.setText(NumberFormat.getCurrencyInstance().format((incomeAmount-expenseAmount)));
        if(IList2.size()>=4){

              LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500);
 incomeRecyclerView.setLayoutParams(lp);

        }
        if(EList1.size()>=4){
//            DisplayMetrics dm = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(dm);
//            int width = dm.widthPixels;
//            int height = (int)(dm.heightPixels*0.4);
//            getWindow().setLayout(width,height);
//            getWindow().setAttributes();

            LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500);
             expenseRecyclerView.setLayoutParams(lp);



//         ViewGroup.LayoutParams params = new  RecyclerView.LayoutParams();
//        expenseRecyclerView.setLayoutParams(params);
    }
        recyclerViewAdapterIncome = new RecyclerViewAdapter(IList2);
        recyclerViewAdapterExpense = new RecyclerViewAdapter(EList1);
        incomeRecyclerView.setAdapter(recyclerViewAdapterIncome);
        expenseRecyclerView.setAdapter(recyclerViewAdapterExpense);
        recyclerViewAdapterExpense.notifyDataSetChanged();
        recyclerViewAdapterIncome.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        expenseTransactionList.removeAll(expenseTransactionList);
        incomeTransactionList.removeAll(incomeTransactionList);
        monthlyExpenseList.removeAll(monthlyExpenseList);
        monthlyIncomeList.removeAll(monthlyIncomeList);
        expenseAmount=0;
        incomeAmount=0;
    }
}
