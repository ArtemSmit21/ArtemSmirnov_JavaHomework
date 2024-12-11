package org.example.Comment;

import org.example.Comment.Exceptions.*;
import org.example.ConnectionDataBaseException;
import org.jdbi.v3.core.ConnectionException;
import java.util.Optional;

public class CommentService {

  private final CommentRepository commentRepository;

  public CommentService(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  public Optional<Long> generateId() throws ConnectionDataBaseException {
    try {
      return commentRepository.generateId();
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
  }

  public Long create(Comment comment) throws ConnectionDataBaseException, CommentCreateException {
    try {
      return commentRepository.create(comment);
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    } catch (CommentDuplicatedException e) {
      throw new CommentCreateException("Cannot create comment with id = " + comment.getId(), e);
    }
  }

  public Comment findById(Long id) throws CommentFindException, ConnectionDataBaseException {
    try {
      return commentRepository.findById(id);
    } catch (CommentNotFoundException e) {
      throw new CommentFindException("Cannot find comment with id = " + id, e);
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
  }

  public Long update(Comment comment) throws CommentUpdateException, ConnectionDataBaseException {
    try {
      return commentRepository.update(comment);
    } catch (CommentNotFoundException e) {
      throw new CommentUpdateException("Cannot update comment with id = " + comment.getId(), e);
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
  }

  public Long delete(Long id) throws ConnectionDataBaseException, CommentDeleteException {
    try {
      return commentRepository.delete(id);
    } catch (CommentNotFoundException e) {
      throw new CommentDeleteException("Cannot delete comment with id = " + id, e);
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
  }
}
