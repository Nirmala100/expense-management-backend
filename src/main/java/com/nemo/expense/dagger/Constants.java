package com.nemo.expense.dagger;

public class Constants {
    public static final String MONGO_CONNECTION_STRING_DEVELOPMENT = "mongodb://localhost:27017";
    public static final String MONGO_CONNECTION_STRING_PROD = "mongodb://expenser:dispenser@mongo:27017/";
    public static final String MONGO_DATABASE_NAME = "ems";
    public static final String EXPENSE_COLLECTION_NAME = "expenses";
    public static final String USERS_COLLECTION_NAME = "users";
    public static final String CATEGORIES_COLLECTION_NAME = "categories";
    public static final int HTTP_PORT = 8081;
}
