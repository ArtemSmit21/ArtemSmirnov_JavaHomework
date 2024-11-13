package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Article.ArticleController;
import org.example.Article.ArticleService;
import org.example.Comment.CommentController;
import org.example.Article.InMemoryArticleRepository;
import org.example.Comment.CommentService;
import org.example.Comment.InMemoryCommentRepository;
import org.example.HTML.ArticleFreemarkerController;
import org.example.HTML.TemplateFactory;
import spark.Service;

import java.util.List;

public class Main {

  public static void main(String[] args) {
    Service service = Service.ignite();
    ObjectMapper objectMapper = new ObjectMapper();

    InMemoryArticleRepository inMemoryArticleRepository = new InMemoryArticleRepository();
    InMemoryCommentRepository inMemoryCommentRepository = new InMemoryCommentRepository();
    ArticleService articleService = new ArticleService(inMemoryArticleRepository, inMemoryCommentRepository);
    CommentService commentService = new CommentService(inMemoryCommentRepository, inMemoryArticleRepository);

    Application application = new Application(
        List.of(
            new ArticleController(
                service,
                articleService,
                commentService,
                objectMapper
            ),
            new CommentController(
                service,
                articleService,
                commentService,
                objectMapper
            ),
            new ArticleFreemarkerController(
                service,
                articleService,
                commentService,
                objectMapper,
                TemplateFactory.freeMarkerEngine()
            )
        )
    );
    application.start();
  }
}