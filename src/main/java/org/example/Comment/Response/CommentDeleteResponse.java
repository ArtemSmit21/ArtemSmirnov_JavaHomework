package org.example.Comment.Response;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Comment.CommentId;

public record CommentDeleteResponse(CommentId commentId) {
  @JsonProperty
  public String getCommentId() {
    return commentId.toString();
  }
}
