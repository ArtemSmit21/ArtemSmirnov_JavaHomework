package org.example.Comment.Exceptions;

public class CommentMismatchException extends RuntimeException {
  public CommentMismatchException(String message) {
    super(message);
  }
}
