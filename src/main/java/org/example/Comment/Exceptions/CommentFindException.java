package org.example.Comment.Exceptions;

public class CommentFindException extends Exception {

  public CommentFindException(String message) {

    super(message);
  }

  public CommentFindException(String message, Throwable cause) {

    super(message, cause);
  }
}
