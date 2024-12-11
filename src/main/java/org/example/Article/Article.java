package org.example.Article;

import org.example.Comment.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Article {

  private final Long id;
  private final String title;
  private final Set<String> tags;
  private final List<Comment> comments;
  private final boolean trending;

  public Article(Long id, String title, Set<String> tags, List<Comment> comments, boolean trending) {
    this.id = id;
    this.title = title;
    this.tags = tags;
    this.comments = comments;
    this.trending = trending;
  }

  public Article withTitle(String title) {
    return new Article(id, title, tags, comments, trending);
  }

  public Article withTags(Set<String> tags) {
    return new Article(id, title, tags, comments, trending);
  }

  public Article withComments(List<Comment> comments) {
    return new Article(id, title, tags, comments, trending);
  }

  public Article withTrending(boolean trending) {
    return new Article(id, title, tags, comments, trending);
  }

  public Long getId() {
    return id;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public List<Long> getCommentsId() {
    List<Long> ids = new ArrayList<>();
    for (Comment comment : comments) {
      ids.add(comment.getId());
    }
    return ids;
  }

  public String getTitle() {
    return title;
  }

  public Set<String> getTags() {
    return tags;
  }

  public boolean getTrending() {
    return trending;
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
    return "ID = " + id.toString() + ", " +
        "Title = " + title + ", " +
        "Tags = " + tags + ", " +
        "Comments = " + comments + ", " +
        "Trending = " + trending;
  }
}
