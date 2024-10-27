package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        UserDataBase db = new UserDataBase();
        User user = new User("Artem", "Smirnov");
        db.updateUserByMsisdn("999", user);

        Map<String, String> input = new HashMap<>();
        input.put("action", "button_click");
        input.put("page", "book_card");
        input.put("msisdn", "999");

        Message message = new Message(input);

        var enrichmentService = new EnrichmentService(List.of(
                new MsisdnEnrichment()
        ));

        message = enrichmentService.enrich(message, EnrichmentType.MSISDN);

        for (Map.Entry<String, String> entry : message.getContent().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}