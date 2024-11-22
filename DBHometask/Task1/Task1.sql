SELECT COUNT(profile_id) AS count_without_posts FROM profile
WHERE profile_id NOT IN (SELECT DISTINCT profile_id FROM post);
