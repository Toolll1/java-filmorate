# java-filmorate
ER-диаграмма:
![This is an image](https://i.postimg.cc/2SR6XFbc/filmorate.png)

Примеры запросов для основных операций:

1. Запрос 10 самых популярных фильмов:
SELECT f.film_name, f.description, GROUP_CONCAT (DISTINCT g.genre_name), f.release_date, f.duration, f.rating, f.ll
FROM (SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.rating,
   COUNT (l.film_id) AS ll
   FROM film AS f
   LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id
   GROUP BY f.film_name) AS f
LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id
LEFT OUTER JOIN film_genre AS fg ON  fg.film_id = f.film_id
LEFT OUTER JOIN genre AS g ON  g.genre_id = fg.genre_id
GROUP BY f.film_name
ORDER BY ll DESC
LIMIT 10;

