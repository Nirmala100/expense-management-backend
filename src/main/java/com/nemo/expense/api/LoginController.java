package com.nemo.expense.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nemo.expense.api.model.CreateExpenseInput;
import com.nemo.expense.api.model.LoginOutput;
import com.nemo.expense.api.model.UserInput;
import com.nemo.expense.api.model.UserOutput;
import com.nemo.expense.api.model.exceptions.AlreadyExistException;
import com.nemo.expense.api.model.exceptions.ResourceNotFoundException;
import com.nemo.expense.database.UserDatabase;
import com.nemo.expense.database.model.ExpenseModel;
import com.nemo.expense.database.model.UserModel;
import com.nemo.jwt.Token;
import io.javalin.http.Context;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

public class LoginController {
    private static final String BEARER_PREFIX = "Bearer ";
    private final UserDatabase userDb;

    @Inject
    public LoginController(UserDatabase userDb) {
        this.userDb = userDb;
    }

    public void createUser(@NotNull Context ctx) {
        UserInput input = ctx.bodyAsClass(UserInput.class);
        String password = input.getPassword();
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(password, salt);
        UserModel userModel = new UserModel(
                UUID.randomUUID().toString(),
                input.getName(),
                input.getEmail(),
                hashedPassword);
        try{
            userDb.createUser(userModel);
            System.out.println("Didn't throw");
            UserOutput output = new UserOutput();
            output.setEmail(input.getEmail());
            ctx.status(200).json(output);
        } catch(AlreadyExistException e) {
            UserOutput output = new UserOutput();
            output.setError("User already exist");
            ctx.status(403).json(output);
        }

    }

    public void getUser(@NotNull Context ctx) {
        UserModel user = ctx.attribute("user");
        UserOutput output = new UserOutput();
        output.setEmail(user.getEmail());
        output.setPassword(user.getPassHashed());
        output.setName(user.getName());
        ctx.status(200).json(output);
    }


    public void login(@NotNull Context ctx) {
        UserInput input = ctx.bodyAsClass(UserInput.class);
        System.out.println("Email: " + input.getEmail());
        System.out.println("Password: " + input.getPassword());
        // check if username exists in database
        try{
            UserModel user = userDb.findUserByEmail(input.getEmail());
            System.out.println("Logging in user " + user);
           // if (!user.getPassHashed().equals(input.getPassword())) {
            if (!BCrypt.checkpw(input.getPassword(),user.getPassHashed())) {
                // return error to caller
                LoginOutput output = new LoginOutput();
                output.setError("Password does not match.");
                ctx.status(403).json(output);
                return;
            }

            Instant now = Instant.now();

            // compute token and return token to caller
            Token token  = new Token();
            token.addPayload("exp", Long.toString(now.plus(30000, ChronoUnit.MINUTES).toEpochMilli()));
            token.addPayload("iss", "EMS");
            token.addPayload("aud", user.getName());
            token.addPayload("email", user.getEmail());
            String generatedToken = token.encode();
            LoginOutput output = LoginOutput.builder().token(generatedToken).build();
            ctx.json(output);
        } catch(ResourceNotFoundException e) {
            LoginOutput output = new LoginOutput();
            output.setError("Invalid User");
            ctx.status(403).json(output);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void validateToken(@NotNull Context ctx) {
        try {
            String authorization = ctx.header("Authorization");
            if (Strings.isNotEmpty(authorization) && authorization.startsWith(BEARER_PREFIX)) {
                authorization = authorization.substring(BEARER_PREFIX.length());
                Token decodedToken = Token.decode(authorization);
                Map<String, String> payload = decodedToken.getPayload();
                payload.forEach((k, v) -> {
                    System.out.println("Token key: " + k + ", value: " + v + "::" + v.getClass());
                });
                System.out.println("Validation " + "EMS".equals(payload.get("iss")) + " " + !isExpired(payload.get("exp")));
                if ("EMS".equals(payload.get("iss")) && !isExpired(payload.get("exp"))) {
                    String email = (String) payload.get("email");
                    ctx.attribute("user", userDb.findUserByEmail(email)); //set user info after validate token
                    return;
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (ResourceNotFoundException e) {
            System.out.println("User not found");
        }
        throw new IllegalArgumentException("Invalid Token");
    }

    private boolean isExpired(String exp) {
        long expiration = Long.parseLong(exp);
        Instant now = Instant.now();
        return now.toEpochMilli() > expiration;
    }

    public void updateUser(@NotNull Context ctx) {
       //get id from the frontend i.e. from context
        UserModel user = ctx.attribute("user");
        UserInput toBeUpdated = ctx.bodyAsClass(UserInput.class);
        System.out.println("Updating userId: " + user.getId());
        UserModel model = new UserModel();
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(toBeUpdated.getPassword(), salt);
        model.setPassHashed(hashedPassword);

        try {
            UserModel updatedUser = userDb.updateUser(user.getId(), model);
            ctx.json(updatedUser);
        } catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
            ctx.status(404).result(e.getMessage());
        }
    }

}
