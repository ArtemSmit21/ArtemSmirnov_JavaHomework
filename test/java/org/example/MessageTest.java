package org.example;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void shouldThrowIllegalArgumentExceptionIfFieldNull() {
        Map<String, String> input = new HashMap<>();
        input.put("action", "button_click");
        input.put("page", null);
        input.put("msisdn", "111");


        assertThrows(IllegalArgumentException.class, () -> new Message(input));
    }
}