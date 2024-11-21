package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDataBaseTest {

    @Test
    void correctWorkUserDataBaseFirstTest() {
        UserDataBase userDataBase = new UserDataBase();

        User user = new User("Artem", "Smirnov");

        userDataBase.updateUserByMsisdn("1111", user);

        assertEquals(user, userDataBase.findByMsisdn("1111"));
    }

    @Test
    void correctWorkUserDataBaseSecondTest() {
        UserDataBase userDataBase = new UserDataBase();

        User user1 = new User("Artem", "Smirnov");

        userDataBase.updateUserByMsisdn("1111", user1);

        User user2 = new User("John", "Doe");

        userDataBase.updateUserByMsisdn("1111", user2);

        assertEquals(user2, userDataBase.findByMsisdn("1111"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenMsisdnIsNotFind() {
        UserDataBase userDataBase = new UserDataBase();

        User user = new User("Artem", "Smirnov");

        userDataBase.updateUserByMsisdn("1111", user);

        assertThrows(IllegalArgumentException.class, () -> userDataBase.findByMsisdn("111"));
    }
}