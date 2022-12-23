package com.nemo.expense.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.nemo.expense.api.model.exceptions.AlreadyExistException;
import com.nemo.expense.api.model.exceptions.ResourceNotFoundException;
import com.nemo.expense.database.model.CategoryModel;
import com.nemo.expense.database.model.ExpenseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.regex;

public class ExpenseDatabase {
    private final MongoCollection<ExpenseModel> mongoCollection;

    public ExpenseDatabase(MongoCollection<ExpenseModel> mongoCollection) {
        this.mongoCollection = mongoCollection;
    }

    public void addExpense(ExpenseModel expenseModel) {
      //  System.out.println("here");
       // System.out.println(expenseModel);
        ExpenseModel expense = mongoCollection.find(
                and(
                        eq("name", expenseModel.getName()),
                        eq("userId",expenseModel.getUserId())
                )).first();
        System.out.println("expense already exist"+ expense);
        if (expense == null) {
            mongoCollection.insertOne(expenseModel);
            System.out.println("Successfully written to database");
         } else {
        throw new AlreadyExistException(String.format("Expense already exist"));
        }
    }

/*    public List<ExpenseModel> getExpensesByUserId(String userId) {
        FindIterable<ExpenseModel> iterable = mongoCollection.find(eq("userId", userId)).limit(5);
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }
    public List<ExpenseModel> getExpensesByCatIdByUserId(String userId, String categoryName) {
        FindIterable<ExpenseModel> iterable = mongoCollection.find(
                and(
                        regex("categoryName", "^" + categoryName),
                        eq("userId",userId)
                )
        ).limit(20);
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    public List<ExpenseModel> getExpenseByUserByDate(String userId, Long fromDate, Long toDate) {
        System.out.println("From Date "+ fromDate + " To Date: " + toDate);
        FindIterable<ExpenseModel> iterable = mongoCollection.find(
                and(
                        and(
                                gte("date", fromDate),
                                lte("date",toDate)
                        ),
                        eq("userId",userId)
                )
        ).limit(20);
        // if (StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList()).isEmpty()) {
        //  return new ArrayList<>();
        //  }
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }*/

    public List<ExpenseModel> getExpenseByFiltering(ExpenseFilter filter) {
        FindIterable<ExpenseModel> iterable = mongoCollection.find(filter.getFilter()).limit(5);
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }


    public ExpenseModel updateExpense(String id, ExpenseModel updated) {
        ExpenseModel oldExpense = mongoCollection.findOneAndReplace(eq("_id", id), updated);
        if (oldExpense != null) {
            return getExpenseById(id);
        } else {
            throw new ResourceNotFoundException(String.format("ExpenseId: %s not found", id));
        }

    }

    public ExpenseModel getExpenseById(String id) {
        ExpenseModel expense = mongoCollection.find(eq("_id", id)).first();
        if (expense == null) {
            throw new ResourceNotFoundException(String.format("ExpenseId: %s not found", id));
        }
        return expense;
    }

    public void deleteExpenseById(String id) {
        DeleteResult result = mongoCollection.deleteOne(eq("_id", id));
        System.out.println("DeleteResult: " + result);
        if (result.getDeletedCount() == 0) {
            throw new ResourceNotFoundException(String.format("ExpenseId: %s not found", id));
        }
    }
}
