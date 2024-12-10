package org.example.Comment.Exceptions;

public class CommentDeleteException extends Exception {

  public CommentDeleteException(String message) {

    super(message);
  }

  public CommentDeleteException(String message, Throwable cause) {

    super(message, cause);
  }
}
