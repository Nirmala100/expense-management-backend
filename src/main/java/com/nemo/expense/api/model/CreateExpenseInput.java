package com.nemo.expense.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateExpenseInput {
    private String id;
    private String userId;
    private String categoryName;
    private Long date;
    private String name;
    private Double price;
}
