package com.example.moneytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.moneytracker.model.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import util.TransactionApi;

public class BarGraphActivity extends AppCompatActivity {

     BarChart barChart;
     BarData barData;
     private Button income_bar;
     private ImageButton pie_chart_btn;
     private int janAmt=0, febAmt=0, marAmt=0,aprAmt=0,mayAmt=0,junAmt=0,julAmt=0,augAmt=0,sepAmt=0,octAmt=0,
    novAmt=0,decAmt=0;
     List<BarEntry> barEntriesArrayList = new ArrayList<>();
      private List<Transaction> TransactionList;

       private FirebaseAuth firebaseAuth;
        private FirebaseUser firebaseUser;
        private FirebaseAuth.AuthStateListener authStateListener;

        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        private CollectionReference collectionReference = db.collection("Transaction");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_graph);

         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        income_bar = findViewById(R.id.income_pie_chart);
        pie_chart_btn = findViewById(R.id.pie_chart_btn);

        income_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BarGraphActivity.this,IncomeBarGraph.class));
                finish();
            }
        });
        pie_chart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BarGraphActivity.this,GraphActivity.class));
            }
        });

         firebaseAuth = FirebaseAuth.getInstance();
         barChart = findViewById(R.id.idBarChart);
           TransactionList = new ArrayList<>();
        getBarEntries();

    }

    private void getBarEntries() {

          collectionReference.whereEqualTo("userId", TransactionApi.getInstance().getUserId())
                  .get()
                  .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                      @Override
                      public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                          if(!queryDocumentSnapshots.isEmpty()){
                              for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                                  Transaction transaction = snapshot.toObject(Transaction.class);
                                  if(transaction.getType().equals("Expense")){
                                      TransactionList.add(transaction);
                                  }
                              }
                              addDatatoGraph(TransactionList);
                          }
                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {

                      }
                  });

    }

    private void addDatatoGraph(List<Transaction> transactionList) {
        for (Transaction transaction: transactionList){
            String month = transaction.getMonth();
            int amount = transaction.getAmount();
            if(month.equals("January")){
                janAmt+=amount;
            }
            if(month.equals("February")){
                febAmt+=amount;
            }
            if(month.equals("March")){
                marAmt+=amount;
            }
            if(month.equals("April")){
                aprAmt+=amount;
            }
             if(month.equals("May")){
                mayAmt+=amount;
            }
              if(month.equals("June")){
                junAmt+=amount;
            }
               if(month.equals("July")){
                julAmt+=amount;
            }
                if(month.equals("August")){
                augAmt+=amount;
            }
                 if(month.equals("September")){
                sepAmt+=amount;
            }
                  if(month.equals("October")){
                octAmt+=amount;
            }
                   if(month.equals("November")){
                novAmt+=amount;
            }
                    if(month.equals("December")){
                decAmt+=amount;
            }
        }
        barEntriesArrayList.add(new BarEntry(1f,janAmt));
        barEntriesArrayList.add(new BarEntry(2f,febAmt));
        barEntriesArrayList.add(new BarEntry(3f,marAmt));
        barEntriesArrayList.add(new BarEntry(4f,aprAmt));
        barEntriesArrayList.add(new BarEntry(5f,mayAmt));
        barEntriesArrayList.add(new BarEntry(6f,junAmt));
        barEntriesArrayList.add(new BarEntry(7f,julAmt));
        barEntriesArrayList.add(new BarEntry(8f,augAmt));
        barEntriesArrayList.add(new BarEntry(9f,sepAmt));
        barEntriesArrayList.add(new BarEntry(10f,octAmt));
        barEntriesArrayList.add(new BarEntry(11f,novAmt));
        barEntriesArrayList.add(new BarEntry(12f,decAmt));


//        final ArrayList<String> xAxisLabel = new ArrayList<>();
//    xAxisLabel.add("Mon");
//    xAxisLabel.add("Tue");
//    xAxisLabel.add("Wed");
//    xAxisLabel.add("Thu");
//    xAxisLabel.add("Fri");
//    xAxisLabel.add("Sat");
//    xAxisLabel.add("Sun");
//
//     XAxis xAxis = barChart.getXAxis();
//    xAxis.setValueFormatter(new ValueFormatter() {
//        @Override
//        public String getFormattedValue(float value, AxisBase axis) {
//            return xAxisLabel.get((int) value);
//
//        }
//    });

  BarDataSet barDataSet = new BarDataSet(barEntriesArrayList, "Monthly Expense");
        barData = new BarData(barDataSet);
        barChart.setData(barData);
        String[] xAxisLabel = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }
}