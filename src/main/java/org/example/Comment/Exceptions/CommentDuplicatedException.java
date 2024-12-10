package org.example.Comment.Exceptions;

public class CommentDuplicatedException extends Exception {

  public CommentDuplicatedException(String message, Throwable cause) {

    super(message, cause);
  }
}
