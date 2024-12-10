package org.example.Comment;

public class Comment {

  private final Long commentId;
  private final String text;

  public Comment(Long commentId, String text) {
    this.commentId = commentId;
    this.text = text;
  }

  public Comment withArticleId(Long articleId) {
    return new Comment(commentId, text);
  }

  public Comment withText(String text) {
    return new Comment(commentId, text);
  }

  public Long getId() {
    return commentId;
  }

  public String getText() {
    return text;
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
