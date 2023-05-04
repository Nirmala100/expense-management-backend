package com.nemo.expense.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.expense.dagger.Constants;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.ContentType;
import io.javalin.json.JavalinJackson;
import io.javalin.security.RouteRole;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class Server {
    private final static Logger log = LoggerFactory.getLogger(Server.class);
    private final CategoryController categoryController;
    private final LoginController loginController;
    private final ExpenseController expenseController;

    private final Javalin application;

    @Inject
    public Server(CategoryController categoryController,
            LoginController loginController,
            ExpenseController expenseController) {
        this.categoryController = categoryController;
        this.loginController = loginController;
        this.expenseController = expenseController;
        this.application = Javalin.create();
    }

    public void prepareApiPath() {
        this.application.updateConfig(config -> {
            configureJacksonSerializer(config);
            allowCors(config);
            config.http.defaultContentType = ContentType.JSON;
            configureAccessControl(config);
        });

        this.application.post("login", loginController::login, Role.ANYONE);
        this.application.routes(() -> path("user", () -> {
            post(loginController::createUser, Role.PER_USER);
            get(loginController::getUser, Role.PER_USER);
        }));

        this.application.routes(() -> path("expenses", () -> {
            //get(expenseController::listExpenses, Role.PER_USER);
            get(expenseController::listExpenses, Role.ANYONE);
            post(expenseController::createExpense, Role.PER_USER);
            path("{id}", () -> {
                get(expenseController::getOneExpense, Role.PER_USER);
                post(expenseController::updateExpense, Role.PER_USER);
                delete(expenseController::deleteExpense, Role.PER_USER);
            });
        }));

        this.application.routes(() -> path( "categories", () -> {
            get(categoryController :: listCategories, Role.ANYONE);
            post(categoryController :: createCategory, Role.PER_USER);
            path("{id}",() -> {
                get(categoryController :: getOneCategory, Role.PER_USER);
                post(categoryController :: updateCategory, Role.PER_USER);
                delete(categoryController :: deleteCategory, Role.PER_USER);
            });
        }));
    }

    private void configureJacksonSerializer(JavalinConfig config) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Ignore null during serialization
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignore missing fields during deserialization
        JavalinJackson jsonMapper = new JavalinJackson(mapper);
        config.jsonMapper(jsonMapper);
    }

    public void start() {
        this.application.start(Constants.HTTP_PORT);
    }

    private void allowCors(JavalinConfig config) {
        config.plugins.enableCors(cors -> {
            cors.add(it -> {
                it.anyHost();
            });
        });
    }

    private void configureAccessControl(JavalinConfig config) {
        config.accessManager((handler, ctx, routeRoles) -> {
            log.info("Handler: {}, Context: {}, RouteRoles: {}", handler.getClass(), ctx, routeRoles);
            if (routeRoles.contains(Role.PER_USER) || Strings.isNotEmpty(ctx.header("Authorization"))) {
                try {
                    loginController.validateToken(ctx);
                } catch (IllegalArgumentException e) {
                    ctx.status(401).result("Unauthorized");
                    return;
                }
                log.debug("Request: {}:{} validated for user: {}", ctx.method().name(),
                        ctx.fullUrl(), ctx.attribute("user"));
            }
            handler.handle(ctx);
        });
    }

    enum Role implements RouteRole {
        ANYONE, PER_USER;
    }
}
