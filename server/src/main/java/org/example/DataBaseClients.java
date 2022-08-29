package org.example;

import java.sql.*;

public class DataBaseClients {

    public DataBaseClients() throws SQLException, ClassNotFoundException {
        setConnection();
        createDB();
    }

    public static Connection connection;
    public static Statement statement;
    public static ResultSet resultSet;

    public static void setConnection() throws ClassNotFoundException, SQLException {
        connection = null;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:RegisteredClients:firstDB");
    }

    public static void createDB() throws SQLException {
        statement = connection.createStatement();
        statement.execute("CREATE TABLE if not exists 'clients'"+"('id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                "'login' text, 'password' text);");
    }

    public void addClient(String login, String password) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO 'clients' ('login', password) VALUES (?,?)")){
            preparedStatement.setString(1,login);
            preparedStatement.setString(2,password);
            preparedStatement.execute();

        }
    }

    public  boolean checkExists(String login) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT login FROM clients WHERE login = ?")){
            preparedStatement.setString(1,login);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return false;
            }else
                //Если нет совпадений, то всё в порядке
                return true;

        }
    }

    public  boolean checkAuthorization(String login, String password) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT login, password FROM clients WHERE login = ? AND password = ?")){
            preparedStatement.setString(1,login);
            preparedStatement.setString(2,password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return true;
            }else
                //Если нет совпадений, то авторизация не должна пройти
                return false;

        }
    }

    public void changeLogin(String login, String newLogin) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clients SET login = ? WHERE login = ?");
        preparedStatement.setString(1,newLogin);
        preparedStatement.setString(2,login);
        preparedStatement.execute();

    }

    public void readDB() throws SQLException {
        resultSet = statement.executeQuery("SELECT * FROM clients");
        while (resultSet.next()){
            String login = resultSet.getString("login");
            String password = resultSet.getString("password");
            System.out.println(login+" "+password);
        }
    }
}
