package org.example;

/**
 * Интерфейс - родитель для всех
 * "баз данных" с данными о юзерах
 */
public interface UserRepository {

    User findByMsisdn(String msisdn);

    void updateUserByMsisdn(String msisdn, User user);
}
