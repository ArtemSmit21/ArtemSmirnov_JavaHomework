package org.example.user;

import org.example.exceptions.NullNameException;

/**
 * Класс с юзерами (потокобезопасный)
 */
public class User {
  public String firstName;
  public String lastName;

  public User(String firstName, String lastName) {
    if (firstName == null || lastName == null) {
      throw new NullNameException("Нельзя передавать null в качестве имени или фамилии!");
    }
    this.firstName = firstName;
    this.lastName = lastName;
  }
}
