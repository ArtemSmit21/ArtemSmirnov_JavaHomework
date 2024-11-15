package org.example.Comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Article.ArticleId;
import org.example.Article.ArticleService;
import org.example.Comment.Request.CommentCreateRequest;
import org.example.Comment.Response.CommentAppendResponse;
import org.example.Comment.Response.CommentDeleteResponse;
import org.example.Controller;
import org.example.ErrorResponse;
import org.example.Comment.Exceptions.CommentCreateException;
import org.example.Comment.Exceptions.CommentFindException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

public class CommentController implements Controller {

  private static final Logger LOG = LoggerFactory.getLogger(CommentController.class);

  private final Service service;
  private final ArticleService articleService;
  private final CommentService commentService;
  private final ObjectMapper objectMapper;

  public CommentController(Service service, ArticleService articleService, CommentService commentService, ObjectMapper objectMapper) {
    this.service = service;
    this.articleService = articleService;
    this.commentService = commentService;
    this.objectMapper = objectMapper;
  }

  @Override
  public void initializeEndpoints() {
    createComment();
    deleteComment();
  }

  private void createComment() {
    service.post(
        "api/comments",
        (Request request, Response response) -> {
          response.type("application/json");
          String body = request.body();
          CommentCreateRequest commentCreateRequest = objectMapper.readValue(body,
              CommentCreateRequest.class);

          CommentId commentId = null;
          ArticleId articleId = null;

          try {
            commentId = commentService.create(commentCreateRequest.articleId(), commentCreateRequest.text());
          } catch (CommentCreateException e) {
            LOG.warn("Cannot create comment", e);
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          try {
            articleId = commentService.appendToArticle(commentCreateRequest.articleId(),
                commentId);
          } catch (Exception e) {
            LOG.warn("Cannot append to article", e);
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          LOG.debug("Comment successfully appended to article: {}", commentId);
          response.status(200);
          return objectMapper.writeValueAsString(new CommentAppendResponse(articleId, commentId));
        }
    );
  }

  private void deleteComment() {
    service.delete(
        "api/delete-comment/:commentId",
        (Request request, Response response) -> {
          response.type("application/json");
          CommentId commentId = new CommentId(Long.parseLong(request.params("commentId")));
          Comment comment;

          try {
            comment = commentService.findById(commentId);
          } catch (CommentFindException e) {
            LOG.warn("Cannot find comment", e);
            response.status(404);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          ArticleId articleId = comment.getArticleId();

          try {
            articleService.deleteComment(articleId, commentId);
          } catch (Exception e) {
            LOG.warn("Cannot delete comment", e);
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          LOG.debug("Comment successfully deleted: {}", commentId);
          response.status(200);
          return objectMapper.writeValueAsString(new CommentDeleteResponse(commentId));
        }
    );
  }
}
