package com.example.moneytracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytracker.R;
import com.example.moneytracker.model.Transaction;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class TransactionRecyclerViewAdapter extends RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder> {

    private OnCardCLickListener cardCLickListener;
    private List<Transaction> transactionList;

    public TransactionRecyclerViewAdapter(List<Transaction> transactionList, OnCardCLickListener cardCLickListener) {
        this.transactionList = transactionList;
        this.cardCLickListener = cardCLickListener;
    }

    @NonNull
    @Override
    public TransactionRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_info_row,parent,false);

        return new ViewHolder(view,cardCLickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionRecyclerViewAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("EEE, d MMMM yyyy");
        if(transaction!=null) {
            if(transaction.getNote().equals("")){
                holder.note.setText("Note: None");
            }else{
                holder.note.setText("Note: "+transaction.getNote());
            }

            holder.category.setText(transaction.getCategory());
            holder.date.setText(simpleDateFormat.format(transaction.getDate()));
            holder.amount.setText(NumberFormat.getCurrencyInstance().format(transaction.getAmount()));
            if (transaction.getType().equals("Income")) {
                holder.amount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.dark_green));
            }
        }

    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        OnCardCLickListener cardCLickListener;
        private TextView category, date, amount,note;

        public ViewHolder(@NonNull View itemView, OnCardCLickListener cardCLickListener) {
            super(itemView);

            note = itemView.findViewById(R.id.note);
            category = itemView.findViewById(R.id.category_row);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount_spend);
            this.cardCLickListener = cardCLickListener;
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            cardCLickListener.onCardCLick(getAdapterPosition());
            return true;
        }
    }

        public interface OnCardCLickListener{
            void onCardCLick(int position);
        }

}
