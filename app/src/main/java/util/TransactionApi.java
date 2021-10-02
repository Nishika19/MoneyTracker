package util;

import android.app.Application;

import com.example.moneytracker.model.Transaction;

public class TransactionApi extends Application {
      private String username;
    private String userId;
    private static TransactionApi instance;

    public static TransactionApi getInstance(){
        if(instance==null){
            instance = new TransactionApi();
        }
        return instance;
    }

    public TransactionApi() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
