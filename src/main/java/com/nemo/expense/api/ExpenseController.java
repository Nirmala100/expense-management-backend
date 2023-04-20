package com.nemo.expense.api;

import com.nemo.expense.api.model.CreateExpenseInput;
import com.nemo.expense.api.model.CreateExpenseOutput;
import com.nemo.expense.api.model.exceptions.AlreadyExistException;
import com.nemo.expense.api.model.exceptions.ResourceNotFoundException;
import com.nemo.expense.database.ExpenseDatabase;
import com.nemo.expense.database.util.ExpenseFilter;
import com.nemo.expense.database.model.ExpenseModel;
import com.nemo.expense.database.model.UserModel;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class ExpenseController {
    private final ExpenseDatabase dbModel;

    @Inject
    public ExpenseController(ExpenseDatabase expenseDb) {
        this.dbModel = expenseDb;
    }

    public void createExpense(@NotNull Context ctx) {
        //saved using token at the time of login
        UserModel user = ctx.attribute("user");
        CreateExpenseInput expense = ctx.bodyAsClass(CreateExpenseInput.class);
        System.out.println(expense.getName());
        System.out.println(expense.getPrice());
        System.out.println(expense.getDate());
        if (expense.getCategoryName() == null || expense.getCategoryName().trim().length() == 0) {
            expense.setCategoryName("Other");
        }

        ExpenseModel model = new ExpenseModel();
        model.setId(UUID.randomUUID().toString());
        model.setName(expense.getName());
        model.setPrice(expense.getPrice());
        model.setUserId(user.getId());
        model.setDate(expense.getDate());
        model.setCategoryName(expense.getCategoryName());
        System.out.println(expense);

        try {
            dbModel.addExpense(model);
            CreateExpenseOutput output = new CreateExpenseOutput();
            output.setName(expense.getName());
            ctx.status(200).json(output);
        } catch (AlreadyExistException e) {
            CreateExpenseOutput output = new CreateExpenseOutput();
            output.setError("Expense already exist");
           // throw e;
           ctx.status(409).json(output);

        }
    }

    public void listExpenses(@NotNull Context ctx) {
        UserModel user = ctx.attribute("user");
        String category = ctx.queryParam("category");
        ExpenseFilter filter = new ExpenseFilter(user.getId())
                .withCategoryName(category);
        if (ctx.queryParam("fromDate") != null && ctx.queryParam("toDate") != null) {
            Long fromDate = Long.parseLong(ctx.queryParam("fromDate"));
            Long toDate = Long.parseLong(ctx.queryParam("toDate"));
            filter = filter.withDateRange(fromDate, toDate);
        }
        List<ExpenseModel> expenses = dbModel.getExpenseByFiltering(filter);
        ctx.json(expenses);
    }

/*    public void listExpensesByCategory(@NotNull Context ctx) {
        UserModel user = ctx.attribute("user");
        if (user != null) {
            ctx.json(dbModel.getExpensesByCatIdByUserId(user.getId(), ctx.pathParam("catId")));
        }
    }*/

    public void updateExpense(@NotNull Context ctx) {
        String id = ctx.pathParam("id");
        CreateExpenseInput toBeUpdated = ctx.bodyAsClass(CreateExpenseInput.class);
        System.out.println("Updating expenseId: " + id);
        ExpenseModel model = new ExpenseModel();

        model.setName(toBeUpdated.getName());
        model.setPrice(toBeUpdated.getPrice());
        model.setCategoryName(toBeUpdated.getCategoryName());
        try {
            ExpenseModel updatedExpense = dbModel.updateExpense(id, model);
            ctx.json(updatedExpense);
        } catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
            ctx.status(404).result(e.getMessage());
        }
    }

    public void getOneExpense(Context ctx) {
        System.out.println("id " + ctx.pathParam("id"));
        System.out.println("db " + dbModel.getExpenseById(ctx.pathParam("id")));
        ctx.json(dbModel.getExpenseById(ctx.pathParam("id")));
    }

    public void deleteExpense(Context ctx) {
        try {
            dbModel.deleteExpenseById(ctx.pathParam("id"));
        } catch (ResourceNotFoundException e) {
            ctx.status(404).result(e.getMessage());
        }
    }

/*    public void getExpensesByDate(@NotNull Context ctx) {
        UserModel user = ctx.attribute("user");
        ExpenseSearch expense = ctx.bodyAsClass(ExpenseSearch.class);
        System.out.println("LongfromDate: " + expense);
      //  List output = dbModel.getExpenseByUserByDate(user.getId(), expense.getFromDate(), expense.getToDate());
        ctx.status(200).json(dbModel.getExpenseByUserByDate(user.getId(), expense.getFromDate(), expense.getToDate()));

    }*/
}
