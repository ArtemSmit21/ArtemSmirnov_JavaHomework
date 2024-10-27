package org.example;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EnrichmentServiceTest {

    @Test
    void correctWorkEnrichmentService() {
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

        input.put("firstName", "Artem");
        input.put("lastName", "Smirnov");

        message = enrichmentService.enrich(message, EnrichmentType.MSISDN);

        assertEquals(message.getContent(), input);
    }

    @Test
    void shouldThrowClassCastException() {
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

        assertThrows(ClassCastException.class, () -> enrichmentService.enrich(message, EnrichmentType.EMAIL));
    }
}