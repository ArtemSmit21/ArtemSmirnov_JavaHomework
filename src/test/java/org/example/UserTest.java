package org.example;

import org.example.exceptions.NullNameException;
import org.example.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

  @Test
  @DisplayName("This test check create user process")
  void test1() {
    User user = new User("John", "Doe");
    assertEquals("John", user.firstName);
    assertEquals("Doe", user.lastName);
  }

  @Test
  @DisplayName("This test check exception (null first name or last name)")
  void test2() {
    assertThrows(NullNameException.class, () -> new User(null, "Doe"));
  }
}
