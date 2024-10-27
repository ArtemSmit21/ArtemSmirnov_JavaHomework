package org.example;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

    @Test
    void shouldReturnEnrichedMessage() {
        Map<String, String> input = new HashMap<>();
        input.put("action", "button_click");
        input.put("page", "book_card");
        input.put("msisdn", "88005553535");

        Message message = new Message(input);

        User user = new User("Vasya", "Ivanov");

        UserDataBase userDataBase = new UserDataBase();
        userDataBase.updateUserByMsisdn("88005553535", user);

        var enrichmentService = new EnrichmentService(List.of(
                new MsisdnEnrichment()
        ));

        Message modifiedMessage = enrichmentService.enrich(message, EnrichmentType.MSISDN);

        input.put("firstName", "Vasya");
        input.put("lastName", "Ivanov");

        assertEquals(input, modifiedMessage.getContent());
    }


    @Test
    void shouldSucceedEnrichmentInConcurrentEnvironmentSuccessfully() throws InterruptedException {
        List<Map<String, String>> enrichmentResults = new CopyOnWriteArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);

        List<String> probableActions = new ArrayList<>(List.of(
                "button_click", "not_button_click"
        ));

        List<String> probablePages = new ArrayList<>(List.of(
                "book_card", "main_page", "payment_page", "unusual_page",
                "beautiful_page", "scary_page", "mobile_application_page"
        ));

        List<String> names = new ArrayList<>(List.of(
                "Artem", "Denis", "Daniil", "Sergey",
                "Vasya", "Petr", "Alexander", "Nicolay"
        ));

        List<String> surnames = new ArrayList<>(List.of(
                "Smirnov", "Ivanov", "Sobolev", "Mikhailov",
                "Popov", "Andropov", "Stalin"
        ));

        Random rd = new Random();

        List<Map<String, String>> correctAns = new ArrayList<>();

        UserDataBase userDataBase = new UserDataBase();

        EnrichmentService enrichmentService = new EnrichmentService(List.of(
                new MsisdnEnrichment()
        ));

        for (int i = 0; i < 5; i++) {
            Map<String, String> input = new HashMap<>();
            input.put("action", probableActions.get(rd.nextInt(probableActions.size())));
            input.put("page", probablePages.get(rd.nextInt(probablePages.size())));

            String msisdn = Integer.toString(rd.nextInt(0, 1000000));
            String name = names.get(rd.nextInt(names.size()));
            String surname = surnames.get(rd.nextInt(surnames.size()));

            input.put("msisdn", msisdn);

            Message message = new Message(input);

            input.put("firstName", name);
            input.put("lastName", surname);

            userDataBase.updateUserByMsisdn(msisdn, new User(name, surname));

            executorService.submit(() -> {
                enrichmentResults.add(
                        enrichmentService.enrich(message, EnrichmentType.MSISDN).getContent()
                );
                latch.countDown();
            });
            correctAns.add(new Message(input).getContent());
        }
        latch.await();

        assertEquals(new HashSet<>(correctAns), new HashSet<>(enrichmentResults));
    }
}