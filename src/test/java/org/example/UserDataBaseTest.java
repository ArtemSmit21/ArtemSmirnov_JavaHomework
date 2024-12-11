package org.example;

import org.example.exceptions.MsisdnNotFoundException;
import org.example.user.User;
import org.example.user.UserDataBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserDataBaseTest {

  private static UserDataBase userDataBase;
  private static User user;

  @BeforeAll
  static void beforeAll() {
    userDataBase = new UserDataBase();
    user = new User("Artem", "Smirnov");
    userDataBase.updateUserByMsisdn("1111", user);
  }

  @Test
  @DisplayName("This test check user data base correct creating 1 person process")
  void test1() throws MsisdnNotFoundException {
    assertEquals(user.firstName, userDataBase.findByMsisdn("1111").firstName);
    assertEquals(user.lastName, userDataBase.findByMsisdn("1111").lastName);
  }

  @Test
  @DisplayName("This test check user data base correct creating 2 person with same id process")
  void test2() throws MsisdnNotFoundException {
    User newUser = new User("John", "Doe");

    userDataBase.updateUserByMsisdn("1111", newUser);

    assertEquals(newUser.firstName, userDataBase.findByMsisdn("1111").firstName);
    assertEquals(newUser.lastName, userDataBase.findByMsisdn("1111").lastName);
  }

  @Test
  @DisplayName("This test check msisdn not found exception")
  void test3() {
    assertThrows(MsisdnNotFoundException.class, () -> userDataBase.findByMsisdn("111"));
  }
}
