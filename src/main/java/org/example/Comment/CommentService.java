package org.example.Comment;

import org.example.Article.Article;
import org.example.Article.ArticleId;
import org.example.Article.ArticleRepository;
import org.example.Article.Exceptions.ArticleFindException;
import org.example.Article.Exceptions.ArticleNotFoundException;
import org.example.Comment.Exceptions.*;

import java.util.ArrayList;
import java.util.List;

public class CommentService {

  private final CommentRepository commentRepository;
  private final ArticleRepository articleRepository;

  public CommentService(CommentRepository commentRepository, ArticleRepository articleRepository) {
    this.commentRepository = commentRepository;
    this.articleRepository = articleRepository;
  }

  public List<Comment> findAll() {
    return commentRepository.findAll();
  }

  public Comment findById(CommentId id) throws CommentFindException {
    try {
      return commentRepository.findById(id);
    } catch (CommentNotFoundException e) {
      throw new CommentFindException("Cannot find comment with id=" + id, e);
    }
  }

  public CommentId create(ArticleId articleId, String text) throws CommentCreateException {
    CommentId commentId = commentRepository.generateId();
    Comment comment = new Comment(commentId, articleId, text);
    try {
      commentRepository.create(comment);
    } catch (CommentIdDuplicatedException e) {
      throw new CommentCreateException("Cannot create comment", e);
    }
    return commentId;
  }

  public void update(CommentId commentId, ArticleId articleId, String text) throws CommentUpdateException {
    Comment comment;
    try {
      comment = commentRepository.findById(commentId);
    } catch (CommentNotFoundException e) {
      throw new CommentUpdateException("Cannot find comment with id=" + commentId, e);
    }
    try {
      commentRepository.update(
          comment.withArticleId(articleId)
              .withText(text)
      );
    } catch (CommentNotFoundException e) {
      throw new CommentUpdateException("Cannot update comment with id=" + commentId, e);
    }
  }

  public void delete(CommentId commentId) throws CommentDeleteException {
    try {
      commentRepository.delete(commentId);
    } catch (CommentNotFoundException e) {
      throw new CommentDeleteException("Cannot delete comment with id=" + commentId, e);
    }
  }

  public ArticleId appendToArticle(ArticleId articleId, CommentId commentId) throws ArticleFindException, CommentFindException {
    Article article;
    Comment comment;

    try {
      article = articleRepository.findById(articleId);
    } catch (ArticleNotFoundException e) {
      throw new ArticleFindException("Cannot find article with id=" + articleId, e);
    }

    try {
      comment = commentRepository.findById(commentId);
    } catch (CommentNotFoundException e) {
      throw new CommentFindException("Cannot find comment with id=" + commentId, e);
    }

    if (article.getComments() != null) {
      if (article.getComments().contains(comment)) {
        throw new CommentAppendingException("This comment is already linked to this article");
      }
    }

    List<Comment> comments = article.getComments() == null ? new ArrayList<>() : article.getComments();
    comments.add(comment);
    articleRepository.update(article.withComments(comments));
    return article.getId();
  }
}
