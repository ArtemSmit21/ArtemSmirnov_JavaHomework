package org.example;

import org.example.message.Message;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTest {

  @Test
  void correctCreatingMessage() {
    Map<String, String> input = new HashMap<>();
    input.put("action", "button_click");
    input.put("page", "book_card");
    input.put("msisdn", "111");
    Message message = new Message(input);

    assertEquals(input, message.getContent());
  }
}
