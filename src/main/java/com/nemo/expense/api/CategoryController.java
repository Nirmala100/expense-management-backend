package com.nemo.expense.api;

import com.nemo.expense.api.model.CreateCategoryInput;
import com.nemo.expense.api.model.CreateCategoryOutput;
import com.nemo.expense.api.model.exceptions.AlreadyExistException;
import com.nemo.expense.api.model.exceptions.ResourceNotFoundException;
import com.nemo.expense.database.CategoryDatabase;
import com.nemo.expense.database.model.CategoryModel;
import com.nemo.expense.database.model.UserModel;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;


public class CategoryController {

    private final CategoryDatabase categoryDb;

    @Inject
    public CategoryController(CategoryDatabase categoryDb) {
        this.categoryDb = categoryDb;
    }

    public void listCategories(@NotNull Context ctx) {
        UserModel user = ctx.attribute("user");
        List<CategoryModel> categories = categoryDb.getCategories();
        if (user != null) {
            categories.addAll(categoryDb.getCategoriesByUserId(user.getId()));
            System.out.println("Fetching categories for user: " + user.getId() + " Received " + categoryDb.getCategoriesByUserId(user.getId()));
        }
        System.out.println("Returning categories: " + categories);
        ctx.json(categories);
    }



    public void createCategory(@NotNull Context ctx) {
        CreateCategoryInput input = ctx.bodyAsClass(CreateCategoryInput.class);
        CategoryModel model = new CategoryModel();
        model.setId(UUID.randomUUID().toString());
        model.setName(input.getName());
        model.setIcon(input.getIcon());
        UserModel user = ctx.attribute("user");//get user when set during token validation
        model.setUserId(user.getId());
        System.out.println("Creating new category: " + model);
        try{
            categoryDb.addCategory(model);
            CreateCategoryOutput output = new CreateCategoryOutput();
            output.setName(input.getName());
            ctx.status(200).json(output);
        } catch (AlreadyExistException e) {
            CreateCategoryOutput output = new CreateCategoryOutput();
            output.setError("Category already exist");
            ctx.status(409).json(output);

        }

    }

    public void getOneCategory(Context ctx) {
        ctx.json(categoryDb.getCategoryById(ctx.pathParam("id")));
    }

    public void updateCategory(Context ctx) {
        UserModel user = ctx.attribute("user");
        CreateCategoryInput toBeUpdated = ctx.bodyAsClass(CreateCategoryInput.class);
        String id = ctx.pathParam("id");
        CategoryModel model = new CategoryModel();
        model.setName(toBeUpdated.getName());
        model.setIcon(toBeUpdated.getIcon());
        model.setUserId(user.getId());
        try{
            checkOwnership(user, id);
            CategoryModel updatedCategory = categoryDb.updateCategory(id, model);
            ctx.json(updatedCategory);
        }catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
            ctx.status(404).result(e.getMessage());
        }

    }

    public void deleteCategory(Context ctx) {
        UserModel user = ctx.attribute("user");
        String categoryId = ctx.pathParam("id");
        try {
            checkOwnership(user, categoryId);
            categoryDb.deleteCategoryById(categoryId);
        } catch(ResourceNotFoundException e) {
            ctx.status(404).result(e.getMessage());
        }
    }

    private void checkOwnership(UserModel user, String categoryId) {
        // If userId matches then return true
        CategoryModel existing = categoryDb.getCategoryById(categoryId);
        if (!user.getId().equals(existing.getUserId())) {
            throw new ResourceNotFoundException(String.format("Category %s not found for user %s", categoryId, user.getId()));
        }
    }

}
