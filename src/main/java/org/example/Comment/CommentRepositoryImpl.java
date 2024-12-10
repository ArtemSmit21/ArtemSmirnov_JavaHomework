package org.example.Comment;

import org.example.Comment.Exceptions.CommentDuplicatedException;
import org.example.Comment.Exceptions.CommentNotFoundException;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.core.statement.Update;
import java.util.Optional;
import static org.example.SqlQuery.FINDCOMMENT;
import static org.example.SqlQuery.DELETECOMMENT;
import static org.example.SqlQuery.UPDATECOMENT;
import static org.example.SqlQuery.CREATECOMMENT;
import static org.example.SqlQuery.GENERATECOMMENTID;

public class CommentRepositoryImpl implements CommentRepository {

  private final Jdbi jdbi;

  public CommentRepositoryImpl(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Optional<Long> generateId() {
    Optional<Long> value;

    try (Handle handle = jdbi.open()) {
      value = Optional.ofNullable((Long) handle.createQuery(GENERATECOMMENTID.getString())
          .mapToMap()
          .first()
          .get("value"));
    }

    return value;
  }

  @Override
  public Long create(Comment comment) throws CommentDuplicatedException {
    Long comment_id;

    try (Handle handle = jdbi.open()) {
      try (Update update = handle.createUpdate(CREATECOMMENT.getString())) {
        comment_id = (Long) update.bind("comment_id", comment.getId())
            .bind("text", comment.getText())
            .executeAndReturnGeneratedKeys("comment_id")
            .mapToMap()
            .first()
            .get("comment_id");
      } catch (UnableToExecuteStatementException e) {
        throw new CommentDuplicatedException(e.getMessage(), e);
      }
    }

    return comment_id;
  }

  @Override
  public Long delete(Long id) throws CommentNotFoundException {
    try (Handle handle = jdbi.open()) {
      try (Update update = handle.createUpdate(DELETECOMMENT.getString())) {
        update.bind("comment_id", id)
            .execute();
      } catch (UnableToExecuteStatementException e) {
        throw new CommentNotFoundException(e.getMessage(), e);
      }
    }
    return id;
  }

  @Override
  public Comment findById(Long id) throws CommentNotFoundException {
    Comment comment;

    try (Handle handle = jdbi.open()) {
      ResultIterable<Comment> result = handle.createQuery(FINDCOMMENT.getString())
          .bind("comment_id", id)
          .map((rs, ctx) -> new Comment(rs.getLong("comment_id"), rs.getString("text")));

      try {
        comment = result.first();
      } catch (IllegalStateException e) {
        throw new CommentNotFoundException(e.getMessage(), e);
      }
    }

    return comment;
  }

  @Override
  public Long update(Comment comment) throws CommentNotFoundException {
    Long comment_id;

    try (Handle handle = jdbi.open()) {
      try (Update update = handle.createUpdate(UPDATECOMENT.getString())) {
        comment_id = (Long) update.bind("comment_id", comment.getId())
            .bind("text", comment.getText())
            .executeAndReturnGeneratedKeys("comment_id")
            .mapToMap()
            .first()
            .get("comment_id");
      } catch (IllegalStateException e) {
        throw new CommentNotFoundException(e.getMessage(), e);
      }
    }

    return comment_id;
  }
}
