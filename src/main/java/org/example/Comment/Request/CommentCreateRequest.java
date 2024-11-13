package org.example.Comment.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Article.ArticleId;

public class CommentCreateRequest {

  private final String text;
  private final ArticleId articleId;

  public CommentCreateRequest(
      @JsonProperty("text") String text,
      @JsonProperty("ArticleId") Long articleId
  ) {
    this.text = text;
    this.articleId = new ArticleId(articleId);
  }

  public String text() {
    return text;
  }

  public ArticleId articleId() {
    return articleId;
  }
}
