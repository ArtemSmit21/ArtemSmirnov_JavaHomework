package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.example.Article.*;
import org.example.Comment.CommentController;
import org.example.Comment.CommentRepositoryImpl;
import org.example.Comment.CommentService;
import org.example.HTML.ArticleFreemarkerController;
import org.example.HTML.TemplateFactory;
import org.flywaydb.core.Flyway;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import spark.Service;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    Config config = ConfigFactory.load();

    Flyway flyway =
        Flyway.configure()
            .locations("classpath:db/migrations")
            .dataSource(config.getString("app.database.url"), config.getString("app.database.user"),
                config.getString("app.database.password"))
            .load();
    flyway.migrate();

    Jdbi jdbi = Jdbi.create(config.getString("app.database.url"), config.getString("app.database.user"),
        config.getString("app.database.password"));

    Service service = Service.ignite();
    ObjectMapper objectMapper = new ObjectMapper();

    ArticleService articleService = new RetryableArticleServiceImpl(
        new ArticleServiceImpl(
            new ArticleRepositoryImpl(jdbi),
            new CommentRepositoryImpl(jdbi),
            new JdbiTransactionManager(jdbi)
        ),
        Retry.of(
            "retry-db",
            RetryConfig.custom()
                .maxAttempts(3)
                .retryExceptions(UnableToExecuteStatementException.class)
                .build()
        )
    );

    CommentService commentService = new CommentService(new CommentRepositoryImpl(jdbi));

    Application application = new Application(
        List.of(
            new ArticleController(service, articleService, commentService, objectMapper),
            new CommentController(service, articleService, commentService, objectMapper),
            new ArticleFreemarkerController(service, articleService, commentService, objectMapper, TemplateFactory.freeMarkerEngine())
        )
    );
    application.start();
  }
}