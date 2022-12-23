package com.nemo.expense.database.model;

import org.bson.codecs.pojo.annotations.BsonId;

public class SubCategoryModel {
    @BsonId
    private String id;
    private String categoryId;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SubCategoryModel{" +
                "id='" + id + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
