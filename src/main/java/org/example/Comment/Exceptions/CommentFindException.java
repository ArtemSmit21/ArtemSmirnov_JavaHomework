package org.example.Comment.Exceptions;

public class CommentFindException extends RuntimeException {

  public CommentFindException(String message) {

    super(message);
  }

  public CommentFindException(String message, Throwable cause) {

    super(message, cause);
  }
}
