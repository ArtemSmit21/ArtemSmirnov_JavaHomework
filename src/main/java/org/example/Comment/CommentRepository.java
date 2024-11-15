package org.example.Comment;

import org.example.Comment.Exceptions.CommentIdDuplicatedException;
import org.example.Comment.Exceptions.CommentNotFoundException;

import java.util.List;

public interface CommentRepository {

  CommentId generateId();

  List<Comment> findAll();

  /**
   * @throws CommentNotFoundException
   */
  Comment findById(CommentId id);

  /**
   * @throws CommentIdDuplicatedException
   */
  void create(Comment comment);

  /**
   * @throws CommentNotFoundException
   */
  void update(Comment comment);

  /**
   * @throws CommentNotFoundException
   */
  void delete(CommentId commentId);
}
