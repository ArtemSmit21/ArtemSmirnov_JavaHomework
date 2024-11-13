package org.example.Article;

import org.example.Article.Exceptions.ArticleIdDuplicatedException;
import org.example.Article.Exceptions.ArticleNotFoundException;

import java.util.List;

public interface ArticleRepository {

  ArticleId generateId();

  List<Article> findAll();

  /**
   * @throws ArticleNotFoundException
   */
  Article findById(ArticleId id);

  /**
   * @throws ArticleIdDuplicatedException
   */
  void create(Article article);

  /**
   * @throws ArticleNotFoundException
   */
  void update(Article article);

  /**
   * @throws ArticleNotFoundException
   */
  void delete(ArticleId id);
}
