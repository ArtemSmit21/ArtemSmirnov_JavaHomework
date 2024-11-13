package org.example.Article;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.*;
import org.example.Article.Request.ArticleCreateRequest;
import org.example.Article.Request.ArticleUpdateRequest;
import org.example.Article.Response.*;
import org.example.Comment.CommentService;
import org.example.Article.Exceptions.ArticleCreateException;
import org.example.Article.Exceptions.ArticleFindException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleController implements Controller {

  private static final Logger LOG = LoggerFactory.getLogger(ArticleController.class);

  private final Service service;
  private final ArticleService articleService;
  private final CommentService commentService;
  private final ObjectMapper objectMapper;

  public ArticleController(Service service, ArticleService articleService, CommentService commentService, ObjectMapper objectMapper) {
    this.service = service;
    this.articleService = articleService;
    this.commentService = commentService;
    this.objectMapper = objectMapper;
  }

  @Override
  public void initializeEndpoints() {
    createArticle();
    getArticle();
    getAllArticles();
    deleteArticle();
    updateArticle();
  }

  private void createArticle() {
    service.post(
        "/api/articles",
        (Request request, Response response) -> {
          response.type("application/json");
          String body = request.body();
          ArticleCreateRequest articleCreateRequest = objectMapper.readValue(body,
              ArticleCreateRequest.class);
          try {
            ArticleId articleId = articleService.create(articleCreateRequest.title(),
                articleCreateRequest.tags());
            LOG.debug("Article created: {}", articleId);
            response.status(200);
            return objectMapper.writeValueAsString(new ArticleCreateResponse(articleId));
          } catch (ArticleCreateException e) {
            LOG.warn("Cannot create article", e);
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }
        }
    );
  }

  private void getArticle() {
    service.get(
        "/api/articles/:articleId",
        (Request request, Response response) -> {
          response.type("application/json");
          ArticleId articleId = new ArticleId(Long.parseLong(request.params("articleId")));
          Article article;

          try {
            article = articleService.findById(articleId);
          } catch (ArticleFindException e) {
            LOG.warn("Article not found", e);
            response.status(404);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          LOG.debug("Article found: {}", articleId);
          response.status(200);
          return objectMapper.writeValueAsString(new ArticleFindResponse(
              articleId.toString(),
              article.getTitle(),
              article.getTags(),
              article.getComments()
          ));
        }
    );
  }

  public void getAllArticles() {
    service.get(
        "/api/all-articles",
        (Request request, Response response) -> {
          response.type("application/json");
          List<Article> articles = articleService.findAll();
          Map<String, ArticleAllFindResponse> articlesMap = new HashMap<>();

          for (Article article : articles) {
            articlesMap.put(article.getId().toString(), new ArticleAllFindResponse(
                article.getTitle(),
                article.getTags(),
                article.getComments()
            ));
          }

          LOG.debug("Successfully retrieved all articles");
          response.status(200);
          return objectMapper.writeValueAsString(articlesMap);
        }
    );
  }

  public void deleteArticle() {
    service.delete(
        "/api/delete-article/:articleId",
        (Request request, Response response) -> {
          response.type("application/json");
          ArticleId articleId = new ArticleId(Long.parseLong(request.params("articleId")));
          Article article;

          try {
            article = articleService.findById(articleId);
          } catch (ArticleFindException e) {
            LOG.warn("Article not found", e);
            response.status(404);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          articleService.delete(articleId);
          LOG.debug("Article deleted: {}", articleId);
          response.status(200);
          return objectMapper.writeValueAsString(new ArticleDeleteResponse(articleId));
        }
    );
  }

  public void updateArticle() {
    service.put(
        "api/update-article/:articleId",
        (Request request, Response response) -> {
          response.type("application/json");
          String body = request.body();
          ArticleUpdateRequest articleUpdateRequest = objectMapper.readValue(body, ArticleUpdateRequest.class);

          ArticleId articleId = new ArticleId(Long.parseLong(request.params("articleId")));
          Article article;

          try {
            article = articleService.findById(articleId);
          } catch (ArticleFindException e) {
            LOG.warn("Article not found", e);
            response.status(404);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          articleService.update(articleId, articleUpdateRequest.title(), articleUpdateRequest.tags());
          LOG.debug("Article updated: {}", articleId);
          response.status(200);
          return objectMapper.writeValueAsString(new ArticleUpdateResponse(articleId));
        }
    );
  }
}
