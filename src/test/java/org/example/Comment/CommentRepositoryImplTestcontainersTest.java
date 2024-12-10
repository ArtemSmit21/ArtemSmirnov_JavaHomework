package org.example.Comment;

import org.example.Comment.Exceptions.CommentDuplicatedException;
import org.example.Comment.Exceptions.CommentNotFoundException;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import static org.example.SqlQuery.CREATECOMMENT;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
class CommentRepositoryImplTestcontainersTest {
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
    CommentRepository commentRepository = new CommentRepositoryImpl(jdbi);
    Optional<Long> id = commentRepository.generateId();
    assert(id.isPresent());
  }

  @Test
  @DisplayName("This test check find comment process")
  void test2() throws CommentNotFoundException {
    CommentRepository commentRepository = new CommentRepositoryImpl(jdbi);
    Optional<Long> id = commentRepository.generateId();
    assert(id.isPresent());

    jdbi.useTransaction(handle -> handle.createUpdate(CREATECOMMENT.getString())
        .bind("comment_id", id.get())
        .bind("text", "test")
        .execute());

    Comment comment = commentRepository.findById(id.get());

    assert(comment.getText().equals("test"));
    assert (comment.getId().equals(id.get()));
  }

  @Test
  @DisplayName("This test check not found exception")
  void test3() throws CommentNotFoundException {
    CommentRepository commentRepository = new CommentRepositoryImpl(jdbi);
    Optional<Long> id = commentRepository.generateId();
    assert(id.isPresent());

    jdbi.useTransaction(handle -> handle.createUpdate(CREATECOMMENT.getString())
        .bind("comment_id", id.get())
        .bind("text", "test")
        .execute());

    assertThrows(CommentNotFoundException.class, () -> commentRepository.findById(id.get() + 1));
  }

  @Test
  @DisplayName("This test check create(non-duplicate) process")
  void test4() throws CommentNotFoundException, CommentDuplicatedException {
    CommentRepository commentRepository = new CommentRepositoryImpl(jdbi);
    Optional<Long> id = commentRepository.generateId();
    assert(id.isPresent());

    commentRepository.create(new Comment(id.get(), "test"));

    Comment comment = commentRepository.findById(id.get());

    assert(comment.getText().equals("test"));
    assert (comment.getId().equals(id.get()));
  }

  @Test
  @DisplayName("This test check create(duplicate) process")
  void test5() throws CommentDuplicatedException {
    CommentRepository commentRepository = new CommentRepositoryImpl(jdbi);
    Optional<Long> id = commentRepository.generateId();
    assert(id.isPresent());

    commentRepository.create(new Comment(id.get(), "test"));

    assertThrows(CommentDuplicatedException.class,
        () -> commentRepository.create(new Comment(id.get(), "test1")));
  }

  @Test
  @DisplayName("This test check delete process")
  void test6() throws CommentDuplicatedException, CommentNotFoundException {
    CommentRepository commentRepository = new CommentRepositoryImpl(jdbi);
    Optional<Long> id = commentRepository.generateId();
    assert(id.isPresent());

    commentRepository.create(new Comment(id.get(), "test"));

    commentRepository.delete(id.get());

    assertThrows(CommentNotFoundException.class, () -> commentRepository.findById(id.get()));
  }

  @Test
  @DisplayName("This test check update process")
  void test7() throws CommentDuplicatedException, CommentNotFoundException {
    CommentRepository commentRepository = new CommentRepositoryImpl(jdbi);
    Optional<Long> id = commentRepository.generateId();
    assert(id.isPresent());

    commentRepository.create(new Comment(id.get(), "test"));
    commentRepository.update(new Comment(id.get(), "test2"));

    Comment comment = commentRepository.findById(id.get());
    assert(comment.getText().equals("test2"));
    assert (comment.getId().equals(id.get()));
  }
}