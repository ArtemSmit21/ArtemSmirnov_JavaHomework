package org.example.Article;

import io.github.resilience4j.retry.Retry;
import org.example.Article.Exceptions.ArticleCreateException;
import org.example.Article.Exceptions.ArticleDeleteException;
import org.example.Article.Exceptions.ArticleFindException;
import org.example.Article.Exceptions.ArticleUpdateException;
import org.example.Comment.Comment;
import org.example.ConnectionDataBaseException;
import java.util.List;
import java.util.Optional;

public class RetryableArticleServiceImpl implements ArticleService {

  private final ArticleService delegate;
  private final Retry retry;

  public RetryableArticleServiceImpl(ArticleService articleService, Retry retry) {
    this.delegate = articleService;
    this.retry = retry;
  }

  @Override
  public Optional<Long> generateId() throws ConnectionDataBaseException {
    return delegate.generateId();
  }

  @Override
  public Optional<List<Article>> findAll() throws ConnectionDataBaseException {
    return delegate.findAll();
  }

  @Override
  public Article findById(Long id) throws ArticleFindException, ConnectionDataBaseException {
    return delegate.findById(id);
  }

  @Override
  public Long create(Article article) throws ArticleCreateException, ConnectionDataBaseException {
    return delegate.create(article);
  }

  @Override
  public Long update(Article article) throws ArticleUpdateException, ConnectionDataBaseException {
    return retry.executeSupplier(
        () -> delegate.update(article)
    );
  }

  @Override
  public Long delete(Long id) throws ArticleDeleteException, ConnectionDataBaseException {
    return delegate.delete(id);
  }

  @Override
  public Long deleteComment(Long articleId, Long commentId) throws ConnectionDataBaseException {
    return retry.executeSupplier(
        () -> delegate.deleteComment(articleId, commentId)
    );
  }

  @Override
  public Long appendCommentToArticle(Long articleId, Comment comment) throws ConnectionDataBaseException {
    return retry.executeSupplier(
        () -> delegate.appendCommentToArticle(articleId, comment)
    );
  }

  @Override
  public void createArticles(List<Article> articles) throws ConnectionDataBaseException {
    delegate.createArticles(articles);
  }
}
