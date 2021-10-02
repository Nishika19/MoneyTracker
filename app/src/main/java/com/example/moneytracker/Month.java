package com.example.moneytracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.moneytracker.Dialog.MonthYearPickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Month extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener{

    private TextView jan, feb, march,april,may,jun,jul,aug,sep,oct,nov,dec,all,monthly,weekly,daily,yearly;
    private String monthSelected, optionSelected;
    Calendar calendar = Calendar.getInstance();
    Calendar calendarWeek = Calendar.getInstance();
    private Date WeekDateSelected, SingleDateSelected, yearSelected;
    SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
     SimpleDateFormat simpleDateFormatCheck = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
    private int flag=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        jan = findViewById(R.id.january);
        feb = findViewById(R.id.february);
        march = findViewById(R.id.march);
        april = findViewById(R.id.april);
        may = findViewById(R.id.may);
        jun = findViewById(R.id.june);
        jul = findViewById(R.id.july);
        aug = findViewById(R.id.august);
        sep = findViewById(R.id.september);
        oct = findViewById(R.id.october);
        nov = findViewById(R.id.november);
        dec = findViewById(R.id.december);
        all = findViewById(R.id.all);
        monthly = findViewById(R.id.monthly);
        daily = findViewById(R.id.daily);
        yearly = findViewById(R.id.yearly);
        weekly = findViewById(R.id.weekly);


        jan.setOnClickListener(this);
        feb.setOnClickListener(this);
        march.setOnClickListener(this);
        april.setOnClickListener(this);
        may.setOnClickListener(this);
        jun.setOnClickListener(this);
        jul.setOnClickListener(this);
        aug.setOnClickListener(this);
        sep.setOnClickListener(this);
        oct.setOnClickListener(this);
        nov.setOnClickListener(this);
        dec.setOnClickListener(this);
        all.setOnClickListener(this);
        monthly.setOnClickListener(this);
        daily.setOnClickListener(this);
        yearly.setOnClickListener(this);
        weekly.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
     int id = v.getId();
     monthSelected = v.getTag().toString();
     //Log.d("optionSelected", "onClick: " + optionSelected);

     if(monthSelected.equals("January") || monthSelected.equals("February") || monthSelected.equals("March")
             || monthSelected.equals("April") || monthSelected.equals("May") || monthSelected.equals("June")
     || monthSelected.equals("July") || monthSelected.equals("August") || monthSelected.equals("September") ||
     monthSelected.equals("October")|| monthSelected.equals("November") || monthSelected.equals("December")){

         optionSelected = monthSelected;
         Intent intent = new Intent(Month.this,SpendingActivity.class);
         intent.putExtra("optionSelected",optionSelected);
       startActivity(intent);
       finish();
     }


        if (id==R.id.monthly) {
            if (jan.getVisibility() == View.GONE) {
                feb.setVisibility(View.VISIBLE);
                march.setVisibility(View.VISIBLE);
                april.setVisibility(View.VISIBLE);
                may.setVisibility(View.VISIBLE);
                jun.setVisibility(View.VISIBLE);
                jul.setVisibility(View.VISIBLE);
                aug.setVisibility(View.VISIBLE);
                sep.setVisibility(View.VISIBLE);
                oct.setVisibility(View.VISIBLE);
                nov.setVisibility(View.VISIBLE);
                dec.setVisibility(View.VISIBLE);
                jan.setVisibility(View.VISIBLE);

            } else {

                feb.setVisibility(View.GONE);
                march.setVisibility(View.GONE);
                april.setVisibility(View.GONE);
                may.setVisibility(View.GONE);
                jun.setVisibility(View.GONE);
                jul.setVisibility(View.GONE);
                aug.setVisibility(View.GONE);
                sep.setVisibility(View.GONE);
                oct.setVisibility(View.GONE);
                nov.setVisibility(View.GONE);
                dec.setVisibility(View.GONE);
                jan.setVisibility(View.GONE);
            }
        }



        if(monthSelected.equals("Daily")){
            showDatePickerDialog();
            flag = 1;
        }

        if(monthSelected.equals("Weekly")){
           showDatePickerDialog();
           flag=2;
        }
        if(id==R.id.all){
            optionSelected = "All";
            Intent intent = new Intent(Month.this,SpendingActivity.class);
         intent.putExtra("optionSingleDateSelected",optionSelected);
         startActivity(intent);
         finish();
        }
        if(id==R.id.yearly){
            MonthYearPickerDialog monthYearPickerDialog = new MonthYearPickerDialog(R.layout.month_year_picker_dialog, this);
            monthYearPickerDialog.show(getSupportFragmentManager(),null);
            flag = 3;
        }

    }

      private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_YEAR));
        datePickerDialog.show();



    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year,month,dayOfMonth);
        if(flag==1){
            SingleDateSelected = calendar.getTime();
            simpleDateFormatCheck.applyPattern("yyyy-MM-d");
            simpleDateFormat.applyPattern("EEE, d MMMM yyyy");
             optionSelected = simpleDateFormatCheck.format(SingleDateSelected);
         Intent intent = new Intent(Month.this,SpendingActivity.class);
         intent.putExtra("optionSingleDateSelected",optionSelected);
         startActivity(intent);
         finish();

        }if(flag==2){
             WeekDateSelected = calendar.getTime();
            simpleDateFormatCheck.applyPattern("dd/MM/yy");
            simpleDateFormat.applyPattern("EEE, d MMMM yyyy");
              optionSelected = simpleDateFormatCheck.format(WeekDateSelected);
         Intent intent = new Intent(Month.this,SpendingActivity.class);
         intent.putExtra("optionWeekDateSelected",optionSelected);
         startActivity(intent);
         finish();
        }

        if(flag==3){
            yearSelected = calendar.getTime();
            simpleDateFormat.applyPattern("yyyy");
            optionSelected = simpleDateFormat.format(yearSelected);
            Log.d("TAG", "onDateSet: "+ simpleDateFormat.format(yearSelected));
            Intent intent = new Intent(Month.this,SpendingActivity.class);
            intent.putExtra("optionYearSelected",optionSelected);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Month.this,SpendingActivity.class));
    }
}





















