package org.example.Article.Exceptions;

public class ArticleUpdateException extends RuntimeException {

  public ArticleUpdateException(String message) {

    super(message);
  }

  public ArticleUpdateException(String message, Throwable cause) {

    super(message, cause);
  }
}
