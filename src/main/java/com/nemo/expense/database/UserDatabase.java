package com.nemo.expense.database;

import com.mongodb.client.MongoCollection;
import com.nemo.expense.api.model.exceptions.AlreadyExistException;
import com.nemo.expense.api.model.exceptions.ResourceNotFoundException;
import com.nemo.expense.database.model.UserModel;

import static com.mongodb.client.model.Filters.eq;

public class UserDatabase {
    private final MongoCollection<UserModel> mongoCollection;

    public UserDatabase(final MongoCollection<UserModel> mongoCollection) {
        this.mongoCollection = mongoCollection;
    }

    public UserModel findUserByEmail(String email) {
        UserModel user = mongoCollection.find(eq("email", email)).first();
        if (user == null) {
            throw new ResourceNotFoundException(String.format("User: %s not found", email));
        }
        return user;
    }

    public void createUser(UserModel newUser) {
        UserModel existedUser = mongoCollection.find(eq("email", newUser.getEmail())).first();
        System.out.println("Existing user " + existedUser);
            if(existedUser == null) {
                mongoCollection.insertOne(newUser);
                System.out.println("Successfully created new user: " + newUser);

            } else {
                throw new AlreadyExistException(String.format("User already exist"));
            }

    }

    public void deleteUser(String email) {
//        mongoCollection.deleteOne(eq("email"))
    }
}
