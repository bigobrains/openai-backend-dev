package com.bigobrains.ai.messaging.cases.management.users;

import com.bigobrains.ai.utils.RandomId;

import java.util.List;

public interface UserRepository {

    List<User> USERS = List.of(
            new User(RandomId.nextId(), "Arvind Badri", "barvind88@gmail.com", User.Role.ADMIN, "Lab Director", "United Diagnostics", "53186"),
            new User(RandomId.nextId(), "Joan Yates", "joan.yates@paypros.com", User.Role.USER, "Clerk", "Titan Staffing Agency", "66141"),
            new User(RandomId.nextId(), "Fabio Pozzo", "fabio.pazzo1@gmail.com", User.Role.ADMIN, "Clerk", "Titan Staffing Agency", "66141")
    );

    default User findByEmailId(String emailId) {
        return USERS.stream().filter(o -> o.getEmailId().equals(emailId.trim())).findAny().orElse(null);
    }

    default List<User> findAll() {
        return USERS;
    }
}
