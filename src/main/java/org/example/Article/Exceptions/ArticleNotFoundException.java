package org.example.Article.Exceptions;

public class ArticleNotFoundException extends Exception {

  public ArticleNotFoundException(String message, Throwable cause) {

    super(message, cause);
  }
}
