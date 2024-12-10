package org.example.Article;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ConnectionDataBaseException;
import org.example.Article.Request.ArticleCreateRequest;
import org.example.Article.Request.ArticleUpdateRequest;
import org.example.Article.Response.ArticleUpdateResponse;
import org.example.Article.Response.ArticlesCreateResponse;
import org.example.Article.Response.ArticleAllFindResponse;
import org.example.Article.Response.ArticleCreateResponse;
import org.example.Article.Response.ArticleDeleteResponse;
import org.example.Article.Response.ArticleFindResponse;
import org.example.Comment.CommentService;
import org.example.Article.Exceptions.ArticleCreateException;
import org.example.Article.Exceptions.ArticleFindException;
import org.example.Controller;
import org.example.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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
    createArticles();
  }

  private void createArticle() {
    service.post(
        "/api/article",
        (Request request, Response response) -> {
          response.type("application/json");

          String body = request.body();

          ArticleCreateRequest articleCreateRequest;

          try {
            articleCreateRequest = objectMapper.readValue(body, ArticleCreateRequest.class);
          } catch (JsonProcessingException e) {
            LOG.warn(e.getMessage());
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          try {
            Optional<Long> articleId = articleService.generateId();
            Article article = new Article(articleId.get(), articleCreateRequest.title(), articleCreateRequest.tags(), null, false);
            articleService.create(article);
            LOG.debug("Article with id = {} created", articleId);
            response.status(200);
            return objectMapper.writeValueAsString(new ArticleCreateResponse(articleId.get()));
          } catch (ArticleCreateException e) {
            LOG.warn("Cannot create article", e);
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          } catch (ConnectionDataBaseException e) {
            LOG.error(e.getMessage());
            response.status(500);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }
        }
    );
  }

  private void getArticle() {
    service.get(
        "/api/article/:articleId",
        (Request request, Response response) -> {
          response.type("application/json");
          Long articleId;

          try {
            articleId = Long.parseLong(request.params("articleId"));
          } catch (NumberFormatException e) {
            LOG.warn(e.getMessage());
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          Article article;

          try {
            article = articleService.findById(articleId);
          } catch (ArticleFindException e) {
            LOG.warn("Article not found", e);
            response.status(404);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          LOG.debug("Article with id = {} found", articleId);
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

  private void getAllArticles() {
    service.get(
        "/api/articles",
        (Request request, Response response) -> {
          response.type("application/json");

          Optional<List<Article>> articles;

          try {
            articles = articleService.findAll();
          } catch (ConnectionDataBaseException e) {
            LOG.error(e.getMessage());
            response.status(500);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          Map<String, ArticleAllFindResponse> articlesMap = new HashMap<>();

          if (articles.isPresent()) {
            for (Article article : articles.get()) {
              articlesMap.put(article.getId().toString(), new ArticleAllFindResponse(
                  article.getTitle(),
                  article.getTags(),
                  article.getComments()
              ));
            }

            LOG.debug("Successfully retrieved all articles");
            response.status(200);
            return objectMapper.writeValueAsString(articlesMap);
          } else {
            LOG.debug("Successfully retrieved all articles");
            response.status(200);
            return objectMapper.writeValueAsString(articlesMap);
          }
        }
    );
  }

  private void deleteArticle() {
    service.delete(
        "/api/article/:articleId",
        (Request request, Response response) -> {
          response.type("application/json");

          Long articleId;

          try {
            articleId = Long.parseLong(request.params("articleId"));
          } catch (NumberFormatException e) {
            LOG.warn(e.getMessage());
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          try {
            articleService.findById(articleId);
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

  private void updateArticle() {
    service.put(
        "api/article/:articleId",
        (Request request, Response response) -> {
          response.type("application/json");
          String body = request.body();

          ArticleUpdateRequest articleUpdateRequest;
          Long articleId;

          try {
            articleUpdateRequest = objectMapper.readValue(body, ArticleUpdateRequest.class);
          } catch (JsonProcessingException e) {
            LOG.warn(e.getMessage());
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          try {
            articleId = Long.parseLong(request.params("articleId"));
          } catch (NumberFormatException e) {
            LOG.warn(e.getMessage());
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          Article article;

          try {
            article = articleService.findById(articleId);
          } catch (ArticleFindException e) {
            LOG.warn("Article not found", e);
            response.status(404);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          article = article.withTags(articleUpdateRequest.tags());
          article = article.withTitle(articleUpdateRequest.title());

          articleService.update(article);
          LOG.debug("Article updated: {}", articleId);
          response.status(200);
          return objectMapper.writeValueAsString(new ArticleUpdateResponse(articleId));
        }
    );
  }

  public void createArticles() {
    service.post(
        "api/articles",
        (Request request, Response response) -> {
          response.type("application/json");
          String body = request.body();

          List<ArticleCreateRequest> articlesRequest;

          try {
            articlesRequest = Arrays.asList(objectMapper.readValue(body, ArticleCreateRequest[].class));
          } catch (JsonProcessingException e) {
            LOG.warn(e.getMessage());
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          List<Article> articles = new ArrayList<>();

          try {
            for (ArticleCreateRequest article : articlesRequest) {
              articles.add(new Article(articleService.generateId().get(),
                  article.title(),
                  article.tags(),
                  null,
                  false
              ));
            }
          } catch (ConnectionDataBaseException e) {
            LOG.error(e.getMessage());
            response.status(500);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }

          try {
            articleService.createArticles(articles);
          } catch (ConnectionDataBaseException e) {
            LOG.error(e.getMessage());
            response.status(500);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }


          LOG.debug("Successfully created articles");
          response.status(200);
          return objectMapper.writeValueAsString(new ArticlesCreateResponse(articles.stream().map(x -> x.getId()).toList()));
        }
    );
  }
}
