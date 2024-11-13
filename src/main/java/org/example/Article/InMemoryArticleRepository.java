package org.example.Article;

import org.example.Article.Exceptions.ArticleIdDuplicatedException;
import org.example.Article.Exceptions.ArticleNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryArticleRepository implements ArticleRepository {

  private final AtomicLong nextId = new AtomicLong(1);
  private final Map<ArticleId, Article> articlesMap = new ConcurrentHashMap<>();

  @Override
  public ArticleId generateId() {
    return new ArticleId(nextId.getAndIncrement());
  }

  @Override
  public List<Article> findAll() {
    return new ArrayList<>(articlesMap.values());
  }

  @Override
  public Article findById(ArticleId id) {
    Article article = articlesMap.get(id);
    if (article == null) {
      throw new ArticleNotFoundException("Cannot find article with id=" + id);
    }
    return article;
  }

  @Override
  public synchronized void create(Article article) {
    if (articlesMap.containsKey(article.getId())) {
      throw new ArticleIdDuplicatedException("Article with the given id already exists: " + article.getId());
    }
    articlesMap.put(article.getId(), article);
  }

  @Override
  public synchronized void update(Article article) {
    if (!articlesMap.containsKey(article.getId())) {
      throw new ArticleNotFoundException("Cannot find article with id=" + article.getId());
    }
    articlesMap.put(article.getId(), article);
  }

  @Override
  public void delete(ArticleId id) {
    if (!articlesMap.containsKey(id)) {
      throw new ArticleNotFoundException("Cannot find article with id=" + id);
    }
    articlesMap.remove(id);
  }
}
