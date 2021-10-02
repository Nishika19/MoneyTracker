package com.example.moneytracker.model;

public class NewCategory {
    private String category;
    private int imageId;
    private String userId;

    public NewCategory() {
    }

    public NewCategory(String category, int imageId, String userId) {
        this.category = category;
        this.imageId = imageId;
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
