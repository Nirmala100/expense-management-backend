package com.nemo.expense;

import com.nemo.expense.api.Server;
import com.nemo.expense.dagger.Components;
import com.nemo.expense.dagger.DaggerComponents;
import com.nemo.expense.database.CategoryDatabase;
import com.nemo.expense.database.ExpenseDatabase;
import com.nemo.expense.database.model.CategoryModel;
import com.nemo.expense.database.model.ExpenseModel;
import com.nemo.expense.database.util.DatabaseFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Components component = DaggerComponents.create();
//        dummyExpenses(component);
        doFirstTimeStuffs(component);
        log.info("Starting server....");
        Server server = component.getServer();
        server.prepareApiPath();
        server.start();
    }

    private static void doFirstTimeStuffs(Components component) {
        ForkJoinPool.commonPool().execute(() -> {
            DatabaseFiller databaseFiller = component.getDatabaseFiller();
            databaseFiller.fillDefaultCategoriesIfNeeded();
        });
    }

    private static void dummyExpenses(Components component) {
        String userId = "68648de8-cc4e-468f-b76a-be01b71da77c";
        LocalDate date = LocalDate.now();
        Random rand = new Random();
        int totalDays = 300;
        double priceMin = 20.0;
        double priceMax = 500.0;
        CategoryDatabase categoryDatabase = component.getCategoryDatabase();
        List<CategoryModel> categories = categoryDatabase.getCategories();
        categories.addAll(categoryDatabase.getCategoriesByUserId(userId));

        ExpenseDatabase expenseDatabase = component.getExpenseDatabase();
        date = date.minus(totalDays, ChronoUnit.DAYS);

        while (totalDays > 0) {
            for (int i=0; i<3; i++) {
                CategoryModel randCategory = categories.get(Math.abs(rand.nextInt(categories.size())));
                double price = priceMin + (priceMax - priceMin) * rand.nextDouble();
                expenseDatabase.addExpense(
                        ExpenseModel.builder()
                                .id(UUID.randomUUID().toString())
                                .name(UUID.randomUUID().toString())
                                .price(price)
                                .userId(userId)
                                .categoryName(randCategory.getName())
                                .date(date.atStartOfDay().toEpochSecond(ZoneOffset.UTC))
                                .build()
                );
            }
            totalDays--;
            date = date.plus(1, ChronoUnit.DAYS);
        }
    }
}
