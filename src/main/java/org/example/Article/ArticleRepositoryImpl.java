package org.example.Article;

import org.example.Article.Exceptions.ArticleIdDuplicatedException;
import org.example.Article.Exceptions.ArticleNotFoundException;
import org.example.Comment.Comment;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.core.statement.Update;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.example.SqlQuery.*;

public class ArticleRepositoryImpl implements ArticleRepository {

  private final Jdbi jdbi;

  public ArticleRepositoryImpl(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Optional<Long> generateId() {
    Optional<Long> value;

    try (Handle handle = jdbi.open()) {
      value = Optional.ofNullable((Long) handle.createQuery(GENERATEARTICLEID.getString())
          .mapToMap()
          .first()
          .get("value"));
    }

    return value;
  }

  @Override
  public Optional<List<Article>> findAll() {
    List<Map<String, Object>> value;

    List<Article> articles = new ArrayList<>();

    try (Handle handle = jdbi.open()) {
      articles = handle.createQuery(FINDALLARTICLES.getString())
          .map((rs, ctx) ->
              new Article(
                  rs.getLong("article_id"),
                  rs.getString("title"),
                  Stream.of((String[]) rs.getArray("tags").getArray())
                      .collect(Collectors.toSet()),
                  rs.getArray("comments_id") == null ? new ArrayList<>() :
                      Stream.of((Long[]) rs.getArray("comments_id").getArray())
                          .map(x -> new Comment(x.longValue(), (String) handle.createQuery(FINDCOMMENT.getString())
                              .bind("comment_id", x)
                              .mapToMap()
                              .first()
                              .get("text"))
                          )
                          .collect(Collectors.toList()),
                  rs.getBoolean("trending")
              ))
          .list();
    }

    return Optional.ofNullable(articles);
  }

  @Override
  public Article findById(Long id) throws ArticleNotFoundException {
    Article article;

    try (Handle handle = jdbi.open()) {
      ResultIterable<Article> result = handle.createQuery(FINDARTICLE.getString())
          .bind("article_id", id)
          .map((rs, ctx) -> new Article(
                  rs.getLong("article_id"),
                  rs.getString("title"),
                  Stream.of((String[]) rs.getArray("tags").getArray())
                      .collect(Collectors.toSet()),
                  rs.getArray("comments_id") == null ? new ArrayList<>() :
                      Stream.of((Long[]) rs.getArray("comments_id").getArray())
                          .map(x -> new Comment(x.longValue(), (String) handle.createQuery(FINDCOMMENTTEXT.getString())
                              .bind("comment_id", x)
                              .mapToMap()
                              .first()
                              .get("text"))
                          )
                          .collect(Collectors.toList()),
                  rs.getBoolean("trending")
              )
          );

      try {
        article = result.first();
      } catch (IllegalStateException e) {
        throw new ArticleNotFoundException(e.getMessage(), e);
      }
    }

    return article;
  }

  @Override
  public Long create(Article article) throws ArticleIdDuplicatedException {
    Long article_id;

    try (Handle handle = jdbi.open()) {
      try (Update update = handle.createUpdate(CREATEARTICLE.getString())) {
        article_id = (Long) update.bind("article_id", article.getId())
            .bind("title", article.getTitle())
            .bindArray("tags", String.class, article.getTags())
            .bindArray("comments_id", Long.class,
                article.getComments() == null ? new ArrayList<Long>() : article.getCommentsId())
            .bind("trending", article.getTrending())
            .executeAndReturnGeneratedKeys("article_id")
            .mapToMap()
            .first()
            .get("article_id");
      } catch (UnableToExecuteStatementException e) {
        throw new ArticleIdDuplicatedException(e.getMessage(), e);
      }
    }

    return article_id;
  }

  @Override
  public void update(Article article) throws ArticleNotFoundException {
    try (Handle handle = jdbi.open()) {
      try (Update update = handle.createUpdate(UPDATEARTICLE.getString())) {
        Long article_id = (Long) update.bind("article_id", article.getId())
            .bind("title", article.getTitle())
            .bindArray("tags", String.class, article.getTags())
            .bindArray("comments_id", Long.class,
                article.getComments() == null ? new ArrayList<Long>() : article.getCommentsId())
            .bind("trending", article.getTrending())
            .executeAndReturnGeneratedKeys("article_id")
            .mapToMap()
            .first()
            .get("article_id");
      } catch (IllegalStateException e) {
        throw new ArticleNotFoundException(e.getMessage(), e);
      }
    }
  }

  @Override
  public Long delete(Long id) throws ArticleNotFoundException {
    Long article_id;

    try (Handle handle = jdbi.open()) {
      try (Update update = handle.createUpdate(DELETEARTICLE.getString())) {
        article_id = (Long) update.bind("article_id", id)
            .executeAndReturnGeneratedKeys("article_id")
            .mapToMap()
            .first()
            .get("article_id");
      } catch (IllegalStateException e) {
        throw new ArticleNotFoundException(e.getMessage(), e);
      }
    }

    return article_id;
  }
}
