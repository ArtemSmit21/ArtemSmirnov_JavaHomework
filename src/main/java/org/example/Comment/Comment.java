package org.example.Comment;

import org.example.Article.ArticleId;

public class Comment {

  private final CommentId commentId;
  private final ArticleId articleId;
  private final String text;

  public Comment(CommentId commentId, ArticleId articleId, String text) {
    this.commentId = commentId;
    this.articleId = articleId;
    this.text = text;
  }

  public Comment withArticleId(ArticleId articleId) {
    return new Comment(commentId, articleId, text);
  }

  public Comment withText(String text) {
    return new Comment(commentId, articleId, text);
  }

  public CommentId getId() {
    return commentId;
  }

  public ArticleId getArticleId() {
    return articleId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Comment comment = (Comment) o;
    return commentId.equals(comment.commentId);
  }

  @Override
  public int hashCode() {
    return commentId.hashCode();
  }

  @Override
  public String toString() {
    return text;
  }
}
