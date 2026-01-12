<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        .container { font-family: Arial, sans-serif; padding: 20px; color: #333; }
        .button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 5px;
            display: inline-block;
            margin-top: 20px;
        }
        .footer { font-size: 12px; color: #777; margin-top: 30px; }
    </style>
</head>
<body>
<div class="container">
    <h2>Вітаємо, ${email}!</h2>
    <p>Дякуємо за реєстрацію в нашому додатку "Book Library".</p>
    <p>Для активації вашого акаунту, будь ласка, натисніть на кнопку нижче:</p>

    <a href="${confirmUrl}" class="button">Підтвердити реєстрацію</a>

    <p>Або скопіюйте це посилання в браузер:</p>
    <p>${confirmUrl}</p>

    <div class="footer">
        <p>Якщо ви не реєструвалися на нашому сайті, просто ігноруйте цей лист.</p>
    </div>
</div>
</body>
</html>