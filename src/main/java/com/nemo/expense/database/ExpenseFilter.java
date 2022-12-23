package com.nemo.expense.database;

import org.bson.conversions.Bson;

import java.util.Objects;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.regex;

public class ExpenseFilter {
    private Bson filter;

    public ExpenseFilter(String userId) {
        this.filter = eq("userId", Objects.requireNonNull(userId));
    }

    public ExpenseFilter withCategoryName(String categoryName) {
        if (categoryName != null) {
            this.filter = and(this.filter, regex("categoryName", "^" + categoryName));
        }
        return this;
    }

    public ExpenseFilter withDateRange(Long fromDate, Long toDate) {
        if (fromDate != null && toDate != null) {
            this.filter = and(
                    this.filter,
                    and(
                            gte("date", fromDate),
                            lte("date",toDate)
                    )
            );
        }
        return this;
    }

    public Bson getFilter() {
        return this.filter;
    }
}
