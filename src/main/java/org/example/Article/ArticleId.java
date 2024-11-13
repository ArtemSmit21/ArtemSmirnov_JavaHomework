package org.example.Article;

import java.util.Objects;

public class ArticleId {

  private final long value;

  public ArticleId(final long value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArticleId articleId = (ArticleId) o;
    return value == articleId.value;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
