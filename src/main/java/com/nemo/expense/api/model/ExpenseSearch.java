package com.nemo.expense.api.model;

public class ExpenseSearch {
    private Long fromDate;
    private Long toDate;
    public ExpenseSearch(){

    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "ExpenseSearch{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }
}
