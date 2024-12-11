package org.example;

import org.example.enrichment.EnrichmentService;
import org.example.enrichment.EnrichmentType;
import org.example.enrichment.MsisdnEnrichment;
import org.example.exceptions.EnrichmentTypeNonCreateException;
import org.example.message.Message;
import org.example.user.User;
import org.example.user.UserDataBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnrichmentServiceTest {

  private static UserDataBase userDataBase;
  private static User user;
  private static EnrichmentService enrichmentService;
  private static Message message;

  @BeforeAll
  static void beforeAll() {
    userDataBase = new UserDataBase();
    user = new User("Artem", "Smirnov");
    userDataBase.updateUserByMsisdn("999", user);

    Map<String, String> input = new HashMap<>();
    input.put("action", "button_click");
    input.put("page", "book_card");
    input.put("msisdn", "999");

    message = new Message(input);

    enrichmentService = new EnrichmentService(List.of(
        new MsisdnEnrichment()
    ));
  }

  @Test
  @DisplayName("This test check enrichment service correct work")
  void test1() {
    Map<String, String> input = message.getContent();

    input.put("firstName", "Artem");
    input.put("lastName", "Smirnov");

    message = enrichmentService.enrich(message, EnrichmentType.MSISDN);

    assertEquals(message.getContent(), input);
  }

  @Test
  @DisplayName("This test check exception")
  void test2() {
    assertThrows(EnrichmentTypeNonCreateException.class, () -> enrichmentService.enrich(message, EnrichmentType.EMAIL));
  }
}
