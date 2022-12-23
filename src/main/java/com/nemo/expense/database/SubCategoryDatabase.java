package com.nemo.expense.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.nemo.expense.api.model.exceptions.ResourceNotFoundException;
import com.nemo.expense.database.model.SubCategoryModel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

public class SubCategoryDatabase {
    private final MongoCollection<SubCategoryModel> mongoCollection;

    public SubCategoryDatabase(MongoCollection<SubCategoryModel> mongoCollection) {
        this.mongoCollection = mongoCollection;
    }
    public void addSubCategory(SubCategoryModel subCategoryModel) {
        mongoCollection.insertOne(subCategoryModel);
        System.out.println("Successfully written to database");
    }

    public List<SubCategoryModel> listSubCategory() {
        FindIterable<SubCategoryModel> iterable = mongoCollection.find().limit(20);
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }


    public SubCategoryModel updateSubCategory(String id, SubCategoryModel updated) {
        SubCategoryModel oldSubCategory = mongoCollection.findOneAndReplace(eq("_id", id), updated);
        if (oldSubCategory != null) {
            return getSubCategoryById(id);
        } else {
            throw new ResourceNotFoundException(String.format("SubCategoryId: %s not found", id));
        }

    }

    public SubCategoryModel getSubCategoryById(String id) {
        SubCategoryModel subCategory = mongoCollection.find(eq("_id", id)).first();
        if (subCategory == null) {
            throw new ResourceNotFoundException(String.format("SubCategoryId: %s not found", id));
        }
        return subCategory;
    }

    public void deleteSubCategoryById(String id) {
        DeleteResult result = mongoCollection.deleteOne(eq("_id", id));
        System.out.println("DeleteResult: " + result);
        if (result.getDeletedCount() == 0) {
            throw new ResourceNotFoundException(String.format("SubCategoryId: %s not found", id));
        }
    }


}
