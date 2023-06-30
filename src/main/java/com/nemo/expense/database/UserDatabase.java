package com.nemo.expense.database;

import com.mongodb.client.MongoCollection;
import com.nemo.expense.api.model.exceptions.AlreadyExistException;
import com.nemo.expense.api.model.exceptions.ResourceNotFoundException;
import com.nemo.expense.database.model.CategoryModel;
import com.nemo.expense.database.model.ExpenseModel;
import com.nemo.expense.database.model.UserModel;
import org.apache.logging.log4j.util.Strings;
//import org.mindrot.jbcrypt.BCrypt;

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

    public UserModel getUserById(String id) {
        UserModel user = mongoCollection.find(eq("_id", id)).first();
        if (user == null) {
            throw new ResourceNotFoundException(String.format("UserId: %s not found", id));
        }
        return user;
    }

    public UserModel updateUser(String id, UserModel updated) {
        UserModel existingUser = getUserById(id);
        if (existingUser != null) {
            //check if user has sent password or not
            if (Strings.isNotEmpty(updated.getEmail())) {
                existingUser.setEmail(updated.getEmail());
            }
            if (Strings.isNotEmpty(updated.getPassHashed())) {
                existingUser.setPassHashed(updated.getPassHashed());
            }
            existingUser = mongoCollection.findOneAndReplace(eq("_id", id), existingUser);

        }else {
            throw new ResourceNotFoundException(String.format("UserId: %s not found", id));
        }

        return existingUser;
    }

    public void deleteUser(String email) {
//        mongoCollection.deleteOne(eq("email"))
    }
}
