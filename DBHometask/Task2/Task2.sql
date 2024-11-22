WITH helpTable (post_id, number_of_comments) AS (
    SELECT post_id, COUNT(post_id) FROM comment
    GROUP BY post_id
)
SELECT helpTable.post_id AS post_id FROM helpTable
INNER JOIN post ON helpTable.post_id = post.post_id AND ASCII(post.title) >= 48 AND ASCII(post.title) <= 57 AND LENGTH(post.content) > 20
ORDER BY post_id
