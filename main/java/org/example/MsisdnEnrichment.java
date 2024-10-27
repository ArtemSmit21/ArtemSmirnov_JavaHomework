package org.example;

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
                Map<String, String> updateMap = new HashMap<String, String>();
                updateMap.put("firstName", user.firstName);
                updateMap.put("lastName", user.lastName);
                message.updateContent(updateMap);
                return message;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Пользователь с таким msisdn не зарегестрирован");
            }
        } else {
            throw new IllegalArgumentException("в сообщении не указан msisdn");
        }
    }
}
