WITH helpTable (post_id, number_of_comments) AS (
    SELECT post_id, COUNT(post_id) FROM comment
    GROUP BY post_id
)
SELECT post.post_id AS post_id FROM post
EXCEPT (SELECT helpTable.post_id FROM helpTable)
UNION ALL
SELECT post.post_id FROM post
INNER JOIN helpTable ON post.post_id = helpTable.post_id AND helpTable.number_of_comments = 1
ORDER BY post_id
LIMIT 10
