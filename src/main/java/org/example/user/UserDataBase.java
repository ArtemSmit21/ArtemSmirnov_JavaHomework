package org.example.user;

import org.example.exceptions.MsisdnNotFoundException;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация потокобезопасной "базы данных"
 * с юзерами
 */
public class UserDataBase implements UserRepository {

  private static final ConcurrentHashMap<String, User> userDataBase = new ConcurrentHashMap<>();

  @Override
  public User findByMsisdn(String msisdn) throws MsisdnNotFoundException {
    if (userDataBase.containsKey(msisdn)) {
      return userDataBase.get(msisdn);
    } else {
      throw new MsisdnNotFoundException("Msisdn " + msisdn + " not found");
    }
  }

  @Override
  public void updateUserByMsisdn(String msisdn, User user) {
    if (userDataBase.containsKey(msisdn)) {
      userDataBase.replace(msisdn, user);
    } else {
      userDataBase.put(msisdn, user);
    }
  }
}
