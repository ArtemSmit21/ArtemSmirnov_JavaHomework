package org.example.Article;

import org.example.Article.Exceptions.ArticleIdDuplicatedException;
import org.example.Article.Exceptions.ArticleNotFoundException;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository {

  Optional<Long> generateId();

  Optional<List<Article>> findAll();

  Article findById(Long id) throws ArticleNotFoundException;

  Long create(Article article) throws ArticleIdDuplicatedException;

  void update(Article article) throws ArticleNotFoundException;

  Long delete(Long id) throws ArticleNotFoundException;
}
