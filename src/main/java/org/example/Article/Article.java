package org.example.Article;

import ch.qos.logback.classic.pattern.LineSeparatorConverter;
import org.example.Comment.Comment;

import java.util.List;
import java.util.Set;

public class Article {

  private final ArticleId id;
  private final String title;
  private final Set<String> tags;
  private final List<Comment> comments;

  public Article(ArticleId id, String title, Set<String> tags, List<Comment> comments) {
    this.id = id;
    this.title = title;
    this.tags = tags;
    this.comments = comments;
  }

  public Article withTitle(String title) {
    return new Article(id, title, tags, comments);
  }

  public Article withTags(Set<String> tags) {
    return new Article(id, title, tags, comments);
  }

  public Article withComments(List<Comment> comments) {
    return new Article(id, title, tags, comments);
  }

  public ArticleId getId() {
    return id;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public String getTitle() {
    return title;
  }

  public Set<String> getTags() {
    return tags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Article article = (Article) o;
    return id.equals(article.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    String sb = "ID = " + id.toString() + ", " +
        "Title = " + title + ", " +
        "Tags = " + tags + ", " +
        "Comments = " + comments;
    return sb;
  }
}
