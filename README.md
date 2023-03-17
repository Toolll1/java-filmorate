# java-filmorate
ER-диаграмма:
![This is an image](https://i.postimg.cc/jSRbRfpR/filmorate.png)



Запрос 10 самых популярных фильмов:

```sql
SELECT f.name, f.description, GROUP_CONCAT (DISTINCT g.name), f.release_date, f.duration, f.rating, f.ll
FROM (SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating,
   COUNT (fl.film_id) AS ll
   FROM film AS f
   LEFT OUTER JOIN film_likes AS fl ON f.film_id = fl.film_id
   GROUP BY f.name) AS f
LEFT OUTER JOIN film_likes AS fl ON f.film_id = fl.film_id
LEFT OUTER JOIN film_genre AS fg ON  fg.film_id = f.film_id
LEFT OUTER JOIN genre AS g ON  g.genre_id = fg.genre_id
GROUP BY f.name
ORDER BY ll DESC
LIMIT 10;
```