package com.nemo.expense.dagger;

import com.nemo.expense.api.Server;
import com.nemo.expense.database.CategoryDatabase;
import com.nemo.expense.database.ExpenseDatabase;
import com.nemo.expense.database.util.DatabaseFiller;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = MongoDBModule.class)
public interface Components {
    Server getServer();
    DatabaseFiller getDatabaseFiller();
    CategoryDatabase getCategoryDatabase();
    ExpenseDatabase getExpenseDatabase();
}
