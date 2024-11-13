package org.example.Comment;

import org.example.Comment.Exceptions.CommentIdDuplicatedException;
import org.example.Comment.Exceptions.CommentNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCommentRepository implements CommentRepository {

  private final AtomicLong nextId = new AtomicLong(1);
  private final Map<CommentId, Comment> commentsMap = new ConcurrentHashMap<>();

  @Override
  public CommentId generateId() {
    return new CommentId(nextId.getAndIncrement());
  }

  @Override
  public List<Comment> findAll() {
    return new ArrayList<>(commentsMap.values());
  }

  @Override
  public Comment findById(CommentId id) {
    Comment comment = commentsMap.get(id);
    if (comment == null) {
      throw new CommentNotFoundException("Cannot find comment with id=" + id);
    }
    return comment;
  }

  @Override
  public synchronized void create(Comment comment) {
    if (commentsMap.containsKey(comment.getId())) {
      throw new CommentIdDuplicatedException("Comment with the given id already exists: " + comment.getId());
    }
    commentsMap.put(comment.getId(), comment);
  }

  @Override
  public synchronized void update(Comment comment) {
    if (!commentsMap.containsKey(comment.getId())) {
      throw new CommentNotFoundException("Cannot find comment with id=" + comment.getId());
    }
    commentsMap.put(comment.getId(), comment);
  }

  @Override
  public void delete(CommentId id) {
    if (!commentsMap.containsKey(id)) {
      throw new CommentNotFoundException("Cannot find comment with id=" + id);
    }
    commentsMap.remove(id);
  }
}
