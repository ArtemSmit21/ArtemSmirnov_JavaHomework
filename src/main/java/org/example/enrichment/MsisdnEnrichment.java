package org.example.enrichment;

import org.example.exceptions.MsisdnNotFoundException;
import org.example.message.Message;
import org.example.user.User;
import org.example.user.UserDataBase;
import java.util.HashMap;
import java.util.Map;

/**
 * Обогащение по MSISDN
 */
public class MsisdnEnrichment implements BaseEnrichments {

  UserDataBase db = new UserDataBase();

  @Override
  public EnrichmentType type() {
    return EnrichmentType.MSISDN;
  }

  @Override
  public Message enrich(Message message) {
    if (message.getContent().containsKey("msisdn")) {
      try {
        User user = db.findByMsisdn(message.getContent().get("msisdn"));
        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("firstName", user.firstName);
        updateMap.put("lastName", user.lastName);
        message.updateContent(updateMap);
        return message;
      } catch (MsisdnNotFoundException e) {
        return message;
      }
    } else {
      return message;
    }
  }
}
