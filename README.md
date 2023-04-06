# java-filmorate
ER-диаграмма:
![This is an image](https://i.postimg.cc/PfBPNG80/filmorate.png)



Запрос 10 самых популярных фильмов:

```sql
SELECT f.name, string_agg (DISTINCT g.name, ',') as genre, f.description, f.release_date, f.duration, f.rating, f.ll as likes
FROM (SELECT f.film_id, f.name, f.description, f.release_date, f.duration, r.name as rating,
   COUNT (fl.film_id) AS ll
   FROM films AS f
   LEFT OUTER JOIN film_likes AS fl ON f.film_id = fl.film_id
   LEFT OUTER JOIN rating AS r ON f.rating_id = r.rating_id 
   GROUP BY f.film_id, r.name) AS f
LEFT OUTER JOIN film_genre AS fg ON  fg.film_id = f.film_id
LEFT OUTER JOIN genre AS g ON  g.genre_id = fg.genre_id
GROUP BY f.name, f.description, f.release_date, f.duration, f.rating, f.ll
ORDER BY f.ll DESC
LIMIT 10;
```