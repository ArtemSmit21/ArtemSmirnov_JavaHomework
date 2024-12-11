package org.example.Comment;

import org.example.Comment.Exceptions.CommentDuplicatedException;
import org.example.Comment.Exceptions.CommentNotFoundException;

import java.util.Optional;

public interface CommentRepository {

  Optional<Long> generateId();

  Long create(Comment comment) throws CommentDuplicatedException;

  Long delete(Long id) throws CommentNotFoundException;

  Comment findById(Long id) throws CommentNotFoundException;

  Long update(Comment comment) throws CommentNotFoundException;
}
