package com.nemo.expense.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserInput {
    private String id;
    private String name;
    private String email;
    private String password;
}
