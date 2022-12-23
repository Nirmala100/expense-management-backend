package com.nemo.expense.database.model;

import org.bson.codecs.pojo.annotations.BsonId;

public class CategoryModel {
    @BsonId
    private String id;
    private String name;

    private String userId;

    public CategoryModel() {

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

    @Override
    public String toString() {
        return "CategoryModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
