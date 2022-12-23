package com.nemo.expense.api.model;

public class CreateCategoryOutput {
    private String name;
    private String error;
    public CreateCategoryOutput() {

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
