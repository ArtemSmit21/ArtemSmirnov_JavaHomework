package org.example.Article;

import org.example.Article.Exceptions.*;
import org.example.Comment.Comment;
import org.example.Comment.CommentRepository;
import org.example.Comment.Exceptions.CommentCreateException;
import org.example.Comment.Exceptions.CommentDuplicatedException;
import org.example.Comment.Exceptions.CommentFindException;
import org.example.Comment.Exceptions.CommentNotFoundException;
import org.example.Comment.Exceptions.CommentMismatchException;
import org.example.ConnectionDataBaseException;
import org.example.TransactionManager;
import org.jdbi.v3.core.ConnectionException;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArticleServiceImpl implements ArticleService {

  private final ArticleRepository articleRepository;
  private final CommentRepository commentRepository;
  private final TransactionManager transactionManager;

  public ArticleServiceImpl(ArticleRepository articleRepository, CommentRepository commentRepository, TransactionManager transactionManager) {
    this.articleRepository = articleRepository;
    this.commentRepository = commentRepository;
    this.transactionManager = transactionManager;
  }

  public Optional<Long> generateId() throws ConnectionDataBaseException {
    try {
      return articleRepository.generateId();
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
  }

  public Optional<List<Article>> findAll() throws ConnectionDataBaseException {
    try {
      return articleRepository.findAll();
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
  }

  public Article findById(Long id) throws ArticleFindException, ConnectionDataBaseException {
    try {
      return articleRepository.findById(id);
    } catch (ArticleNotFoundException e) {
      throw new ArticleFindException("Cannot find article with id = " + id, e);
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
  }

  public Long create(Article article) throws ArticleCreateException, ConnectionDataBaseException {
    try {
      return articleRepository.create(article);
    } catch (ArticleIdDuplicatedException e) {
      throw new ArticleCreateException("Cannot create article with id = " + article.getId(), e);
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
  }

  public Long update(Article article) throws ArticleUpdateException, ConnectionDataBaseException {
    try {
      transactionManager.useTransaction(TransactionIsolationLevel.REPEATABLE_READ, () -> {
        try {
          articleRepository.update(article);
        } catch (ArticleNotFoundException e) {
          throw new ArticleUpdateException("Cannot update article with id = " + article.getId(), e);
        }
      });
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
    return 1L;
  }

  public Long delete(Long id) throws ArticleDeleteException, ConnectionDataBaseException {
    try {
      transactionManager.useTransaction(TransactionIsolationLevel.REPEATABLE_READ, () -> {
        Article article;
        try {
          article = articleRepository.findById(id);
        } catch (ArticleNotFoundException e) {
          throw new ArticleDeleteException("Cannot delete article with id = " + id, e);
        }
        try {
          articleRepository.delete(id);
        } catch (ArticleNotFoundException e) {
          throw new ArticleDeleteException("Cannot delete article with id = " + id, e);
        }
        for (Comment comment : article.getComments()) {
          try {
            commentRepository.delete(comment.getId());
          } catch (CommentNotFoundException e) {
            throw new ArticleDeleteException("Cannot delete article with id = " + comment.getId(), e);
          }
        }
      });
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
    return id;
  }

  public Long deleteComment(Long articleId, Long commentId) throws ConnectionDataBaseException {
    try {
      transactionManager.useTransaction(TransactionIsolationLevel.REPEATABLE_READ, () -> {

        Article article;
        Comment comment;

        try {
          article = articleRepository.findById(articleId);
        } catch (ArticleNotFoundException e) {
          throw new ArticleFindException("Cannot find article with id = " + articleId, e);
        }

        try {
          comment = commentRepository.findById(commentId);
        } catch (CommentNotFoundException e) {
          throw new CommentFindException("Cannot find comment with id = " + commentId, e);
        }

        List<Comment> comments = article.getComments();

        if (comments == null) {
          throw new CommentMismatchException("Article with id = " + articleId + " has no comments");
        }

        if (!comments.contains(comment)) {
          throw new CommentMismatchException("Article with id = " + articleId + " has no comment with id = " + commentId);
        }

        comments.remove(comment);
        boolean trending = comments.size() > 2;

        try {
          update(article.withComments(comments).withTrending(trending));
        } catch (ArticleUpdateException e) {
          throw new ArticleUpdateException("Cannot update article with id = " + articleId, e);
        } catch (ConnectionDataBaseException e) {
          throw new ConnectionException(e);
        }
      });
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
    return 1L;
  }

  public Long appendCommentToArticle(Long articleId, Comment comment) throws ConnectionDataBaseException {
    try {
      transactionManager.useTransaction(TransactionIsolationLevel.REPEATABLE_READ, () -> {

        Article article;

        try {
          article = articleRepository.findById(articleId);
        } catch (ArticleNotFoundException e) {
          throw new ArticleFindException("Cannot find article with id = " + articleId, e);
        }

        try {
          commentRepository.create(comment);
        } catch (CommentDuplicatedException e) {
          throw new CommentCreateException("Cannot create comment with id = " + comment.getId(), e);
        }

        List<Comment> comments = article.getComments();

        if (comments == null) {
          comments = new ArrayList<>();
        }

        if (comments.contains(comment)) {
          throw new CommentMismatchException("Article with id = " + articleId + " already has comment with id = " + comment.getId());
        }

        comments.add(comment);

        boolean trending = comments.size() > 2;

        try {
          update(article.withComments(comments).withTrending(trending));
        } catch (ArticleUpdateException e) {
          throw new ArticleUpdateException("Cannot update article with id = " + articleId, e);
        } catch (ConnectionDataBaseException e) {
          throw new ConnectionException(e);
        }
      });
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
    return 1L;
  }

  @Override
  public void createArticles(List<Article> articles) throws ConnectionDataBaseException, ArticleCreateException {
    transactionManager.useTransaction(TransactionIsolationLevel.READ_COMMITTED, () -> {
      for (Article article : articles) {
        try {
          articleRepository.create(article);
        } catch (ArticleIdDuplicatedException e) {
          throw new ArticleCreateException("Cannot create article with id = " + article.getId(), e);
        } catch (ConnectionException e) {
          throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
        }
      }
    });
  }
}
