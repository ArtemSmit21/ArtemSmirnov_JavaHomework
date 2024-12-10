package org.example.Comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Article.ArticleService;
import org.example.Article.Exceptions.ArticleFindException;
import org.example.Article.Exceptions.ArticleUpdateException;
import org.example.Comment.Exceptions.CommentCreateException;
import org.example.Comment.Exceptions.CommentFindException;
import org.example.Comment.Exceptions.CommentMismatchException;
import org.example.Comment.Request.CommentCreateRequest;
import org.example.Comment.Response.CommentAppendResponse;
import org.example.Comment.Response.CommentDeleteResponse;
import org.example.ConnectionDataBaseException;
import org.example.Controller;
import org.example.ErrorResponse;
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
        "api/articles/:articleId/comments",
        (Request request, Response response) -> {
          response.type("application/json");
          String body = request.body();

          CommentCreateRequest commentCreateRequest;

          try {
            commentCreateRequest = objectMapper.readValue(body, CommentCreateRequest.class);
          } catch (JsonProcessingException e) {
            LOG.warn(e.getMessage());
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          Long articleId;
          Long commentId;

          try {
            articleId = Long.parseLong(request.params(":articleId"));
          } catch (NumberFormatException e) {
            LOG.warn(e.getMessage());
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          try {
            commentId = commentService.generateId().get();
          } catch (ConnectionDataBaseException e) {
            LOG.error(e.getMessage());
            response.status(500);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          try {
            articleService.appendCommentToArticle(articleId, new Comment(commentId, commentCreateRequest.text()));
          } catch (ConnectionDataBaseException e) {
            LOG.error(e.getMessage());
            response.status(500);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          } catch (ArticleFindException | CommentCreateException | CommentMismatchException | ArticleUpdateException e) {
            LOG.warn(e.getMessage());
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
        "api/articles/:articleId/comments/:commentId",
        (Request request, Response response) -> {
          response.type("application/json");

          Long commentId;
          Long articleId;

          try {
            commentId = Long.parseLong(request.params(":commentId"));
          } catch (NumberFormatException e) {
            LOG.warn(e.getMessage());
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          try {
            articleId = Long.parseLong(request.params(":articleId"));
          } catch (NumberFormatException e) {
            LOG.warn(e.getMessage());
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          try {
            articleService.deleteComment(articleId, commentId);
          } catch (ArticleFindException | CommentFindException | CommentMismatchException | ArticleUpdateException e) {
            LOG.warn("Cannot delete comment", e);
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          } catch (ConnectionDataBaseException e) {
            LOG.error(e.getMessage());
            response.status(500);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          LOG.debug("Comment successfully deleted: {}", commentId);
          response.status(200);
          return objectMapper.writeValueAsString(new CommentDeleteResponse(commentId));
        }
    );
  }
}
