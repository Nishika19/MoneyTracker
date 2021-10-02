package com.example.moneytracker.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytracker.R;
import com.example.moneytracker.model.Transaction;

import java.text.NumberFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Transaction> transactionList;
      Integer totalAmount=0;

    public RecyclerViewAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.transaction_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
         holder.categoryDetail.setText(transaction.getCategory());
            holder.amountDetail.setText(NumberFormat.getCurrencyInstance().format(transaction.getAmount()));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryDetail, amountDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryDetail = itemView.findViewById(R.id.category_detail_mainRow);
            amountDetail = itemView.findViewById(R.id.expense_amount);
        }
    }
}
