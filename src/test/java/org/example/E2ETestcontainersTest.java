package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.example.Article.*;
import org.example.Comment.Comment;
import org.example.Comment.CommentController;
import org.example.Comment.CommentRepositoryImpl;
import org.example.Comment.CommentService;
import org.example.HTML.ArticleFreemarkerController;
import org.example.HTML.TemplateFactory;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import spark.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class E2ETestcontainersTest {

  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:latest");

  private static Jdbi jdbi;
  private static Service service;

  @BeforeAll
  static void beforeAll() {
    String postgresJdbcUrl = POSTGRES.getJdbcUrl();
    Flyway flyway =
        Flyway.configure()
            .outOfOrder(true)
            .locations("classpath:db/migrations")
            .dataSource(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword())
            .load();
    flyway.migrate();
    jdbi = Jdbi.create(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword());
    service = Service.ignite();
  }

  @BeforeEach
  void beforeEach() {
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM article").execute());
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM comment").execute());
  }

  @AfterEach
  void afterEach() {
    service.stop();
    service.awaitStop();
  }

  @Test
  void E2ETest() throws IOException, InterruptedException {
    ObjectMapper objectMapper = new ObjectMapper();

    ArticleService articleService = new RetryableArticleServiceImpl(
        new ArticleServiceImpl(
            new ArticleRepositoryImpl(jdbi),
            new CommentRepositoryImpl(jdbi),
            new JdbiTransactionManager(jdbi)
        ),
        Retry.of(
            "retry-db",
            RetryConfig.custom()
                .maxAttempts(3)
                .retryExceptions(UnableToExecuteStatementException.class)
                .build()
        )
    );

    CommentService commentService = new CommentService(new CommentRepositoryImpl(jdbi));

    Application application = new Application(
        List.of(
            new ArticleController(service, articleService, commentService, objectMapper),
            new CommentController(service, articleService, commentService, objectMapper),
            new ArticleFreemarkerController(service, articleService, commentService, objectMapper, TemplateFactory.freeMarkerEngine())
        )
    );
    application.start();
    service.awaitInitialization();

    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            { "title": "test", "tags": ["1", "2"] }
                            """
                    )
                )
                .uri(URI.create("http://localhost:4567/api/article"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, response.statusCode());

    HttpResponse<String> response1 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            { "text": "Test Text" }
                            """
                    )
                )
                .uri(URI.create("http://localhost:4567/api/articles/1/comments"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, response1.statusCode());

    HttpResponse<String> response2 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .PUT(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            { "title": "testModified", "tags": ["modified"] }
                            """
                    )
                )
                .uri(URI.create("http://localhost:4567/api/article/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, response2.statusCode());

    HttpResponse<String> response3 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:4567/api/articles/1/comments/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, response3.statusCode());

    HttpResponse<String> response4 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/api/article/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, response4.statusCode());

    Article article = articleService.findById(1L);

    String correctTitle = "testModified";

    List<String> listCorrectTags = new ArrayList<>();
    listCorrectTags.add("modified");

    Set<String> correctTags = new HashSet<>(listCorrectTags);

    List<Comment> correctComments = new ArrayList<>(0);

    assertEquals(article.getTitle(), correctTitle);
    assertEquals(article.getTags(), correctTags);
    assertEquals(article.getComments(), correctComments);
  }
}

