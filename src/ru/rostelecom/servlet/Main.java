package ru.rostelecom.servlet;

/**
 * Created by Olcha on 23.11.2015.
 */
//Тестовый метод для корректности запроса в БД. Вставляем свой логин и пароль в chechUser и смотрим в консоль.
public class Main {
    public static void main(String[] args){
        DatabaseHelper databaseHelper = new DatabaseHelper();
        //Тестирование правильности ввода логина и пароля
        System.out.println(databaseHelper.checkUser("demonlord32","1234"));
        //Тестирование добавления данных в БД
        //System.out.println(databaseHelper.insertUser(1,"aaaa","qwerty","testname","fdsfsd","w","1","1"));
    }
}
