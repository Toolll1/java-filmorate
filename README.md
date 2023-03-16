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


//комментарии по диаграмме 
1. мне кажется лучше сменить имя в user с user_name, на name (имхо), так вроде даже принято называть
2. можешь пометить поля типо email, login тегом UNIQUE (если через quickDBD делал), так будет более наглядно какие поля должны быть уникальны, не являсь ключом 
3. лучше поменять friends на user_friends (будет понятно к какой сущности относится)
4. likes лучше поменять на film_likes (тоже будет более понятно)
5. для рейтинга лучше сделать отдельную таблицу, а в film передать rating_id. По rating_id выдавать инфу про рейтинг уже. Мне кажется так более универсально и это больше удовлетворяет принципу реляционности, где данные связаны между собой, но если ты что-то менешь (например название рейтинга изменилось), то это поменыяется везде в таблице, не придется в куче разных строк менять значение рейтинга!
