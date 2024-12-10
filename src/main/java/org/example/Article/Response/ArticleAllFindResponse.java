package org.example.Article.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Comment.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record ArticleAllFindResponse(String title, Set<String> tags, List<Comment> comments) {
  @JsonProperty
  public String getTitle() {
    return title;
  }

  @JsonProperty
  public Set<String> getTags() {
    return tags;
  }

  @JsonProperty
  public List<String> getComments() {
    List<String> stringComments = new ArrayList<>();
    if (comments != null) {
      for (Comment comment : comments) {
        stringComments.add(comment.toString());
      }
    }
    return stringComments;
  }
}
