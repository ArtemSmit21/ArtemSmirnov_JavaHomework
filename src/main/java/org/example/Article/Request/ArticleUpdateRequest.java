package org.example.Article.Request;

import java.util.Set;

public record ArticleUpdateRequest(String title, Set<String> tags) {
}
