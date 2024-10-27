package org.example;

/**
 * Класс с юзерами (потокобезопасный)
 */
public class User {
    public String firstName;
    public String lastName;

    public User(String firstName, String lastName) {
        synchronized (this) {
            if (firstName == null || lastName == null) {
                throw new IllegalArgumentException("Нельзя передавать null в качестве имени или фамилии!");
            }
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}
