package com.nemo.expense.database.util;

import com.nemo.expense.database.CategoryDatabase;
import com.nemo.expense.database.model.CategoryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DatabaseFiller {
    private static final Logger log = LoggerFactory.getLogger(DatabaseFiller.class);
    private final CategoryDatabase categoryDatabase;

    @Inject
    public DatabaseFiller(CategoryDatabase categoryDatabase) {
        this.categoryDatabase = categoryDatabase;
    }

    public void fillDefaultCategoriesIfNeeded() {
        List<CategoryModel> categories = categoryDatabase.getCategories();
        if (categories.size() == 0) {
            log.info("Creating default categories");
            getDefaultCategories().stream().forEach(category -> {
                category.setId(UUID.randomUUID().toString());
                this.categoryDatabase.addCategory(category);
            });
        }
    }

    private List<CategoryModel> getDefaultCategories() {
        List<CategoryModel> defaultCategories = new ArrayList<>();
        defaultCategories.addAll(
                Arrays.asList(
                    CategoryModel.builder().icon("shopping_basket").name("Grocery").build(),
                    CategoryModel.builder().icon("restaurant_menu").name("Restaurant").build(),
                    CategoryModel.builder().icon("local_gas_station").name("Petrol").build(),
                    CategoryModel.builder().icon("local_taxi").name("Taxi").build(),
                    CategoryModel.builder().icon("settings_input_composite").name("Maintenance").build(),
                    CategoryModel.builder().icon("attach_money").name("Utility Bill").build(),
                    CategoryModel.builder().icon("pets").name("Personal Care").build(),
                    CategoryModel.builder().icon("shopping_cart").name("Shopping").build(),
                    CategoryModel.builder().icon("streetview").name("Insurance").build(),
                    CategoryModel.builder().icon("home").name("Rent").build()
                )
        );
        return defaultCategories;
    }
}
