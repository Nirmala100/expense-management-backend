package com.nemo.expense.database.model;

import org.bson.codecs.pojo.annotations.BsonId;

import java.util.Date;

public class ExpenseModel {
    @BsonId
    private String id;
    private String name;
    private Double price;

    private String userId;
    private String categoryName;

    private Long date;

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ExpenseModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", userId='" + userId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
