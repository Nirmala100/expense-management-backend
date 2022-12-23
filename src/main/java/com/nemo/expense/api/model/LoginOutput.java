package com.nemo.expense.api.model;

public class LoginOutput {
    private  String token;
    private String error;

    public LoginOutput(String token) {
        this.token = token;
    }
    public LoginOutput(){

    }

    public String getToken() {
        return token;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
