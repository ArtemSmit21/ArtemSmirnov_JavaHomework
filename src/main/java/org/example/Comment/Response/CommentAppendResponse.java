package org.example.Comment.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Article.ArticleId;
import org.example.Comment.CommentId;

public record CommentAppendResponse(ArticleId articleId, CommentId commentId) {
  @JsonProperty
  public String getArticleId() {
    return articleId.toString();
  }

  @JsonProperty
  public String getCommentId() {
    return commentId.toString();
  }
}
