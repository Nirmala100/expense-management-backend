package com.nemo.expense.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserModel {
    @BsonId
    private String id;
    private String name;
    private String email;
    private String passHashed;
}
