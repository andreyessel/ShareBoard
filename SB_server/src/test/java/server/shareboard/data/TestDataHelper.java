package server.shareboard.data;

import server.shareboard.models.*;

import java.time.LocalDate;
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

    public static Listing existingNeedListing() {
        return new Listing(
                1, 1, 1,
                ListingType.NEED,
                10, 10,
                Urgency.HIGH,
                LocalDate.of(2026, 9, 1),
                null,
                Status.OPEN,
                LocalDateTime.now()
        );
    }

    public static Listing listingToCreate() {
        return new Listing(
                0, 1, 1,
                ListingType.NEED,
                5, 5,
                Urgency.MEDIUM,
                LocalDate.of(2026, 9, 15),
                null,
                Status.OPEN,
                LocalDateTime.now()
        );
    }
}