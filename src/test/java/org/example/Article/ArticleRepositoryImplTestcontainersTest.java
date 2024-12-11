package org.example.Article;

import org.example.Article.Exceptions.ArticleIdDuplicatedException;
import org.example.Article.Exceptions.ArticleNotFoundException;
import org.example.Comment.Comment;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.example.SqlQuery.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
class ArticleRepositoryImplTestcontainersTest {

  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:latest");

  private static Jdbi jdbi;

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
  }

  @BeforeEach
  void beforeEach() {
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM article").execute());
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM comment").execute());
  }

  @Test
  @DisplayName("This test check generating Id process")
  void test1() {
    ArticleRepository articleRepository = new ArticleRepositoryImpl(jdbi);
    Optional<Long> id = articleRepository.generateId();
    assert(id.isPresent());
  }

  @Test
  @DisplayName("This test check find article without comments process")
  void test2() throws ArticleNotFoundException {
    ArticleRepository articleRepository = new ArticleRepositoryImpl(jdbi);

    Optional<Long> id = articleRepository.generateId();
    assert(id.isPresent());

    Set<String> testTags = new HashSet<>();
    testTags.add("test_tag1");
    testTags.add("test_tag2");

    jdbi.useTransaction(handle -> handle.createUpdate(CREATEARTICLE.getString())
        .bind("article_id", id.get())
        .bind("title", "test_title")
        .bindArray("tags", String.class, testTags)
        .bindArray("comments_id", Long.class, new long[]{})
        .bind("trending", false)
        .execute());

    Article article = articleRepository.findById(id.get());

    assertEquals("test_title", article.getTitle());
    assertEquals(testTags, article.getTags());
    assertEquals(0, article.getComments().size());
    assertEquals(false, article.getTrending());
  }

  @Test
  @DisplayName("This test check find article with comments process")
  void test3() throws ArticleNotFoundException {
    ArticleRepository articleRepository = new ArticleRepositoryImpl(jdbi);

    Optional<Long> id = articleRepository.generateId();
    assert(id.isPresent());

    Set<String> testTags = new HashSet<>();
    testTags.add("test_tag1");
    testTags.add("test_tag2");

    Long[] commentIdsTest = new Long[]{1L, 2L};

    jdbi.useTransaction(handle -> handle.createUpdate(CREATEARTICLE.getString())
        .bind("article_id", id.get())
        .bind("title", "test_title")
        .bindArray("tags", String.class, testTags)
        .bindArray("comments_id", Long.class, commentIdsTest)
        .bind("trending", false)
        .execute());

    jdbi.useTransaction(handle -> handle.createUpdate(CREATECOMMENT.getString())
        .bind("comment_id", commentIdsTest[0])
        .bind("text", "testText1")
        .execute());

    jdbi.useTransaction(handle -> handle.createUpdate(CREATECOMMENT.getString())
        .bind("comment_id", commentIdsTest[1])
        .bind("text", "testText2")
        .execute());

    Article article = articleRepository.findById(id.get());

    assertEquals("test_title", article.getTitle());
    assertEquals(testTags, article.getTags());
    for (int i = 0; i < article.getComments().size(); i++) {
      assertEquals(article.getComments().get(i).getId(), commentIdsTest[i]);
      assertEquals("testText" + (i+1), article.getComments().get(i).getText());
    }
    assertEquals(false, article.getTrending());
  }

  @Test
  @DisplayName("This test check create article without comments process")
  void test4() throws ArticleIdDuplicatedException, ArticleNotFoundException {
    ArticleRepository articleRepository = new ArticleRepositoryImpl(jdbi);

    Optional<Long> id = articleRepository.generateId();
    assert(id.isPresent());

    Set<String> testTags = new HashSet<>();
    testTags.add("test_tag1");
    testTags.add("test_tag2");

    articleRepository.create(new Article(id.get(), "testTitle", testTags, null, false));

    Article article = articleRepository.findById(id.get());

    assertEquals("testTitle", article.getTitle());
    assertEquals(testTags, article.getTags());
    assertEquals(0, article.getComments().size());
    assertEquals(false, article.getTrending());
  }

  @Test
  @DisplayName("This test check create article with comments process")
  void test5() throws ArticleIdDuplicatedException, ArticleNotFoundException {
    ArticleRepository articleRepository = new ArticleRepositoryImpl(jdbi);

    Optional<Long> id = articleRepository.generateId();
    assert(id.isPresent());

    Set<String> testTags = new HashSet<>();
    testTags.add("test_tag1");
    testTags.add("test_tag2");

    jdbi.useTransaction(handle -> handle.createUpdate(CREATECOMMENT.getString())
        .bind("comment_id", 1L)
        .bind("text", "testText1")
        .execute());

    jdbi.useTransaction(handle -> handle.createUpdate(CREATECOMMENT.getString())
        .bind("comment_id", 2L)
        .bind("text", "testText2")
        .execute());

    List<Comment> comments = new ArrayList<>();
    comments.add(new Comment(1L, "testText1"));
    comments.add(new Comment(2L, "testText2"));

    articleRepository.create(new Article(id.get(), "testTitle", testTags, comments, false));

    Article article = articleRepository.findById(id.get());

    assertEquals("testTitle", article.getTitle());
    assertEquals(testTags, article.getTags());
    assertEquals(false, article.getTrending());
    for (int i = 0; i < article.getComments().size(); i++) {
      assertEquals(article.getComments().get(i).getId(), comments.get(i).getId());
      assertEquals("testText" + (i+1), article.getComments().get(i).getText());
    }
  }

  @Test
  @DisplayName("This test check update article process")
  void test6() throws ArticleNotFoundException, ArticleIdDuplicatedException {
    ArticleRepository articleRepository = new ArticleRepositoryImpl(jdbi);

    Optional<Long> id = articleRepository.generateId();
    assert(id.isPresent());

    Set<String> testTags = new HashSet<>();
    testTags.add("test_tag1");
    testTags.add("test_tag2");

    articleRepository.create(new Article(id.get(), "testTitle", testTags, null, false));

    Set<String> newTags = new HashSet<>();
    newTags.add("new_test_tag1");
    newTags.add("new_test_tag2");

    Article newArticle = articleRepository.findById(id.get()).withTags(newTags).withTitle("new_title");

    articleRepository.update(newArticle);

    Article updatedArticle = articleRepository.findById(id.get());

    assertEquals("new_title", updatedArticle.getTitle());
    assertEquals(newTags, updatedArticle.getTags());
    assertEquals(0, updatedArticle.getComments().size());
    assertEquals(false, updatedArticle.getTrending());
  }

  @Test
  @DisplayName("This test check delete article without comments process")
  void test7() throws ArticleNotFoundException, ArticleIdDuplicatedException {
    ArticleRepository articleRepository = new ArticleRepositoryImpl(jdbi);

    Optional<Long> id = articleRepository.generateId();
    assert(id.isPresent());
    Set<String> testTags = new HashSet<>();
    testTags.add("test_tag1");
    testTags.add("test_tag2");

    articleRepository.create(new Article(id.get(), "testTitle", testTags, null, false));

    articleRepository.delete(id.get());

    assertThrows(ArticleNotFoundException.class, () -> articleRepository.findById(id.get()));
  }

  @Test
  @DisplayName("This test check find all articles process")
  void test8() throws ArticleIdDuplicatedException {
    ArticleRepository articleRepository = new ArticleRepositoryImpl(jdbi);

    Optional<Long> id = articleRepository.generateId();
    assert(id.isPresent());
    Set<String> testTags = new HashSet<>();
    testTags.add("test_tag1");
    testTags.add("test_tag2");

    Article article1 = new Article(id.get(), "testTitle", testTags, null, false);
    articleRepository.create(article1);

    Optional<Long> newId = articleRepository.generateId();
    assert(newId.isPresent());
    Set<String> newTestTags = new HashSet<>();
    newTestTags.add("new_test_tag1");
    newTestTags.add("new_test_tag2");

    Article article2 = new Article(newId.get(), "testTitle", newTestTags, null, false);
    articleRepository.create(article2);

    Optional<List<Article>> articles = articleRepository.findAll();
    assert(articles.isPresent());

    List<Article> articleList = articles.get();
    assertEquals(2, articleList.size());
    assertEquals(article1, articleList.get(0));
    assertEquals(article2, articleList.get(1));
  }

  @Test
  @DisplayName("This test check all exceptions")
  void test9() throws ArticleIdDuplicatedException {
    ArticleRepository articleRepository = new ArticleRepositoryImpl(jdbi);
    Optional<Long> id = articleRepository.generateId();
    assert(id.isPresent());

    assertThrows(ArticleNotFoundException.class, () -> articleRepository.findById(id.get()));

    Set<String> testTags = new HashSet<>();
    testTags.add("test_tag1");
    testTags.add("test_tag2");

    Article article = new Article(id.get(), "testTitle", testTags, null, false);

    articleRepository.create(article);
    assertThrows(ArticleIdDuplicatedException.class, () -> articleRepository.create(article));

    assertThrows(ArticleNotFoundException.class,
        () -> articleRepository.update(
            new Article(id.get() + 1, "testTitle", testTags, null, false)));

    assertThrows(ArticleNotFoundException.class, () -> articleRepository.delete(id.get() + 1));
  }
}