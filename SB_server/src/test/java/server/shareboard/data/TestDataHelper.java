package server.shareboard.data;

import server.shareboard.models.User;

import java.time.LocalDateTime;

public class TestDataHelper {

    public static User existingUser() {
        return new User(
                1,
                "existingUser",
                "hashed_password_1",
                false,
                LocalDateTime.now()
        );
    }

    public static User userToCreate() {
        return new User(
                0,
                "newUser",
                "hashed_password_2",
                false,
                LocalDateTime.now()
        );
    }

    public static User userAfterCreate() {
        User user = userToCreate();
        user.setUserId(3);
        return user;
    }
}