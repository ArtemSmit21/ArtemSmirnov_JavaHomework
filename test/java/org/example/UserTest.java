package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void correctWorkUserCreation() {
        User user = new User("John", "Doe");
        assertEquals("John", user.firstName);
        assertEquals("Doe", user.lastName);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfFieldNull() {
        assertThrows(IllegalArgumentException.class, () -> new User(null, "Doe"));
    }
}