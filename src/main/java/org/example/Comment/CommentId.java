package org.example.Comment;

import java.util.Objects;

public class CommentId {

  private final long value;

  public CommentId(final long value) {
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
    CommentId commentId = (CommentId) o;
    return value == commentId.value;
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
