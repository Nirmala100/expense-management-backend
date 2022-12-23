package com.nemo.expense.api;

import com.nemo.expense.api.model.CreateSubCategoryInput;
import com.nemo.expense.api.model.exceptions.ResourceNotFoundException;
import com.nemo.expense.database.Database;
import com.nemo.expense.database.SubCategoryDatabase;
import com.nemo.expense.database.model.SubCategoryModel;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SubCategoryController {

    private final SubCategoryDatabase dbModel;

    public SubCategoryController(Database database) {
        this.dbModel = new SubCategoryDatabase(database.getSubCategoryCollection());
    }
    public void listSubCategories(@NotNull Context ctx) {
        ctx.json(dbModel.listSubCategory());
    }
    public void createSubCategory(@NotNull Context ctx) {
        CreateSubCategoryInput input = ctx.bodyAsClass(CreateSubCategoryInput.class);
        SubCategoryModel model = new SubCategoryModel();
        model.setId(UUID.randomUUID().toString());
        model.setName(input.getName());
        model.setCategoryId(input.getCategoryId());
        dbModel.addSubCategory(model);
    }
    public void getOneSubCategory(@NotNull Context ctx) {
        ctx.json(dbModel.getSubCategoryById(ctx.pathParam("id")));
    }
    public void updateSubCategory(@NotNull Context ctx) {
        CreateSubCategoryInput toBeUpdated = ctx.bodyAsClass(CreateSubCategoryInput.class);
        SubCategoryModel model = new SubCategoryModel();
        model.setCategoryId(toBeUpdated.getCategoryId());
        model.setName(toBeUpdated.getName());
        String id = ctx.pathParam("id");
        try {
           SubCategoryModel updatedSubCategory = dbModel.updateSubCategory(id, model);
           ctx.json(updatedSubCategory);
        } catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
            ctx.status(404).result(e.getMessage());
        }
    }

    public void deleteSubCategory(Context ctx) {
        try {
            dbModel.deleteSubCategoryById(ctx.pathParam("id"));
        } catch(ResourceNotFoundException e) {
            System.out.println(e.getMessage());
            ctx.status(404).result(e.getMessage());
        }
    }

}
