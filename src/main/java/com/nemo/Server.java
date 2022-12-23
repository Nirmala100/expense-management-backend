package com.nemo;

import com.nemo.expense.api.CategoryController;
import com.nemo.expense.api.SubCategoryController;
import com.nemo.expense.database.Database;
import com.nemo.expense.api.ExpenseController;
import com.nemo.expense.api.LoginController;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.security.RouteRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        Database database = new Database();
        database.initializeConnection();
        ExpenseController expenseController = new ExpenseController(database);
        LoginController loginController = new LoginController(database);
        CategoryController categoryController = new CategoryController(database);
        SubCategoryController subCategoryController = new SubCategoryController(database);

        log.info("Starting server...");
        Javalin app = Javalin.create(config  -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                });
            });
            config.http.defaultContentType = ContentType.JSON;
            config.accessManager((handler, ctx, routeRoles) -> {
                log.info("Handler: {}, Context: {}, RouteRoles: {}", handler, ctx, routeRoles);
                if (routeRoles.contains(Role.PER_USER)) {
                    try {
                        loginController.validateToken(ctx);
                    } catch (IllegalArgumentException e) {
                        ctx.status(401).result("Unauthorized");
                        return;
                    }
                    System.out.println("Request validated for user " + ctx.attribute("user"));
                }
                handler.handle(ctx);
            });
        });

        app.post("login", loginController::login, Role.ANYONE);
        app.routes(() -> path("user", () -> {
            post(loginController::createUser, Role.ANYONE);
        }));

//        app.post("search", expenseController::getExpensesByDate, Role.PER_USER);
        app.routes(() -> path("expenses", () -> {
            get(expenseController::listExpenses, Role.PER_USER);
            post(expenseController::createExpense, Role.PER_USER);
            path("{id}", () -> {
                get(expenseController::getOneExpense, Role.PER_USER);
                post(expenseController::updateExpense, Role.PER_USER);
                delete(expenseController::deleteExpense, Role.PER_USER);
            });
        }));


        app.routes(() -> path( "categories", () -> {
            get(categoryController :: listCategories, Role.PER_USER);
            post(categoryController :: createCategory, Role.PER_USER);
            path("{id}",() -> {
                get(categoryController :: getOneCategory, Role.PER_USER);
                post(categoryController :: updateCategory, Role.PER_USER);
                delete(categoryController :: deleteCategory, Role.PER_USER);
            });
        }));

        app.routes(() -> path( "subcategories", () -> {
            get(subCategoryController :: listSubCategories, Role.ANYONE);
            post(subCategoryController :: createSubCategory, Role.ANYONE);
            path("{id}",() -> {
                get(subCategoryController :: getOneSubCategory, Role.ANYONE);
                post(subCategoryController :: updateSubCategory, Role.ANYONE);
                delete(subCategoryController :: deleteSubCategory, Role.ANYONE);
            });
        }));

        app.before(ctx -> {
            // runs before all requests
        });
        app.before("/path/*", ctx -> {
            // runs before request to /path/*
        });
        app.after(ctx -> {
            log.info("After {}", ctx.result());
        });
        app.start(8081);
    }
}

enum Role implements RouteRole {
    ANYONE, PER_USER;
}
