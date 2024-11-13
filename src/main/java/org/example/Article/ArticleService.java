package org.example.Article;

import org.example.Article.Exceptions.*;
import org.example.Comment.Comment;
import org.example.Comment.CommentId;
import org.example.Comment.CommentRepository;
import org.example.Comment.Exceptions.CommentFindException;
import org.example.Comment.Exceptions.CommentNotFoundException;

import java.util.List;
import java.util.Set;

public class ArticleService {

  private final ArticleRepository articleRepository;
  private final CommentRepository commentRepository;

  public ArticleService(ArticleRepository articleRepository, CommentRepository commentRepository) {
    this.articleRepository = articleRepository;
    this.commentRepository = commentRepository;
  }

  public List<Article> findAll() {
    return articleRepository.findAll();
  }

  public Article findById(ArticleId id) throws ArticleFindException {
    try {
      return articleRepository.findById(id);
    } catch (ArticleNotFoundException e) {
      throw new ArticleFindException("Cannot find article with id=" + id, e);
    }
  }

  public ArticleId create(String title, Set<String> tags) throws ArticleCreateException {
    ArticleId articleId = articleRepository.generateId();
    Article article = new Article(articleId, title, tags, null);
    try {
      articleRepository.create(article);
    } catch (ArticleIdDuplicatedException e) {
      throw new ArticleCreateException("Cannot create article", e);
    }
    return articleId;
  }

  public void update(ArticleId id, String title, Set<String> tags) throws ArticleUpdateException {
    Article article;
    try {
      article = articleRepository.findById(id);
    } catch (ArticleNotFoundException e) {
      throw new ArticleUpdateException("Cannot find article with id=" + id);
    }

    try {
      articleRepository.update(
          article.withTitle(title)
              .withTags(tags)
      );
    } catch (ArticleNotFoundException e) {
      throw new ArticleUpdateException("Cannot update article with id=" + id);
    }
  }

  public void delete(ArticleId id) throws ArticleDeleteException {
    try {
      Article article = articleRepository.findById(id);
      List<Comment> comments = article.getComments();
      articleRepository.delete(id);
      if (comments != null) {
        for (Comment comment : comments) {
          commentRepository.delete(comment.getId());
        }
      }
    } catch (ArticleNotFoundException e) {
      throw new ArticleDeleteException("Cannot delete article with id=" + id, e);
    }
  }

  public void deleteComment(ArticleId articleId, CommentId commentId) throws ArticleFindException, CommentFindException {
    Article article;
    Comment comment;

    try {
      article = articleRepository.findById(articleId);
    } catch (ArticleNotFoundException e) {
      throw new ArticleFindException("Cannot find article with id=" + articleId);
    }

    try {
      comment = commentRepository.findById(commentId);
    } catch (CommentNotFoundException e) {
      throw new CommentFindException("Cannot find comment with id=" + commentId);
    }

    List<Comment> currentComments = article.getComments();
    currentComments.remove(comment);
    commentRepository.delete(commentId);
    article = article.withComments(currentComments);
  }
}
