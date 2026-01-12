<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>${book.title} - Коментарі</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f4f4f9;
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1, h3 { color: #2c3e50; }
        hr { border: 0; height: 1px; background: #eee; margin: 20px 0; }
        a { color: #3498db; text-decoration: none; }
        a:hover { text-decoration: underline; }

        /* Стилі для книги */
        .book-info {
            text-align: center;
            margin-bottom: 30px;
        }
        .book-meta { color: #777; font-style: italic; }

        /* Стилі для списку коментарів */
        ul.comment-list { list-style-type: none; padding: 0; }
        li.comment-item {
            background: #fdfdfd;
            border: 1px solid #eee;
            padding: 15px;
            margin-bottom: 10px;
            border-radius: 4px;
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
        }
        .comment-content { flex-grow: 1; margin-right: 15px; }
        .comment-author { font-weight: bold; color: #2c3e50; }
        .comment-meta { font-size: 0.85em; color: #999; display: block; margin-top: 5px; }

        /* Стилі для форм та кнопок */
        form label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
        }
        input[type="text"], textarea {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box; /* Щоб padding не збільшував ширину */
            font-family: inherit;
        }
        textarea { resize: vertical; height: 80px; }

        button {
            background-color: #3498db;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1em;
            transition: background-color 0.2s;
        }
        button:hover { background-color: #2980b9; }

        /* Червона кнопка видалення */
        button.delete-btn {
            background-color: #e74c3c;
            padding: 6px 12px;
            font-size: 0.9em;
        }
        button.delete-btn:hover { background-color: #c0392b; }
        .no-comments { color: #777; font-style: italic; }
    </style>
</head>
<body>
<div class="container">
    <div class="book-info">
        <h1>${book.title}</h1>
        <p class="book-meta">${book.author}, ${book.pubYear}</p>
    </div>

    <hr>

    <h3>Коментарі</h3>
    <c:if test="${empty comments}">
        <p class="no-comments">Ще немає коментарів. Будьте першим!</p>
    </c:if>

    <ul class="comment-list">
        <c:forEach var="c" items="${comments}">
            <li class="comment-item">
                <div class="comment-content">
                    <span class="comment-author">${c.author}</span>:
                        ${c.text}
                        <%-- Переконайтесь, що поле називається createdAt у класі Comment --%>
                    <span class="comment-meta">(${c.createdAt})</span>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/comments" style="margin: 0;">
                    <input type="hidden" name="bookId" value="${book.id}">
                    <input type="hidden" name="commentId" value="${c.id}">
                    <input type="hidden" name="_method" value="delete">
                    <button type="submit" class="delete-btn">Видалити</button>
                </form>
            </li>
        </c:forEach>
    </ul>

    <hr>

    <h3>Додати коментар</h3>
    <form method="post" action="${pageContext.request.contextPath}/comments">
        <input type="hidden" name="bookId" value="${book.id}">
        <div>
            <label for="authorInput">Автор:</label>
            <input type="text" id="authorInput" name="author" required placeholder="Ваше ім'я">
        </div>
        <div>
            <label for="textInput">Текст коментаря:</label>
            <textarea id="textInput" name="text" required placeholder="Напишіть ваш відгук тут..."></textarea>
        </div>
        <button type="submit">Додати коментар</button>
    </form>

    <p style="margin-top: 30px;">
        <a href="${pageContext.request.contextPath}/books">← Повернутись до списку книг</a>
    </p>
</div>
</body>
</html>