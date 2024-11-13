package org.example.Article.Exceptions;

public class ArticleFindException extends Exception {

  public ArticleFindException(String message) {

    super(message);
  }

  public ArticleFindException(String message, Throwable cause) {

    super(message, cause);
  }
}
