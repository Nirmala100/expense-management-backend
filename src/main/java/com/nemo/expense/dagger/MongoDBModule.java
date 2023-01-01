package com.nemo.expense.dagger;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.nemo.expense.database.CategoryDatabase;
import com.nemo.expense.database.ExpenseDatabase;
import com.nemo.expense.database.UserDatabase;
import com.nemo.expense.database.model.CategoryModel;
import com.nemo.expense.database.model.ExpenseModel;
import com.nemo.expense.database.model.UserModel;
import dagger.Module;
import dagger.Provides;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import javax.inject.Singleton;

@Module
public class MongoDBModule {

    @Provides
    @Singleton
    public MongoClient provideMongoClient() {
        CodecRegistry pojoCodecRegistry = CodecRegistries
                .fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry);
        ConnectionString connectionString = new ConnectionString(Constants.MONGO_CONNECTION_STRING);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
        return MongoClients.create(clientSettings);
    }

    @Provides
    @Singleton
    public MongoDatabase provideMongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(Constants.MONGO_DATABASE_NAME);
    }

    @Provides
    @Singleton
    public CategoryDatabase provideCategoryDatabase(MongoDatabase database) {
        MongoCollection<CategoryModel> mongoCollection =
                database.getCollection(Constants.CATEGORIES_COLLECTION_NAME, CategoryModel.class);
        return new CategoryDatabase(mongoCollection);
    }

    @Provides
    @Singleton
    public UserDatabase provideUserDatabase(MongoDatabase database) {
        MongoCollection<UserModel> mongoCollection =
                database.getCollection(Constants.USERS_COLLECTION_NAME, UserModel.class);
        return new UserDatabase(mongoCollection);
    }

    @Provides
    @Singleton
    public ExpenseDatabase provideExpenseDatabase(MongoDatabase database) {
        MongoCollection<ExpenseModel> mongoCollection =
                database.getCollection(Constants.EXPENSE_COLLECTION_NAME, ExpenseModel.class);
        return new ExpenseDatabase(mongoCollection);
    }
}
