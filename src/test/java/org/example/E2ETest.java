package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Article.*;
import org.example.Comment.Comment;
import org.example.Comment.CommentController;
import org.example.Comment.CommentService;
import org.example.Comment.InMemoryCommentRepository;
import org.example.Article.Exceptions.ArticleFindException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.*;

class E2ETest {

  private Service service;

  @BeforeEach
  void beforeEach() {
    service = Service.ignite();
  }

  @AfterEach
  void afterEach() {
    service.stop();
    service.awaitStop();
  }

  @Test
  void E2ETest() throws IOException, InterruptedException, ArticleFindException {
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

    assertEquals(200, response.statusCode());

    HttpResponse<String> response1 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            { "text": "Test Text", "ArticleId": "1" }"""
                    )
                )
                .uri(URI.create("http://localhost:4567/api/comments"))
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
                            { "title": "testModified", "tags": ["modified"] }"""
                    )
                )
                .uri(URI.create("http://localhost:4567/api/update-article/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, response2.statusCode());

    HttpResponse<String> response3 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:4567/api/delete-comment/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, response3.statusCode());

    HttpResponse<String> response4 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/api/articles/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, response4.statusCode());

    Article article = articleService.findById(new ArticleId(1));

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