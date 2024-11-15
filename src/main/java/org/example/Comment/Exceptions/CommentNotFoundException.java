package org.example.Comment.Exceptions;

public class CommentNotFoundException extends RuntimeException {

  public CommentNotFoundException(String message) {

    super(message);
  }
}
