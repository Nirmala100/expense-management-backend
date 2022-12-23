package com.nemo.expense.api.model;

public class CreateExpenseOutput {
    private String name;
    private String error;

    public CreateExpenseOutput() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
