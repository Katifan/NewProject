package ru.rostelecom.servlet;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by Olcha on 06.11.2015.
 */
/*
    Сервлет, который обрабатывает запросы методом POST. Он получается пароль и логин и проверяет их наличие в БД.
*/
@WebServlet("/login")
public class TryLogin extends HttpServlet{
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //Получение json из потока
        JsonReader reader = Json.createReader(req.getInputStream());
        JsonObject newJson = reader.readObject();
        reader.close();

        //Вытаскиваем логин и пароль оттуда
        String username = newJson.getString("login");
        String password = newJson.getString("password");

        //Готовим ответ на корректность пароля и логина
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        DatabaseHelper dbHelper = new DatabaseHelper();

        //Отправляем в ответ
        out.print(dbHelper.checkUser(username, password));
    }

}
