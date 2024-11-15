package org.example.Comment.Exceptions;

public class CommentUpdateException extends Exception {

  public CommentUpdateException(String message) {

    super(message);
  }

  public CommentUpdateException(String message, Throwable cause) {

    super(message, cause);
  }
}
