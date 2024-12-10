package org.example.Article;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.example.Article.Exceptions.ArticleFindException;
import org.example.Article.Exceptions.ArticleIdDuplicatedException;
import org.example.Article.Exceptions.ArticleNotFoundException;
import org.example.Comment.Comment;
import org.example.Comment.CommentRepository;
import org.example.Comment.CommentRepositoryImpl;
import org.example.Comment.Exceptions.CommentDuplicatedException;
import org.example.Comment.Exceptions.CommentNotFoundException;
import org.example.JdbiTransactionManager;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
class ArticleServiceImplTestcontainersTest {

  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

  private static Jdbi jdbi;

  private static ArticleService articleService;

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

    articleService = new RetryableArticleServiceImpl(
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
  }

  @BeforeEach
  void beforeEach() {
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM article").execute());
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM comment").execute());
  }

  @Test
  @DisplayName("This test check delete article with comments process")
  void test1() throws CommentDuplicatedException, ArticleIdDuplicatedException, ArticleNotFoundException {
    CommentRepository commentRepository = new CommentRepositoryImpl(jdbi);

    Long articleId = articleService.generateId().get();

    Long firstId = commentRepository.generateId().get();
    Long secondId = commentRepository.generateId().get();

    Comment firstComment = new Comment(firstId, "first comment");
    Comment secondComment = new Comment(secondId, "second comment");

    commentRepository.create(firstComment);
    commentRepository.create(secondComment);

    List<Comment> commentsList = new ArrayList<>();
    commentsList.add(firstComment);
    commentsList.add(secondComment);


    Set<String> testTags = new HashSet<>();
    testTags.add("test_tag1");
    testTags.add("test_tag2");

    Article article = new Article(articleId, "testArticle", testTags, commentsList, false);

    articleService.create(article);

    articleService.delete(articleId);

    assertThrows(ArticleFindException.class, () -> articleService.findById(articleId));
    assertThrows(CommentNotFoundException.class, () -> commentRepository.findById(firstId));
    assertThrows(CommentNotFoundException.class, () -> commentRepository.findById(secondId));
  }

  @Test
  @DisplayName("This test check creating many articles process")
  void test2() {
    Long firstArticleId = articleService.generateId().get();
    Long secondArticleId = articleService.generateId().get();

    Set<String> firstArticleTags = new HashSet<>();
    firstArticleTags.add("first_test_tag1");
    firstArticleTags.add("first_test_tag2");

    Set<String> secondArticleTags = new HashSet<>();
    secondArticleTags.add("second_test_tag1");
    secondArticleTags.add("second_test_tag2");

    Article firstArticle = new Article(firstArticleId, "firstArticle", firstArticleTags, null, false);
    Article secondArticle = new Article(secondArticleId, "secondArticle", secondArticleTags, null, false);

    List<Article> articlesList = new ArrayList<>();
    articlesList.add(firstArticle);
    articlesList.add(secondArticle);

    articleService.createArticles(articlesList);

    assertEquals(firstArticle, articleService.findById(firstArticleId));
    assertEquals(secondArticle, articleService.findById(secondArticleId));
  }

  @Test
  @DisplayName("This test check appending comment to article(without comments) process")
  void test3() {
    Long articleId = articleService.generateId().get();

    Set<String> tags = new HashSet<>();
    tags.add("test_tag1");
    tags.add("test_tag2");

    Article article = new Article(articleId, "testArticle", tags, null, false);
    articleService.create(article);

    CommentRepository commentRepository = new CommentRepositoryImpl(jdbi);
    Long commentId = commentRepository.generateId().get();
    Comment firstComment = new Comment(commentId, "first comment");

    articleService.appendCommentToArticle(articleId, firstComment);
    Article foundedArticle = articleService.findById(articleId);

    List<Comment> commentsList = foundedArticle.getComments();

    assertEquals(firstComment, commentsList.get(0));
    assert(commentsList.size() == 1);
  }

  @Test
  @DisplayName("This test check appending comment to article(with comments) process")
  void test4() throws CommentDuplicatedException {
    CommentRepository commentRepository = new CommentRepositoryImpl(jdbi);

    Long articleId = articleService.generateId().get();

    Long firstId = commentRepository.generateId().get();
    Long secondId = commentRepository.generateId().get();

    Comment firstComment = new Comment(firstId, "first comment");
    Comment secondComment = new Comment(secondId, "second comment");

    commentRepository.create(firstComment);

    List<Comment> commentsList = new ArrayList<>();
    commentsList.add(firstComment);


    Set<String> testTags = new HashSet<>();
    testTags.add("test_tag1");
    testTags.add("test_tag2");

    Article article = new Article(articleId, "testArticle", testTags, commentsList, false);

    articleService.create(article);
    articleService.appendCommentToArticle(articleId, secondComment);

    List<Comment> foundedComments = articleService.findById(articleId).getComments();

    assertEquals(firstComment, foundedComments.get(0));
    assertEquals(secondComment, foundedComments.get(1));
    assert(foundedComments.size() == 2);
  }

  @Test
  @DisplayName("This test check trending changing process")
  void test5() {
    CommentRepository commentRepository = new CommentRepositoryImpl(jdbi);

    Long articleId = articleService.generateId().get();

    Set<String> testTags = new HashSet<>();
    testTags.add("test_tag1");
    testTags.add("test_tag2");

    Article article = new Article(articleId, "testArticle", testTags, null, false);
    articleService.create(article);

    Comment firstComment = new Comment(commentRepository.generateId().get(), "first comment");
    articleService.appendCommentToArticle(articleId, firstComment);

    Comment secondComment = new Comment(commentRepository.generateId().get(), "second comment");
    articleService.appendCommentToArticle(articleId, secondComment);

    Comment thirdComment = new Comment(commentRepository.generateId().get(), "third comment");
    articleService.appendCommentToArticle(articleId, thirdComment);

    assert(articleService.findById(articleId).getTrending() == true);

    articleService.deleteComment(articleId, firstComment.getId());

    assert(articleService.findById(articleId).getTrending() == false);
  }
}
