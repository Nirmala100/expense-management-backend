package com.nemo.expense.api.model;

public class UserOutput {
    private String email;
    private String error;
    public UserOutput() {

    }

    public void setError(String error) {
        this.error = error;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getError() {
        return error;
    }
}
