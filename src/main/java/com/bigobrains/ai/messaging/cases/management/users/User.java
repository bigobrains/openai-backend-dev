package com.bigobrains.ai.messaging.cases.management.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;
    private String name;
    private String emailId;
    private Role role;
    private String title;
    private String companyName;
    private String zipCode;

    public enum Role {
        ADMIN,
        USER,
        ANONYMOUS_USER
    }
}
