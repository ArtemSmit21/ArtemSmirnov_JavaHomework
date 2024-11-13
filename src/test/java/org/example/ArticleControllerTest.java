package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Article.ArticleController;
import org.example.Article.ArticleService;
import org.example.Comment.CommentController;
import org.example.Article.InMemoryArticleRepository;
import org.example.Comment.CommentService;
import org.example.Comment.InMemoryCommentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spark.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArticleControllerTest {

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
  @DisplayName("This test check 404 status when we not found article (creating)")
  void test1() throws IOException, InterruptedException {
    HttpResponse<String> response4 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/api/articles/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(404, response4.statusCode());
  }

  @Test
  @DisplayName("This test check 404 status when we not found article (deleting)")
  void test2() throws IOException, InterruptedException {
    HttpResponse<String> response4 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/api/delete-article/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(404, response4.statusCode());
  }

  @Test
  @DisplayName("This test check 404 status when we not found article (updating)")
  void test3() throws IOException, InterruptedException {
    HttpResponse<String> response4 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/api/update-article/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(404, response4.statusCode());
  }
}
