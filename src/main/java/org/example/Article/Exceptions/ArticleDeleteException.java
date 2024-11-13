package org.example.Article.Exceptions;

public class ArticleDeleteException extends Exception {

  public ArticleDeleteException(String message) {

    super(message);
  }

  public ArticleDeleteException(String message, Throwable cause) {

    super(message, cause);
  }
}
