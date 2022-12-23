package com.nemo.expense.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.nemo.expense.api.model.exceptions.AlreadyExistException;
import com.nemo.expense.api.model.exceptions.ResourceNotFoundException;
import com.nemo.expense.database.model.CategoryModel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class CategoryDatabase {
    private final MongoCollection<CategoryModel> mongoCollection;
    public CategoryDatabase(MongoCollection<CategoryModel> categoryCollection) {
        this.mongoCollection = categoryCollection;
    }

    public List<CategoryModel> getCategories() {
        FindIterable<CategoryModel> iterable = mongoCollection.find().limit(20);
        return StreamSupport.stream(iterable.spliterator(),false).collect(Collectors.toList());
    }

    public List<CategoryModel> getCategoriesByUserId(String userId) {
        FindIterable<CategoryModel> iterable = mongoCollection.find(eq("userId",userId));
        return StreamSupport.stream(iterable.spliterator(),false).collect(Collectors.toList());
    }

    public void addCategory(CategoryModel categoryModel) {
       // CategoryModel category = mongoCollection.find(eq("name",categoryModel.getName())).first();
        CategoryModel category = mongoCollection.find(
                and(
                    eq("name", categoryModel.getName()),
                    eq("userId", categoryModel.getUserId())
                )).first();
        if (category == null) {
            mongoCollection.insertOne(categoryModel);
            System.out.println("Successfully written to database");
        }else {
            throw new AlreadyExistException(String.format("Category already exist"));
        }

    }

    public CategoryModel getCategoryById(String id) {
        CategoryModel category = mongoCollection.find(eq("_id",id)).first();
        System.out.println(category);
        if (category == null) {
            throw new ResourceNotFoundException(String.format("CategoryId : %s not found", id));
        }
        return category;
    }

    public CategoryModel updateCategory(String id, CategoryModel updated) {
        CategoryModel oldCategory = mongoCollection.findOneAndReplace(eq("_id", id), updated);
        if (oldCategory != null) {
            return getCategoryById(id);
        } else {
            throw new ResourceNotFoundException(String.format("CategoryId: %s not found", id));
        }
    }

    public void deleteCategoryById(String id) {
        DeleteResult result = mongoCollection.deleteOne(eq("_id", id));
        System.out.println("DeleteResult: " + result);
        if (result.getDeletedCount() == 0) {
            throw new ResourceNotFoundException(String.format("CategoryId: %s not found", id));
        }
    }
}
