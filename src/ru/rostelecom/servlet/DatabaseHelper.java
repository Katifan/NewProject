package ru.rostelecom.servlet;

import org.hibernate.*;
import org.hibernate.cfg.AnnotationConfiguration;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Olcha on 22.11.2015.
 */
public class DatabaseHelper {
    private static SessionFactory dbSessions;

    //Создаём новую сессию для работы с БД
    private void createSessionFactory(){
        dbSessions = new AnnotationConfiguration()
                .configure("/resources/hibernate.cfg.xml")
                .addAnnotatedClass(MyTable.class)
                .buildSessionFactory();
    }

    //Проверяем наличие пользователя в БД
    public String checkUser(String login, String password){
        if(dbSessions == null)
            createSessionFactory();
        Session session = dbSessions.openSession();

        try {
            Query query = session.createQuery("FROM MyTable WHERE login = :login AND password = :password");
            query.setParameter("login", login);
            query.setParameter("password", password);
            //List usersList = session.createQuery("FROM ru.rostelecom.servlet.MyTable").list();
            List usersList = query.list();
//            for (Iterator iterator = usersList.iterator(); iterator.hasNext(); ) {
//                MyTable counter = (MyTable) iterator.next();
//
//                if (counter.getLogin().equals(login) && counter.getPassword().equals(password)){
//                    return "Добро пожаловать " + counter.getFirst_name() + " " + counter.getLast_name();
//                }
//            }
            MyTable user = (MyTable)usersList.iterator().next();
            return "Добро пожаловать " + user.getFirst_name() + " " + user.getLast_name();
        }catch (HibernateException e){
            e.printStackTrace();
        }catch (NoSuchElementException e){
            return "Простите, но ваш логин или пароль введён неверно. Пожалуйста, введите снова пароль и логин";
        }finally {
            session.close();
        }
        //Этот return придётся оставить, чтобы метод работал.
        return "Простите, но ваш логин или пароль введён неверно. Пожалуйста, введите снова пароль и логин";
    }


    //Добавление нового пользователя в БД, с заданными параметрами
    public String insertUser(int id, String login, String password, String firstName,
                             String lastName, String gender, String birthDate, String phoneNumber){
        if (dbSessions == null)
            createSessionFactory();
        Session session = dbSessions.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            MyTable table = new MyTable();
            table.setId(id);
            table.setLogin(login);
            table.setPassword(password);
            table.setFirst_name(firstName);
            table.setLast_name(lastName);
            table.setGender(gender);
            table.setBirthDate(birthDate);
            table.setPhoneNumber(phoneNumber);
            session.save(table);
            transaction.commit();
        }catch (HibernateException e){
            if (transaction!=null)
                transaction.rollback();
            e.printStackTrace();
            return "Ошибка добавления данных";
        }finally {
            session.close();
        }
        return "Данные успешно добавлены";
    }
}
