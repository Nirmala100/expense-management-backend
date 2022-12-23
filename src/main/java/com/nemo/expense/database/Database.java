package com.nemo.expense.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.nemo.expense.database.model.CategoryModel;
import com.nemo.expense.database.model.ExpenseModel;
import com.nemo.expense.database.model.SubCategoryModel;
import com.nemo.expense.database.model.UserModel;
import jdk.jfr.Category;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class Database {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<ExpenseModel> expenseCollection;
    private MongoCollection<UserModel> userCollection;

    private MongoCollection<CategoryModel> categoryCollection;
    private MongoCollection<SubCategoryModel> subCategoryCollection;

    public MongoCollection<ExpenseModel> getExpenseCollection() {
        return expenseCollection;
    }

    public void initializeConnection() {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry);
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

        mongoClient = MongoClients.create(clientSettings);
        database = mongoClient.getDatabase("ems");
        expenseCollection = database.getCollection("expenses", ExpenseModel.class);
        userCollection = database.getCollection("users", UserModel.class);
        categoryCollection = database.getCollection("categories", CategoryModel.class);
        subCategoryCollection = database.getCollection("subCategories", SubCategoryModel.class);

        System.out.println("Expense Collection: " + expenseCollection);
    }

    public MongoCollection<UserModel> getUserCollection() {
        return userCollection;
    }

    public MongoCollection<CategoryModel> getCategoryCollection() {
        return categoryCollection;
    }
    public MongoCollection<SubCategoryModel> getSubCategoryCollection() {
        return subCategoryCollection;
    }
}
