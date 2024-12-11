package org.example;

public enum SqlQuery {

  CREATEARTICLE("INSERT INTO article (article_id, title, tags, comments_id, trending) VALUES" +
      " (:article_id, :title, :tags, :comments_id, :trending)"),

  CREATECOMMENT("INSERT INTO comment (comment_id, text) VALUES (:comment_id, :text)"),

  GENERATEARTICLEID("SELECT nextval('article_article_id_seq') AS value"),

  GENERATECOMMENTID("SELECT nextval('comment_comment_id_seq') AS value"),

  FINDALLARTICLES("SELECT * FROM article"),

  FINDARTICLE("SELECT * FROM article WHERE article_id = :article_id"),

  FINDCOMMENT("SELECT * FROM comment WHERE comment_id = :comment_id"),

  UPDATECOMENT("UPDATE comment SET text = :text WHERE comment_id = :comment_id"),

  FINDCOMMENTTEXT("SELECT text FROM comment AS text WHERE comment_id = :comment_id"),

  UPDATEARTICLE("UPDATE article SET title = :title, tags = :tags, comments_id = :comments_id, trending = :trending WHERE article_id = :article_id"),

  DELETECOMMENT("DELETE FROM comment WHERE comment_id = :comment_id"),

  DELETEARTICLE("DELETE FROM article WHERE article_id = :article_id");

  private final String query;

  private SqlQuery(String query) {
    this.query = query;
  }

  public String getString() {
    return query;
  }
}
