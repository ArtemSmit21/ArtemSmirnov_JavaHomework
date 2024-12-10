package org.example.Article.Request;

import java.util.Set;

public record ArticleCreateRequest(String title, Set<String> tags) {
}
