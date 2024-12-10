package org.example.Article.Exceptions;

public class ArticleCreateException extends RuntimeException {

  public ArticleCreateException(String message, Throwable cause) {

    super(message, cause);
  }
}
