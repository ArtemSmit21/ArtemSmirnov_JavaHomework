CREATE TABLE article
(
    article_id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    tags TEXT[] NOT NULL,
    comments_id BIGINT[],
    trending boolean NOT NULL
)