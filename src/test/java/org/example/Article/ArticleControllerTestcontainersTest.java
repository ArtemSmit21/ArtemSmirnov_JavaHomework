package org.example.Article;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.example.Application;
import org.example.Comment.CommentController;
import org.example.Comment.CommentRepositoryImpl;
import org.example.Comment.CommentService;
import org.example.HTML.ArticleFreemarkerController;
import org.example.HTML.TemplateFactory;
import org.example.JdbiTransactionManager;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import spark.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class ArticleControllerTestcontainersTest {

  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

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
                .DELETE()
                .uri(URI.create("http://localhost:4567/api/article/1"))
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
                .PUT(
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

    assertEquals(404, response4.statusCode());
  }
}