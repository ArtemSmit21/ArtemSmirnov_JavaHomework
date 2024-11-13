package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Article.ArticleController;
import org.example.Article.ArticleService;
import org.example.Comment.CommentController;
import org.example.Article.InMemoryArticleRepository;
import org.example.Comment.CommentService;
import org.example.Comment.InMemoryCommentRepository;
import org.example.HTML.ArticleFreemarkerController;
import org.example.HTML.TemplateFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spark.Service;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ArticleFreemarkerControllerTest {
  private Service service;

  @BeforeEach
  void beforeEach() {
    service = Service.ignite();

    ObjectMapper objectMapper = new ObjectMapper();

    InMemoryArticleRepository inMemoryArticleRepository = new InMemoryArticleRepository();
    InMemoryCommentRepository inMemoryCommentRepository = new InMemoryCommentRepository();
    ArticleService articleService = new ArticleService(inMemoryArticleRepository, inMemoryCommentRepository);
    CommentService commentService = new CommentService(inMemoryCommentRepository, inMemoryArticleRepository);

    Application application = new Application(
        List.of(
            new ArticleController(
                service,
                articleService,
                commentService,
                objectMapper
            ),
            new CommentController(
                service,
                articleService,
                commentService,
                objectMapper
            ),
            new ArticleFreemarkerController(
                service,
                articleService,
                commentService,
                objectMapper,
                TemplateFactory.freeMarkerEngine()
            )
        )
    );
    application.start();
    service.awaitInitialization();
  }

  @AfterEach
  void afterEach() {
    service.stop();
    service.awaitStop();
  }

  @Test
  @DisplayName("This text check content in html")
  void test1() throws IOException, InterruptedException {

    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            { "title": "test", "tags": ["1", "2"] }"""
                    )
                )
                .uri(URI.create("http://localhost:4567/api/articles"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    HttpResponse<String> html = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertTrue(html.body().contains("Список статей"));
    assertTrue(html.body().contains("Название"));
    assertTrue(html.body().contains("Количество комментариев"));
    assertTrue(html.body().contains("test"));
    assertTrue(html.body().contains("0"));

    HttpResponse<String> response1 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:4567/api/delete-article/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    html = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertFalse(html.body().contains("test"));
  }
}
