<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Каталог книг</title>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f7f6; display: flex; flex-direction: column; align-items: center; padding: 40px; margin: 0; }
        h1 { color: #2c3e50; margin-bottom: 30px; font-size: 2.5em; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }
        .search-container { width: 100%; max-width: 700px; margin-bottom: 25px; display: flex; gap: 10px; }
        .search-input, .sort-select { padding: 12px; border: 2px solid #ddd; border-radius: 8px; outline: none; }
        .search-input { flex-grow: 1; }
        .search-button { padding: 12px 25px; background: #3498db; color: white; border: none; border-radius: 8px; cursor: pointer; font-weight: bold; }
        ul { list-style: none; padding: 0; width: 100%; max-width: 600px; }
        li { background: #fff; margin: 15px 0; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        a.book-link { display: block; padding: 20px; text-decoration: none; color: #34495e; font-weight: 500; border-left: 5px solid #3498db; border-radius: 10px; }
        .pagination { margin-top: 30px; display: flex; gap: 15px; align-items: center; }
        .page-link { text-decoration: none; color: #3498db; font-weight: bold; padding: 10px 15px; border: 1px solid #3498db; border-radius: 8px; }
    </style>
</head>
<body>

<h1>Каталог книг</h1>

<p style="color: #7f8c8d; font-style: italic; margin-bottom: 20px;">
    ${welcomeMessage}
</p>

<form method="get" action="${pageContext.request.contextPath}/books" class="search-container">
    <input type="text" name="q" class="search-input" placeholder="Пошук..." value="${param.q}">
    <select name="sort" class="sort-select">
        <option value="id" ${param.sort == 'id' ? 'selected' : ''}>За ID</option>
        <option value="title" ${param.sort == 'title' ? 'selected' : ''}>За назвою</option>
        <option value="author" ${param.sort == 'author' ? 'selected' : ''}>За автором</option>
        <option value="pub_year" ${param.sort == 'pub_year' ? 'selected' : ''}>За роком</option>
    </select>
    <button type="submit" class="search-button">Шукати</button>
</form>

<ul>
    <c:forEach var="book" items="${books}">
        <li><a href="${pageContext.request.contextPath}/comments?bookId=${book.id}" class="book-link">
                ${book.title} — ${book.author} (${book.pubYear})
        </a></li>
    </c:forEach>
</ul>

<div class="pagination">
    <c:if test="${not empty bookPage and bookPage.request.page > 0}">
        <a href="?q=${param.q}&sort=${param.sort}&page=${bookPage.request.page - 1}" class="page-link">← Назад</a>
    </c:if>

    <c:if test="${not empty bookPage}">
        <span style="font-weight: bold;">Сторінка ${bookPage.request.page + 1}</span>
    </c:if>

    <c:if test="${not empty bookPage and (bookPage.request.page + 1) * bookPage.request.size < bookPage.total}">
        <a href="?q=${param.q}&sort=${param.sort}&page=${bookPage.request.page + 1}" class="page-link">Вперед →</a>
    </c:if>
</div>

</body>
</html>