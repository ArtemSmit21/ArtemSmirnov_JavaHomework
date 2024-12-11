package org.example.user;

import org.example.exceptions.MsisdnNotFoundException;

/**
 * Интерфейс - родитель для всех
 * "баз данных" с данными о юзерах
 */
public interface UserRepository {

  User findByMsisdn(String msisdn) throws MsisdnNotFoundException;

  void updateUserByMsisdn(String msisdn, User user);
}
