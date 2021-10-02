package com.example.moneytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.moneytracker.model.NewCategory;
import com.example.moneytracker.model.Transaction;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.TransactionApi;

public class Income_pie_chart extends AppCompatActivity {

     PieChart pieChart;
    PieData pieData;
    private Button expense_pie_chart;
    private ImageButton barGraph;
    private List<Transaction> TransactionList;
    List<PieEntry> pieEntryList = new ArrayList<>();

         private FirebaseAuth firebaseAuth;
        private FirebaseUser firebaseUser;
        private FirebaseAuth.AuthStateListener authStateListener;

        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        private CollectionReference collectionReference = db.collection("Transaction");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_pie_chart);

         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

          firebaseAuth = FirebaseAuth.getInstance();
          TransactionList = new ArrayList<>();
          expense_pie_chart = findViewById(R.id.expense_pie_chart);
          barGraph = findViewById(R.id.bar_graph_btn);
          barGraph.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(Income_pie_chart.this,BarGraphActivity.class));
                  finish();
              }
          });
          expense_pie_chart.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(Income_pie_chart.this,GraphActivity.class));
                  finish();
              }
          });
         pieChart = findViewById(R.id.pieChart);
         pieChart.setUsePercentValues(true);

         AddListToChart();
    }

    private void AddListToChart() {
        collectionReference.whereEqualTo("userId",TransactionApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                                Transaction transaction = snapshot.toObject(Transaction.class);
                                if(transaction.getType().equals("Income")) {
                                    TransactionList.add(transaction);
                                }
                            }
                            List<Transaction> incomeList = IncomeFinalList(TransactionList);
                            pieChartEntry(incomeList);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void pieChartEntry(List<Transaction> incomeList) {
        for(int i=0;i<incomeList.size();i++){
            pieEntryList.add(new PieEntry(incomeList.get(i).getAmount(),incomeList.get(i).getCategory()));
        }
         PieDataSet pieDataSet = new PieDataSet(pieEntryList,"Categorical Income Pie Chart");
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

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

}