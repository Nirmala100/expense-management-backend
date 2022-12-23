package com.nemo.expense.api;

import com.nemo.expense.api.model.CreateCategoryInput;
import com.nemo.expense.api.model.CreateCategoryOutput;
import com.nemo.expense.api.model.exceptions.AlreadyExistException;
import com.nemo.expense.api.model.exceptions.ResourceNotFoundException;
import com.nemo.expense.database.CategoryDatabase;
import com.nemo.expense.database.Database;
import com.nemo.expense.database.model.CategoryModel;
import com.nemo.expense.database.model.UserModel;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class CategoryController {

    private final CategoryDatabase dbModel;
    public CategoryController(Database database) {

        this.dbModel = new CategoryDatabase(database.getCategoryCollection());
    }

    public void listCategories(@NotNull Context ctx) {
        UserModel user = ctx.attribute("user");
        if (user == null){
            ctx.json(dbModel.getCategories());
        } else{
            ctx.json(dbModel.getCategoriesByUserId(user.getId()));
        }

    }



    public void createCategory(@NotNull Context ctx) {
        CreateCategoryInput input = ctx.bodyAsClass(CreateCategoryInput.class);
        CategoryModel model = new CategoryModel();
        model.setId(UUID.randomUUID().toString());
        model.setName(input.getName());
        UserModel user = ctx.attribute("user");//get user when set during token validation
        model.setUserId(user.getId());
        try{
            dbModel.addCategory(model);
            CreateCategoryOutput output = new CreateCategoryOutput();
            output.setName(input.getName());
            ctx.status(200).json(output);
        }
        catch(AlreadyExistException e) {
            CreateCategoryOutput output = new CreateCategoryOutput();
            output.setError("Category already exist");
            ctx.status(409).json(output);

        }

    }

    public void getOneCategory(Context ctx) {
        ctx.json(dbModel.getCategoryById(ctx.pathParam("id")));
    }

    public void updateCategory(Context ctx) {
        CreateCategoryInput toBeUpdated = ctx.bodyAsClass(CreateCategoryInput.class);
        String id = ctx.pathParam("id");
        CategoryModel model = new CategoryModel();
        model.setName(toBeUpdated.getName());

        try{
            CategoryModel updatedCategory = dbModel.updateCategory(id, model);
            ctx.json(updatedCategory);
        }catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
            ctx.status(404).result(e.getMessage());
        }

    }

    public void deleteCategory(Context ctx) {
        try {
            dbModel.deleteCategoryById(ctx.pathParam("id"));
        } catch(ResourceNotFoundException e) {
            System.out.println(e.getMessage());
            ctx.status(404).result(e.getMessage());
        }
    }


}
