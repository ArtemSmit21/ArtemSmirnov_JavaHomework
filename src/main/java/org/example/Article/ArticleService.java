package org.example.Article;

import org.example.Article.Exceptions.ArticleCreateException;
import org.example.Article.Exceptions.ArticleDeleteException;
import org.example.Article.Exceptions.ArticleFindException;
import org.example.Article.Exceptions.ArticleUpdateException;
import org.example.Comment.Comment;
import org.example.ConnectionDataBaseException;

import java.util.List;
import java.util.Optional;

public interface ArticleService {

  Optional<Long> generateId() throws ConnectionDataBaseException;

  Optional<List<Article>> findAll() throws ConnectionDataBaseException;

  Article findById(Long id) throws ArticleFindException, ConnectionDataBaseException;

  Long create(Article article) throws ArticleCreateException, ConnectionDataBaseException;

  Long update(Article article) throws ArticleUpdateException, ConnectionDataBaseException;

  Long delete(Long id) throws ArticleDeleteException, ConnectionDataBaseException;

  Long deleteComment(Long articleId, Long commentId) throws ConnectionDataBaseException;

  Long appendCommentToArticle(Long articleId, Comment comment) throws ConnectionDataBaseException;

  void createArticles(List<Article> articles) throws ConnectionDataBaseException;
}
